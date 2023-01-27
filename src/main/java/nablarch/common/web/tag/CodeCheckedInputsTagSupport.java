package nablarch.common.web.tag;

import java.util.Collection;
import java.util.List;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * コード値の複数のchecked属性を持つinputタグを出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class CodeCheckedInputsTagSupport extends InputTagSupport {
    
    /** コード値の複数のchecked属性を持つinputタグを出力するクラスの実装をサポートするクラス */
    private CodeCheckedInputsTagWriter writer = new CodeCheckedInputsTagWriter();
    
    /**
     * XHTMLのonchange属性を設定する。
     * @param onchange XHTMLのonchange属性
     */
    public void setOnchange(String onchange) {
        getAttributes().put(HtmlAttribute.ONCHANGE, onchange);
    }
    
    /**
     * コードIDを設定する。
     * @param codeId コードID
     */
    public void setCodeId(String codeId) {
        writer.setCodeId(codeId);
    }

    /**
     * 使用するパターンのカラム名を設定する。
     * @param pattern 使用するパターンのカラム名
     */
    public void setPattern(String pattern) {
        writer.setPattern(pattern);
    }

    /**
     * 取得するオプション名称のカラム名を設定する。
     * @param optionColumnName 取得するオプション名称のカラム名
     */
    public void setOptionColumnName(String optionColumnName) {
        writer.setOptionColumnName(optionColumnName);
    }

    /**
     * ラベルを整形するパターンを設定する。
     * <pre>
     * プレースホルダを下記に示す。
     * 
     * $NAME$: コード値に対応するコード名称
     * $SHORTNAME$: コード値に対応するコードの略称
     * $OPTIONALNAME$: コード値に対応するコードのオプション名称
     *                 $OPTIONALNAME$を使用する場合は、optionColumnName属性の指定が必須となる。
     * $VALUE$: コード値
     * 
     * デフォルトは"$NAME$"。
     * </pre>
     * @param labelPattern ラベルを整形するパターン
     */
    public void setLabelPattern(String labelPattern) {
        writer.setLabelPattern(labelPattern);
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
     * type属性にサブクラスが返す値を設定する。
     * name属性に対応する入力データが存在する場合はchecked属性を設定する。
     * 属性とラベルはHTMLエスケープして出力する。
     * {@link nablarch.common.web.tag.FormContext}にname属性を設定する。
     * 
     * 確認画面：
     * name属性に対応する入力データが存在する場合は指定されたフォーマットで出力する。
     * ラベルはHTMLエスケープして出力する。
     * 
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
     * コード値の複数のchecked属性を持つinputタグを出力するクラスの実装をサポートするクラス。
     * @author Kiyohito Itoh
     */
    private static final class CodeCheckedInputsTagWriter extends CodeTagWriterSupport {

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
