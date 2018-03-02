package nablarch.common.web.exclusivecontrol;

import static nablarch.fw.ExecutionContext.FW_PREFIX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.exclusivecontrol.ExclusiveControlContext;
import nablarch.common.exclusivecontrol.ExclusiveControlUtil;
import nablarch.common.exclusivecontrol.OptimisticLockException;
import nablarch.common.exclusivecontrol.Version;
import nablarch.common.util.WebRequestUtil;
import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

/**
 * 画面処理における排他制御機能(楽観的ロック)のユーティリティクラス。
 * <p/>
 * 楽観的ロックは、下記の機能により実現する。
 * <ul>
 * <li>処理対象データに対するバージョン番号を取得する。</li>
 * <li>取得済みのバージョン番号が更新されていないかチェックする。</li>
 * <li>取得済みのバージョン番号が更新されていないかチェックし、バージョン番号を更新する。</li>
 * </ul>
 * 取得したバージョン番号は、フレームワークにより、ウィンドウスコープを使用して画面間を持回る。
 * このため、本クラスは、n:formタグとhiddenタグの暗号化機能の使用を前提とする。
 *
 * 本クラスは、画面処理に依存しない楽観的ロック機能の処理を{@link ExclusiveControlUtil}に委譲する。
 * 
 * {@link nablarch.common.dao.UniversalDao UniversalDao}を使用する場合には、
 * このクラスではなく{@link nablarch.common.dao.UniversalDao UniversalDao}を使用して排他制御を行うこと。
 * 
 * @author Kiyohito Itoh
 */
@Published
public final class HttpExclusiveControlUtil {

    /** 隠蔽コンストラクタ */
    private HttpExclusiveControlUtil() {
    }

    /** バージョン番号をhiddenタグに出力する際に使用するパラメータ名 */
    public static final String VERSION_PARAM_NAME = FW_PREFIX + "version";
    
    /** バージョン番号をリクエストスコープに設定する際に使用する変数名 */
    public static final String VERSIONS_VARIABLE_NAME = FW_PREFIX + "versions";
    
