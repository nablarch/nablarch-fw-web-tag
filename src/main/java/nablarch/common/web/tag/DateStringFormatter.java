package nablarch.common.web.tag;

/**
 * 日付文字列のフォーマットを行うクラス。
 * <p/>
 * {@link YYYYMMDDFormatter}に置き換わりました。
 * 
 * {@link YYYYMMDDFormatter}を継承し、
 * {@link AbstractDateStringFormatter#getDefaultPatternToOut()}以外は
 * オーバーライドしないため、処理内容は{@link YYYYMMDDFormatter}と同じ。
 * 
 * @author Kiyohito Itoh
 * @deprecated {@link YYYYMMDDFormatter}に置き換わりました。
 */
public class DateStringFormatter extends YYYYMMDDFormatter {
    /**
     * {@inheritDoc}
     * <p/>
     * {@link nablarch.common.web.tag.CustomTagConfig#getDatePattern()}を返す。
     */
    @Override
    String getDefaultPatternToOut() {
        return TagUtil.getCustomTagConfig().getDatePattern();
    }
}
