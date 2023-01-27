package nablarch.common.web.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * {@link nablarch.common.web.session.store.HiddenStore}に設定された内容を保持する
 * inputタグ(type="hidden")を出力するクラス。
 *
 * @author Naoki Yamamoto
 */
public class HiddenStoreTag extends InputTagSupport {

    /**
     * {@inheritDoc}
     * <pre>
     * {@link nablarch.common.web.session.store.HiddenStore}に設定された内容をスコープから取得し、出力を行う。
     * </pre>
     */
    public int doStartTag() throws JspException {

        checkChildElementsOfForm();

        final HtmlAttributes htmlAttributes = getAttributes();

        String name = htmlAttributes.get(HtmlAttribute.NAME);
        Object value = pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
        if (value != null) {
            htmlAttributes.put(HtmlAttribute.TYPE, "hidden");
            htmlAttributes.put(HtmlAttribute.VALUE, value.toString());

            TagUtil.print(pageContext, TagUtil.createTagWithoutBody("input", htmlAttributes));
            TagUtil.getFormContext(pageContext).addInputName(name);
        }
        return SKIP_BODY;
    }

    @Override
    protected String getTagName() {
        return "hiddenStore";
    }
}
