
package nablarch.common.web.tag;


import nablarch.core.util.DateUtil;
import nablarch.common.web.tag.TagUtil.PatternHolder;
import nablarch.core.ThreadContext;
import nablarch.core.util.StringUtil;

import jakarta.servlet.jsp.PageContext;
import java.util.Date;
import java.util.Locale;

/**
 * 日付文字列をフォーマットするクラス。
 * 内部でのみ使用する。
 *
 * @author T.Kawasaki
 */
abstract class AbstractDateStringFormatter implements ValueFormatter {

    /**
     * デフォルトの出力パターンを取得する。
     *
     * @return デフォルトの出力パターン
     */
    abstract String getDefaultPatternToOut();

    /**
     * 元となる日付文字列の形式を取得する。
     *
     * @return 日付文字列の形式
     */
    abstract String getSrcPattern();

    /** {@inheritDoc} */
    public String format(PageContext pageContext, String name, Object value, String pattern) {
        String strValue = (String) value;
        if (StringUtil.isNullOrEmpty(strValue)) {
            return strValue;
        }
        
        if (StringUtil.isNullOrEmpty(pattern)) {
            pattern = getDefaultPatternToOut();
        }

        String separator = TagUtil.getCustomTagConfig().getPatternSeparator();
        PatternHolder patternHolder = new PatternHolder(pattern, separator, "");

        String formatPattern = patternHolder.getFormat();

        String srcPattern  = (formatPattern.length() > 0)
                           ? formatPattern
                           : getSrcPattern();

        if (TagUtil.getValue(pageContext, name, false) == null) { // 入力値の場合
            // 指定されたパターンでパース
            Locale locale = patternHolder.getLocale() == null ? getLocale() : patternHolder.getLocale();
            Date date = DateUtil.getParsedDate(strValue, srcPattern, locale);
            if (date == null) {
                // 日付の区切り文字を取り除いたパターンでパース
                date = DateUtil.getParsedDate(strValue, DateUtil.getNumbersOnlyFormat(srcPattern), locale);
            }
            if (date == null) {
                // 既定のパターンでパース
                date = DateUtil.getParsedDate(strValue, getSrcPattern());
            }
            if (date == null) {
                throw new IllegalArgumentException(
                    String.format("failed to parse string as date. string = [%s], format = [%s]",
                                  strValue, srcPattern));
            }
            return TagUtil.formatDate(date, pattern);
        }
        return TagUtil.formatDate(DateUtil.getParsedDate(strValue, getSrcPattern()), pattern);
    }

    /**
     * {@link ThreadContext}から言語を取得する。
     * <p/>
     * {@link ThreadContext}に言語が設定されていない場合は、
     * {@link Locale#getDefault()}で取得した言語を返却する。
     *
     * @return 言語
     */
    private Locale getLocale() {
        return ThreadContext.getLanguage() != null ? ThreadContext.getLanguage() : Locale.getDefault();
    }
}
