package nablarch.common.web.tag;

/**
 * inputタグの共通属性を出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class InputTagSupport extends FocusAttributesTagSupport {

    /**
     * XHTMLのname属性を設定する。
     * @param name XHTMLのname属性
     */
    public void setName(String name) {
        getAttributes().put(HtmlAttribute.NAME, name);
    }

    /**
     * XHTMLのdisabled属性を設定する。
     * @param disabled XHTMLのdisabled属性
     */
    public void setDisabled(boolean disabled) {
        getAttributes().put(HtmlAttribute.DISABLED, disabled);
    }

    /**
     * HTML5のautofocus属性を設定する。
     * @param autofocus HTML5のautofocus属性
     */
    public void setAutofocus(boolean autofocus) {
        getAttributes().put(HtmlAttribute.AUTOFOCUS, autofocus);
    }
}
