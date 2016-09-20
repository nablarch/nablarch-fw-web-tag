package nablarch.common.web.tag;

/**
 * 年月日のフォーマットを行うクラス。
 * @author Kiyohito Itoh
 */
public class YYYYMMDDFormatter extends AbstractDateStringFormatter {

    /** {@inheritDoc} */
    @Override
    String getDefaultPatternToOut() {
        return TagUtil.getCustomTagConfig().getYyyymmddPattern();
    }

    /** {@inheritDoc} */
    @Override
    String getSrcPattern() {
        return "yyyyMMdd";
    }
}
