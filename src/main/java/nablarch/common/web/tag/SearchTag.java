package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="search")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class SearchTag extends TextTag {
    @Override
    protected String getTagName() {
        return "search";
    }
}
