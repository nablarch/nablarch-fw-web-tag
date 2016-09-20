package nablarch.common.web.tag;

/**
 * フォーカスを取得可能なタグの属性を出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class FocusAttributesTagSupport extends GenericAttributesTagSupport {
    
    /**
     * XHTMLのaccesskey属性を設定する。
     * @param accesskey XHTMLのaccesskey属性
     */
    public void setAccesskey(String accesskey) {
        getAttributes().put(HtmlAttribute.ACCESSKEY, accesskey);
    }

    /**
     * XHTMLのtabindex属性を設定する。
     * @param tabindex XHTMLのtabindex属性
     */
    public void setTabindex(int tabindex) {
        getAttributes().put(HtmlAttribute.TABINDEX, tabindex);
    }

    /**
     * XHTMLのonfocus属性を設定する。
     * @param onfocus XHTMLのonfocus属性
     */
    public void setOnfocus(String onfocus) {
        getAttributes().put(HtmlAttribute.ONFOCUS, onfocus);
    }

    /**
     * XHTMLのonblur属性を設定する。
     * @param onblur XHTMLのonblur属性
     */
    public void setOnblur(String onblur) {
        getAttributes().put(HtmlAttribute.ONBLUR, onblur);
    }
}
