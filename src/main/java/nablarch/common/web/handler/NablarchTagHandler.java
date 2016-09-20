package nablarch.common.web.handler;

import static nablarch.fw.ExecutionContext.FW_PREFIX;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.util.WebRequestUtil;
import nablarch.common.web.hiddenencryption.HiddenEncryptionUtil;
import nablarch.common.web.hiddenencryption.KeyEncryptionContextNotFoundException;
import nablarch.common.web.hiddenencryption.TamperingDetectedException;
import nablarch.common.web.tag.CheckboxTag;
import nablarch.common.web.tag.CustomTagConfig;
import nablarch.common.web.tag.TagUtil;
import nablarch.core.ThreadContext;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpRequestHandler;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.handler.HttpAccessLogFormatter.HttpAccessLogContext;
import nablarch.fw.web.handler.HttpAccessLogUtil;
import nablarch.fw.web.servlet.ServletExecutionContext;

/**
 * Nablarchのカスタムタグ機能に必要なリクエスト処理を行うハンドラ。<br>
 * このハンドラは、1リクエストにつき一度だけ下記の処理を行う。
 * 内部フォワードにより1リクエストにつき複数回呼ばれても初回のみ処理を行う。
 * <ul>
 * <li>ボタン又はリンク毎のパラメータ変更機能を実現するために、リクエストに変更パラメータを設定する。</li>
 * <li>リクエストにcheckboxタグのチェックなしに対応する値を設定する。</li>
 * <li>hiddenタグの暗号化機能に対応する改竄チェックと復号を行う。</li>
 * <li>HTTPアクセスログのリクエストパラメータを出力する。</li>
 * <li>カスタムタグのデフォルト値をJSPで参照できるように、{@link nablarch.common.web.tag.CustomTagConfig}をリクエストスコープに設定する。</li>
 * </ul>
 * 改竄チェックと復号は、カスタムタグのデフォルト値設定において、hiddenタグの暗号化機能を「使用する」に設定している場合のみ処理を行う。
 * hiddenタグの暗号化機能を「使用しない」に設定している場合は、何もせずに次のハンドラに処理を委譲する。
 * さらに、カスタムタグのデフォルト値設定の暗号化を行わないリクエストIDに現在のリクエストIDが含まれる場合は、
 * 改竄チェックと復号を行わずに次のハンドラに処理を委譲する。
 * <br>
 * このハンドラを使用する場合は、改竄を検知した場合に遷移する画面とステータスを必ずプロパティで指定する必要がある。
 * <br><br>
 * HTTPアクセスログの出力は、{@link HttpAccessLogUtil}に委譲する。
 * 
 * @author Kiyohito Itoh
 */
public class NablarchTagHandler implements HttpRequestHandler {
    
    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(NablarchTagHandler.class);
    
    /** 復号したリクエストパラメータをリクエストスコープに設定する属性名 */
    public static final String DECRYPTED_PARAMS = FW_PREFIX + "hiddenEncryption_decrypted_params";
    
    /** {@link CustomTagConfig}をリクエストスコープに格納する際に使用するキー */
    public static final String CUSTOM_TAG_CONFIG_KEY = FW_PREFIX + "tag_config";
    
    /** 改竄を検知した場合に送信する画面のリソースパス */
    private String path;
    
    /** セッションから情報が取得出来なかった場合に表示する画面のリソースパス */
    private String sessionExpirePath;
    
    /** 改竄を検知した場合のレスポンスステータス */
    private int statusCode = 400;
    
    /** セッションから情報が取得出来なかった場合のレスポンスステータス */
    private int sessionExpireStatusCode = 400;
    
    /**
     * 改竄を検知した場合に送信する画面のリソースパスを設定する。
     * @param path 改竄を検知した場合に送信する画面のリソースパス
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * セッションから暗号化鍵情報が取得出来なかった場合に表示する画面のリソースパスを設定する。 <br />
     * この値を設定しなかった場合、 path プロパティに設定した改竄エラー画面が表示される。
     * 
     * @param sessionExpirePath セッションから情報が取得出来なかった場合に表示する画面のリソースパス
     */
    public void setSessionExpirePath(String sessionExpirePath) {
        this.sessionExpirePath = sessionExpirePath;
    }
    
