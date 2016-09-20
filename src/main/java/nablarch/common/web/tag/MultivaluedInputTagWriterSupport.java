package nablarch.common.web.tag;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import nablarch.core.util.StringUtil;

/**
 * 多値の入力項目の実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class MultivaluedInputTagWriterSupport implements Serializable {
    
    /** リスト表示時に使用するタグ */
    private ListFormat listFormat;
    
    /** エラーレベルのメッセージに使用するCSSクラス名 */
    private String errorCss;
    
    /** name属性のエイリアス */
    private Set<String> nameAlias;
    
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
    public void setListFormat(ListFormat listFormat) {
        this.listFormat = listFormat;
    }
    
    /**
     * エラーレベルのメッセージに使用するCSSクラス名を設定する。<br>
     * デフォルトは"nablarch_error"。
     * @param errorCss エラーレベルのメッセージに使用するCSSクラス名
     */
    public void setErrorCss(String errorCss) {
        this.errorCss = errorCss;
    }
    
    /**
     * name属性のエイリアスを設定する。<br>
     * 複数指定する場合はカンマ区切り。
     * @param nameAlias name属性のエイリアス
     */
    public void setNameAlias(String nameAlias) {
        this.nameAlias = TagUtil.getCommaSeparatedValue(nameAlias);
    }
    
    /**
     * 多値の入力項目を出力する。
     * <pre>
     * 選択項目リストが存在しない場合は何も出力しない。
     * 入力画面の場合は、{@link #createInputTag(PageContext, HtmlAttributes, List, Collection, ListFormat, HtmlAttributes)}に処理を移譲する。
     * 確認画面の場合は、{@link #createOutputTag(PageContext, HtmlAttributes, List, Collection, ListFormat)}に処理を移譲する。
     * 
     * 入力画面の場合のみname属性に対応するエラーメッセージが存在する場合はclass属性に指定されたCSSクラス名を追記で設定する。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @throws JspException JSP例外
     */
    public void writeTag(PageContext pageContext, HtmlAttributes attributes) throws JspException {
        
        List<?> list = getList(pageContext);
        if (list.isEmpty()) {
            return;
        }
        
        Collection<?> values = getValues(pageContext, attributes);
        ListFormat useListFormat = listFormat != null ? listFormat : getDefaultListFormat();
        
        String tag;
        if (isConfirmationPage(pageContext)) {
            tag = createOutputTag(pageContext, attributes, list, values, useListFormat);
        } else {
            String errorCssClass = TagUtil.editClassAttributeForError(pageContext, attributes, errorCss, nameAlias);
            HtmlAttributes listAttributes = new HtmlAttributes();
            if (!StringUtil.isNullOrEmpty(errorCssClass)) {
                listAttributes.put(HtmlAttribute.CLASS, errorCssClass);
            }
            tag = createInputTag(pageContext, attributes, list, values, useListFormat, listAttributes);
        }
        TagUtil.print(pageContext, tag);
    }
    
    /**
     * 多値としてname属性に対応するvalue属性を取得する。<br>
     * 取得先はリクエストパラメータと変数スコープとする。
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @return value属性。存在しない場合は空のリスト
     */
    protected Collection<?> getValues(PageContext pageContext, HtmlAttributes attributes) {
        String name = attributes.get(HtmlAttribute.NAME);
        return TagUtil.getMultipleValues(pageContext, name);
    }
    
    /**
     * JSPが生成する画面が確認画面であるか否かを判定する。
     * @param pageContext ページコンテキスト
     * @return 確認画面の場合はtrue、確認画面でない場合はfalse
     */
    protected boolean isConfirmationPage(PageContext pageContext) {
        return TagUtil.isConfirmationPage(pageContext);
    }
    
    /**
     * 出力タグを作成する。
     * <pre>
     * name属性に対応する入力データが存在する場合は指定されたフォーマットで出力する。
     * ラベルはHTMLエスケープして出力する。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param list リスト
     * @param values 入力値
     * @param listFormat リストタグ
     * @return 出力タグ
     */
    protected String createOutputTag(PageContext pageContext, HtmlAttributes attributes,
                                      List<?> list, Collection<?> values,
                                      ListFormat listFormat) {
        StringBuilder elements = new StringBuilder();
        for (Object element : list) {
            String value = getValue(element);
            if (TagUtil.contains(values, value)) {
                elements.append(listFormat.getElementTag(HtmlAttributes.EMPTY_ATTRIBUTES,
                                                         TagUtil.escapeHtml(getFormattedLabel(element, value), true)));
            }
        }
        StringBuilder listTag = new StringBuilder();
        if (elements.length() != 0) {
            listTag.append(listFormat.getListStartTag(HtmlAttributes.EMPTY_ATTRIBUTES))
                   .append(elements)
                   .append(listFormat.getListEndTag());
        }
        return listTag.toString();
    }
    
    /**
     * 入力タグを作成する。
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param list リスト
     * @param values 入力値
     * @param listFormat リストタグ
     * @param listAttributes リストタグ用の属性。
     *                        name属性に対応するエラーメッセージが存在する場合は、CSSクラス名を設定している。
     * @return 入力タグ
     */
    protected abstract String createInputTag(PageContext pageContext, HtmlAttributes attributes,
                                                List<?> list, Collection<?> values,
                                                ListFormat listFormat, HtmlAttributes listAttributes);
    
    /**
     * 値を取得する。
     * @param element リスト要素
     * @return 値
     */
    protected abstract String getValue(Object element);
    
    /**
     * 整形済みのラベルを取得する。
     * @param element リスト要素
     * @param value リスト要素の値
     * @return 整形済みのラベル
     */
    protected abstract String getFormattedLabel(Object element, String value);
    
    /**
     * リストを取得する。
     * @param pageContext ページコンテキスト
     * @return リスト
     */
    protected abstract List<?> getList(PageContext pageContext);
    
    /**
     * リスト表示時に使用するフォーマットのデフォルト値を取得する。
     * @return リスト表示時に使用するフォーマットのデフォルト値
     */
    protected abstract ListFormat getDefaultListFormat();
}
