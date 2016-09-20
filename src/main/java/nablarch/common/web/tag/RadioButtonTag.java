package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="radio")を出力するクラス。<br>
 * n:radioButtonsタグで表示できないレイアウト時に使用する。
 * @author Kiyohito Itoh
 */
public class RadioButtonTag extends CheckedInputTagSupport {

    /**
     * {@inheritDoc}
     */
    protected String getType() {
        return "radio";
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "radioButton";
    }
}
