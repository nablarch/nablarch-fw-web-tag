package nablarch.common.web.tag;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.servlet.jsp.PageContext;

import nablarch.core.ThreadContext;
import nablarch.core.util.I18NUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.validation.convertor.ConversionUtil;

/**
 * 10進数のフォーマットを行うクラス。
 * @author Kiyohito Itoh
 */
public class DecimalFormatter implements ValueFormatter {

    /**
     * {@inheritDoc}
     * <pre><code>
     * 値はjava.lang.Number型又は数字の文字列を指定する。
     * 数字の文字列の場合は、言語に対応する1000の区切り文字を取り除いた後でフォーマットする。
     * パターンにはjava.text.DecimalFormatが規定している構文を指定する。
     * パターンには区切り文字"|"を使用して言語を付加することができる。
     * 言語はパターンの末尾に付加する。
     * {@link CustomTagConfig}を使用して、区切り文字"|"の変更を行うことができる。
     * 言語が指定されなかった場合は{@link ThreadContext}に設定された言語が使用される。
     * 例:
     * decimal{###,###,###.000} --> {@link ThreadContext}に設定された言語を使用し、パターンのみ指定する場合。
     * decimal{###,###,###.000|ja} --> パターンと言語を指定する場合。
     * </code></pre>
     */
    public String format(PageContext pageContext, String name, Object value, String pattern) {

        // パターンに言語が含まれていたら言語を抜き出す。
        Locale language = null;
        if (StringUtil.hasValue(pattern)) {
            int languageSeparatorIndex = pattern.indexOf(TagUtil.getCustomTagConfig().getPatternSeparator());
            if (languageSeparatorIndex != -1) {
                String langStr = pattern.substring(languageSeparatorIndex + 1).trim();
                if (StringUtil.hasValue(langStr)) {
                    language = I18NUtil.createLocale(langStr);
                }
                pattern = pattern.substring(0, languageSeparatorIndex).trim();
            }
        }
        if (language == null) {
            language = ThreadContext.getLanguage() != null ? ThreadContext.getLanguage() : Locale.getDefault();
        }

        // 値に文字列が指定された場合はBigDecimalに変換する。
        Number number;
        if (value instanceof String) {
            String strValue = (String) value;
            if (StringUtil.isNullOrEmpty(strValue)) {
                return strValue;
            }
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(language);
            number = new BigDecimal(ConversionUtil.convertToNumber(strValue, symbols));
        } else {
            number = (Number) value;
        }

        return I18NUtil.formatDecimal(number, pattern, language);
    }
}
