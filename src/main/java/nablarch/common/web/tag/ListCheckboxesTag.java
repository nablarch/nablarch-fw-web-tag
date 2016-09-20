package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行う選択項目(inputタグ(type="checkbox"))を出力するクラス。
 * @author Kiyohito Itoh
 */
public class ListCheckboxesTag extends ListCheckedInputsTagSupport {
    
    @Override
    protected String getType() {
        return "checkbox";
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "checkboxes";
    }
}
