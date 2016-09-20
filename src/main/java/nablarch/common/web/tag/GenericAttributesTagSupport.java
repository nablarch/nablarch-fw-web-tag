package nablarch.common.web.tag;

import nablarch.core.util.Builder;

/**
 * XHTMLの共通属性を出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class GenericAttributesTagSupport extends HtmlTagSupport {

    /**
     * 自身のタグがFormタグの子要素として使用されているかどうか（フォームコンテキスト情報が存在するかどうか）をチェックする。
     * <p/>
     * Formタグの子要素でなければならないタグは、本メソッドを実行してチェックすること。
     * <p/>
     * 自身のタグがFormタグの子要素でない場合、IllegalStateExceptionをスローする。
     */
    protected void checkChildElementsOfForm() {
        if (TagUtil.getFormContext(pageContext) == null) {
            throw new IllegalStateException(Builder.concat(
                    "invalid location of the ", getTagName(), " tag. the ", getTagName(), " tag must locate in the form tag."));
        }
    }
    
    /**
     * タグ名を取得する。
     * @return タグ名
     */
    protected abstract String getTagName();
    
    /**
     * XHTMLのid属性を設定する。
     * @param id XHTMLのid属性
     */
    public void setId(String id) {
        getAttributes().put(HtmlAttribute.ID, id);
    }

    /**
     * XHTMLのclass属性を設定する。
     * @param cssClass XHTMLのclass属性
     */
    public void setCssClass(String cssClass) {
        getAttributes().put(HtmlAttribute.CLASS, cssClass);
    }

    /**
     * XHTMLのstyle属性を設定する。
     * @param style XHTMLのstyle属性
     */
    public void setStyle(String style) {
        getAttributes().put(HtmlAttribute.STYLE, style);
    }

    /**
     * XHTMLのtitle属性を設定する。
     * @param title XHTMLのtitle属性
     */
    public void setTitle(String title) {
        getAttributes().put(HtmlAttribute.TITLE, title);
    }

    /**
     * XHTMLのlang属性を設定する。
     * @param lang XHTMLのlang属性
     */
    public void setLang(String lang) {
        getAttributes().put(HtmlAttribute.LANG, lang);
    }

    /**
     * XHTMLのxml:lang属性を設定する。
     * @param xmlLang XHTMLのxml:lang属性
     */
    public void setXmlLang(String xmlLang) {
        getAttributes().put(HtmlAttribute.XML_LANG, xmlLang);
    }

    /**
     * XHTMLのdir属性を設定する。
     * @param dir XHTMLのdir属性
     */
    public void setDir(String dir) {
        getAttributes().put(HtmlAttribute.DIR, dir);
    }

    /**
     * XHTMLのonclick属性を設定する。
     * @param onclick XHTMLのonclick属性
     */
    public void setOnclick(String onclick) {
        getAttributes().put(HtmlAttribute.ONCLICK, onclick);
    }

    /**
     * XHTMLのondblclick属性を設定する。
     * @param ondblclick XHTMLのondblclick属性
     */
    public void setOndblclick(String ondblclick) {
        getAttributes().put(HtmlAttribute.ONDBLCLICK, ondblclick);
    }

    /**
     * XHTMLのonmousedown属性を設定する。
     * @param onmousedown XHTMLのonmousedown属性
     */
    public void setOnmousedown(String onmousedown) {
        getAttributes().put(HtmlAttribute.ONMOUSEDOWN, onmousedown);
    }

    /**
     * XHTMLのonmouseup属性を設定する。
     * @param onmouseup XHTMLのonmouseup属性
     */
    public void setOnmouseup(String onmouseup) {
        getAttributes().put(HtmlAttribute.ONMOUSEUP, onmouseup);
    }
    
    /**
     * XHTMLのonmouseover属性を設定する。
     * @param onmouseover XHTMLのonmouseover属性
     */
    public void setOnmouseover(String onmouseover) {
        getAttributes().put(HtmlAttribute.ONMOUSEOVER, onmouseover);
    }
    
    /**
     * XHTMLのonmousemove属性を設定する。
     * @param onmousemove XHTMLのonmousemove属性
     */
    public void setOnmousemove(String onmousemove) {
        getAttributes().put(HtmlAttribute.ONMOUSEMOVE, onmousemove);
    }

    /**
     * XHTMLのonmouseout属性を設定する。
     * @param onmouseout XHTMLのonmouseout属性
     */
    public void setOnmouseout(String onmouseout) {
        getAttributes().put(HtmlAttribute.ONMOUSEOUT, onmouseout);
    }

    /**
     * XHTMLのonkeypress属性を設定する。
     * @param onkeypress XHTMLのonkeypress属性
     */
    public void setOnkeypress(String onkeypress) {
        getAttributes().put(HtmlAttribute.ONKEYPRESS, onkeypress);
    }

    /**
     * XHTMLのonkeydown属性を設定する。
     * @param onkeydown XHTMLのonkeydown属性
     */
    public void setOnkeydown(String onkeydown) {
        getAttributes().put(HtmlAttribute.ONKEYDOWN, onkeydown);
    }

    /**
     * XHTMLのonkeyup属性を設定する。
     * @param onkeyup XHTMLのonkeyup属性
     */
    public void setOnkeyup(String onkeyup) {
        getAttributes().put(HtmlAttribute.ONKEYUP, onkeyup);
    }
}
