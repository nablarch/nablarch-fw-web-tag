package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="month")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class MonthTag extends TextTag {
    @Override
    protected String getTagName() {
        return "month";
    }
}
