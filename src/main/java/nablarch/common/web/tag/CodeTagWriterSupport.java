package nablarch.common.web.tag;

import java.util.List;

import javax.servlet.jsp.PageContext;

import nablarch.common.code.CodeUtil;


/**
 * コード値の選択項目を出力するタグの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class CodeTagWriterSupport extends MultivaluedInputTagWriterSupport {

    /** コードID */
    private String codeId;
    
    /** パターン */
    private String pattern;
    
    /** 取得するオプション名称のカラム名 */
    private String optionColumnName;
    
    /** ラベルを整形するパターン */
    private String labelPattern;
    
    /**
     * コードIDを設定する。
     * @param codeId コードID
     */
    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    /**
     * 使用するパターンのカラム名を設定する。
     * @param pattern 使用するパターンのカラム名
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 取得するオプション名称のカラム名を設定する。
     * @param optionColumnName 取得するオプション名称のカラム名
     */
    public void setOptionColumnName(String optionColumnName) {
        this.optionColumnName = optionColumnName;
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
        this.labelPattern = labelPattern;
    }

    @Override
    protected String getFormattedLabel(Object element, String value) {
        return TagUtil.getCodeLabel(labelPattern, codeId, value, optionColumnName);
    }
    
    @Override
    protected List<?> getList(PageContext pageContext) {
        return pattern == null ? CodeUtil.getValues(codeId) : CodeUtil.getValues(codeId, pattern);
    }

    @Override
    protected String getValue(Object element) {
        return (String) element;
    }

    @Override
    protected ListFormat getDefaultListFormat() {
        return TagUtil.getCustomTagConfig().getCodeListFormat();
    }
}
