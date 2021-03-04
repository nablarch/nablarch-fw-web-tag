package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="color")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class ColorTag extends TextTag {
    @Override
    protected String getTagName() {
        return "color";
    }
}
