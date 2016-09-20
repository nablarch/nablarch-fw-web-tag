package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うコード値の選択項目(inputタグ(type="checkbox"))を出力するクラス。
 * @author Kiyohito Itoh
 */
public class CodeCheckboxesTag extends CodeCheckedInputsTagSupport {

    @Override
    protected String getType() {
        return "checkbox";
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "codeCheckboxes";
    }
}
