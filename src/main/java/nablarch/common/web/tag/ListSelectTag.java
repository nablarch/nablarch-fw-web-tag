package nablarch.common.web.tag;

import java.util.Collection;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import nablarch.core.util.StringUtil;

/**
 * 入力データ復元とHTMLエスケープを行う選択項目(selectタグ)を出力するクラス。
 * @author Kiyohito Itoh
 */
public class ListSelectTag extends SelectTagSupport {
    
    /** selectタグを出力するクラスの実装をサポートするクラス */
    private ListSelectTagWriter writer = new ListSelectTagWriter();

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
     * "$VALUE$ - $LABEL$"と指定した場合、ラベル＝グループ1、値＝G001とすると、整形後のラベルは"G001 - グループ1"となる。
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
     * リスト先頭に選択なしのオプションを追加するか否かを設定する。<br>
     * デフォルトはfalse。
     * @param withNoneOption 追加する場合はtrue、追加しない場合はfalse
     */
    public void setWithNoneOption(boolean withNoneOption) {
        writer.setWithNoneOption(withNoneOption);
    }
    
    /**
     * リスト先頭に選択なしのオプションを追加する場合に使用するラベルを設定する。
     * <pre>
     * この属性は、withNoneOptionにtrueを指定した場合のみ有効となる。
     * デフォルトは""。
     * </pre>
     * @param noneOptionLabel リスト先頭に選択なしのオプションを追加する場合に使用するラベル
     */
    public void setNoneOptionLabel(String noneOptionLabel) {
        writer.setNoneOptionLabel(noneOptionLabel);
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
     * リストをoptionタグに展開したselectタグを出力する。
     * 
     * selectタグ：
     * 指定された属性を使用してselectタグを出力する。
     * 属性はHTMLエスケープして出力する。
     * 選択なしオプションが指定された場合は、選択なしのoptionタグを出力する。
     * 選択なしオプションのvalue属性は常に空文字となる。
     * 
     * optionタグ：
     * リストから取得した値とラベルを使用してoptionタグを出力する。
     * name属性に対応する入力データが存在する場合はselected属性を設定する。
     * 属性とラベルはHTMLエスケープして出力する。
     * 
     * {@link nablarch.common.web.tag.FormContext}にname属性を設定する。
     * 
     * 確認画面：
     * name属性に対応する入力データが存在する場合は指定されたフォーマットで出力する。
     * ラベルはHTMLエスケープして出力する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();
        writer.writeTag(pageContext, getAttributes());
        TagUtil.setNameToFormContext(pageContext, getAttributes());
        return SKIP_BODY;
    }
    
    /**
     * selectタグを出力するクラスの実装をサポートするクラス。
     * @author Kiyohito Itoh
     */
    private static final class ListSelectTagWriter extends ListTagWriterSupport {
        
        /** リスト先頭に選択なしのオプションを追加するか否か */
        private boolean withNoneOption = false;
        
        /**
         * リスト先頭に選択なしのオプションを追加する場合に使用するラベル
         */
        private String noneOptionLabel = "";
        
        /**
         * リスト先頭に選択なしのオプションを追加するか否かを設定する。<br>
         * デフォルトはfalse。
         * @param withNoneOption 追加する場合はtrue、追加しない場合はfalse
         */
        private void setWithNoneOption(boolean withNoneOption) {
            this.withNoneOption = withNoneOption;
        }

        /**
         * リスト先頭に選択なしのオプションを追加する場合に使用するラベルを設定する。
         * <pre>
         * この属性は、withNoneOptionにtrueを指定した場合のみ有効となる。
         * デフォルトは""。
         * </pre>
         * @param noneOptionLabel リスト先頭に選択なしのオプションを追加する場合に使用するラベル
         */
        private void setNoneOptionLabel(String noneOptionLabel) {
            this.noneOptionLabel = noneOptionLabel;
        }
        
        /**
         * {@inheritDoc}}
         * <pre>
         * リストをoptionタグに展開したselectタグを出力する。
         * 
         * selectタグ：
         * 指定された属性を使用してselectタグを出力する。
         * 属性はHTMLエスケープして出力する。
         * 選択なしオプションが指定された場合は、選択なしのoptionタグを出力する。
         * 選択なしオプションのvalue属性は常に空文字となる。
         * 
         * optionタグ：
         * リストから取得した値とラベルを使用してoptionタグを出力する。
         * name属性に対応する入力データが存在する場合はselected属性を設定する。
         * 属性とラベルはHTMLエスケープして出力する。
         * </pre>
         */
        protected String createInputTag(PageContext pageContext, HtmlAttributes attributes, List<?> list, Collection<?> values,
                                         ListFormat listFormat, HtmlAttributes listAttributes) {
            StringBuilder sb = new StringBuilder();
            sb.append(TagUtil.createStartTag("select", attributes));
            if (withNoneOption) {
                HtmlAttributes optionAttributes = new HtmlAttributes();
                optionAttributes.put(HtmlAttribute.VALUE, "");
                sb.append(TagUtil.getCustomTagConfig().getLineSeparator())
                  .append(TagUtil.createTagWithBody("option", optionAttributes, TagUtil.escapeHtml(noneOptionLabel, false)));
            }
            String errorCssClass = listAttributes.get(HtmlAttribute.CLASS);
            for (Object element : list) {
                HtmlAttributes optionAttributes = new HtmlAttributes();
                String value = getValue(element);
                if (!StringUtil.isNullOrEmpty(errorCssClass)) {
                    optionAttributes.put(HtmlAttribute.CLASS, errorCssClass);
                }
                optionAttributes.put(HtmlAttribute.VALUE, value);
                optionAttributes.put(HtmlAttribute.SELECTED, TagUtil.contains(values, value));
                sb.append(TagUtil.getCustomTagConfig().getLineSeparator())
                  .append(TagUtil.createTagWithBody("option", optionAttributes, TagUtil.escapeHtml(getFormattedLabel(element, value), false)));
            }
            return sb.append(TagUtil.createEndTag("select")).toString();
        }
    }
}
