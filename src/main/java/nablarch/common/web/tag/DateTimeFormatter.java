package nablarch.common.web.tag;

import java.util.Date;

import javax.servlet.jsp.PageContext;

import nablarch.core.ThreadContext; // SUPPRESS CHECKSTYLE Javadocで使うから。

/**
 * 日時のフォーマットを行うクラス。
 * @author Kiyohito Itoh
 */
public class DateTimeFormatter implements ValueFormatter {

    /**
     * {@inheritDoc}
     * <pre><code>
     * 日時のフォーマット。
     * 値はjava.util.Date型を指定する。
     * パターンにはjava.text.SimpleDateFormatが規定している構文を指定する。
     * パターンには区切り文字"|"を使用してロケールおよびタイムゾーンを付加することができる。
     * ロケールおよびタイムゾーンはこの順番でパターンの末尾に付加する。
     * {@link CustomTagConfig}を使用して、パターンのデフォルト値の設定と、
     * 区切り文字"|"の変更を行うことができる。
     * タイムゾーンが指定されなかった場合は{@link ThreadContext}に設定されたタイムゾーンが使用される。
     * 例:
     * dateTime --> デフォルトのパターンと{@link ThreadContext}に設定されたロケールおよびタイムゾーンを使用する場合。
     * dateTime{|ja|Asia/Tokyo} --> デフォルトのパターンを使用し、ロケールおよびタイムゾーンのみ指定する場合。
     * dateTime{yyyy年MMM月d日(E) a hh:mm|ja|America/New_York} --> パターン、ロケール、タイムゾーンを全て指定する場合。
     * dateTime{yy/MM/dd HH:mm:ss} --> {@link ThreadContext}に設定されたロケールとタイムゾーンを使用し、パターンのみ指定する場合。
     * dateTime{yy/MM/dd HH:mm:ss||Asia/Tokyo} --> パターンとタイムゾーンを指定する場合。
     * </code></pre>
     */
    public String format(PageContext pageContext, String name, Object value, String pattern) {
        return TagUtil.formatDateTime((Date) value, pattern);
    }    
}
