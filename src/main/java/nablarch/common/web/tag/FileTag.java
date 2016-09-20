package nablarch.common.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="file")を出力するクラス。
 *
 * @author T.Kawasaki
 */
public class FileTag extends InputTagSupport {

    /** タグ出力サポートクラス*/
    private FileTagWriter writer = new FileTagWriter();

    /**
     * XHTMLのreadonly属性を設定する。
     *
     * @param readonly XHTMLのreadonly属性
     */
    public void setReadonly(boolean readonly) {
        getAttributes().put(HtmlAttribute.READONLY, readonly);
    }

    /**
     * XHTMLのsize属性を設定する。
     *
     * @param size XHTMLのsize属性
     */
    public void setSize(int size) {
        getAttributes().put(HtmlAttribute.SIZE, size);
    }

    /**
     * XHTMLのmaxlength属性を設定する。
     *
     * @param maxlength XHTMLのmaxlength属性
     */
    public void setMaxlength(int maxlength) {
        getAttributes().put(HtmlAttribute.MAXLENGTH, maxlength);
    }

    /**
     * XHTMLのonselect属性を設定する。
     *
     * @param onselect XHTMLのonselect属性
     */
    public void setOnselect(String onselect) {
        getAttributes().put(HtmlAttribute.ONSELECT, onselect);
    }

    /**
     * XHTMLのonchange属性を設定する。
     *
     * @param onchange XHTMLのonchange属性
     */
    public void setOnchange(String onchange) {
        getAttributes().put(HtmlAttribute.ONCHANGE, onchange);
    }

    /**
     * HTML5のmultiple属性を設定する。
     * @param multiple HTML5のmultiple属性
     */
    public void setMultiple(boolean multiple) {
        getAttributes().put(HtmlAttribute.MULTIPLE, multiple);
    }

    /**
     * エラーレベルのメッセージに使用するCSSクラス名を設定する。<br>
     * デフォルトは"nablarch_error"。
     *
     * @param errorCss エラーレベルのメッセージに使用するCSSクラス名
     */
    public void setErrorCss(String errorCss) {
        writer.setErrorCss(errorCss);
    }

    /**
     * name属性のエイリアスを設定する。<br>
     * 複数指定する場合はカンマ区切り。
     *
     * @param nameAlias name属性のエイリアス
     */
    public void setNameAlias(String nameAlias) {
        writer.setNameAlias(nameAlias);
    }

    /**
     * XHTMLのaccept属性を設定する。
     *
     * @param accept XHTMLのaccept属性
     */
    public void setAccept(String accept) {
        getAttributes().put(HtmlAttribute.ACCEPT, accept);
    }

    /**
     * {@inheritDoc}
     * <pre>
     * 入力画面と確認画面で出力内容が異なる。
     *
     * 入力画面：
     * 指定された属性を使用してボディを持たないタグを出力する。
     * value属性に指定されたvalueを設定する。
     * type属性に"file"を設定する。
     * 属性はHTMLエスケープして出力する。
     * {@link nablarch.common.web.tag.FormContext}にname属性を設定する。
     *
     * 確認画面：
     * アップロードされたファイル名をHTMLエスケープして出力する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();
        getAttributes().put(HtmlAttribute.TYPE, "file");
        writer.writeTag(pageContext, getAttributes());
        TagUtil.setNameToFormContext(pageContext, getAttributes());
        return SKIP_BODY;
    }
    
    /**
     * fileタグを出力するクラスの実装をサポートするクラス。
     * @author Masato Inoue
     */
    private static final class FileTagWriter extends SinglevaluedInputTagWriterSupport {

        /** 空文字 */
        private static final String EMPTY = "";

        /** デフォルトコンストラクタ。 */
        public FileTagWriter() {
            super("input");
        }
        
        /**
         * {@inheritDoc}
         * <pre>
         * value属性に固定で空文字を設定する。
         * </pre>
         */
        @Override
        protected String createInputTag(PageContext pageContext, HtmlAttributes attributes, Object value) {
            attributes.put(HtmlAttribute.VALUE, EMPTY);
            return TagUtil.createTagWithoutBody(getTagName(), attributes);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "file";
    }
}
