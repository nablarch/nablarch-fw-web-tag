package nablarch.common.web.tag;

import java.io.Serializable;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * 単一値の入力項目の実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public class SinglevaluedInputTagWriterSupport implements Serializable {

    /** タグ名 */
    private String tagName;
    
    /** エラーレベルのメッセージに使用するCSSクラス名 */
    private String errorCss;
    
    /** name属性のエイリアス */
    private Set<String> nameAlias;
    
    /**
     * デフォルトコンストラクタ。
     * @param tagName タグ名
     */
    public SinglevaluedInputTagWriterSupport(String tagName) {
        this.tagName = tagName;
    }
    
    /**
     * タグ名を取得する。
     * @return タグ名
     */
    protected String getTagName() {
        return tagName;
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
     * 単一値の入力項目を出力する。
     * <pre>
     * 入力画面の場合は、{@link #createInputTag(javax.servlet.jsp.PageContext, HtmlAttributes, Object)}に処理を移譲する。
     * 確認画面の場合は、{@link #createOutputTag(javax.servlet.jsp.PageContext, HtmlAttributes, Object)}に処理を移譲する。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @throws JspException JSP例外
     */
    public void writeTag(PageContext pageContext, HtmlAttributes attributes) throws JspException {
        
        String name = attributes.get(HtmlAttribute.NAME);
        Object object = TagUtil.getSingleValue(pageContext, name);
        Object value = object != null ? object : "";
        
        String tag;
        if (TagUtil.isConfirmationPage(pageContext)) {
            tag = createOutputTag(pageContext, attributes, value);
        } else {
            TagUtil.editClassAttributeForError(pageContext, attributes, errorCss, nameAlias);
            tag = createInputTag(pageContext, attributes, value);
        }
        TagUtil.print(pageContext, tag);
    }

    /**
     * 出力タグを作成する。
     * <pre>
     * name属性に対応する入力データが存在する場合はHTMLエスケープして出力する。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param value 入力値
     * @return 出力タグ
     */
    protected String createOutputTag(PageContext pageContext, HtmlAttributes attributes, Object value) {
        return TagUtil.escapeHtml(value, true);
    }

    /**
     * 入力タグを作成する。
     * <pre>
     * 指定された属性を使用してボディを持たないタグを出力する。
     * value属性に指定されたvalueを設定する。
     * 属性はHTMLエスケープして出力する。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param value 入力値
     * @return 入力タグ
     */
    protected String createInputTag(PageContext pageContext, HtmlAttributes attributes, Object value) {
        attributes.put(HtmlAttribute.VALUE, value);
        return TagUtil.createTagWithoutBody(tagName, attributes);
    }
}
