package nablarch.common.web.tag;

import jakarta.servlet.jsp.JspException;

/**
 * コンテキストパスの付加とURLリライトに対応するlinkタグを出力するクラス。
 * @author Kiyohito Itoh
 */
public class LinkTag extends GenericAttributesTagSupport {
    
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
     * XHTMLのtype属性を設定する。
     * @param type XHTMLのtype属性
     */
    public void setType(String type) {
        getAttributes().put(HtmlAttribute.TYPE, type);
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
     * XHTMLのmedia属性を設定する。
     * @param media XHTMLのmedia属性
     */
    public void setMedia(String media) {
        getAttributes().put(HtmlAttribute.MEDIA, media);
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
        TagUtil.print(pageContext, TagUtil.createTagWithoutBody("link", getAttributes()));
        
        return SKIP_BODY;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "link";
    }
}
