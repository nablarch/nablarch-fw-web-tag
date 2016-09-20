package nablarch.common.web.tag;

import java.util.List;

import nablarch.common.code.CodeUtil;

/**
 * 入力データ復元とHTMLエスケープを行うコード値の単一入力項目(inputタグ(type="checkbox"))を出力するクラス。
 * @author Kiyohito Itoh
 */
public class CodeCheckboxTag extends CheckboxTagSupport {
    
    /** コードID */
    private String codeId;
    
    /** 取得するオプション名称のカラム名 */
    private String optionColumnName;
    
    /** ラベルを整形するパターン */
    private String labelPattern;
    
    /** チェックなしの場合に使用するコード値 */
    private String offCodeValue;
    
    /**
     * コードIDを設定する。
     * @param codeId コードID
     */
    public void setCodeId(String codeId) {
        this.codeId = codeId;
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
    
    /**
     * チェックなしの場合に使用するコード値を設定する。
     * @param offCodeValue チェックなしの場合に使用するコード値
     */
    public void setOffCodeValue(String offCodeValue) {
        this.offCodeValue = offCodeValue;
    }

    @Override
    protected String getOffLabel() {
        return TagUtil.getCodeLabel(labelPattern, codeId, getOffValue(), optionColumnName);
    }
    
    /**
     * {@inheritDoc}<br>
     * offCodeValue属性が指定されない場合は、
     * codeId属性の値からチェックなしの場合に使用するコード値を検索する。
     * 検索結果が2件、かつ1件がvalue属性の値である場合は、
     * 残りの1件をチェックなしのコード値として使用する。
     * 検索で見つからない場合は、デフォルト値を返す。
     */
    protected String getOffValue() {
        
        if (offCodeValue != null) {
            return offCodeValue;
        }
        
        List<String> values = CodeUtil.getValues(codeId);
        String value = getValue();
        if (values.size() == 2 && values.contains(value)) {
            return values.get(0).equals(value) ? values.get(1) : values.get(0);
        }
        return super.getOffValue();
    }
    
    @Override
    protected String getLabel() {
        return TagUtil.getCodeLabel(labelPattern, codeId, getValue(), optionColumnName);
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "codeCheckbox";
    }
}
