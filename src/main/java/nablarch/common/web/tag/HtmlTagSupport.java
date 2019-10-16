package nablarch.common.web.tag;


import nablarch.core.util.annotation.Published;

import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * HTMLのタグを出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public abstract class HtmlTagSupport extends CustomTagSupport implements DynamicAttributes {
    
    /** HTMLの属性 */
    private HtmlAttributes attributes = new HtmlAttributes();
    
    /**
     * HTMLの属性を取得する。
     * @return XHTMLの属性
     */
    protected HtmlAttributes getAttributes() {
        return attributes;
    }

    @Override
    public void setDynamicAttribute(String uri, String localName, Object value) {
        attributes.put(localName, value);
    }
    
    /**
     * {@inheritDoc}<br>
     * 何もしない。
     */
    public void doCatch(Throwable e) throws Throwable {
        throw e;
    }
    
    /**
     * {@inheritDoc}<br>
     * HTMLの属性をクリアする。
     */
    public void doFinally() {
        attributes.clear();
    }
}
