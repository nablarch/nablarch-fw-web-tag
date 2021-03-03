package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="datetime-local")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class DatetimeLocalTag extends TextTag {
    @Override
    protected String getTagName() {
        return "datetime-local";
    }
}
