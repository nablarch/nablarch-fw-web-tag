package nablarch.common.web.tag;

import java.util.Collection;
import java.util.Set;
import javax.servlet.jsp.JspException;

import nablarch.core.util.StringUtil;

/**
 * checked属性を持つinputタグを出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class CheckedInputTagSupport extends InputTagSupport {

    /** ラベル */
    private String label;
    
    /** エラーレベルのメッセージに使用するCSSクラス名 */
    private String errorCss;
    
    /** name属性のエイリアス */
    private Set<String> nameAlias;
    
    /**
     * XHTMLのvalue属性を設定する。
     * @param value XHTMLのvalue属性
     */
    public void setValue(String value) {
        getAttributes().put(HtmlAttribute.VALUE, value);
    }
    
    /**
     * XHTMLのonchange属性を設定する。
     * @param onchange XHTMLのonchange属性
     */
    public void setOnchange(String onchange) {
        getAttributes().put(HtmlAttribute.ONCHANGE, onchange);
    }
    
    /**
     * ラベルを設定する。
     * @param label ラベル
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * エラーレベルのメッセージに使用するCSSクラス名を設定する。<br>
     * デフォルトは"nablarch_error"。
     * @param errorCss エラーレベルのメッセージに使用するCSSクラス名
     */
    public void setErrorCss(String errorCss) {
        this.errorCss = errorCss;
    }
    
    /**
     * name属性のエイリアスを設定する。<br>
     * 複数指定する場合はカンマ区切り。
     * @param nameAlias name属性のエイリアス
     */
    public void setNameAlias(String nameAlias) {
        this.nameAlias = TagUtil.getCommaSeparatedValue(nameAlias);
    }
    
    /**
     * type属性を取得する。
     * @return type属性
     */
    protected abstract String getType();
    
    /**
     * {@inheritDoc}
     * <pre>
     * 入力画面と確認画面で出力内容が異なる。
     * 
     * 入力画面：
     * inputタグとラベルを連結して出力する。
     * type属性にサブクラスが返す値を設定する。
     * チェックありの場合はchecked属性を設定する。
     * name属性に対応するエラーメッセージが存在する場合はclass属性に指定されたCSSクラス名を追記で設定する。
     * 属性とラベルはHTMLエスケープして出力する。
     * {@link nablarch.common.web.tag.FormContext}にname属性を設定する。
     *
     * 確認画面：
     * チェックありの場合はラベルを出力する。
     * ラベルはHTMLエスケープして出力する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        
        checkChildElementsOfForm();
        boolean checked = isChecked();
        
        if (TagUtil.isConfirmationPage(pageContext)) {
            createOutputTag(checked);
        } else {
            createInputTag(checked);
        }
        
        TagUtil.setNameToFormContext(pageContext, getAttributes());
        
        return SKIP_BODY;
    }

    /**
     * 項目がチェック状態を取得する。
     * @return 項目がチェックされている場合 true
     */
    protected boolean isChecked() {
        String name = getAttributes().get(HtmlAttribute.NAME);
        Collection<?> values = TagUtil.getMultipleValues(pageContext, name);
        String value = getValue();
        boolean checked = TagUtil.contains(values, value);
        return checked;
    }
    
    /**
     * value属性の値を取得する。
     * @return value属性の値
     */
    protected String getValue() {
        return getAttributes().get(HtmlAttribute.VALUE);
    }
    
    /**
     * ラベルを取得する。
     * @return ラベル
     */
    protected String getLabel() {
        return label;
    }
    
    /**
     * 出力タグを作成する。
     * <pre>
     * チェックありの場合はラベルを出力する。
     * ラベルはHTMLエスケープして出力する。
     * </pre>
     * @param checked チェックありか否か。チェックありの場合はtrue
     * @throws JspException JSP例外
     */
    protected void createOutputTag(boolean checked) throws JspException {
        if (checked) {
            TagUtil.print(pageContext, TagUtil.escapeHtml(getLabel(), true));
        }
    }
    
    /**
     * 入力タグを作成する。
     * <pre>
     * inputタグとラベルを連結して出力する。
     * type属性にサブクラスが返す値を設定する。
     * チェックありの場合はchecked属性を設定する。
     * name属性に対応するエラーメッセージが存在する場合はclass属性に指定されたCSSクラス名を追記で設定する。
     * 属性とラベルはHTMLエスケープして出力する。
     * </pre>
     * @param checked チェックありか否か。チェックありの場合はtrue
     * @throws JspException JSP例外
     */
    protected void createInputTag(boolean checked) throws JspException {
        String id = getAttributes().get(HtmlAttribute.ID);
        if (StringUtil.isNullOrEmpty(id)) {
            id = TagUtil.generateUniqueName(pageContext, getType());
            getAttributes().put(HtmlAttribute.ID, id);
        }
        
        String errorCssClass = TagUtil.editClassAttributeForError(pageContext, getAttributes(), errorCss, nameAlias);
        
        String label = getLabel();
        String labelTag = StringUtil.hasValue(label)
                            ? TagUtil.createLabelTag(getType(), TagUtil.escapeHtml(label, true), id, errorCssClass) : "";
        
        getAttributes().put(HtmlAttribute.TYPE, getType());
        getAttributes().put(HtmlAttribute.CHECKED, checked);
        TagUtil.print(pageContext,
                      TagUtil.createTagWithoutBody("input", getAttributes()) + labelTag);
    }
}
