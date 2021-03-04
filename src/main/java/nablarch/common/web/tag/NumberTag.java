package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="number")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class NumberTag extends TextTag {
    @Override
    protected String getTagName() {
        return "number";
    }
}
