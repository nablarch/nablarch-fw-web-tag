package nablarch.common.web.tag;

import java.util.List;

import javax.servlet.jsp.PageContext;

import nablarch.core.util.ObjectUtil;
import nablarch.core.util.StringUtil;

/**
 * リストの選択項目を出力するタグの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class ListTagWriterSupport extends MultivaluedInputTagWriterSupport {
    
    /** リストを取得するための名前 */
    private String listName;
    
    /** リスト要素から値を取得するためのプロパティ名 */
    private String elementValueProperty;
    
    /** リスト要素からラベルを取得するためのプロパティ名 */
    private String elementLabelProperty;
    
    /** リスト要素のラベルを整形するためのパターン */
    private String elementLabelPattern;
    
    /**
     * リストを取得するための名前を設定する。
     * @param listName リストを取得するための名前
     */
    public void setListName(String listName) {
        this.listName = listName;
    }

    /**
     * リスト要素から値を取得するためのプロパティ名を設定する。
     * @param elementValueProperty リスト要素から値を取得するためのプロパティ名
     */
    public void setElementValueProperty(String elementValueProperty) {
        this.elementValueProperty = elementValueProperty;
    }

    /**
     * リスト要素からラベルを取得するためのプロパティ名を設定する。
     * @param elementLabelProperty リスト要素からラベルを取得するためのプロパティ名
     */
    public void setElementLabelProperty(String elementLabelProperty) {
        this.elementLabelProperty = elementLabelProperty;
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
        this.elementLabelPattern = elementLabelPattern;
    }

    /**
     * 値を取得する。
     * @param element リスト要素
     * @return 値
     */
    protected String getValue(Object element) {
        Object valueObj = ObjectUtil.getProperty(element, elementValueProperty);
        return valueObj != null ? StringUtil.toString(valueObj) : "";
    }
    
    /**
     * 整形済みのラベルを取得する。
     * @param element リスト要素
     * @param value リスト要素の値
     * @return 整形済みのラベル
     */
    protected String getFormattedLabel(Object element, String value) {
        Object labelObj = ObjectUtil.getProperty(element, elementLabelProperty);
        String label = labelObj != null ? StringUtil.toString(labelObj) : "";
        String useLabelPattern = elementLabelPattern != null ? elementLabelPattern : TagUtil.getCustomTagConfig().getElementLabelPattern();
        return useLabelPattern.replace("$LABEL$", label).replace("$VALUE$", value);
    }
    
    /**
     * リストを取得する。
     * @param pageContext ページコンテキスト
     * @return リスト
     */
    @SuppressWarnings("unchecked")
    protected List<Object> getList(PageContext pageContext) {
        return (List<Object>) TagUtil.getMultipleValues(pageContext, listName);
    }
    
    /**
     * {@inheritDoc}
     */
    protected ListFormat getDefaultListFormat() {
        return TagUtil.getCustomTagConfig().getListFormat();
    }
}
