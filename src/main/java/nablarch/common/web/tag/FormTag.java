package nablarch.common.web.tag;

import static nablarch.fw.ExecutionContext.FW_PREFIX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import nablarch.common.util.WebRequestUtil;
import nablarch.common.web.WebConfig;
import nablarch.common.web.WebConfigFinder;
import nablarch.common.web.exclusivecontrol.HttpExclusiveControlUtil;
import nablarch.common.web.hiddenencryption.HiddenEncryptionUtil;
import nablarch.common.web.tag.ChangeParamNameTag.ChangeParamName;
import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.common.web.token.TokenUtil;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.Builder;
import nablarch.fw.web.post.PostResubmitPreventHandler;
import nablarch.fw.web.servlet.NablarchHttpServletRequestWrapper;

/**
 * サブミット制御(ボタンとアクションの紐付け、二重サブミット防止)と不正画面遷移チェックを行うformタグを出力するクラス。
 * @author Kiyohito Itoh
 */
public class FormTag extends GenericAttributesTagSupport {
    
    /** ウィンドウスコープ変数のプレフィックス */
    private Set<String> windowScopePrefixes;
    
    /** トークンを設定するか否か */
    private Boolean useToken;

    /** URIをhttpsにするか否か */
    private Boolean secure;

    /** POST再送信を防止するか否か */
    private boolean preventPostResubmit = false;
    
    /**
     * ウィンドウスコープ変数のプレフィックスを設定する。
     * <pre>
     * 複数指定する場合はカンマ区切り。
     * </pre>
     * @param windowScopePrefixes ウィンドウスコープ変数のプレフィックス
     */
    public void setWindowScopePrefixes(String windowScopePrefixes) {
        Set<String> values = TagUtil.getCommaSeparatedValue(windowScopePrefixes);
        this.windowScopePrefixes = new HashSet<String>(values.size());
        for (String prefix : values) {
            this.windowScopePrefixes.add(prefix.trim());
        }
    }
    
    /**
     * トークンを設定するか否かを設定する。
     * <pre>
     * デフォルトはfalse。
     * confirmationPageタグが指定された場合は、デフォルトがtrueとなる。
     * </pre>
     * @param useToken トークンを設定する場合はtrue、設定しない場合はfalse。
     */
    public void setUseToken(Boolean useToken) {
        this.useToken = useToken;
    }


    /**
     * URIをhttpsにするか否かを設定する。
     * @param secure httpsにする場合はtrue、しない場合はfalse。
     */
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    /**
     * POST再送信を防止するか否かを設定する。
     * <p/>
     * デフォルトはfalse。
     * @param preventPostResubmit POST再送信を防止する場合はtrue、しない場合はfalse。
     */
    public void setPreventPostResubmit(boolean preventPostResubmit) {
        this.preventPostResubmit = preventPostResubmit;
    }
    
    /**
     * XHTMLのaction属性を設定する。
     * @param action XHTMLのaction属性
     */
    public void setAction(String action) {
        getAttributes().put(HtmlAttribute.ACTION, action);
    }

    /**
     * HTML5のautocomplete属性を設定する。
     * @param autocomplete HTML5のautocomplete属性
     */
    public void setAutocomplete(String autocomplete) {
        getAttributes().put(HtmlAttribute.AUTOCOMPLETE, autocomplete);
    }

    /**
     * XHTMLのmethod属性を設定する。<br>
     * デフォルトはpost。
     * @param method XHTMLのmethod属性
     */
    public void setMethod(String method) {
        getAttributes().put(HtmlAttribute.METHOD, method);
    }
    
    /**
     * XHTMLのname属性を設定する。
     * @param name XHTMLのname属性
     */
    public void setName(String name) {
        getAttributes().put(HtmlAttribute.NAME, name);
    }

    /**
     * XHTMLのenctype属性を設定する。
     * @param enctype XHTMLのenctype属性
     */
    public void setEnctype(String enctype) {
        getAttributes().put(HtmlAttribute.ENCTYPE, enctype);
    }

    /**
     * XHTMLのonsubmit属性を設定する。
     * @param onsubmit XHTMLのonsubmit属性
     */
    public void setOnsubmit(String onsubmit) {
        getAttributes().put(HtmlAttribute.ONSUBMIT, onsubmit);
    }

    /**
     * XHTMLのonreset属性を設定する。
     * @param onreset XHTMLのonreset属性
     */
    public void setOnreset(String onreset) {
        getAttributes().put(HtmlAttribute.ONRESET, onreset);
    }

    /**
     * XHTMLのaccept属性を設定する。
     * @param accept XHTMLのaccept属性
     */
    public void setAccept(String accept) {
        getAttributes().put(HtmlAttribute.ACCEPT, accept);
    }

    /**
     * XHTMLのaccept-charset属性を設定する。
     * @param acceptCharset XHTMLのaccept-charset属性
     */
    public void setAcceptCharset(String acceptCharset) {
        getAttributes().put(HtmlAttribute.ACCEPT_CHARSET, acceptCharset);
    }

