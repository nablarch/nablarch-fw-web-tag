package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="url")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class UrlTag extends TextTag {
    @Override
    protected String getTagName() {
        return "url";
    }
}
