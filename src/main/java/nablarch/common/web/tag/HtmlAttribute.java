package nablarch.common.web.tag;

import nablarch.core.util.annotation.Published;

/**
 * HTMLの属性を表す列挙型。<br>
 * カスタムタグで使用する属性のみ定義している。
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public enum HtmlAttribute {

    /*
     * XHTMLの属性定義
     */

    // core
    
    /** id属性 */
    ID,
    
    /** class属性 */
    CLASS,
    
    /** style属性 */
    STYLE,
    
    /** title属性 */
    TITLE,
    
    // i18n
    
    /** lang属性 */
    LANG,
    
    /** xml:lang属性 */
    XML_LANG {
        /** {@inheritDoc}} */
        public String getXHtmlName() { return "xml:lang"; }
    },
    
    /** dir属性 */
    DIR,
    
    // focus
    
    /** accesskey属性 */
    ACCESSKEY,
    
    /** tabindex属性 */
    TABINDEX,
    
    // input
    
    /** type属性 */
    TYPE,
    
    /** name属性 */
    NAME,
    
    /** value属性 */
    VALUE,
    
    /** size属性 */
    SIZE,
    
    /** maxlength属性 */
    MAXLENGTH,
    
    /** multiple属性 */
    MULTIPLE,
    
    /** selected属性 */
    SELECTED,
    
    /** checked属性 */
    CHECKED,
    
    /** disabled属性 */
    DISABLED,
    
    /** readonly属性 */
    READONLY,
    
    /** src属性 */
    SRC,
    
    /** alt属性 */
    ALT,
    
    /** usemap属性 */
    USEMAP,
    
    /** align属性 */
    ALIGN,
    
    /** rows属性 */
    ROWS,
    
    /** cols属性 */
    COLS,
    
    // form
    
    /** action属性 */
    ACTION,
    
    /** method属性 */
    METHOD,
    
    /** enctype属性 */
    ENCTYPE,
    
    /** accept属性 */
    ACCEPT,
    
    /** accept-charset属性 */
    ACCEPT_CHARSET {
        /** {@inheritDoc}} */
        public String getXHtmlName() { return "accept-charset"; }
    },
    
    /** target属性 */
    TARGET,
    
    // a
    
    /** charset属性 */
    CHARSET,
    
    /** href属性 */
    HREF,
    
    /** hreflang属性 */
    HREFLANG,
    
    /** rel属性 */
    REL,
    
    /** rev属性 */
    REV,
    
    /** shape属性 */
    SHAPE,
    
    /** coords属性 */
    COORDS,
    
    // label
    
    /** for属性 */
    FOR,
    
    // img
    
    /** longdesc属性 */
    LONGDESC,
    
    /** height属性 */
    HEIGHT,
    
    /** width属性 */
    WIDTH,
    
    /** ismap属性 */
    ISMAP,
    
    /** border属性 */
    BORDER,
    
    /** hspace属性 */
    HSPACE,
    
    /** vspace属性 */
    VSPACE,
    
    // link
    
    /** media属性 */
    MEDIA,
    
    // script   
    
    /** language属性 */
    LANGUAGE,
    
    /** defer属性 */
    DEFER,
    
    /** xml:space属性 */
    XML_SPACE {
        /** {@inheritDoc}} */
        public String getXHtmlName() { return "xml:space"; }
    },
    
    // events
    
    /** onclick属性 */
    ONCLICK,
    
    /** ondblclick属性 */
    ONDBLCLICK,
    
    /** onselect属性 */
    ONSELECT,
    
    /** onchange属性 */
    ONCHANGE,
    
    /** onmousedown属性 */
    ONMOUSEDOWN,
    
    /** onmouseup属性 */
    ONMOUSEUP,
    
    /** onmouseover属性 */
    ONMOUSEOVER,
    
    /** onmousemove属性 */
    ONMOUSEMOVE,
    
    /** onmouseout属性 */
    ONMOUSEOUT,
    
    /** onkeypress属性 */
    ONKEYPRESS,
    
    /** onkeydown属性 */
    ONKEYDOWN,
    
    /** onkeyup属性 */
    ONKEYUP,
    
    /** onfocus属性 */
    ONFOCUS,
    
    /** onclur属性 */
    ONBLUR,
    
    /** onsubmit属性 */
    ONSUBMIT,
    
    /** onreset属性 */
    ONRESET,

    /*
     * HTML5の属性定義
     */

    /** autocomplete属性 */
    AUTOCOMPLETE,

    /** autofocus属性 */
    AUTOFOCUS,

    /** placeholder属性 */
    PLACEHOLDER,

    NONCE;

    /**
     * XHTMLの属性名を取得する。
     * <pre>
     * デフォルト実装では、列挙型の名前を小文字に変換した文字列を返す。
     * 列挙型の名前とXHTMLの属性名が異なる場合にオーバーライドする。
     * </pre>
     * @return XHTMLの属性名
     */
    public String getXHtmlName() {
        return name().toLowerCase();
    }
}
