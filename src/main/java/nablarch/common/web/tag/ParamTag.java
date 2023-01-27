package nablarch.common.web.tag;

import jakarta.servlet.jsp.JspException;

/**
 * サブミット時に追加するパラメータを指定するクラス。
 * @author Kiyohito Itoh
 */
public class ParamTag extends ParamTagSupport {

    /**
     * {@inheritDoc}
     * <pre>
     * このタグが正しい場所に配置されているかをチェックする。
     * </pre>
     */
    @Override
    public int doStartTag() throws JspException {
        FormContext formContext = TagUtil.getFormContext(pageContext);
        if (formContext == null || formContext.getCurrentSubmissionInfo() == null) {
            throw new IllegalStateException(
                "invalid location of the param tag. "
              + "the param tag must locate in the tag to submit(submit or submitLink or button).");
        }
        return super.doStartTag();
    }

    /**
     * {@inheritDoc}
     * <pre>
     * フォームコンテキストに含まれるサブミット情報にパラメータを追加する。
     * </pre>
     */
    @Override
    protected void addParam(String name, String value) {
        TagUtil.getFormContext(pageContext).getCurrentSubmissionInfo().addParam(name, value);
    }
}
