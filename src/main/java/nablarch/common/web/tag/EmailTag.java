package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="email")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class EmailTag extends TextTag {
    @Override
    protected String getTagName() {
        return "email";
    }
}
