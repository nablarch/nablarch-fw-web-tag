package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="range")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class RangeTag extends TextTag {
    @Override
    protected String getTagName() {
        return "range";
    }
}
