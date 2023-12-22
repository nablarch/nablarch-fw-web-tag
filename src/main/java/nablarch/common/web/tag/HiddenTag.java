package nablarch.common.web.tag;

import nablarch.core.util.StringUtil;

import java.util.Collection;

import jakarta.servlet.jsp.JspException;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="hidden")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class HiddenTag extends InputTagSupport {
    
    /**
     * {@inheritDoc}
     * <pre>
     * 入力画面と確認画面で出力内容が異なる。
     * 
     * 入力画面：
     * {@link nablarch.common.web.tag.FormContext}にname属性を設定する。
     * フォームコンテキストにhiddenタグの情報を追加する。
     * このhiddenタグの出力は{@link FormTag}が行う。
     * 
     * 確認画面：
     * 何も処理しない。
     * </pre>
     */
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();
        
        if (!TagUtil.isConfirmationPage(pageContext)) {
            
            getAttributes().put(HtmlAttribute.TYPE, "hidden");
            String name = getAttributes().get(HtmlAttribute.NAME);
            FormContext formContext = TagUtil.getFormContext(pageContext);
            
            Collection<?> values = TagUtil.getMultipleValues(pageContext, name);
            if (values.isEmpty()) {
                formContext.addHiddenTagInfo(createHiddenTagInfo(""));
            } else {
                for (Object value : values) {
                    formContext.addHiddenTagInfo(createHiddenTagInfo(value));
                }
            }
            TagUtil.setNameToFormContext(pageContext, getAttributes());
        }
        
        return SKIP_BODY;
    }
    
    /**
     * フォームコンテキストに設定するhiddenタグの情報を生成する。
     * @param value 値
     * @return hiddenタグの情報
     */
    private HtmlAttributes createHiddenTagInfo(Object value) {
        HtmlAttributes attributes = new HtmlAttributes();
        attributes.putAll(getAttributes());
        attributes.put(HtmlAttribute.VALUE, value != null ? StringUtil.toString(value) : "");
        return attributes;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "hidden";
    }
}
