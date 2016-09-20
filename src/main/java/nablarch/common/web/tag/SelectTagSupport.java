package nablarch.common.web.tag;

/**
 * selectタグの属性を出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public class SelectTagSupport extends GenericAttributesTagSupport {

    /**
     * XHTMLのname属性を設定する。
     * @param name XHTMLのname属性
     */
    public void setName(String name) {
        getAttributes().put(HtmlAttribute.NAME, name);
    }
    
    /**
     * XHTMLのsize属性を設定する。
     * @param size XHTMLのsize属性
     */
    public void setSize(int size) {
        getAttributes().put(HtmlAttribute.SIZE, size);
    }
    
    /**
     * XHTMLのmultiple属性を設定する。
     * @param multiple XHTMLのmultiple属性
     */
    public void setMultiple(boolean multiple) {
        getAttributes().put(HtmlAttribute.MULTIPLE, multiple);
    }
    
    /**
     * XHTMLのdisabled属性を設定する。
     * @param disabled XHTMLのdisabled属性
     */
    public void setDisabled(boolean disabled) {
        getAttributes().put(HtmlAttribute.DISABLED, disabled);
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
    
    /**
     * XHTMLのonchange属性を設定する。
     * @param onchange XHTMLのonchange属性
     */
    public void setOnchange(String onchange) {
        getAttributes().put(HtmlAttribute.ONCHANGE, onchange);
    }

    /**
     * HTML5のautofocus属性を設定する。
     * @param autofocus HTML5のautofocus属性
     */
    public void setAutofocus(boolean autofocus) {
        getAttributes().put(HtmlAttribute.AUTOFOCUS, autofocus);
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "select";
    }
}