    /**
     * バージョン番号を準備する。
     * <p/>
     * 指定された{@link ExclusiveControlContext}リストを使用してバージョン番号を取得し、
     * 次回リクエスト時にバージョン番号を送信するために、バージョン番号をリクエストスコープに設定する。
     * リクエストスコープに設定したバージョン番号は、n:formタグによりウィンドウスコープに設定される。
     * <p/>
     * 1つでもバージョン番号を準備できなかった場合は処理を中断しfalseを返す。
     * 
     * @param context 実行コンテキスト
     * @param exclusiveControlContexts 排他制御コンテキストリスト
     * @return すべてのバージョン番号を準備できた場合はtrue
     */
    public static boolean prepareVersions(ExecutionContext context, List<? extends ExclusiveControlContext> exclusiveControlContexts) {
        for (ExclusiveControlContext exclusiveControlContext : exclusiveControlContexts) {
            if (!prepareVersion(context, exclusiveControlContext)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * バージョン番号を準備する。
     * <p/>
     * 指定された{@link ExclusiveControlContext}を使用してバージョン番号を取得し、
     * 次回リクエスト時にバージョン番号を送信するために、バージョン番号をリクエストスコープに設定する。
     * リクエストスコープに設定したバージョン番号は、n:formタグによりウィンドウスコープに設定される。
     * 
     * @param context 実行コンテキスト
     * @param exclusiveControlContext 排他制御コンテキスト
     * @return バージョン番号を準備できた場合はtrue
     */
    public static boolean prepareVersion(ExecutionContext context, ExclusiveControlContext exclusiveControlContext) {
        Version version = ExclusiveControlUtil.getVersion(exclusiveControlContext);
        if (version == null) {
            context.getRequestScopeMap().remove(VERSIONS_VARIABLE_NAME);
            return false;
        }
        getVersions(context).add(convertToVersionString(version));
        return true;
    }
    
    /**
     * リクエストスコープからバージョン番号リストを取得する。
     * @param context 実行コンテキスト
     * @return バージョン番号リスト
     */
    @SuppressWarnings("unchecked")
    private static List<String> getVersions(ExecutionContext context) {
        Map<String, Object> requestScopeMap = context.getRequestScopeMap();
        if (!requestScopeMap.containsKey(VERSIONS_VARIABLE_NAME)) {
            requestScopeMap.put(VERSIONS_VARIABLE_NAME, new ArrayList<String>());
        }
        return (List<String>) requestScopeMap.get(VERSIONS_VARIABLE_NAME);
    }
    
    /**
     * バージョン番号オブジェクトを復元可能な文字列に変換する。
     * @param version バージョン番号オブジェクト
     * @return 復元可能な文字列
     */
    private static String convertToVersionString(Version version) {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("tableName", Arrays.asList(version.getTableName()));
        params.put("versionColumnName", Arrays.asList(version.getVersionColumnName()));
        params.put("version", Arrays.asList(version.getVersion()));
        params.put("primaryKeys", Arrays.asList(convertToConditionString(version.getPrimaryKeyCondition())));
        return WebRequestUtil.convertToParamsString(params);
    }
    
    /**
     * バージョン番号の主キー条件を復元可能な文字列に変換する。
     * @param condition バージョン番号の主キー条件
     * @return 復元可能な文字列
     */
    private static String convertToConditionString(Map<String, Object> condition) {
        Map<String, List<String>> conditionParams = new HashMap<String, List<String>>();
        for (Map.Entry<String, Object> entry : condition.entrySet()) {
            conditionParams.put(entry.getKey(), Arrays.asList(entry.getValue().toString()));
        }
        return WebRequestUtil.convertToParamsString(conditionParams);
    }
    
    /**
     * バージョン番号をチェックする。
     * <p/>
     * リクエストパラメータに含まれるバージョン番号を使用して、
     * バージョン番号が更新されていないかをチェックする。
     * どれか一つでもバージョン番号が更新されていた場合は、
     * 更新されているバージョン番号を設定した{@link OptimisticLockException}を送出する。
     * <p/>
     * 全てのバージョン番号が更新されていない場合は、
     * 次回リクエスト時にバージョン番号を送信するために、バージョン番号をリクエストスコープに設定する。
     * リクエストスコープに設定したバージョン番号は、n:formタグによりウィンドウスコープに設定される。
     * 
     * @param request リクエスト
     * @param context 実行コンテキスト
     * @throws OptimisticLockException バージョン番号が更新されていた場合
     */
    public static void checkVersions(HttpRequest request, ExecutionContext context) throws OptimisticLockException {
        executeCheckVersions(request, context, null);
    }
    
    /**
     * 指定されたウィンドウスコープ変数上の配列に格納された各PK値に対して
     * バージョン番号をチェックする。
     * <p/>
     * リクエストパラメータに含まれるバージョン番号を使用して、
     * バージョン番号が更新されていないかをチェックする。
     * どれか一つでもバージョン番号が更新されていた場合は、
     * 更新されているバージョン番号を設定した{@link OptimisticLockException}を送出する。
     * <p/>
     * 全てのバージョン番号が更新されていない場合は、
     * 次回リクエスト時にバージョン番号を送信するために、バージョン番号をリクエストスコープに設定する。
     * リクエストスコープに設定したバージョン番号は、n:formタグによりウィンドウスコープに設定される。
     * <p/>
     * なお、PKが組み合わせキーとなる場合は{@link HttpExclusiveControlUtil#checkVersion(HttpRequest, ExecutionContext, ExclusiveControlContext)}
     * を使用すること。
     * 
     * @param request リクエスト
     * @param context 実行コンテキスト
     * @param targetPkListParamName 更新対象のPK値の配列を格納したウィンドウスコープ変数名
     * @throws OptimisticLockException バージョン番号が更新されていた場合
     */
    public static void checkVersions(HttpRequest request, ExecutionContext context, String targetPkListParamName)
    throws OptimisticLockException {
        if (targetPkListParamName == null) {
            throw new IllegalArgumentException(
                "the request parameter name of update target PK-list was null."
            );
        }
        executeCheckVersions(request, context, targetPkListParamName);
    }

    /**
     * 引数で渡された排他制御コンテキストに格納されたPK値に対してバージョン番号をチェックする。
     * <p/>
     * 排他制御コンテキストに格納されたバージョン番号を使用して、バージョン番号が更新されていないかをチェックする。
     * どれか一つでもバージョン番号が更新されていた場合は、更新されているバージョン番号を設定した{@link OptimisticLockException}を送出する。
     * <p/>
     * 全てのバージョン番号が更新されていない場合は、
     * 次回リクエスト時にバージョン番号を送信するために、バージョン番号をリクエストスコープに設定する。
     * リクエストスコープに設定したバージョン番号は、n:formタグによりウィンドウスコープに設定される。
     * 
     * @param request リクエスト
     * @param context 実行コンテキスト
     * @param exclusiveControlContext 排他制御コンテキスト
     * @throws OptimisticLockException バージョン番号が更新されていた場合
     */
    public static void checkVersion(HttpRequest request, ExecutionContext context, ExclusiveControlContext exclusiveControlContext)
    throws OptimisticLockException {
        if (exclusiveControlContext == null) {
            throw new IllegalArgumentException("exclusiveControlContext was null.");
        }
        List<Version> version = getVersion(request, exclusiveControlContext);
        ExclusiveControlUtil.checkVersions(version);
        getVersions(context).add(convertToVersionString(version.get(0)));
    }
    
    /**
     * 論理排他チェックを行う。
     * @param request リクエスト
     * @param context 実行コンテキスト
     * @param targetPkListParamName 更新対象のPK値の配列を格納したウィンドウスコープ変数名
     * @throws OptimisticLockException バージョン番号が更新されていた場合
     */
    private static void executeCheckVersions(HttpRequest request, ExecutionContext context, String targetPkListParamName)
    throws OptimisticLockException {
        List<Version> versions = (targetPkListParamName == null)
                               ? getVersions(request)
                               : getVersions(request, targetPkListParamName);
        ExclusiveControlUtil.checkVersions(versions);
        for (Version version : versions) {
            getVersions(context).add(convertToVersionString(version));
        }
    }
    
    /**
     * リクエストパラメータに含まれるバージョン番号を取得する。
     * @param request リクエスト
     * @return バージョン番号
     */
    private static List<Version> getVersions(HttpRequest request) {
        List<Version> versions = new ArrayList<Version>();
        if (!request.getParamMap().containsKey(VERSION_PARAM_NAME)) {
            throw new IllegalArgumentException("version parameter was not found.");
        }
        for (String versionString : request.getParam(VERSION_PARAM_NAME)) {
            versions.add(convertToVersion(versionString));
        }
        return versions;
    }
    
    /**
     * バージョン番号を取得する。
     * @param request HTTPリクエストオブジェクト              
     * @param targetPkListParamName 更新対象PK値のリストを格納したウィンドウスコープ上の変数名
     * @return バージョン番号
     */
    private static List<Version> getVersions(HttpRequest request, String targetPkListParamName) {
        if (!request.getParamMap().containsKey(VERSION_PARAM_NAME)) {
            throw new IllegalArgumentException("version parameter was not found.");
        }
        
        String[] targetPkArray = request.getParam(targetPkListParamName);
        List<String> targetPkList = (targetPkArray == null)
                                  ? Arrays.asList(new String[0]) 
                                  : Arrays.asList(targetPkArray);
        
        List<Version> versions = new ArrayList<Version>();
        for (String versionString : request.getParam(VERSION_PARAM_NAME)) {
            Version version = convertToVersion(versionString);
            String pk = version.getPrimaryKeyCondition()
                       .values()
                       .iterator()
                       .next()
                       .toString();
            if (targetPkList.contains(pk)) {
                versions.add(version);
            }
        }
        return versions;
    }
    
    /**
     * 指定された排他制御コンテキストに対応するバージョン番号をリクエストパラメータから取得する。
     * @param request HTTPリクエストオブジェクト
     * @param context 排他制御コンテキスト
     * @return バージョン番号
     */
    private static List<Version> getVersion(HttpRequest request, ExclusiveControlContext context) {
        if (!request.getParamMap().containsKey(VERSION_PARAM_NAME)) {
            throw new IllegalArgumentException("version parameter was not found.");
        }
        Enum<?>[] primaryKeyColumnNames = context.getPrimaryKeyColumnNames();
        Map<String, Object> contextCondition = context.getCondition();
        for (String versionString : request.getParam(VERSION_PARAM_NAME)) {
            Version version = convertToVersion(versionString);
            if (!isSameTable(context, version)) {
                continue;
            }
            Map<String, Object> paramCondition = version.getPrimaryKeyCondition();
            if (isSameCondition(contextCondition, paramCondition, primaryKeyColumnNames)) {
                List<Version> list = new ArrayList<Version>();
                list.add(version);
                return list;
            }
        }
        throw new IllegalArgumentException(
            String.format("version was not found. tableName = [%s], condition = [%s], version parameter = %s",
                    context.getTableName(), context.getCondition(), Arrays.asList(request.getParam(VERSION_PARAM_NAME))));
    }

    /**
     * 排他制御コンテキストのテーブルとリクエストパラメータから取得したテーブルが一致するか否かを判定する。
     * @param context 排他制御コンテキスト
     * @param version リクエストパラメータから取得したバージョン情報
     * @return 排他制御コンテキストとリクエストパラメータのテーブルが一致する場合は {@code true}
     */
    private static boolean isSameTable(ExclusiveControlContext context, Version version) {
        return version.getTableName().equals(context.getTableName());
    }

    /**
     * 排他制御コンテキストの主キー条件とリクエストパラメータから取得した主キー条件が一致するか否かを判定する。
     * <p/>
     * どちらか一方の主キー値が取得できない場合は、falseを返す。
     * 
     * @param contextCondition 排他制御コンテキストの主キー条件
     * @param paramCondition リクエストパラメータから取得した主キー条件
     * @param primaryKeyColumnNames 主キーのカラム名の配列
     * @return 主キー条件が全てマッチすればtrue、一つでも異なればfalse
     */
    private static boolean isSameCondition(Map<String, Object> contextCondition, Map<String, Object> paramCondition, Enum<?>[] primaryKeyColumnNames) {
        for (Enum<?> primaryKeyColumnName : primaryKeyColumnNames) {

            String columnName = primaryKeyColumnName.toString();

            Object contextValue = getPrimaryKeyValue(contextCondition, columnName);
            if (contextValue == null) {
                return false;
            }

            Object paramValue = getPrimaryKeyValue(paramCondition, columnName);
            if (paramValue == null) {
                return false;
            }

            if (!contextValue.equals(paramValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 主キー値を取得する。
     * @param condition 主キー条件
     * @param primaryKeyColumnName 取得したい主キーのカラム名
     * @return 主キー値
     */
    private static Object getPrimaryKeyValue(Map<String, Object> condition, String primaryKeyColumnName) {
        Object value = condition.get(primaryKeyColumnName.toUpperCase());
        return value != null ? value : condition.get(primaryKeyColumnName.toLowerCase());
    }

    /**
     * バージョン番号文字列からバージョン番号オブジェクトに変換する。
     * @param versionString バージョン番号文字列
     * @return バージョン番号オブジェクト
     */
    private static Version convertToVersion(String versionString) {
        Map<String, List<String>> params = WebRequestUtil.convertToParamsMap(versionString);
        return new Version(
                params.get("tableName").get(0),
                params.get("versionColumnName").get(0),
                params.get("version").get(0),
                convertToCondition(params.get("primaryKeys").get(0)));
    }
    
    /**
     * 主キー条件文字列からマップオブジェクトに変換する。
     * @param conditionString 主キー条件文字列
     * @return マップオブジェクト
     */
    private static Map<String, Object> convertToCondition(String conditionString) {
        Map<String, List<String>> conditionParams = WebRequestUtil.convertToParamsMap(conditionString);
        Map<String, Object> condition = new HashMap<String, Object>();
        for (Map.Entry<String, List<String>> conditionParam : conditionParams.entrySet()) {
            condition.put(conditionParam.getKey(), conditionParam.getValue().get(0));
        }
        return condition;
    }
    
    /**
     * バージョン番号の更新チェックとバージョン番号の更新を行う。
     * <p/>
     * リクエストパラメータに含まれるバージョン番号を使用して、
     * バージョン番号が更新されていないかのチェックと更新を行う。
     * どれか一つでもバージョン番号が更新されていた場合は、
     * 更新されているバージョン番号を設定した{@link OptimisticLockException}を送出する。
     * 
     * @param request リクエスト
     * @throws OptimisticLockException バージョン番号が更新されていた場合
     */
    public static void updateVersionsWithCheck(HttpRequest request)
    throws OptimisticLockException {
        executeUpdateVersionsWithCheck(request, null);
    }
    
    /**
     * 指定されたウィンドウスコープ変数上の配列に格納された各PK値に対して
     * バージョン番号の更新チェックとバージョン番号の更新を行う。
     * <p/>
     * リクエストパラメータに含まれるバージョン番号を使用して、
     * バージョン番号が更新されていないかのチェックと更新を行う。
     * どれか一つでもバージョン番号が更新されていた場合は、
     * 更新されているバージョン番号を設定した{@link OptimisticLockException}を送出する。
     * <p/>
     * なお、PKが組み合わせキーとなる場合は、{@link HttpExclusiveControlUtil#updateVersionWithCheck(HttpRequest, ExclusiveControlContext)}
     * を使用すること。
     * 
     * @param request リクエスト
     * @param targetPkListParamName 更新対象のPK値の配列を格納したウィンドウスコープ変数名
     * @throws OptimisticLockException バージョン番号が更新されていた場合
     */
    public static void updateVersionsWithCheck(HttpRequest request, String targetPkListParamName) 
    throws OptimisticLockException {
        if (targetPkListParamName == null) {
            throw new IllegalArgumentException(
                "the request parameter name of update target PK-list was null."
            );
        }
        executeUpdateVersionsWithCheck(request, targetPkListParamName);
    }
    
    /**
     * 引数で渡された排他制御コンテキストに格納されたPK値に対してバージョン番号のチェックとバージョン情報の更新を行う。
     * <p/>
     * 排他制御コンテキストに格納されたバージョン番号を使用して、バージョン番号が更新されていないかのチェックと更新を行う。
     * どれか一つでもバージョン番号が更新されていた場合は、更新されているバージョン番号を設定した{@link OptimisticLockException}を送出する。
     * <p/>
     * 
     * @param request リクエスト
     * @param exclusiveControlContext 排他制御コンテキスト
     * @throws OptimisticLockException バージョン番号が更新されていた場合
     */
    public static void updateVersionWithCheck(HttpRequest request, ExclusiveControlContext exclusiveControlContext)
    throws OptimisticLockException {
        if (exclusiveControlContext == null) {
            throw new IllegalArgumentException("exclusiveControlContext was null.");
        }
        ExclusiveControlUtil.updateVersionsWithCheck(getVersion(request, exclusiveControlContext));
    }
    
    /**
     * 論理排他チェック後、バージョン番号を更新する。
     * 
     * @param request リクエスト
     * @param targetPkListParamName 更新対象のPK値の配列を格納したウィンドウスコープ変数名
     * @throws OptimisticLockException バージョン番号が更新されていた場合
     */
    private static void executeUpdateVersionsWithCheck(HttpRequest request, String targetPkListParamName)
    throws OptimisticLockException {
        List<Version> versions = (targetPkListParamName == null)
                               ? getVersions(request)
                               : getVersions(request, targetPkListParamName);
        ExclusiveControlUtil.updateVersionsWithCheck(versions);
    }
}
