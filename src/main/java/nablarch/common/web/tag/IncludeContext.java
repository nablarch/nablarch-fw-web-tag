package nablarch.common.web.tag;

import static nablarch.fw.ExecutionContext.FW_PREFIX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.jsp.PageContext;

/**
 * インクルードのコンテキスト情報を保持するクラス。<br>
 * このコンテキスト情報はページコンテキストに格納する。
 * @author Kiyohito Itoh
 */
public class IncludeContext {

    /** フォームコンテキストをページコンテキストに格納する際に使用するキー */
    private static final String KEY_INCLUDE_CONTEXT = FW_PREFIX + "includeContext";

    /**
     * ページコンテキストにインクルードテキストを設定する。
     * @param pageContext ページコンテキスト
     * @param includeContext インクルードコンテキスト
     */
    public static void setIncludeContext(PageContext pageContext, IncludeContext includeContext) {
        pageContext.setAttribute(KEY_INCLUDE_CONTEXT, includeContext, PageContext.REQUEST_SCOPE);
    }

    /**
     * ページコンテキストからインクルードコンテキストを取得する。
     * @param pageContext ページコンテキスト
     * @return インクルードコンテキスト
     */
    public static IncludeContext getIncludeContext(PageContext pageContext) {
        return (IncludeContext) pageContext.getAttribute(KEY_INCLUDE_CONTEXT, PageContext.REQUEST_SCOPE);
    }

    /** インクルード時に含めるパラメータ */
    private Map<String, List<String>> params = new HashMap<String, List<String>>();

    /**
     * インクルード時に含めるパラメータを追加する。
     * @param name パラメータ名
     * @param value 値
     */
    public void addParam(String name, String value) {
        if (!params.containsKey(name)) {
            params.put(name, new ArrayList<String>());
        }
        params.get(name).add(value);
    }

    /**
     * インクルード時に含めるパラメータを取得する。
     * @return インクルード時に含めるパラメータ
     */
    public Map<String, List<String>> getParams() {
        return params;
    }
}