    /**
     * XHTMLのtarget属性を設定する。
     * @param target XHTMLのtarget属性
     */
    public void setTarget(String target) {
        getAttributes().put(HtmlAttribute.TARGET, target);
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 開始タグとサブミット制御に使用するJavaScriptのサブミット関数を出力する。
     * method属性がpostかつ画面内で1番目のフォームタグの場合のみサブミット関数を出力する。
     * サブミット関数が出力される前にサブミットされJavaScriptエラーが発生することを防ぐため、
     * JavaScriptのサブミット関数は開始タグの直前に出力する。
     * method属性の指定がない場合は、postを設定する。
     * name属性の指定がない場合は、値を生成し設定する。
     * 属性はHTMLエスケープして出力する。
     * </pre>
     */
    @Override
    public int doStartTag() throws JspException {
        
        if (getAttributes().get(HtmlAttribute.METHOD) == null) {
            getAttributes().put(HtmlAttribute.METHOD, "post");
        }
        
        String formName = getName();
        if (getAttributes().get(HtmlAttribute.NAME) == null) {
            getAttributes().put(HtmlAttribute.NAME, formName);
        }

        if (getAttributes().get(HtmlAttribute.ACTION) != null) {
            TagUtil.overrideUriAttribute(pageContext, getAttributes(), HtmlAttribute.ACTION, secure);
        }

        if (getAttributes().get(HtmlAttribute.AUTOCOMPLETE) == null) {
            AutocompleteDisableTarget defaultTarget = TagUtil.getCustomTagConfig().getAutocompleteDisableTarget();
            if (defaultTarget == AutocompleteDisableTarget.ALL) {
                getAttributes().put(HtmlAttribute.AUTOCOMPLETE, "off");
            }
        }
        
        if (!isGetRequest()) {
            printSubmitFunctionJS(pageContext); // サブミット関数は開始タグの直前に出力する。
        }

        StringBuilder sb = new StringBuilder();
        sb.append(TagUtil.createStartTag("form", getAttributes()));
        TagUtil.print(pageContext, TagUtil.getCustomTagConfig().getLineSeparator() + sb.toString());
        TagUtil.setFormContext(pageContext, new FormContext(formName));
        
        return EVAL_BODY_INCLUDE;
    }
    
    /**
     * name属性を取得する。
     * <pre>
     * 未指定の場合は、値を生成する。
     * 値のフォーマット：「"nablarch_form"＋連番」
     * </pre>
     * @return name属性
     */
    private String getName() {
        String name = getAttributes().get(HtmlAttribute.NAME);
        if (name != null) {
            return name;
        }
        return TagUtil.generateUniqueName(pageContext, "form");
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 閉じタグとサブミット制御に使用するJavaScriptのサブミット情報を出力する。
     * 
     * サブミット制御のJavaScriptの出力が完了する前にサブミットされることを防ぐため、
     * サブミット制御のJavaScriptの出力が完了したことを示すマーカを閉じタグの直後に出力する。
     * ここで出力したマーカをサブミット関数が参照し、サブミット可否の判定に使用する。
     *
     * CSRFトークンがリクエスト属性に存在すればhiddenタグに追加する。
     *
     * 楽観的ロックで使用するバージョン番号をhiddenタグに追加する。
     * 
     * ウィンドウスコープ変数のプレフィックスが指定されている場合は、リクエストパラメータからhiddenタグを出力する。
     * リクエストパラメータからhiddenタグを出力する際は、既に入力項目として出力されているパラメータは除く。
     * 
     * hiddenタグは、改竄や情報漏洩などのセキュリティ上の問題となるケースがあるため、デフォルトで暗号化して出力する。
     * ただし、下記のどちらかの条件を満たす場合は暗号化せずに平文でhiddenタグを出力する。
     * ・hiddenタグの暗号化機能を「使用しない」に設定している。
     * ・フォーム内のリクエストIDが設定された暗号化を行わないリクエストIDに全て含まれる。
     * 上記設定は、カスタムタグのデフォルト値設定から行う。
     * 
     * {@link #useToken}にtrueが指定されている場合、又は{@link #useToken}が未指定で確認画面の場合は、
     * トークンを生成しhiddenタグを出力する。
     * 
     * ただし、method属性がgetかつ{@link CustomTagConfig}のuseGetRequestがtrueの場合は、上記処理は行わずに閉じタグのみを出力して処理を終了する。
     * </pre>
     */
    @Override
    public int doEndTag() throws JspException {
        
        if (isGetRequest()) {
            outputCloseTag();
            return EVAL_PAGE;
        }

        setCsrfTokenToFormContext(pageContext);

        boolean printToken = useToken != null ? useToken : TagUtil.isConfirmationPage(pageContext);
        if (printToken) {
            setTokenToSessionAndFormContext(pageContext);
        }
        
        setOptimisticLockVersionToFormContext(pageContext);
        setWindowScopeToFormContext(pageContext);
        setFormatSpecForChangeParamName(pageContext);
        printNablarchHidden(pageContext);
        printSubmitInfoJS(pageContext, getAttributes());

        if (preventPostResubmit) {
            TagUtil.print(pageContext, TagUtil.getCustomTagConfig().getLineSeparator() + createPostResubmitPreventParam());
        }        

        outputCloseTag();

        // サブミット制御のJavaScriptの出力が完了したことを示すマーカは閉じタグの直後に出力する。
        printSubmitEndMarkJS(pageContext, getAttributes());

        return EVAL_PAGE;
    }

    /**
     * このフォームがGETメソッドによるリクエストか否かを判定する。
     * 
     * @return このフォームがGETメソッドによるリクエストの場合はtrue、GETメソッドでない場合はfalse
     */
    private boolean isGetRequest() {
        return TagUtil.getCustomTagConfig().getUseGetRequest() && "get".equalsIgnoreCase((String) getAttributes().get(HtmlAttribute.METHOD));
    }
    
    /**
     * 閉じタグを出力する。
     * @throws JspException　JSP例外
     */
    private void outputCloseTag() throws JspException {
        TagUtil.print(pageContext, TagUtil.getCustomTagConfig().getLineSeparator() + TagUtil.createEndTag("form"));
        TagUtil.setFormContext(pageContext, null);
    }

    /**
     * POST再送信の防止を指示するパラメータ用のhiddenタグを作成する。
     * @return POST再送信の防止を指示するパラメータ用のhiddenタグ
     */
    private String createPostResubmitPreventParam() {
        return TagUtil.createHiddenTag(PostResubmitPreventHandler.POST_RESUBMIT_PREVENT_PARAM, "true");
    }

    /**
     * 楽観的ロックで使用するバージョン番号をフォームコンテキストに設定する。
     * @param pageContext ページコンテキスト
     */
    @SuppressWarnings("unchecked")
    private void setOptimisticLockVersionToFormContext(PageContext pageContext) {
        Object versions = TagUtil.getValue(pageContext, HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME, false);
        if (versions == null) {
            return;
        }
        FormContext formContext = TagUtil.getFormContext(pageContext);
        for (String version : (List<String>) versions) {
            formContext.addHiddenTagInfo(HttpExclusiveControlUtil.VERSION_PARAM_NAME, version);
        }
    }

    /**
     * CSRFトークンがリクエスト属性に存在すればフォームコンテキストに設定する。
     * @param pageContext ページコンテキスト
     */
    private void setCsrfTokenToFormContext(PageContext pageContext) {
        WebConfig config = WebConfigFinder.getWebConfig();
        Object csrfToken = pageContext.getRequest().getAttribute(config.getCsrfTokenSessionStoredVarName());
        if (csrfToken != null) {
            TagUtil.getFormContext(pageContext)
                    .addHiddenTagInfo(config.getCsrfTokenParameterName(), csrfToken.toString());
        }
    }

    /**
     * トークンを生成し、セッションスコープとフォームコンテキストに設定する。
     * @param pageContext ページコンテキスト
     * @throws JspException JSP例外
     */
    private void setTokenToSessionAndFormContext(PageContext pageContext) throws JspException {
        String name = WebConfigFinder.getWebConfig().getDoubleSubmissionTokenParameterName();
        final HttpServletRequest httpServletRequest = (HttpServletRequest) pageContext.getRequest();
        String token = TokenUtil.generateToken(new NablarchHttpServletRequestWrapper(httpServletRequest));
        TagUtil.getFormContext(pageContext).addHiddenTagInfo(name, token);
    }
    
    /**
     * ウィンドウスコープをフォームコンテキストに設定する。
     * @param pageContext ページコンテキスト
     * @throws JspException JSP例外
     */
    @SuppressWarnings("unchecked")
    private void setWindowScopeToFormContext(PageContext pageContext) throws JspException {
        FormContext formContext = TagUtil.getFormContext(pageContext);
        Set<String> inputNames = formContext.getInputNames();
        // ウィンドウスコープに一致しないもの、既に入力項目として出力されたものを除いて、
        // 出力対象のhiddenタグにリクエストパラメータを追加する。
        Map<String, String[]> params = pageContext.getRequest().getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String name = param.getKey();
            if (!containsWindowScopePrefix(name) || inputNames.contains(name)) {
                continue;
            }
            final String[] values = param.getValue();
            final String[] convertValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                convertValues[i] = values[i] == null ? "" : values[i];
            }
            formContext.addHiddenTagInfo(name, convertValues);
        }
    }

    /**
     * n:changeParamNameタグにより送信される可能性があるフォーマット情報をnablarch_hiddenに出力する。
     * @param pageContext ページコンテキスト
     * @throws JspException JSP例外
     */
    private void setFormatSpecForChangeParamName(PageContext pageContext) throws JspException {

        FormContext formContext = TagUtil.getFormContext(pageContext);

        for (SubmissionInfo submissionInfo : formContext.getSubmissionInfoList()) {
            for (ChangeParamName changeParamName : submissionInfo.getChangeParamNames()) {
                copyFormatSpec(formContext, changeParamName, "_nablarch_formatSpec");
                copyFormatSpec(formContext, changeParamName, "_nablarch_formatSpec_separator");
            }
        }
    }

    /**
     * パラメータ名の変更情報に合わせてフォーマット情報をコピーする。
     * <p/>
     * パラメータ名の変更情報に対応するフォーマット情報がnablarch_hiddenに存在しない場合は何もしない。
     * 
     * @param formContext フォームコンテキスト
     * @param changeParamName パラメータ名の変更情報
     * @param formatSpecSuffix フォーマット情報のパラメータに付けるサフィックス
     */
    private void copyFormatSpec(FormContext formContext, ChangeParamName changeParamName, String formatSpecSuffix) {
        String inputName = changeParamName.getInputName();
        String paramName = changeParamName.getParamName();
        HtmlAttributes formatSpecParam = formContext.getHiddenTagInfo(inputName + formatSpecSuffix);
        if (formatSpecParam != null) {
            formContext.addHiddenTagInfo(
                paramName + formatSpecSuffix, (String) formatSpecParam.get(HtmlAttribute.VALUE));
        }
    }

    /**
     * ウィンドウスコープ及びフォームコンテキストに設定されたhidden情報をまとめてnablarch_hiddenに出力する。
     * <p/>
     * nablarch_submitも合わせて出力する。
     * nablarch_submitの初期値は空文字とする。
     * 
     * @param pageContext ページコンテキスト
     * @throws JspException JSP例外
     */
    private void printNablarchHidden(PageContext pageContext) throws JspException {
        
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        
        FormContext formContext = TagUtil.getFormContext(pageContext);
        Map<String, List<String>> hiddenTagValues = getHiddenTagValues(formContext);
        
        String value;
        
        List<String> requestIds = formContext.getRequestIds();
        
        value = needsHiddenEncryption(config, requestIds)
              ? HiddenEncryptionUtil.encryptHiddenValues(pageContext, requestIds, hiddenTagValues)
              : WebRequestUtil.convertToParamsString(hiddenTagValues);
                          
        TagUtil.print(pageContext, config.getLineSeparator() + TagUtil.createHiddenTag(HiddenEncryptionUtil.KEY_HIDDEN_NAME, value));
        TagUtil.print(pageContext, config.getLineSeparator() + TagUtil.createHiddenTag(HiddenEncryptionUtil.KEY_SUBMIT_NAME, ""));
    }
    
    /**
     * フォームコンテキストから暗号化対象のhiddenタグの値を取得する。
     * @param formContext フォームコンテキスト
     * @return 暗号化対象のhiddenタグの値
     */
    private Map<String, List<String>> getHiddenTagValues(FormContext formContext) {
        Map<String, List<String>> values = new HashMap<String, List<String>>();
        setUpHiddenTagInfo(values, formContext.getHiddenTagInfoList());
        setUpSubmissionInfo(values, formContext.getSubmissionInfoList());
        return values;
    }
    
    /**
     * フォームに含まれるhiddenタグの情報をhiddenタグの値に設定する。
     * @param values hiddenタグの値
     * @param hiddenTagInfoList フォームに含まれるhiddenタグの情報
     */
    private void setUpHiddenTagInfo(Map<String, List<String>> values, List<HtmlAttributes> hiddenTagInfoList) {
        for (HtmlAttributes attributes : hiddenTagInfoList) {
            String name = attributes.get(HtmlAttribute.NAME);
            String value = attributes.get(HtmlAttribute.VALUE);
            if (values.containsKey(name)) {
                values.get(name).add(value);
            } else {
                List<String> paramValues = new ArrayList<String>();
                paramValues.add(value);
                values.put(name, paramValues);
            }
        }
    }
    
    /**
     * サブミット情報に含まれるパラメータをhiddenタグの値に設定し、元々含まれていた値をクリアする。
     * サブミット情報に含まれるパラメータには、サブミットした要素を識別するname属性のみ設定する。
     * @param values hiddenタグの値
     * @param submissionInfoList フォームに含まれるサブミット情報
     */
    private void setUpSubmissionInfo(Map<String, List<String>> values, List<SubmissionInfo> submissionInfoList) {
        for (SubmissionInfo info : submissionInfoList) {
            String submitName = info.getName();
            String name = HiddenEncryptionUtil.KEY_HIDDEN_SUBMIT_NAME_PREFIX + submitName;
            values.put(name, Arrays.asList(WebRequestUtil.convertToParamsString(info.getParamsMap())));
            info.clearParams();
            info.addParam(HiddenEncryptionUtil.KEY_SUBMIT_NAME, submitName);
        }
    }
    
    /**
     * hiddenタグの暗号化が必要か否かを判定する。<br>
     * カスタムタグのデフォルト値設定について下記の条件を全て満たす場合にtrueを返す。
     * <ul>
     * <li>hiddenタグの暗号化機能を「使用する」に設定している</li>
     * <li>フォーム内のリクエストIDに、hiddenタグ暗号化対象となるものが1つ以上存在する。</li>
     * </ul>
     * なお、本来であれば、同一のForm内にhiddenタグの暗号化を行うものとそうでないものが
     * 混在することは設計上ありえないため、そのような状態である場合は、hiddenタグの暗号化機能の
     * 有効無効に関わらず、ワーニングレベルのログを出力する。
     * 
     * @param config カスタムタグ設定
     * @param requestIds フォームに含まれるリクエストID
     * @return hiddenタグの暗号化が必要な場合はtrue
     * @throws JspException 強制暗号化フラグ(hiddenタグ)の出力に失敗した場合。
     * 
     */
    private boolean needsHiddenEncryption(CustomTagConfig config, List<String> requestIds)
    throws JspException {
        Set<String> openHiddenRequests = config.getNoHiddenEncryptionRequestIds();
        boolean hasOpenHiddenRequest = false;
        boolean hasClosedHiddenRequest = false;
        List<String> notEncryptedRequests = new ArrayList<String>();
        
        for (String requestId : requestIds) {
            if (openHiddenRequests.contains(requestId)) {
                hasOpenHiddenRequest = true;
                notEncryptedRequests.add(requestId); 
            } else {
                hasClosedHiddenRequest = true;
            }
        }
        
        // 暗号化が有効で、一つでも暗号化対象のリクエストパスが含まれればtrueを返す。
        boolean needsEncryption = config.getUseHiddenEncryption() && hasClosedHiddenRequest;
        
        // タグ内にhiddenタグの内容を暗号化するリンクとそうでないリンクが混在している場合
        // ワーニングログを出力した上で強制的に暗号化を行う。
        if (hasClosedHiddenRequest && hasOpenHiddenRequest) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.logWarn(
                    "A requestPath that requires the hidden tag encryption "
                  + "can not belongs to a <form> tag that contains another path "
                  + "that is declared not to be encrypted in the configuration. "
                  + "form tag's name = [" + getAttributes().get(HtmlAttribute.NAME) + "] "
                  + "request paths declared not to be encrypted = " + notEncryptedRequests.toString()
               );
            }
            if (needsEncryption) {
                TagUtil.print(pageContext,
                    config.getLineSeparator()
                  + TagUtil.createHiddenTag(HiddenEncryptionUtil.KEY_NEEDS_ENCRYPTION, "")
                );
            }
        }        
        return needsEncryption;
    }
    
    /**
     * クライアントステートのプレフィックスに含まれているかを判定する。
     * @param name パラメータ名
     * @return 含まれている場合はtrue、含まれていない場合はfalse
     */
    private boolean containsWindowScopePrefix(String name) {
        if (windowScopePrefixes == null) {
            return false;
        }
        for (String prefix : windowScopePrefixes) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * サブミット制御に使用するJavaScriptのサブミット関数を出力する。<br>
     * 画面内で1番目のフォームタグの場合のみサブミット関数を出力する。
     * @param pageContext ページコンテキスト
     * @throws JspException JSP例外
     */
    private void printSubmitFunctionJS(PageContext pageContext) throws JspException {
        if (!TagUtil.jsSupported(pageContext)) {
            return;
        }
        String ls = TagUtil.getCustomTagConfig().getLineSeparator();
        if (isFirstForm()) {
            TagUtil.print(pageContext, ls + TagUtil.createScriptTag(pageContext, getSubmitFunction()));
        }
    }

    /**
     * サブミット制御に使用するJavaScriptのサブミット情報を出力する。
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @throws JspException JSP例外
     */
    private void printSubmitInfoJS(PageContext pageContext, HtmlAttributes attributes) throws JspException {
        if (!TagUtil.jsSupported(pageContext)) {
            return;
        }

        StringBuilder javaScript = new StringBuilder();
        String ls = TagUtil.getCustomTagConfig().getLineSeparator();

        FormContext formContext = TagUtil.getFormContext(pageContext);

        // サブミット用のスクリプトが登録されていた場合は、合わせて出力する。
        // CSP対応のため、HTMLタグの属性に直接出力するのではなくscriptタグ内に含める。
        appendInlineOnclickSubmissionScripts(formContext, javaScript);

        // サブミッション情報のスクリプトを追加する
        appendSubmissionInfoScripts(formContext, attributes, javaScript);
        
        TagUtil.print(pageContext, ls + TagUtil.createScriptTag(pageContext, javaScript.toString()));
    }

    /**
     * {@link FormContext}内にサブミット用のスクリプトが登録されていた場合、引数の{@code javaScript}に
     * スクリプトを追加する。
     *
     * @param formContext {@link FormContext}
     * @param javaScript スクリプトを追加する{@link StringBuilder}
     */
    private void appendInlineOnclickSubmissionScripts(FormContext formContext, StringBuilder javaScript) {
        String ls = TagUtil.getCustomTagConfig().getLineSeparator();

        List<String> inlineOnclickSubmissionScripts = formContext.getInlineSubmissionScripts();
        if (!inlineOnclickSubmissionScripts.isEmpty()) {
            for (String script : inlineOnclickSubmissionScripts) {
                javaScript.append(ls).append(script);
            }

            javaScript.append(ls).append(ls);
        }
    }

    /**
     * {@link FormContext}内のサブミッション情報からスクリプトを生成し、{@code javaScript}に
     * 追加する。
     *
     * @param formContext {@link FormContext}
     * @param attributes 属性
     * @param javaScript スクリプト追加対象
     */
    private void appendSubmissionInfoScripts(FormContext formContext, HtmlAttributes attributes, StringBuilder javaScript) {
        String ls = TagUtil.getCustomTagConfig().getLineSeparator();
        String formName = TagUtil.escapeHtml(attributes.get(HtmlAttribute.NAME), false);

        javaScript.append(SUBMISSION_INFO_VAR).append(".").append(formName).append(" = {").append(ls);
        List<SubmissionInfo> infoList = formContext.getSubmissionInfoList();
        for (int i = 0; i < infoList.size(); i++) {
            SubmissionInfo info = infoList.get(i);
            String hash;
            SubmissionAction submissionAction = info.getAction();
            if (SubmissionAction.POPUP == submissionAction) {
                String popupWindowName = info.getPopupWindowName();
                String popupOption = info.getPopupOption();
                hash = String.format(POPUP_SUBMISSION_INFO_HASH,
                        info.getName(),
                        info.getUri(),
                        info.isAllowDoubleSubmission(),
                        submissionAction.name(),
                        popupWindowName != null ? "\"" + popupWindowName + "\"" : null,
                        popupOption != null ? popupOption : "",
                        createChangeParamNamesHash(info.getChangeParamNames()));
            } else if (SubmissionAction.DOWNLOAD == submissionAction) {
                hash = String.format(DOWNLOAD_SUBMISSION_INFO_HASH,
                        info.getName(),
                        info.getUri(),
                        info.isAllowDoubleSubmission(),
                        submissionAction.name(),
                        createChangeParamNamesHash(info.getChangeParamNames()));
            } else {
                hash = String.format(SUBMISSION_INFO_HASH,
                        info.getName(),
                        info.getUri(),
                        info.isAllowDoubleSubmission(),
                        submissionAction.name());
            }
            javaScript.append(hash);
            if (i != infoList.size() - 1) {
                javaScript.append(",");
            }
            javaScript.append(ls);
        }
        javaScript.append("};");
    }

    /**
     * サブミット制御に使用するJavaScriptの出力が完了したことを示すマーカ情報を出力する。
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @throws JspException JSP例外
     */
    private void printSubmitEndMarkJS(PageContext pageContext, HtmlAttributes attributes) throws JspException {
        if (!TagUtil.jsSupported(pageContext)) {
            return;
        }

        StringBuilder javaScript = new StringBuilder();
        String ls = TagUtil.getCustomTagConfig().getLineSeparator();
        String formName = TagUtil.escapeHtml(attributes.get(HtmlAttribute.NAME), false);
        javaScript.append(SUBMISSION_END_MARK_PREFIX).append(".").append(formName).append(" = true;");
        TagUtil.print(pageContext, ls + TagUtil.createScriptTag(pageContext, javaScript.toString()));
    }

    /**
     * パラメータ名の変更情報リストからハッシュを生成する。
     * @param changeParamNames パラメータ名の変更情報リスト
     * @return パラメータ名の変更情報リストハッシュ
     */
    private String createChangeParamNamesHash(List<ChangeParamName> changeParamNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (ChangeParamName changeParamName : changeParamNames) {
            if (sb.length() != 1) {
                sb.append(",");
            }
            sb.append(String.format(PARAM_HASH,
                                    TagUtil.escapeJavaScriptString(changeParamName.getInputName()),
                                    TagUtil.escapeJavaScriptString(changeParamName.getParamName())));
        }
        sb.append("}");
        return sb.toString();
    }
    
    /** 画面内で1番目のフォームタグかどうかの情報をリクエストスコープに設定する際に使用するキー */
    private static final String FIRST_FORM = FW_PREFIX + "first_form";
    
    /**
     * 画面内で1番目のフォームタグかどうかを返す。
     * @return 画面内で1番目のフォームタグの場合はtrue
     */
    private boolean isFirstForm() {
        String isFirstForm = (String) pageContext.getAttribute(FIRST_FORM, PageContext.REQUEST_SCOPE);
        if (isFirstForm == null) {
            pageContext.setAttribute(FIRST_FORM, "", PageContext.REQUEST_SCOPE);
            return true;
        }
        return false;
    }
    
    /** サブミット情報を格納するグローバル変数名 */
    private static final String SUBMISSION_INFO_VAR = FW_PREFIX + "submission_info";

    /** サブミット制御のJavaScriptの出力が完了したことを示すマーカのプレフィックス */
    private static final String SUBMISSION_END_MARK_PREFIX = SUBMISSION_INFO_VAR + ".endMark";

    /** フォームのサブミット制御を行うJavaScript関数 */
    private static String submitFunction;
    
    /**
     * フォームのサブミット制御を行うJavaScript関数を取得する。
     * リポジトリから改行コード（カスタムタグのデフォルト値設定）を取得するため、このメソッドを設けている。
     * @return フォームのサブミット制御を行うJavaScript関数
     */
    private static String getSubmitFunction() {
        if (submitFunction != null) {
            return submitFunction;
        }
        String ls = TagUtil.getCustomTagConfig().getLineSeparator();
        submitFunction = Builder.join(new String[] {
                                        SUBMIT_FUNCTION,
                                        SUBMIT_ON_WINDOW_FUNCTION,
                                        SUBMIT_TO_NEW_FORM_FUNCTION,
                                        FIND_FORM_FUNCTION,
                                        INVOKE_ON_SUBMIT_FUNCTION,
                                        ADD_HIDDEN_TAG_FUNCTION,
                                        STOP_SUBMISSION_FUNCTION,
                                        "var $submissionInfoVar$ = {};",
                                        "$submissionEndMarkPrefix$ = {};"}, ls)
                                .replace(Builder.LS, ls)
                                .replace("$fwPrefix$", FW_PREFIX)
                                .replace("$submissionInfoVar$", SUBMISSION_INFO_VAR)
                                .replace("$submissionEndMarkPrefix$", SUBMISSION_END_MARK_PREFIX)
                                .replace("$popupAction$", SubmissionAction.POPUP.name())
                                .replace("$downloadAction$", SubmissionAction.DOWNLOAD.name());
        return submitFunction;
    }
    
    /** サブミット情報のハッシュ */
    private static final String SUBMISSION_INFO_HASH
        = "\"%s\": { \"action\": \"%s\", \"allowDoubleSubmission\": %s, \"submissionAction\": \"%s\" }";
    
    /** ダウンロード用のサブミット情報のハッシュ */
    private static final String DOWNLOAD_SUBMISSION_INFO_HASH
        = "\"%s\": { \"action\": \"%s\", \"allowDoubleSubmission\": %s, \"submissionAction\": \"%s\", \"changeParamNames\": %s }";
    
    /** ポップアップ用のサブミット情報のハッシュ */
    private static final String POPUP_SUBMISSION_INFO_HASH
        = "\"%s\": { \"action\": \"%s\", \"allowDoubleSubmission\": %s, \"submissionAction\": \"%s\""
                + ", \"popupWindowName\": %s, \"popupOption\": \"%s\", \"changeParamNames\": %s }";
    
    /** パラメータのハッシュ値 */
    private static final String PARAM_HASH = "\"%s\": \"%s\"";
    
    /** submit関数 */
    private static final String SUBMIT_FUNCTION = Builder.join(new String[] {
            
            // サブミット時に呼ばれる関数
            "function $fwPrefix$submit(event, element) {",

                 // HTMLタグのイベントハンドラに直接設定する実装からscriptタグに移した際に
                 // 後方互換を保つためにeventから対象の要素を取得する
            "    if (element == null) {",
            "        element = event.currentTarget;",
                     // eventにcurrentTargetが設定されていない場合はtargetから取得する。
                     // jQueryによるイベント発火($(...).click()など)を行った場合はこのケースになる
            "        if (element == null) {",
            "            element = event.target;",
            "        }",
            "    }",

            "    var isAnchor = element.tagName.match(/a/i);",

                 // formタグを取得する。
            "    var form = $fwPrefix$findForm(element, isAnchor);",
            "    if (form == null) {",
            "        return false;",
            "    }",

                 // サブミット制御のJavaScriptの出力が完了したことを示すマーカを取得する。
            "    var formName = form.attributes['name'].nodeValue;",
            "    if ($submissionEndMarkPrefix$[formName] == null) {",
            "        return false;",
            "    }",

            "    if ((typeof form.onsubmit) == \"function\") {",
                     // formタグのonsubmitを呼び出す。
            "        if (!$fwPrefix$invokeOnsubmit(form, event)) {",
            "            return false;",
            "        }",
            "    }",

            "    var submitName = element.name;",

                 // フォームに含まれるサブミット情報を取得する。
            "    var formData = $submissionInfoVar$[formName];",

                 // イベント発生元のサブミット情報を取得する。
            "    var submissionData = formData[submitName];",

            "    if (!submissionData.allowDoubleSubmission) {",
                     // リクエストの二重送信を防止する。
            "        element.onclick = $fwPrefix$stopSubmission;",
            "        if (!isAnchor) {",
            "            element.disabled = true;",
            "        }",
            "    }",

                 // nablarch_submitの値を更新する。
            "    form[\"" + HiddenEncryptionUtil.KEY_SUBMIT_NAME + "\"].value = submitName;",

            "    if (submissionData.submissionAction == \"$popupAction$\"",
            "            || submissionData.submissionAction == \"$downloadAction$\") {",
                     // 新しいフォームにサブミットする。
            "        $fwPrefix$submitToNewForm(submitName, form, submissionData)",
            "    } else {",
                     // 画面上のフォームをサブミットする。
            "        $fwPrefix$submitOnWindow(submitName, form, submissionData);",
            "    }",

            "    return false;",
            "}"

    }, Builder.LS);
    
    /** submitOnWindow関数 */
    private static final String SUBMIT_ON_WINDOW_FUNCTION = Builder.join(new String[] {

            // 画面上のフォームをサブミットする。
            "function $fwPrefix$submitOnWindow(submitName, form, submissionData) {",
                 // サブミット情報からサブミット先のactionを取得してサブミットする。
            "    form.action = submissionData.action;",
            "    form.submit();",
            "}"

    }, Builder.LS);
    
    /** submitToNewForm関数 */
    private static final String SUBMIT_TO_NEW_FORM_FUNCTION = Builder.join(new String[] {

            // オープンした画面を保持するハッシュ(keyはウィンドウ名)を宣言する。
            "var nablarch_opened_windows = {};",

            // 新しい画面にサブミットする。
            "function $fwPrefix$submitToNewForm(submitName, form, submissionData) {",

            "    var target = submissionData.popupWindowName;",
            "    if (target == null) {",
                     // 現在時刻のミリ秒を使用して一意なターゲット名を作成する。
            "        target = \"$fwPrefix$_target_\" + (+new Date());",
            "    }",

                 // ブランクで新しい画面をオープンする。
            "    if (submissionData.submissionAction == \"$popupAction$\") {",
            "        var windowOption = submissionData.popupOption;",
            "        var openedWindow = window.open(\"about:blank\", target, windowOption != null ? windowOption : \"\");",
            "        nablarch_opened_windows[target] = openedWindow;",
            "    }",

                 // サブミット用のフォームを新規に作成する。
            "    var tempForm = document.createElement(\"form\");",

                 // 元画面のフォームの要素をサブミット用のフォームにコピーする。
            "    var changeParamNames = submissionData.changeParamNames;",
            "    for (var i = 0; i < form.elements.length; i++) {",
            "        var element = form.elements[i];",
            "        if (element.type.match(/^submit$|^button$/i)) {",
            "            continue;",
            "        }",
            "        var paramName = changeParamNames[element.name];",
            "        if (paramName != null) {",
                         // パラメータ名の変更情報に一致する要素は、パラメータ名を変更する。
            "            $fwPrefix$addHiddenTagFromElement(tempForm, paramName, element);",
            "        } else {",
                         // パラメータ名の変更情報に一致しない要素は、そのまま送信する。
            "            $fwPrefix$addHiddenTagFromElement(tempForm, element.name, element);",
            "        }",
            "    }",

                 // サブミット情報からサブミット先のactionを取得してサブミットする。
            "    if (submissionData.submissionAction == \"$popupAction$\") {",
            "        tempForm.target = target;",
            "    }",
            "    tempForm.action = submissionData.action;",
            "    tempForm.method = \"post\";",

                 // 新規に作成したフォームを一時的に追加する。
            "    var body = document.getElementsByTagName(\"body\")[0];",
            "    body.appendChild(tempForm);",

                 // 新規に作成したフォームをサブミットする。
            "    tempForm.submit();",

                 // 新規に作成したフォームを削除する。
            "    body.removeChild(tempForm);",
            "}"

    }, Builder.LS);
    
    /** findForm関数 */
    private static final String FIND_FORM_FUNCTION = Builder.join(new String[] {

            // エレメントに対するフォームを検索する。
            "function $fwPrefix$findForm(element, isAnchor) {",
            "    if (isAnchor) {",
                     // aタグは親階層を辿って検索する。
            "        var parent = element.parentNode;",
            "        while (parent != null && !parent.tagName.match(/^form$|^body$/i)) {",
            "            parent = parent.parentNode;",
            "        }",
            "        if (parent == null || !parent.tagName.match(/form/i)) {",
            "            return null;",
            "        }",
            "        return parent;",
            "    } else {",
            "        return element.form;",
            "    }",
            "}"

    }, Builder.LS);
    
    /** invokeOnSubmit関数 */
    private static final String INVOKE_ON_SUBMIT_FUNCTION = Builder.join(new String[] {

            // formタグのonsubmitを呼び出す。
            "function $fwPrefix$invokeOnsubmit(form, event) {",
            "    var onSubmitFunc = form.onsubmit;",
            "    var ret = onSubmitFunc.call(form, event);",
                 // 明示的にfalseが返ってきた場合のみfalseを返す。
            "    return !( (ret != undefined && ret != null) && ret == false );",
            "}"

    }, Builder.LS);
    
    /** addHiddenTag関数 */
    private static final String ADD_HIDDEN_TAG_FUNCTION = Builder.join(new String[] {

            // エレメントからhiddenタグを追加する。
            "function $fwPrefix$addHiddenTagFromElement(form, name, element) {",
                 // skip if the element is disabled.
            "    if (element.disabled) {",
            "        return;",
            "    }",
                 // select
            "    if (element.tagName.match(/select/i)) {",
            "        for (var i = 0; i < element.options.length; i++) {",
            "            var option = element.options[i];",
            "            if (option.selected) {",
            "                $fwPrefix$addHiddenTag(form, name, option.value);",
            "            }",
            "        }",

                 // checkboxとradio
            "    } else if (element.type.match(/^checkbox$|^radio$/i)) {",
            "        if (element.checked) {",
            "            $fwPrefix$addHiddenTag(form, name, element.value);",
            "        }",

                 // 上記以外
            "    } else {",
            "        $fwPrefix$addHiddenTag(form, name, element.value);",
            "    }",
            "}",

            // フォームにhiddenタグを追加する。
            "function $fwPrefix$addHiddenTag(form, name, value) {",
            "    var input = document.createElement(\"input\");",
            "    input.type = \"hidden\";",
            "    input.name = name;",
            "    input.value = value;",
            "    form.appendChild(input);",
            "}"

    }, Builder.LS);
    
    /** stopSubmission関数 */
    private static final String STOP_SUBMISSION_FUNCTION = Builder.join(new String[] {

            // リクエストの二重送信防止時に、2回目以降のサブミット時に呼ばれる関数
            "function $fwPrefix$stopSubmission() {",
            "    if ((typeof $fwPrefix$handleDoubleSubmission) == \"function\") {",
            "         $fwPrefix$handleDoubleSubmission(this);",
            "    }",
            "    return false;",
            "}"

    }, Builder.LS);
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "form";
    }
    
    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(FormTag.class);

}
