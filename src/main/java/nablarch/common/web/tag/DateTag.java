package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="date")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class DateTag extends TextTag {
    @Override
    protected String getTagName() {
        return "date";
    }
}
