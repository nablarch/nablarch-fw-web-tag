package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うコード値の選択項目(inputタグ(type="radio"))を出力するクラス。
 * @author Kiyohito Itoh
 */
public class CodeRadioButtonsTag extends CodeCheckedInputsTagSupport {

    @Override
    protected String getType() {
        return "radio";
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "codeRadioButtons";
    }
}
