package nablarch.common.web.tag;

import java.util.Collection;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import nablarch.core.util.StringUtil;

/**
 * 入力データ復元とHTMLエスケープを行うコード値の選択項目(selectタグ)を出力するクラス。
 * @author Kiyohito Itoh
 */
public class CodeSelectTag extends SelectTagSupport {
    
    /** コード値のselectタグを出力するクラスの実装をサポートするクラス */
    private CodeSelectTagWriter writer = new CodeSelectTagWriter();
    
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
     * リスト先頭に選択なしのオプションを追加するか否かを設定する。<br>
     * デフォルトはfalse。
     * @param withNoneOption 追加する場合はtrue、追加しない場合はfalse
     */
    public void setWithNoneOption(boolean withNoneOption) {
        writer.setWithNoneOption(withNoneOption);
    }
    
    /**
     * リスト先頭に選択なしのオプションを追加する場合に使用するラベルを設定する。
     * この属性は、withNoneOptionにtrueを指定した場合のみ有効となる。<br>
     * デフォルトは""。
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
     * コード値のselectタグを出力するクラスの実装をサポートするクラス。
     * @author Kiyohito Itoh
     */
    private static final class CodeSelectTagWriter extends CodeTagWriterSupport {
        
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
         * リスト先頭に選択なしのオプションを追加する場合に使用するラベルを設定する。<br>
         * この属性は、withNoneOptionにtrueを指定した場合のみ有効となる。<br>
         * デフォルトは""。
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
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "codeSelect";
    }
}
