package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="week")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class WeekTag extends TextTag {
    @Override
    protected String getTagName() {
        return "week";
    }
}
