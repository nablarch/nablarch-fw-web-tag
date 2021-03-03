package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="time")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class TimeTag extends TextTag {
    @Override
    protected String getTagName() {
        return "time";
    }
}