    /**
     * 改竄を検知した場合のレスポンスステータスを設定する。<br>
     * デフォルトは400。
     * @param statusCode 改竄を検知した場合のレスポンスステータス
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * セッションから情報が取得出来なかった場合のレスポンスステータスを設定する。
     * @param sessionExpireStatusCode セッションから情報が取得出来なかった場合のレスポンスステータス
     */
    public void setSessionExpireStatusCode(int sessionExpireStatusCode) {
        this.sessionExpireStatusCode = sessionExpireStatusCode;
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * 1リクエストにつき一度だけ下記の処理を順に行う。
     * 内部フォワードにより1リクエストにつき複数回呼ばれても初回のみ処理を行う。
     * <ul>
     * <li>リクエストに変更パラメータを設定する。</li>
     * <li>"nablarch_tag_config"という変数名でリクエストスコープに{@link nablarch.common.web.tag.CustomTagConfig}を設定する。</li>
     * <li>hiddenタグの値を復号し、リクエストパラメータに設定する。
     * 復号では、改竄チェックを行い、改竄を検知した場合は指定された画面に遷移する。
     * 復号が成功した場合は、次のハンドラに処理を委譲する。
     * 復号処理は、{@link HiddenEncryptionUtil#decryptHiddenValues(ExecutionContext, String)}に移譲する。</li>
     * <li>HTTPアクセスログのリクエストパラメータを出力する。</li>
     * </ul>
     */
    public HttpResponse handle(HttpRequest request, ExecutionContext context) {
    	
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        boolean useGetRequest = config.getUseGetRequest();
        boolean isFirstHandling = !context.getRequestScopeMap().containsKey(CUSTOM_TAG_CONFIG_KEY);
        
        if (isFirstHandling // リクエスト内で1回目の呼び出しの場合かつ、GETリクエストを使用しない場合
                && (!useGetRequest || !"get".equalsIgnoreCase(request.getMethod()))) {
            try {
                context.setRequestScopedVar(CUSTOM_TAG_CONFIG_KEY, config);
                
                String nablarchHiddenValue = getNablarchHiddenValue(request);
                String submitName = getSubmitName(request);
                
                Map<String, List<String>> params = null;
                if (isDecryptionRequired(request, config)) { // nablarch_hiddenパラメータを暗号化している場合。
                    if (nablarchHiddenValue == null) {
                        throw new TamperingDetectedException("valid hidden parameter not found.");
                    }
                    if (submitName == null) {
                        throw new TamperingDetectedException("valid submitName parameter not found.");
                    }
                    params = HiddenEncryptionUtil.decryptHiddenValues(context, nablarchHiddenValue);
                    context.setRequestScopedVar(DECRYPTED_PARAMS, params);
                    
                } else { // nablarch_hiddenパラメータを暗号化していない場合。
                    if (StringUtil.hasValue(nablarchHiddenValue)) {
                        params = WebRequestUtil.convertToParamsMap(nablarchHiddenValue);
                    }
                }
                
                if (params != null) {
                    setNablarchHiddenValueToRequest(request, submitName, params);
                }
                setCheckboxOffValueToRequest(request);
                
            } catch (KeyEncryptionContextNotFoundException e) {
                LOGGER.logInfo("session expired. cause = [" + e.getMessage() + "]");
                throw new HttpErrorResponse(sessionExpireStatusCode, 
                        sessionExpirePath == null ? path : sessionExpirePath, e);
            } catch (TamperingDetectedException e) {
                LOGGER.logInfo("tampering detected. cause = [" + e.getMessage() + "]");
                throw new HttpErrorResponse(statusCode, path, e);
            } finally {
                writeParametersLog(request, context);
            }
        }
        
        restoreKeyValueSet(request);

        return context.handleNext(request);
    }

    /**
     * 複合キーの値を復元する。
     * @param request {@link HttpRequest}
     */
    protected void restoreKeyValueSet(HttpRequest request) {
        TagUtil.restoreKeyValueSetFromHidden(request);
    }
    
    /**
     * hiddenタグの暗号化機能に対応する復号を行うか否かを判定する。
     * <p/>
     * hidden暗号化機能を使用する設定になっている場合、
     * かつ現在のリクエストIDが暗号化しないリクエストID設定に含まれない場合にtrueを返す。
     * @param config カスタムタグ設定
     * @param request HTTPリクエストオブジェクト
     * @return 復号を行う場合はtrue
     */
    private boolean isDecryptionRequired(HttpRequest request, CustomTagConfig config) {
        return config.getUseHiddenEncryption()
            && !config.getNoHiddenEncryptionRequestIds().contains(ThreadContext.getRequestId())
            || request.getParamMap().containsKey(HiddenEncryptionUtil.KEY_NEEDS_ENCRYPTION);
    }
    
    /**
     * HTTPアクセスログのリクエストパラメータを出力する。
     * @param request {@link HttpRequest}
     * @param context {@link ExecutionContext}
     * @throws ClassCastException
     *   context の型がServletExecutionContext で無い場合。
     */
    protected void writeParametersLog(HttpRequest request, ExecutionContext context) throws ClassCastException {
        HttpAccessLogContext logContext = HttpAccessLogUtil.getAccessLogContext(request, (ServletExecutionContext) context);
        HttpAccessLogUtil.logParameters(logContext);
    }
    
    /**
     * nablarch_hiddenパラメータをリクエストに設定する。
     * @param request リクエスト
     * @param submitName サブミットされた要素のname属性
     * @param params nablarch_hiddenパラメータ
     */
    private void setNablarchHiddenValueToRequest(HttpRequest request, String submitName, Map<String, List<String>> params) {
        
        boolean hitSubmission = false;
        submitName = HiddenEncryptionUtil.KEY_HIDDEN_SUBMIT_NAME_PREFIX + submitName;
        Map<String, String[]> submitParams = new HashMap<String, String[]>();
        
        for (Map.Entry<String, List<String>> param : params.entrySet()) {
            String name = param.getKey();
            if (name.startsWith(HiddenEncryptionUtil.KEY_HIDDEN_SUBMIT_NAME_PREFIX)) { // サブミット情報の場合。
                
                if (submitName.equals(param.getKey())) { // リクエストされたサブミット名に該当するサブミット情報の場合。
                    hitSubmission = true;
                    String value = param.getValue().get(0);
                    if (StringUtil.hasValue(value)) {
                        for (Map.Entry<String, List<String>> submitParam : WebRequestUtil.convertToParamsMap(value).entrySet()) {
                            List<String> submitParamValues = submitParam.getValue();
                            String[] arraySubmitParamValues = new String[submitParamValues.size()];
                            submitParams.put(submitParam.getKey(), submitParamValues.toArray(arraySubmitParamValues));
                        }
                    }
                }
            } else {
                // サブミット情報以外は、全てリクエストに設定する。
                List<String> values = param.getValue();
                String[] arrayValues = new String[values.size()];
                request.getParamMap().put(param.getKey(), values.toArray(arrayValues));
            }
        }
        
        // サブミット情報による上書き
        request.getParamMap().putAll(submitParams);
        
        if (!hitSubmission) {
            // 復号したhiddenパラメータから、今回のリクエストでサブミットされたものに該当する
            // サブミット情報が見つからない場合は、改竄エラーとする。
            throw new TamperingDetectedException("submitName was invalid.");
        }
    }
    
    /**
     * checkboxタグのチェックなしに対応する値をリクエストに設定する。
     * @param request リクエスト
     */
    private void setCheckboxOffValueToRequest(HttpRequest request) {
        Map<String, String[]> paramsToOverride = new HashMap<String, String[]>();
        Map<String, String[]> params = request.getParamMap();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String name = param.getKey();
            if (name.startsWith(CheckboxTag.CHECKBOX_OFF_PARAM_PREFIX)) {
                String originalName = name.substring(CheckboxTag.CHECKBOX_OFF_PARAM_PREFIX.length());
                if (!params.containsKey(originalName)) {
                    paramsToOverride.put(originalName, param.getValue());
                }
            }
        }
        params.putAll(paramsToOverride);
    }
    
    /**
     * nablarch_hiddenパラメータの値を取得する。
     * @param request リクエスト
     * @return nablarch_hiddenパラメータの値。有効な値が存在しない場合はnull
     */
    private String getNablarchHiddenValue(HttpRequest request) {
        String[] values = request.getParamMap().remove(HiddenEncryptionUtil.KEY_HIDDEN_NAME);
        return (values != null && values.length == 1) ? values[0] : null;
    }
    
    /**
     * サブミットされた要素のname属性を取得する。
     * @param request リクエスト
     * @return サブミットされた要素のname属性。有効な値が存在しない場合はnull
     */
    private String getSubmitName(HttpRequest request) {
        String[] values = request.getParamMap().remove(HiddenEncryptionUtil.KEY_SUBMIT_NAME);
        return (values != null && values.length == 1) ? values[0] : null;
    }
}
