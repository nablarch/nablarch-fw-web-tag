package nablarch.common.web.tag;

/**
 * 年月のフォーマットを行うクラス。
 * <pre>
 * 年月のフォーマット。
 * 値はyyyyMM形式またはパターン形式の文字列を指定する。
 * パターンにはjava.text.SimpleDateFormatが規定している構文を指定する。
 * パターン文字には、y(年)、M(月)のみ指定可能。
 * {@link CustomTagConfig}を使用してパターンのデフォルト値を設定することができる。
 * 例:
 * yyyymm --> デフォルトのパターンを使用する場合。
 * yyyymm{yyyy/MM} --> パターンを指定する場合。
 * </pre>
 *
 * @author T.Kawasaki
 */
public class YYYYMMFormatter extends AbstractDateStringFormatter {

    /** {@inheritDoc} */
    @Override
    String getSrcPattern() {
        return "yyyyMM";
    }

    /** {@inheritDoc} */
    @Override
    String getDefaultPatternToOut() {
        return TagUtil.getCustomTagConfig().getYyyymmPattern();
    }
}
