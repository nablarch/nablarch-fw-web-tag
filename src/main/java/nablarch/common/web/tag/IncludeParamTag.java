package nablarch.common.web.tag;

import javax.servlet.jsp.JspException;

/**
 * インクルード時に追加するパラメータを指定するクラス。
 * @author Kiyohito Itoh
 */
public class IncludeParamTag extends ParamTagSupport {

    /**
     * {@inheritDoc}
     * <pre>
     * このタグが正しい場所に配置されているかをチェックする。
     * </pre>
     */
    @Override
    public int doStartTag() throws JspException {
        IncludeContext includeContext = IncludeContext.getIncludeContext(pageContext);
        if (includeContext == null) {
            throw new IllegalStateException(
                "invalid location of the includeParam tag. "
              + "the includeParam tag must locate in the include tag.");
        }
        return super.doStartTag();
    }

    /**
     * {@inheritDoc}
     * <pre>
     * インクルードコンテキストに含まれるパラメータを追加する。
     * </pre>
     */
    @Override
    protected void addParam(String name, String value) {
        IncludeContext.getIncludeContext(pageContext).addParam(name, value);
    }
}
