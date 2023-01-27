package nablarch.common.web.tag;

import nablarch.core.util.StringUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * 入力データ復元とHTMLエスケープを行うtextareaタグを出力するクラス。
 * @author Kiyohito Itoh
 */
public class TextareaTag extends FocusAttributesTagSupport {
    
    /** 単一値の入力項目のサポートクラス */
    private SinglevaluedInputTagWriterSupport writer = new TextareaTagWriter("textarea");
    
    /**
     * XHTMLのname属性を設定する。
     * @param name XHTMLのname属性
     */
    public void setName(String name) {
        getAttributes().put(HtmlAttribute.NAME, name);
    }

    /**
     * XHTMLのrows属性を設定する。
     * @param rows XHTMLのrows属性
     */
    public void setRows(int rows) {
        getAttributes().put(HtmlAttribute.ROWS, rows);
    }

    /**
     * XHTMLのcols属性を設定する。
     * @param cols XHTMLのcols属性
     */
    public void setCols(int cols) {
        getAttributes().put(HtmlAttribute.COLS, cols);
    }
    
    /**
     * XHTMLのdisabled属性を設定する。
     * @param disabled XHTMLのdisabled属性
     */
    public void setDisabled(boolean disabled) {
        getAttributes().put(HtmlAttribute.DISABLED, disabled);
    }
    
    /**
     * XHTMLのreadonly属性を設定する。
     * @param readonly XHTMLのreadonly属性
     */
    public void setReadonly(boolean readonly) {
        getAttributes().put(HtmlAttribute.READONLY, readonly);
    }
    
    /**
     * XHTMLのonselect属性を設定する。
     * @param onselect XHTMLのonselect属性
     */
    public void setOnselect(String onselect) {
        getAttributes().put(HtmlAttribute.ONSELECT, onselect);
    }
    
    /**
     * XHTMLのonchange属性を設定する。
     * @param onchange XHTMLのonchange属性
     */
    public void setOnchange(String onchange) {
        getAttributes().put(HtmlAttribute.ONCHANGE, onchange);
    }

    /**
     * HTML5のautofocus属性を設定する。
     * @param autofocus HTML5のautofocus属性
     */
    public void setAutofocus(boolean autofocus) {
        getAttributes().put(HtmlAttribute.AUTOFOCUS, autofocus);
    }

    /**
     * HTML5のplaceholder属性を設定する。
     * @param placeholder HTML5のplaceholder属性
     */
    public void setPlaceholder(String placeholder) {
        getAttributes().put(HtmlAttribute.PLACEHOLDER, placeholder);
    }

    /**
     * HTML5のmaxlength属性を設定する。
     * @param maxlength HTML5のmaxlength属性
     */
    public void setMaxlength(int maxlength) {
        getAttributes().put(HtmlAttribute.MAXLENGTH, maxlength);
    }

    /**
     * エラーレベルのメッセージに使用するCSSクラス名を設定する。<br>
     * デフォルトは"nablarch_error"。
     * @param errorCss エラーレベルのメッセージに使用するCSSクラス名
     */
    public void setErrorCss(String errorCss) {
        writer.setErrorCss(errorCss);
    }
    
    /**
     * name属性のエイリアスを設定する。<br>
     * 複数指定する場合はカンマ区切り。
     * @param nameAlias name属性のエイリアス
     */
    public void setNameAlias(String nameAlias) {
        writer.setNameAlias(nameAlias);
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 入力画面と確認画面で出力内容が異なる。
     * 
     * 入力画面：
     * 指定された属性を使用してtextareaタグを出力する。
     * name属性に対応する入力データをボディに設定する。
     * 属性はHTMLエスケープして出力する。
     * {@link nablarch.common.web.tag.FormContext}にname属性を設定する。
     * 
     * 確認画面：
     * name属性に対応する入力データが存在する場合はHTMLエスケープして出力する。
     * 改行コードはbrタグに置き換える。
     * </pre>
     */
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();
        writer.writeTag(pageContext, getAttributes());
        TagUtil.setNameToFormContext(pageContext, getAttributes());
        return SKIP_BODY;
    }
    
    /**
     * textareaタグを出力するクラスの実装をサポートするクラス。
     * @author Kiyohito Itoh
     */
    private static final class TextareaTagWriter extends SinglevaluedInputTagWriterSupport {
        
        /**
         * デフォルトコンストラクタ。
         * @param tagName タグ名
         */
        public TextareaTagWriter(String tagName) {
            super(tagName);
        }
        
        /**
         * {@inheritDoc}
         * <pre>
         * 指定された属性を使用してtextareaタグを出力する。
         * name属性に対応する入力データをボディに設定する。
         * 属性はHTMLエスケープして出力する。
         * </pre>
         */
        protected String createInputTag(PageContext pageContext, HtmlAttributes attributes, Object value) {
            // 開始タグ直後の改行はレンダリング時に削除されてしまう。
            // 入力データの先頭に改行を表示出来るようにするため、先頭に削除用の改行を設定。
            String s = new StringBuilder()
                    .append(TagUtil.getCustomTagConfig().getLineSeparator())
                    .append(StringUtil.toString(value))
                    .toString();
            return TagUtil.createTagWithBody(getTagName(), attributes, TagUtil.escapeHtml(s, false));
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "textarea";
    }
}
