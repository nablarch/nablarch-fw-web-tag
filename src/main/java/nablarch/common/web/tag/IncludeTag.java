package nablarch.common.web.tag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import nablarch.core.util.StringUtil;

/**
 * インクルード先のパスを言語対応のパスに変換してからインクルードを行うクラス。
 * @author Kiyohito Itoh
 */
public class IncludeTag extends CustomTagSupport {

    /** インクルード先のパス */
    private String path;

    /**
     * インクルード先のパスを設定する。
     * @param path インクルード先のパス
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * インクルード時に含めるパラメータを保持するインクルードコンテキストを設定する。
     */
    @Override
    public int doStartTag() throws JspException {
        IncludeContext.setIncludeContext(pageContext, new IncludeContext());
        return EVAL_BODY_INCLUDE;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * {@link TagUtil#getResourcePathForLanguage(javax.servlet.jsp.PageContext, String)}
     * メソッドを呼び出し、指定されたパスを言語対応のリソースパスに変換してからインクルードを行う。
     * <p/>
     * インクルードコンテキストに設定されたパラメータからクエリー文字列を作成しパスに追加する。
     */
    @Override
    public int doEndTag() throws JspException {
        String pathForLanguage = TagUtil.getResourcePathForLanguage(pageContext, path);
        String queryString = createQueryString(IncludeContext.getIncludeContext(pageContext).getParams());
        IncludeContext.setIncludeContext(pageContext, null);
        if (StringUtil.hasValue(queryString)) {
            pathForLanguage += "?" + queryString;
        }
        try {
            pageContext.include(pathForLanguage);
        } catch (ServletException e) {
            throw new JspException(e);
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    /**
     * クエリー文字列を作成する。
     * @param params パラメータ
     * @return クエリー文字列
     */
    protected String createQueryString(Map<String, List<String>> params) {
        StringBuilder qs = new StringBuilder();
        for (Map.Entry<String, List<String>> param : params.entrySet()) {
            String name = param.getKey();
            for (String value : param.getValue()) {
                if (qs.length() != 0) {
                    qs.append("&");
                }
                try {
                    qs.append(name).append("=").append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // unreachable
                    throw new RuntimeException(e);
                }
            }
        }
        return qs.toString();
    }
}
