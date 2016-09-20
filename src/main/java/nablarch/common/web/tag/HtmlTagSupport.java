package nablarch.common.web.tag;


import nablarch.core.util.annotation.Published;

/**
 * HTMLのタグを出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public abstract class HtmlTagSupport extends CustomTagSupport {
    
    /** HTMLの属性 */
    private HtmlAttributes attributes = new HtmlAttributes();
    
    /**
     * HTMLの属性を取得する。
     * @return XHTMLの属性
     */
    protected HtmlAttributes getAttributes() {
        return attributes;
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
