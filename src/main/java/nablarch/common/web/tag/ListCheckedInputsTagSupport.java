package nablarch.common.web.tag;

import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * 複数のchecked属性を持つinputタグを出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class ListCheckedInputsTagSupport extends InputTagSupport {
    
    /** 複数のchecked属性を持つinputタグのサポートクラス。 */
    private ListCheckedInputsTagWriter writer = new ListCheckedInputsTagWriter();
    
    /**
     * XHTMLのonchange属性を設定する。
     * @param onchange XHTMLのonchange属性
     */
    public void setOnchange(String onchange) {
        getAttributes().put(HtmlAttribute.ONCHANGE, onchange);
    }
    
    /**
     * リストを取得するための名前を設定する。
     * @param listName リストを取得するための名前
     */
    public void setListName(String listName) {
        writer.setListName(listName);
    }

    /**
     * リスト要素から値を取得するためのプロパティ名を設定する。
     * @param elementValueProperty リスト要素から値を取得するためのプロパティ名
     */
    public void setElementValueProperty(String elementValueProperty) {
        writer.setElementValueProperty(elementValueProperty);
    }

    /**
     * リスト要素からラベルを取得するためのプロパティ名を設定する。
     * @param elementLabelProperty リスト要素からラベルを取得するためのプロパティ名
     */
    public void setElementLabelProperty(String elementLabelProperty) {
        writer.setElementLabelProperty(elementLabelProperty);
    }

    /**
     * リスト要素のラベルを整形するためのパターンを設定する。
     * <pre>
     * プレースホルダを下記に示す。
     * $LABEL$: ラベル
     * $VALUE$: 値
     * 
     * "$VALUE$ - $LABEL$"と指定した場合、ラベル＝グループ1、値＝G001とすると、整形後のラベルは"G001 - グループ1"となる。<br>
     * 
     * デフォルトは"$LABEL$"。
     * </pre>
     * @param elementLabelPattern リスト要素のラベルを整形するためのパターン
     */
    public void setElementLabelPattern(String elementLabelPattern) {
        writer.setElementLabelPattern(elementLabelPattern);
    }

    /**
     * リスト表示時に使用するフォーマットを設定する。
     * <pre>
     * 下記のいずれかを指定する。
     * br(brタグ)
     * div(divタグ)
     * span(spanタグ)
     * ul(ulタグ)
     * ol(olタグ)
     * sp(スペース区切り)
     * 
     * デフォルトはbr。
     * </pre>
     * @param listFormat リスト表示時に使用するフォーマット
     */
    public void setListFormat(String listFormat) {
        if (listFormat == null || !ListFormat.getFormats().contains(listFormat)) {
            throw new IllegalArgumentException(
                String.format("listFormat was invalid. listFormat must specify the following values. values = %s listFormat = [%s]",
                              ListFormat.getFormats(), listFormat));
        }
        writer.setListFormat(ListFormat.getFormatByTagName(listFormat));
    }
    
    /**
     * エラーレベルのメッセージに使用するCSSクラス名を設定する。<br>
     * デフォルトは"nablarch_error"。
     * @param errorCss エラーレベルのメッセージに使用するCSSクラス名
     */
    public void setErrorCss(String errorCss) {
        writer.setErrorCss(errorCss);
    }
    
    /**
     * name属性のエイリアスを設定する。<br>
     * 複数指定する場合はカンマ区切り。
     * @param nameAlias name属性のエイリアス
     */
    public void setNameAlias(String nameAlias) {
        writer.setNameAlias(nameAlias);
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 入力画面と確認画面で出力内容が異なる。
     * 
     * 入力画面：
     * inputタグとラベルを連結したコンテンツを指定されたフォーマットで出力する。
     * name属性に対応する入力データが存在する場合はchecked属性を設定する。
     * type属性にサブクラスが返す値を設定する。
     * 属性とラベルはHTMLエスケープして出力する。
     * {@link nablarch.common.web.tag.FormContext}にname属性を設定する。
     * 
     * 確認画面：
     * name属性に対応する入力データが存在する場合は指定されたフォーマットで出力する。
     * ラベルはHTMLエスケープして出力する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();
        getAttributes().put(HtmlAttribute.TYPE, getType());
        writer.writeTag(pageContext, getAttributes());
        TagUtil.setNameToFormContext(pageContext, getAttributes());
        return SKIP_BODY;
    }
    
    /**
     * type属性を取得する。
     * @return type属性
     */
    protected abstract String getType();
    
    /**
     * 複数のchecked属性を持つinputタグを出力するクラスの実装をサポートするクラス。
     * @author Kiyohito Itoh
     */
    private static final class ListCheckedInputsTagWriter extends ListTagWriterSupport {

        /**
         * {@inheritDoc}
         * <pre>
         * inputタグとラベルを連結したコンテンツを指定されたフォーマットで出力する。
         * name属性に対応する入力データが存在する場合はchecked属性を設定する。
         * 属性とラベルはHTMLエスケープして出力する。
         * autofocus属性(HTML5)は先頭のタグだけに出力する。
         * </pre>
         */
        protected String createInputTag(PageContext pageContext, HtmlAttributes attributes, List<?> list, Collection<?> values,
                                         ListFormat listFormat, HtmlAttributes listAttributes) {
            String errorCssClass = listAttributes.get(HtmlAttribute.CLASS);
            StringBuilder sb = new StringBuilder();
            sb.append(listFormat.getListStartTag(listAttributes));
            for (Object element : list) {
                if (sb.length() != 0 && attributes.get(HtmlAttribute.AUTOFOCUS) != null) {
                    attributes.put(HtmlAttribute.AUTOFOCUS, null);
                }
                String value = getValue(element);
                attributes.put(HtmlAttribute.VALUE, value);
                attributes.put(HtmlAttribute.CHECKED, TagUtil.contains(values, value));
                
                String type = attributes.get(HtmlAttribute.TYPE);
                String id = TagUtil.generateUniqueName(pageContext, type);
                attributes.put(HtmlAttribute.ID, id);
                String label = TagUtil.createLabelTag(type, TagUtil.escapeHtml(getFormattedLabel(element, value), true),
                                                      id, errorCssClass);
                
                String checkbox = TagUtil.createTagWithoutBody("input", attributes) + label;
                
                sb.append(listFormat.getElementTag(listAttributes, checkbox));
            }
            return sb.append(listFormat.getListEndTag()).toString();
        }
    }
}
