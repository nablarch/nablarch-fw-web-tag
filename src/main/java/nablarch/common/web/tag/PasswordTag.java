package nablarch.common.web.tag;

import nablarch.core.util.StringUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="password")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class PasswordTag extends InputTagSupport {
    
    /** passwordタグのサポートクラス */
    private PasswordTagWriter writer = new PasswordTagWriter();

    /**
     * HTML5のautocomplete属性を設定する。
     * @param autocomplete HTML5のautocomplete属性
     */
    public void setAutocomplete(String autocomplete) {
        getAttributes().put(HtmlAttribute.AUTOCOMPLETE, autocomplete);
    }

    /**
     * 再表示時に入力データを復元するか否かを設定する。
     * @param restoreValue 復元する場合はtrue、復元しない場合はfalse
     */
    public void setRestoreValue(boolean restoreValue) {
        writer.setRestoreValue(restoreValue);
    }

    /**
     * 値のみ表示する場合に使用する置換文字を設定する。
     * @param replacement 値のみ表示する場合に使用する置換文字
     */
    public void setReplacement(char replacement) {
        writer.setReplacement(replacement);
    }

    /**
     * XHTMLのreadonly属性を設定する。
     * @param readonly XHTMLのreadonly属性
     */
    public void setReadonly(boolean readonly) {
        getAttributes().put(HtmlAttribute.READONLY, readonly);
    }

    /**
     * XHTMLのsize属性を設定する。
     * @param size XHTMLのsize属性
     */
    public void setSize(int size) {
        getAttributes().put(HtmlAttribute.SIZE, size);
    }

    /**
     * XHTMLのmaxlength属性を設定する。
     * @param maxlength XHTMLのmaxlength属性
     */
    public void setMaxlength(int maxlength) {
        getAttributes().put(HtmlAttribute.MAXLENGTH, maxlength);
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
     * HTML5のplaceholder属性を設定する。
     * @param placeholder HTML5のplaceholder属性
     */
    public void setPlaceholder(String placeholder) {
        getAttributes().put(HtmlAttribute.PLACEHOLDER, placeholder);
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
     * 指定された属性を使用してinputタグを出力する。
     * 入力データの復元が指定されている場合は、value属性を設定する。
     * type属性に"password"を設定する。
     * 属性はHTMLエスケープして出力する。
     * {@link nablarch.common.web.tag.FormContext}にname属性を設定する。
     * 
     * 確認画面：
     * 入力データを指定された置換文字に置き換え、HTMLエスケープして出力する。
     * 
     * </pre>
     */
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();
        getAttributes().put(HtmlAttribute.TYPE, "password");
        writer.writeTag(pageContext, getAttributes());
        TagUtil.setNameToFormContext(pageContext, getAttributes());
        return SKIP_BODY;
    }
    
    /**
     * passwordタグを出力するクラスの実装をサポートするクラス。
     * @author Kiyohito Itoh
     */
    private static final class PasswordTagWriter extends SinglevaluedInputTagWriterSupport {

        /** 再表示時に入力データを復元するか否か */
        private boolean restoreValue = false;

        /** 値のみ表示する場合に使用する置換文字 */
        private char replacement = '*';
        
        /** デフォルトコンストラクタ。 */
        public PasswordTagWriter() {
            super("input");
        }
        
        /**
         * 再表示時に入力データを復元するか否かを設定する。
         * @param restoreValue 復元する場合はtrue、復元しない場合はfalse
         */
        private void setRestoreValue(boolean restoreValue) {
            this.restoreValue = restoreValue;
        }
        
        /**
         * 値のみ表示する場合に使用する置換文字を設定する。
         * @param replacement 値のみ表示する場合に使用する置換文字
         */
        private void setReplacement(char replacement) {
            this.replacement = replacement;
        }

        /**
         * {@inheritDoc}
         * <pre>
         * 指定された属性を使用してinputタグを出力する。
         * 入力データの復元が指定されている場合は、value属性を設定する。
         * 属性はHTMLエスケープして出力する。
         * </pre>
         */
        protected String createInputTag(PageContext pageContext, HtmlAttributes attributes, Object value) {
            if (restoreValue) {
                attributes.put(HtmlAttribute.VALUE, value);
            }
            if (attributes.get(HtmlAttribute.AUTOCOMPLETE) == null) {
                AutocompleteDisableTarget defaultTarget = TagUtil.getCustomTagConfig().getAutocompleteDisableTarget();
                if (defaultTarget == AutocompleteDisableTarget.ALL
                        || defaultTarget == AutocompleteDisableTarget.PASSWORD) {
                    attributes.put(HtmlAttribute.AUTOCOMPLETE, "off");
                }
            }
            return TagUtil.createTagWithoutBody(getTagName(), attributes);
        }

        /**
         * {@inheritDoc}
         * <pre>
         * 入力データを指定された置換文字に置き換え、HTMLエスケープして出力する。
         * </pre>
         */
        protected String createOutputTag(PageContext pageContext, HtmlAttributes attributes, Object value) {
            String password = StringUtil.toString(value);
            StringBuilder sb = new StringBuilder(password.length());
            for (int i = 0; i < password.length(); i++) {
                sb.append(replacement);
            }
            return TagUtil.escapeHtml(sb.toString(), true);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "password";
    }
}
