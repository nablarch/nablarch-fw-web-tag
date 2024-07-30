package nablarch.common.web.tag;

import jakarta.servlet.jsp.JspException;

import nablarch.core.util.StringUtil;

/**
 * コンテキストパスの付加とURLリライトに対応するscriptタグを出力するクラス。
 * @author Kiyohito Itoh
 */
public class ScriptTag extends HtmlTagSupport {
    
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
     * XHTMLのid属性を設定する。
     * @param id XHTMLのid属性
     */
    public void setId(String id) {
        getAttributes().put(HtmlAttribute.ID, id);
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
     * XHTMLのlanguage属性を設定する。
     * @param language XHTMLのlanguage属性
     */
    public void setLanguage(String language) {
        getAttributes().put(HtmlAttribute.LANGUAGE, language);
    }
    
    /**
     * XHTMLのsrc属性を設定する。
     * @param src XHTMLのsrc属性
     */
    public void setSrc(String src) {
        getAttributes().put(HtmlAttribute.SRC, src);
    }

    /**
     * XHTMLのdefer属性を設定する。
     * @param defer XHTMLのdefer属性
     */
    public void setDefer(String defer) {
        getAttributes().put(HtmlAttribute.DEFER, defer);
    }

    /**
     * XHTMLのxml:space属性を設定する。
     * @param xmlSpace XHTMLのdefer属性
     */
    public void setXmlSpace(String xmlSpace) {
        getAttributes().put(HtmlAttribute.XML_SPACE, xmlSpace);
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 属性はHTMLエスケープして出力する。
     * 絶対URLでない場合は言語対応のリソースパスに変換する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        if (!TagUtil.jsSupported(pageContext)) {
            return SKIP_BODY;
        }
        
        HtmlAttributes attributes = getAttributes();
        String src = attributes.get(HtmlAttribute.SRC);
        boolean hasSrc = StringUtil.hasValue(src);
        if (hasSrc) {
            TagUtil.overrideUriAttribute(pageContext, getAttributes(), HtmlAttribute.SRC, secure);
        }


        if (TagUtil.hasCspNonce(pageContext)) {
            attributes.put(HtmlAttribute.NONCE, TagUtil.getCspNonce(pageContext));
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(TagUtil.createStartTag("script", getAttributes()));

        if (!hasSrc) {
            CustomTagConfig config = TagUtil.getCustomTagConfig();
            sb.append(config.getLineSeparator())
              .append(config.getScriptBodyPrefix())
              .append(config.getLineSeparator());
        }
        TagUtil.print(pageContext, sb.toString());
        
        return EVAL_BODY_INCLUDE;
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 閉じタグを出力する。
     * </pre>
     */
    public int doEndTag() throws JspException {
        if (!TagUtil.jsSupported(pageContext)) {
            return EVAL_PAGE;
        }
        
        StringBuilder sb = new StringBuilder();

        HtmlAttributes attributes = getAttributes();
        String src = attributes.get(HtmlAttribute.SRC);
        if (!StringUtil.hasValue(src)) {
            CustomTagConfig config = TagUtil.getCustomTagConfig();
            sb.append(config.getLineSeparator())
              .append(config.getScriptBodySuffix())
              .append(config.getLineSeparator());
        }
        sb.append(TagUtil.createEndTag("script"));
        
        TagUtil.print(pageContext, sb.toString());
        
        return EVAL_PAGE;
    }
}
