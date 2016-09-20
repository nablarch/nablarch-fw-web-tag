package nablarch.common.web.tag;

import javax.servlet.jsp.JspException;

/**
 * コンテキストパスの付加とURLリライトに対応するaタグを出力するクラス。
 * @author Kiyohito Itoh
 */
public class AnchorTag extends FocusAttributesTagSupport {
    
    /** URIをhttpsにするか否か */
    private Boolean secure = null;
    
    /**
     * URIをhttpsにするか否かを設定する。
     * @param secure httpsにする場合はtrue、しない場合はfalse。
     */
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    /**
     * XHTMLのcharset属性を設定する。
     * @param charset XHTMLのcharset属性
     */
    public void setCharset(String charset) {
        getAttributes().put(HtmlAttribute.CHARSET, charset);
    }
    
    /**
     * XHTMLのtype属性を設定する。
     * @param type XHTMLのtype属性
     */
    public void setType(String type) {
        getAttributes().put(HtmlAttribute.TYPE, type);
    }
    
    /**
     * XHTMLのname属性を設定する。
     * @param name XHTMLのname属性
     */
    public void setName(String name) {
        getAttributes().put(HtmlAttribute.NAME, name);
    }
    
    /**
     * XHTMLのhref属性を設定する。
     * @param href XHTMLのhref属性
     */
    public void setHref(String href) {
        getAttributes().put(HtmlAttribute.HREF, href);
    }
    
    /**
     * XHTMLのhreflang属性を設定する。
     * @param hreflang XHTMLのhreflang属性
     */
    public void setHreflang(String hreflang) {
        getAttributes().put(HtmlAttribute.HREFLANG, hreflang);
    }
    
    /**
     * XHTMLのrel属性を設定する。
     * @param rel XHTMLのrel属性
     */
    public void setRel(String rel) {
        getAttributes().put(HtmlAttribute.REL, rel);
    }
    
    /**
     * XHTMLのrev属性を設定する。
     * @param rev XHTMLのrev属性
     */
    public void setRev(String rev) {
        getAttributes().put(HtmlAttribute.REV, rev);
    }
    
    /**
     * XHTMLのshape属性を設定する。
     * @param shape XHTMLのshape属性
     */
    public void setShape(String shape) {
        getAttributes().put(HtmlAttribute.SHAPE, shape);
    }
    
    /**
     * XHTMLのcoords属性を設定する。
     * @param coords XHTMLのcoords属性
     */
    public void setCoords(String coords) {
        getAttributes().put(HtmlAttribute.COORDS, coords);
    }
    
    /**
     * XHTMLのtarget属性を設定する。
     * @param target XHTMLのtarget属性
     */
    public void setTarget(String target) {
        getAttributes().put(HtmlAttribute.TARGET, target);
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 属性はHTMLエスケープして出力する。
     * 絶対URLでない場合は言語対応のリソースパスに変換する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        
        TagUtil.overrideUriAttribute(pageContext, getAttributes(), HtmlAttribute.HREF, secure);
        TagUtil.print(pageContext, TagUtil.createStartTag("a", getAttributes()));
        
        return EVAL_BODY_INCLUDE;
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 閉じタグを出力する。
     * </pre>
     */
    public int doEndTag() throws JspException {
        TagUtil.print(pageContext, TagUtil.createEndTag("a"));
        return EVAL_PAGE;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "a";
    }
}
