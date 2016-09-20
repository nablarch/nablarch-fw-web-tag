package nablarch.common.web.tag;

import javax.servlet.jsp.JspException;

/**
 * コンテキストパスの付加とURLリライトに対応するimgタグを出力するクラス。
 * @author Kiyohito Itoh
 */
public class ImgTag extends GenericAttributesTagSupport {
    
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
     * XHTMLのsrc属性を設定する。
     * @param src XHTMLのsrc属性
     */
    public void setSrc(String src) {
        getAttributes().put(HtmlAttribute.SRC, src);
    }
    
    /**
     * XHTMLのalt属性を設定する。
     * @param alt XHTMLのalt属性
     */
    public void setAlt(String alt) {
        getAttributes().put(HtmlAttribute.ALT, alt);
    }
    
    /**
     * XHTMLのname属性を設定する。
     * @param name XHTMLのname属性
     */
    public void setName(String name) {
        getAttributes().put(HtmlAttribute.NAME, name);
    }
    
    /**
     * XHTMLのlongdesc属性を設定する。
     * @param longdesc XHTMLのlongdesc属性
     */
    public void setLongdesc(String longdesc) {
        getAttributes().put(HtmlAttribute.LONGDESC, longdesc);
    }
    
    /**
     * XHTMLのheight属性を設定する。
     * @param height XHTMLのheight属性
     */
    public void setHeight(String height) {
        getAttributes().put(HtmlAttribute.HEIGHT, height);
    }
    
    /**
     * XHTMLのwidth属性を設定する。
     * @param width XHTMLのwidth属性
     */
    public void setWidth(String width) {
        getAttributes().put(HtmlAttribute.WIDTH, width);
    }
    
    /**
     * XHTMLのusemap属性を設定する。
     * @param usemap XHTMLのusemap属性
     */
    public void setUsemap(String usemap) {
        getAttributes().put(HtmlAttribute.USEMAP, usemap);
    }
    
    /**
     * XHTMLのismap属性を設定する。
     * @param ismap XHTMLのismap属性
     */
    public void setIsmap(String ismap) {
        getAttributes().put(HtmlAttribute.ISMAP, ismap);
    }
    
    /**
     * XHTMLのalign属性を設定する。
     * @param align XHTMLのalign属性
     */
    public void setAlign(String align) {
        getAttributes().put(HtmlAttribute.ALIGN, align);
    }
    
    /**
     * XHTMLのborder属性を設定する。
     * @param border XHTMLのborder属性
     */
    public void setBorder(String border) {
        getAttributes().put(HtmlAttribute.BORDER, border);
    }
    
    /**
     * XHTMLのhspace属性を設定する。
     * @param hspace XHTMLのhspace属性
     */
    public void setHspace(String hspace) {
        getAttributes().put(HtmlAttribute.HSPACE, hspace);
    }
    
    /**
     * XHTMLのvspace属性を設定する。
     * @param vspace XHTMLのvspace属性
     */
    public void setVspace(String vspace) {
        getAttributes().put(HtmlAttribute.VSPACE, vspace);
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 属性はHTMLエスケープして出力する。
     * 絶対URLでない場合は言語対応のリソースパスに変換する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        
        TagUtil.overrideUriAttribute(pageContext, getAttributes(), HtmlAttribute.SRC, secure);
        TagUtil.print(pageContext, TagUtil.createTagWithoutBody("img", getAttributes()));
        
        return SKIP_BODY;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "img";
    }
}
