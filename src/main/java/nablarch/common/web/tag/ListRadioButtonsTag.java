package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行う選択項目(inputタグ(type="radio"))を出力するクラス。
 * @author Kiyohito Itoh
 */
public class ListRadioButtonsTag extends ListCheckedInputsTagSupport {

    /**
     * {@inheritDoc}
     */
    protected String getType() {
        return "radio";
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "radioButtons";
    }
}
