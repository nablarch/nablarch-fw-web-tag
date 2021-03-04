package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="tel")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class TelTag extends TextTag {
    @Override
    protected String getTagName() {
        return "tel";
    }
}
