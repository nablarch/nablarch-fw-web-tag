package nablarch.common.web.tag;    // SUPPRESS CHECKSTYLE カスタムタグの共通処理を局所化するため。

import static nablarch.fw.ExecutionContext.FW_PREFIX;
import static nablarch.fw.ExecutionContext.THROWN_APPLICATION_EXCEPTION_KEY;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import nablarch.common.code.CodeUtil;
import nablarch.common.web.HtmlTagUtil;
import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.core.ThreadContext;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.DateUtil;
import nablarch.core.util.FormatSpec;
import nablarch.core.util.I18NUtil;
import nablarch.core.util.ObjectUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.core.validation.ValidationResultMessage;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.handler.KeitaiAccessHandler;
import nablarch.fw.web.handler.SecureHandler;

/**
 * カスタムタグの作成を助けるユーティリティ。
 * @author Kiyohito Itoh
 */
public final class TagUtil {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(TagUtil.class);

    /** NablarchTagHandler で展開する際に使用する、許容する値のリストを保持するキー名のプレフィクス */
    public static final String PARAM_VALUES_KEY_PREFIX = ExecutionContext.FW_PREFIX + "expand_param_values_";

    /** NablarchTagHandler で展開する属性名のリストのキー名 */
    public static final String VAR_NAMES_KEY = ExecutionContext.FW_PREFIX + "expand_params";

    /** NablarchTagHandler で展開する際に使用する、許容する名称のリストを保持するキー名のプレフィクス */
    public static final String PARAM_NAMES_KEY_PREFIX = ExecutionContext.FW_PREFIX + "expand_param_names_";

    /** 隠蔽コンストラクタ */
    private TagUtil() {
    }

    /** スペースのパターン */
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");

    /** ドットのパターン */
    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

    /** カンマのパターン */
    private static final Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");

    /**
     * カンマ区切りの値を分割して返す。
     * @param value カンマ区切りの値
     * @return カンマ区切りの値を分割した値
     */
    public static Set<String> getCommaSeparatedValue(String value) {
        return new HashSet<String>(Arrays.asList(COMMA_PATTERN.split(value)));
    }

    /**
     * カンマ区切りの値を分割して返す。
     * @param value カンマ区切りの値
     * @return カンマ区切りの値を分割した値
     */
    public static List<String> getCommaSeparatedValueAsList(String value) {
        return Arrays.asList(COMMA_PATTERN.split(value));
    }

    /** カスタムタグのデフォルト値をリポジトリから取得する際に使用する名前 */
    private static final String CUSTOM_TAG_CONFIG_NAME = "customTagConfig";

    /** アプリケーションでカスタムタグのデフォルト値を設定していない場合に使用するデフォルト値 */
    private static final CustomTagConfig DEFAULT_CONFIG = new CustomTagConfig();

    /**
     * カスタムタグのデフォルト値を取得する。
     * @return カスタムタグのデフォルト値
     */
    public static CustomTagConfig getCustomTagConfig() {
        CustomTagConfig config = (CustomTagConfig) SystemRepository.getObject(CUSTOM_TAG_CONFIG_NAME);
        return config != null ? config : DEFAULT_CONFIG;
    }

    // タグの出力をサポートするメソッド

    /**
     * 作成したタグを出力する。
     *
     * @param pageContext ページコンテキスト
     * @param content コンテンツ
     * @throws JspException JSP例外
     */
    @Published(tag = "architect")
    public static void print(PageContext pageContext, String content) throws JspException {
        try {
            pageContext.getOut().print(content);
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    /**
     * 開始タグを作成する。
     * @param tagName タグ名
     * @param attributes 属性
     * @return オープンタグ
     */
    public static String createStartTag(String tagName, HtmlAttributes attributes) {
        return attributes.isEmpty() ? String.format("<%s>", tagName) : String.format("<%s %s>", tagName, attributes.toHTML(tagName));
    }

    /**
     * 終了タグを作成する。
     * @param tagName タグ名
     * @return 終了タグ
     */
    public static String createEndTag(String tagName) {
        return String.format("</%s>", tagName);
    }

    /**
     * ボディを持つタグを作成する。
     * @param tagName タグ名
     * @param attributes 属性
     * @param body ボディ
     * @return ボディを持つタグ
     */
    public static String createTagWithBody(String tagName, HtmlAttributes attributes, String body) {
        return new StringBuilder()
                        .append(createStartTag(tagName, attributes))
                        .append(body)
                        .append(createEndTag(tagName))
                        .toString();
    }

    /**
     * ボディを持たないタグを作成する。
     * @param tagName タグ名
     * @param attributes 属性
     * @return ボディを持たないタグ
     */
    public static String createTagWithoutBody(String tagName, HtmlAttributes attributes) {
        return attributes.isEmpty() ? String.format("<%s />", tagName) : String.format("<%s %s />", tagName, attributes.toHTML(
                tagName));
    }

    /**
     * hiddenタグを作成する。
     * @param name name属性
     * @param value value属性
     * @return hiddenタグ
     */
    public static String createHiddenTag(String name, String value) {
        HtmlAttributes attributes = new HtmlAttributes();
        attributes.put(HtmlAttribute.TYPE, "hidden");
        attributes.put(HtmlAttribute.NAME, name);
        attributes.put(HtmlAttribute.VALUE, value);
        return createTagWithoutBody("input", attributes);
    }

    /**
     * labelタグを作成する。<br>
     *
     * @param type labelタグが対応するinputタグのtype属性
     * @param content labelタグの内容
     * @param forAttribute for属性。未指定の場合はnull
     * @param cssClass CSSクラス名
     * @return labelタグ
     */
    public static String createLabelTag(String type, String content, String forAttribute, String cssClass) {
        HtmlAttributes attributes = new HtmlAttributes();
        attributes.put(HtmlAttribute.FOR, forAttribute);
        if (!StringUtil.isNullOrEmpty(cssClass)) {
            attributes.put(HtmlAttribute.CLASS, cssClass);
        }
        return createTagWithBody("label", attributes, content);
    }

    /**
     * JavaScriptを含めるscriptタグを作成する。
     * <p/>
     * scriptタグのtype属性に"text/javascript"を指定する。
     * さらに、{@link CustomTagConfig#getScriptBodyPrefix()}と{@link CustomTagConfig#getScriptBodySuffix()}を
     * 指定されたJavaScriptの前後に付加する。
     * <p/>
     * CustomTagConfigのデフォルト値を使用する場合のscriptタグの作成例を下記に示す。
     *
     * <pre>
     * {@literal
     * <script type="text/javascript">
     * //<![CDATA[
     *     (ここに指定されたJavaScriptがくる)
     * //]]>
     * </script>
     * }
     * </pre>
     *
     * <p>
     * また、セキュアハンドラでnonceが生成されていた場合は、scriptタグにnonce属性を自動で付加する。
     * </p>
     *
     * <pre>
     * {@literal
     * <script type="text/javascript" nonce="[セキュアハンドラで生成したnonce]">
     * //<![CDATA[
     *     (ここに指定されたJavaScriptがくる)
     * //]]>
     * </script>
     * }
     * </pre>
     *
     * @param pageContext ページコンテキスト
     * @param javaScript scriptタグのボディに指定するJavaScript
     * @return scriptタグ
     */
    public static String createScriptTag(PageContext pageContext, String javaScript) {

        CustomTagConfig config = getCustomTagConfig();
        String ls = config.getLineSeparator();

        HtmlAttributes attributes = new HtmlAttributes();
        attributes.put(HtmlAttribute.TYPE, "text/javascript");

        if (hasCspNonce(pageContext)) {
            attributes.put(HtmlAttribute.NONCE, getCspNonce(pageContext));
        }

        return new StringBuilder()
                .append(createStartTag("script", attributes))
                .append(ls)
                .append(config.getScriptBodyPrefix())
                .append(ls)
                .append(javaScript)
                .append(ls)
                .append(config.getScriptBodySuffix())
                .append(ls)
                .append(createEndTag("script")).toString();
    }

    /**
     * リクエストスコープにCSP対応用のnonceが保存されているか否か確認する。
     * nonceが保存されている場合、{@code true}を返却する。
     *
     * @param pageContext ページコンテキスト
     * @return リクエストスコープにCSP対応用のnonceが保存されている場合{@code true}
     */
    public static boolean hasCspNonce(PageContext pageContext) {
        return StringUtil.hasValue(getCspNonce(pageContext));
    }

    /**
     * リクエストスコープに格納されているnonceを取得する。取得できなかった場合は{@code null}を返却する
     *
     * @param pageContext ページコンテキスト
     * @return リクエストスコープに格納されているnonce
     */
    public static String getCspNonce(PageContext pageContext) {
        return (String) getSingleValueOnScope(pageContext, SecureHandler.CSP_NONCE_KEY);
    }

    /**
     * 画面上のタグのユニークな名前を生成する。<br>
     * 値のフォーマット："nablarch_<タグを識別するキー><連番>"
     * @param pageContext ページコンテキスト
     * @param tagKey タグを識別するキー
     * @return 画面上のタグのユニークな名前
     */
    public static String generateUniqueName(PageContext pageContext, String tagKey) {
        return FW_PREFIX + tagKey + getOrderOfAppearance(pageContext, FW_PREFIX + tagKey);
    }

    /**
     * 画面上のタグの出現順を取得する。
     * @param pageContext ページコンテキスト
     * @param tagKey タグを識別するキー
     * @return 画面上のタグの出現順
     */
    public static int getOrderOfAppearance(PageContext pageContext, String tagKey) {
        AtomicInteger count = (AtomicInteger) pageContext.getAttribute(tagKey, PageContext.REQUEST_SCOPE);
        if (count == null) {
            count = new AtomicInteger();
            pageContext.setAttribute(tagKey, count, PageContext.REQUEST_SCOPE);
        }
        return count.incrementAndGet();
    }

    // 入力フォームをサポートするメソッド
    /**
     * "データタイプ{パターン}"形式のフォーマット文字列から{@link FormatSpec}を生成する。
     * <p/>
     * パターンの付加情報を区切りセパレータには、{@link CustomTagConfig#getPatternSeparator()}を指定する。
     * @param format "データタイプ{パターン}"形式のフォーマット文字列
     * @return {@link FormatSpec}
     */
    public static FormatSpec createFormatSpec(String format) {
        return FormatSpec.valueOf(format, getCustomTagConfig().getPatternSeparator());
    }

    /**
     * 値をフォーマットする。
     * <pre><code>
     * フォーマットは"データタイプ{パターン}"形式で指定する。
     * フレームワークがデフォルトでサポートしているフォーマットを下記に示す。
     *
     * yyyymmdd:
     *   年月日のフォーマット。
     *   値はyyyyMMdd形式の文字列を指定する。
     *   パターンにはjava.text.SimpleDateFormatが規定している構文を指定する。
     *   パターン文字には、y(年)、M(月)、d(月における日)のみ指定可能。
     *   パターン文字列を省略した場合は{@link CustomTagConfig}に設定されたデフォルトのパターンを
     *   使用する。
     *
     *   また、パターンの後に区切り文字"|"を使用してフォーマットのロケールを付加することができる。
     *   ロケールを明示的に指定しない場合は、ThreadContextのロケール設定値を使用する。
     *   ThreadContextも設定されていない場合は、システムデフォルトロケール値を使用する。
     *
     *   例:
     *     yyyymmdd --> デフォルトのパターンと{@link ThreadContext}に設定されたロケールを使用する。
     *     yyyymmdd{yyyy/MMM/dd} --> 明示的にパターンを指定し、{@link ThreadContext}に設定されたロケールを使用する
     *     yyyymmdd{|ja} --> デフォルトのパターンを使用し、ロケールのみ指定する場合。
     *     yyyymmdd{yyyy年MM月d日(E)|ja} --> パターン、ロケールの両方を明示的に指定する場合。
     *
     * dateTime:
     *   値はjava.util.Date型を指定する。
     *   パターンにはjava.text.SimpleDateFormatが規定している構文を指定する。
     *   パターンには区切り文字"|"を使用してロケールおよびタイムゾーンを付加することができる。
     *   ロケールおよびタイムゾーンはこの順番でパターンの末尾に付加する。
     *   {@link CustomTagConfig}を使用して、パターンのデフォルト値の設定と、
     *   区切り文字"|"の変更を行うことができる。
     *   タイムゾーンが指定されなかった場合は{@link ThreadContext}に設定されたタイムゾーンが使用される。
     *
     *   例:
     *     dateTime --> デフォルトのパターンと{@link ThreadContext}に設定されたロケールおよびタイムゾーンを使用する場合。
     *     dateTime{|ja|Asia/Tokyo} --> デフォルトのパターンを使用し、ロケールおよびタイムゾーンのみ指定する場合。
     *     dateTime{yyyy年MMM月d日(E) a hh:mm|ja|America/New_York} --> パターン、ロケール、タイムゾーンを全て指定する場合。
     *     dateTime{yy/MM/dd HH:mm:ss} --> {@link ThreadContext}に設定されたロケールとタイムゾーンを使用し、パターンのみ指定する場合。
     *     dateTime{yy/MM/dd HH:mm:ss||Asia/Tokyo} --> パターンとタイムゾーンを指定する場合。
     *
     * decimal:
     *   10進数のフォーマット。
     *   値はjava.lang.Number型又は数字の文字列を指定する。
     *   数字の文字列の場合は、カンマを取り除いた後でフォーマットする。
     *   パターンにはjava.text.DecimalFormatが規定している構文を指定する。
     *   パターンには区切り文字"|"を使用して言語を指定することができる。
     *   言語はパターンの末尾に付加する。
     *   {@link CustomTagConfig}を使用して、区切り文字"|"の変更を行うことができる。
     *   言語が指定されなかった場合は{@link nablarch.core.ThreadContext}に設定された言語が使用される。
     *   例:
     *   decimal{###,###,###.000} --> {@link nablarch.core.ThreadContext}に設定された言語を使用し、パターンのみ指定する場合。
     *   decimal{###,###,###.000|ja} --> パターンと言語を指定する場合。
     *
     * フォーマットは、{@link ValueFormatter}を実装したクラスが行う。
     * 実装したクラスをリポジトリに登録することでフォーマットを変更することができる。
     * リポジトリへの登録は、Map型でデータタイプ名をキーに、{@link ValueFormatter}を実装したクラスを値に指定する。
     * フレームワークがデフォルトでサポートしているフォーマットに対する設定例を下記に示す。
     * フォーマッタのマップは、"valueFormatters"という名前でリポジトリに登録する。
     *
     * {@literal
     * <map name="valueFormatters">
     *     <entry key="yyyymmdd">
     *         <value-component class="nablarch.common.web.tag.YYYYMMDDFormatter" />
     *     </entry>
     *     <entry key="dateTime">
     *         <value-component class="nablarch.common.web.tag.DateTimeFormatter" />
     *     </entry>
     *     <entry key="decimal">
     *         <value-component class="nablarch.common.web.tag.DecimalFormatter" />
     *     </entry>
     * </map>
     * }
     *
     * リポジトリにフォーマッタが登録されていない場合は、フレームワークがデフォルトでサポートしている
     * フォーマットを使用する。
     *
     * フォーマットで例外が発生した場合は、指定された値のtoStringメソッドを呼び出した結果を返す。
     *
     * </code></pre>
     * @param pageContext ページコンテキスト
     * @param name name属性の値
     * @param formatSpec valueFormat属性の値
     * @param value 値
     * @return フォーマット済みの値
     */
    public static String formatValue(PageContext pageContext, String name, FormatSpec formatSpec, Object value) {

        if (value == null) {
            return null;
        }
        if (formatSpec == null) {
            throw new IllegalArgumentException("formatSpec must not be null.");
        }

        ValueFormatter formatter = getValueFormatter(formatSpec.getDataType());
        if (formatter == null) {
            throw new IllegalArgumentException(
                String.format("valueFormat attribute was invalid. "
                            + "expected = [<data type>{<pattern>}] "
                            + "actual = [%s], data type = [%s]",
                            formatSpec.getFormat(), formatSpec.getDataType()));
        }
        try {
            return formatter.format(pageContext, name, value, formatSpec.getPattern());
        } catch (RuntimeException e) {
            // フォーマットの指定不正などで例外が発生するケース。
            // フォーマットをユーザに選択させるようなケースでは、
            // 例外が発生するケースが有るため障害とはせずに元の値をそのまま返却する。
            return StringUtil.toString(value);
        }
    }

    /**
     * 日付をフォーマットする。
     * <pre><code>
     * 値はyyyyMMdd形式またはパターン形式の文字列を指定する。
     * パターンにはjava.text.SimpleDateFormatが規定している構文を指定する。
     * パターン文字には、y(年)、M(月)、d(月における日)のみ指定可能。
     * パターン文字列を省略した場合は カスタムタグのデフォルト値の設定 に設定されたデフォルトのパターンが使用される。
     * また、パターンの後に区切り文字”|”を使用してフォーマットのロケールを付加することができる。
     * ロケールを明示的に指定しない場合は、{@link ThreadContext}のロケール設定値を使用する。
     * 例:
     * null もしくは 空文字 --> デフォルトのパターンと{@link ThreadContext}に設定されたロケールを使用する場合。
     * "yyyymmdd" --> デフォルトのパターンと{@link ThreadContext}に設定されたロケールを使用する。
     * "yyyymmdd{yyyy/MM/dd}" --> 明示的に指定されたパターンと、{@link ThreadContext}に設定されたロケールを使用する。
     * "yyyymmdd{|ja}" --> デフォルトのパターンを使用し、ロケールのみ指定する場合。
     * "yyyymmdd{yyyy年MM月d日|ja}" --> パターン、ロケールの両方を明示的に指定する場合。
     * </code></pre>
     * @param date 日付
     * @param pattern フォーマット書式文字列
     * @return フォーマット済み文字
     */
    public static String formatDate(Date date, String pattern) {
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        PatternHolder patternHolder = new PatternHolder(pattern, config.getPatternSeparator(), config.getDatePattern());
        if (patternHolder.locale != null) {
            return DateUtil.formatDate(date, patternHolder.format, patternHolder.locale);
        } else {
            return DateUtil.formatDate(date, patternHolder.format);
        }
    }

    /**
     * 日時をフォーマットする。
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
     * null もしくは 空文字 --> デフォルトのパターンと{@link ThreadContext}に設定されたロケールおよびタイムゾーンを使用する場合。
     * "|ja|Asia/Tokyo" --> デフォルトのパターンを使用し、ロケールおよびタイムゾーンのみ指定する場合。
     * "yyyy年MMM月d日(E) a hh:mm|ja|America/New_York" --> パターン、ロケール、タイムゾーンを全て指定する場合。
     * "yy/MM/dd HH:mm:ss" --> {@link ThreadContext}に設定されたロケールとタイムゾーンを使用し、パターンのみ指定する場合。
     * "yy/MM/dd HH:mm:ss||Asia/Tokyo" --> パターンとタイムゾーンを指定する場合。
     * </code></pre>
     * @param date  日時
     * @param pattern フォーマット書式文字列
     * @return フォーマット済み文字
     */
    public static String formatDateTime(Date date, String pattern) {
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        PatternHolder patternHolder = new PatternHolder(pattern, config.getPatternSeparator(), config.getDateTimePattern());
        if (patternHolder.locale != null && patternHolder.timeZone != null) {
            return I18NUtil.formatDateTime(date, patternHolder.format, patternHolder.locale, patternHolder.timeZone);
        } else if (patternHolder.locale != null) {
            return I18NUtil.formatDateTime(date, patternHolder.format, patternHolder.locale);
        } else {
            return I18NUtil.formatDateTime(date, patternHolder.format);
        }
    }

    /**
     * valueFormat属性に指定されるpattern文字列を保持するクラス。
     * @author Kiyohito Itoh
     */
    static final class PatternHolder {
        /** フォーマット */
        private final String format;
        /** ロケール */
        private Locale locale;
        /** タイムゾーン */
        private TimeZone timeZone;
        /**
         * コンストラクタ
         * @param pattern valueFormat属性に指定されるpattern文字列
         * @param patternSeparator pattern文字列にロケールとタイムゾーンを含める場合に使用する区切り文字
         * @param defaultFormat pattern文字列にフォーマットが含まれない場合に使用するデフォルトフォーマット
         */
        PatternHolder(String pattern, String patternSeparator, String defaultFormat) {

            // ロケールとタイムゾーンの取得
            List<String> opts = new ArrayList<String>();
            if (StringUtil.hasValue(pattern)) {
                int separatorIndex = pattern.lastIndexOf(patternSeparator);
                if (separatorIndex != -1) {
                    opts.add(0, pattern.substring(separatorIndex + 1).trim());
                    pattern = pattern.substring(0, separatorIndex).trim();
                }
                separatorIndex = pattern.lastIndexOf(patternSeparator);
                if (StringUtil.hasValue(pattern) && separatorIndex != -1) {
                    opts.add(0, pattern.substring(separatorIndex + 1).trim());
                    pattern = pattern.substring(0, separatorIndex).trim();
                }
            }

            format = StringUtil.hasValue(pattern) ? pattern : defaultFormat;
            locale = extractLocale(opts);
            timeZone = extractTimezone(opts);
        }

        /**
         * フォーマットを取得する。
         * @return フォーマット
         */
        public String getFormat() {
            return format;
        }

        /**
         * ロケールを取得する。
         * @return ロケール
         */
        public Locale getLocale() {
            return locale;
        }

        /**
         * タイムゾーンを取得する。
         * @return タイムゾーン
         */
        public TimeZone getTimeZone() {
            return timeZone;
        }

        /**
         * このパターン情報が使用するロケールを抽出する。
         *
         * @param opts オプション情報
         * @return ロケール
         */
        private Locale extractLocale(final List<String> opts) {
            if (opts.isEmpty()) {
                return null;
            }
            
            if (StringUtil.hasValue(opts.get(0))) {
                return I18NUtil.createLocale(opts.get(0));
            } else {
                return ThreadContext.getLanguage() != null ? ThreadContext.getLanguage() : Locale.getDefault();
            }
        }

        /**
         * このパターン情報が使用するタイムゾーンを抽出する。
         *
         * @param opts オプション情報
         * @return タイムゾーン
         */
        private TimeZone extractTimezone(final List<String> opts) {
            if (opts.size() <= 1) {
                return null;
            }
            if (StringUtil.hasValue(opts.get(1))) {
                return TimeZone.getTimeZone(opts.get(1));
            } else {
                return ThreadContext.getTimeZone() != null ? ThreadContext.getTimeZone() : TimeZone.getDefault();
            }
        }
    }

    /** フォーマッタをリポジトリから取得する際に使用する名前 */
    private static final String VALUE_FORMATTERS_NAME = "valueFormatters";

    /**
     * データ型に応じたフォーマッタを取得する。
     * @param dataType データタイプ
     * @return フォーマッタ。見つからない場合はnull
     */
    private static ValueFormatter getValueFormatter(String dataType) {
        @SuppressWarnings("unchecked")
        Map<String, ValueFormatter> formatters = (Map<String, ValueFormatter>) SystemRepository.getObject(VALUE_FORMATTERS_NAME);
        if (formatters == null) {
            formatters = DEFAULT_VALUE_FORMATTERS;
        }
        return formatters.get(dataType);
    }

    /** デフォルトのフォーマッタ */
    private static final Map<String, ValueFormatter> DEFAULT_VALUE_FORMATTERS;

    static {
        Map<String, ValueFormatter> formatters = new HashMap<String, ValueFormatter>();
        formatters.put("dateString", new DateStringFormatter());
        formatters.put("yyyymmdd", new YYYYMMDDFormatter());
        formatters.put("yyyymm", new YYYYMMFormatter());
        formatters.put("dateTime", new DateTimeFormatter());
        formatters.put("decimal", new DecimalFormatter());
        DEFAULT_VALUE_FORMATTERS = formatters;
    }

    /**
     * URIを指定する属性に対して、元のURIをエンコードしたURIで上書く。<br>
     * エンコード処理は、{@link #encodeUri(PageContext, String, Boolean)}に処理を移譲する。
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param attribute 対象とする属性
     * @param secure httpsにする場合はtrue、しない場合はfalse。指定しない場合はnull
     */
    public static void overrideUriAttribute(PageContext pageContext, HtmlAttributes attributes, HtmlAttribute attribute, Boolean secure) {
        String uri = attributes.get(attribute);
        attributes.put(attribute, encodeUri(pageContext, uri, secure));
    }

    /**
     * URIの指定方法に応じてURIを組み立て、エンコードしたURIを返す。
     * <pre>
     * URIは下記のいずれかの方法で指定する。
     *
     * 絶対URL:
     *   http又はhttpsから始まるパスを指定する。
     *   他システム連携などでアプリケーションとホストが異なるURIを指定する場合に使用する。
     *   指定されたパスをそのまま使用する。
     *
     * コンテキストからの相対パス
     *   /(スラッシュ)から始まるパスを指定する。
     *   アプリケーション内のパスを指定する場合に使用する。
     *   指定されたパスの先頭にコンテキストパスを付加して使用する。
     *
     * 現在のパスからの相対パス
     *   /(スラッシュ)から始まらないパス(絶対URLを除く)を指定する。
     *   アプリケーション内のパスを指定する場合に使用する。
     *   指定されたパスをそのまま使用する。
     *
     * コンテキストからの相対パスを指定している場合は、secure引数を指定することでURIのhttpsとhttpを切り替える。
     * secure引数が指定された場合は、カスタムタグのデフォルト値(ポート番号、ホスト)とコンテキストパスを使用してURIを組み立てる。
     * URIが/(スラッシュ)から始まる場合のみ、コンテキストパスを付加する。
     *
     * エンコード前のURIが絶対URLでない場合は
     * {@link #getResourcePathForLanguage(PageContext, String)}メソッドを呼び出し、
     * 言語対応のリソースパスに変換する。
     *
     * エンコード処理は、{@link HttpServletResponse#encodeURL(String)}に処理を移譲する。
     *
     * </pre>
     * @param pageContext ページコンテキスト
     * @param uri URI
     * @param secure httpsにする場合はtrue、しない場合はfalse。指定しない場合はnull
     * @return エンコード済みのURI
     * @throws IllegalArgumentException secure引数が指定されたにも関わらず、絶対URL又は現在のパスからの相対パスが指定された場合
     * @throws IllegalStateException secure引数が指定されたにも関わらず、ホスト(カスタムタグのデフォルト値)が設定されていない場合
     */
    public static String encodeUri(PageContext pageContext, String uri, Boolean secure)
            throws IllegalArgumentException, IllegalStateException {
        if (StringUtil.isNullOrEmpty(uri)) {
            throw new IllegalArgumentException("uri is null.");
        }

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

        URISpecifiedFormat pathFormat = URISpecifiedFormat.match(uri);
        if (pathFormat != URISpecifiedFormat.ABSOLUTE_URL) {
            uri = getResourcePathForLanguage(pageContext, uri);
        }

        if (pathFormat == URISpecifiedFormat.CONTEXT_RELATIVE_PATH) {
            uri = request.getContextPath() + uri;
            if (secure != null) {
                String basePath = getCustomTagConfig().getBasePath(secure);
                if (StringUtil.isNullOrEmpty(basePath)) {
                    throw new IllegalStateException(
                        "host is null or blank. if you want to specify secure, host property of CustomTagConfig in repository must be specified.");
                }
                String protocol = secure ? "https" : "http";
                uri = protocol + "://" + basePath + (uri.startsWith("/") ? uri : "/" + uri);
            }
        } else {
            if (secure != null) {
                throw new IllegalArgumentException(
                        String.format("invalid path and secure were specified. "
                                + "if you want to specify secure, context relative path must be specified."
                                + " path = [%s], secure = [%s]", uri, secure));
            }
        }

        return response.encodeURL(uri);
    }

    /** URI指定形式 */
    private enum URISpecifiedFormat {
        /** 絶対URL */
        ABSOLUTE_URL,
        /** コンテキストからの相対パス */
        CONTEXT_RELATIVE_PATH,
        /** 現在のパスからの相対パス */
        CURRENT_PATH_RELATIVE_PATH;
        /**
         * URIがマッチするパス指定形式を返す。
         * @param uri URI
         * @return URIがマッチするパス指定形式
         */
        static URISpecifiedFormat match(String uri) {
            if (uri.startsWith("/")) {
                return CONTEXT_RELATIVE_PATH;
            } else if (uri.startsWith("http:") || uri.startsWith("https:")) {
                return ABSOLUTE_URL;
            } else {
                return CURRENT_PATH_RELATIVE_PATH;
            }
        }
    }

    /**
     * 言語対応のリソースパスを取得する。
     * <p/>
     * {@link CustomTagConfig}に指定された{@link nablarch.fw.web.i18n.ResourcePathRule}に処理を委譲する。
     * @param pageContext ページコンテキスト
     * @param path リソースパス
     * @return 言語対応のリソースパス
     */
    public static String getResourcePathForLanguage(PageContext pageContext, String path) {
        return getCustomTagConfig().getResourcePathRule()
                    .getPathForLanguage(path, (HttpServletRequest) pageContext.getRequest());
    }

    /**
     * クリック時のサブミット情報を登録し、JavaScriptを生成する。
     * {@link TagUtil#setSubmissionInfoToFormContext} を使ったサブミット情報登録後に呼び出すこと。
     *
     * <pre></pre>
     * リクエストスコープにCSP対応用のnonceが保存されているか否かで動作が変わる。
     *
     * ・CSP対応用のnonceが保存されていた場合
     * 　・{@link FormTag}が生成するscriptタグの一部として出力する
     * 　・本メソッド実行時には出力せずフォームコンテキストへの登録のみとし、{@link FormTag}の処理でためこんだスクリプトを一括で出力する
     * ・CSP対応用のnonceが保存されていない場合
     * 　・対象のタグのonclick属性としてイベントハンドラを出力する
     *
     * なお、いずれの場合も属性にonclickが指定されている場合、または{@code suppressDefaultSubmit}プロパティが
     * {@code true}の場合はスクリプトを生成しない。
     * </pre>
     *
     * @param pageContext ページコンテキスト
     * @param tagName クリック対象のタグ名
     * @param attributes 属性
     * @param suppressDefaultSubmit Nablarchのデフォルトのsubmit関数呼び出しを抑制するか否か。{@code true}の場合は抑制する
     */
    public static void registerOnclickForSubmission(PageContext pageContext, String tagName, HtmlAttributes attributes, boolean suppressDefaultSubmit) {
        if (hasCspNonce(pageContext)) {
            registerOnclickScriptForSubmission(pageContext, tagName, attributes, suppressDefaultSubmit);
        } else {
            editOnclickAttributeForSubmission(pageContext, attributes, suppressDefaultSubmit);
        }
    }

    /**
     * クリック時に動作するスクリプトと、{@link FormTag}が出力するスクリプトと同じタイミングで
     * 出力するようにフォームコンテキストに登録する。
     *
     * onclick属性が編集されている場合、または{@code suppressDefaultSubmit}プロパティが{@code true}の
     * 場合は登録しない。
     *
     * @param pageContext ページコンテキスト
     * @param tagName クリック対象のタグ名
     * @param attributes 属性
     * @param suppressDefaultSubmit Nablarchのデフォルトのsubmit関数呼び出しを抑制するか否か。{@code true}の場合は抑制する
     */
    private static void registerOnclickScriptForSubmission(PageContext pageContext, String tagName, HtmlAttributes attributes, boolean suppressDefaultSubmit) {
        if (!jsSupported(pageContext)) {
            return;
        }

        String onclick = attributes.get(HtmlAttribute.ONCLICK);
        if (!StringUtil.isNullOrEmpty(onclick)) {
            // onclick属性が指定されていた場合はスクリプトを登録しない
            return;
        }

        if (suppressDefaultSubmit) {
            // Nablarchのデフォルトのsubmit関数呼び出しが抑制されている場合は、スクリプトを登録しない
            return;
        }

        CustomTagConfig customTagConfig = TagUtil.getCustomTagConfig();
        String ls = customTagConfig.getLineSeparator();
        FormContext formContext = getFormContext(pageContext);
        StringBuilder javaScript = new StringBuilder();
        javaScript.append("document.querySelector(\"");
        javaScript.append("form[name='");
        javaScript.append(formContext.getName());
        javaScript.append("'] ");
        javaScript.append(tagName);
        javaScript.append("[name='");
        javaScript.append(attributes.<String>get(HtmlAttribute.NAME));
        // 通常addEventListenerを使うところだが、従来の実装がonclick属性に直接設定するものだったため、
        // 動作を近いものにするためにonclickプロパティを使用している
        javaScript.append("']\").onclick = window.");
        javaScript.append(ExecutionContext.FW_PREFIX + "submit;");
        formContext.addInlineSubmissionScript(javaScript.toString());
    }

    /**
     * サブミット制御のためにonclick属性を編集する。<br>
     * onclick属性が編集されている場合は、または{@code suppressDefaultSubmit}プロパティが{@code true}の
     * 場合は編集しない。
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param suppressDefaultSubmit デフォルトのsubmit関数呼び出しを抑制するか否か。{@code true}の場合は抑制する
     */
    private static void editOnclickAttributeForSubmission(PageContext pageContext, HtmlAttributes attributes, boolean suppressDefaultSubmit) {
        if (!jsSupported(pageContext)) {
            return;
        }
        String onclick = attributes.get(HtmlAttribute.ONCLICK);
        if (!StringUtil.isNullOrEmpty(onclick)) {
            // onclick属性が指定されていた場合はスクリプトを登録しない
            return;
        }

        if (suppressDefaultSubmit) {
            // Nablarchのデフォルトのsubmit関数呼び出しを抑制されている場合は、スクリプトを登録しない
            return;
        }

        attributes.put(HtmlAttribute.ONCLICK, ONCLICK_FOR_SUBMISSION);
    }

    /** サブミット制御用のonclick属性の値 */
    private static final String ONCLICK_FOR_SUBMISSION = "return window." + FW_PREFIX + "submit(event, this);";

    /**
     * name属性又はname属性のエイリアスに対応するエラーメッセージが存在する場合は、class属性に指定されたCSSクラス名を追記する。<br>
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param cssClass CSSクラス名
     * @param nameAlias name属性のエイリアス
     * @return 追記したCSSクラス名。追記しなかった場合はnull
     */
    public static String editClassAttributeForError(PageContext pageContext, HtmlAttributes attributes, String cssClass, Set<String> nameAlias) {
        Set<String> names = nameAlias != null ? new HashSet<String>(nameAlias) : new HashSet<String>();
        names.add(attributes.<String>get(HtmlAttribute.NAME));
        Message message = TagUtil.findMessage(pageContext, names);
        String useCssClass = null;
        if (message != null) {
            useCssClass = cssClass != null ? cssClass : getCustomTagConfig().getErrorCss();
            editClassAttribute(pageContext, attributes, useCssClass);
        }
        return useCssClass;
    }

    /**
     * class属性を編集する。
     * <pre>
     * 指定されたCSSクラス名を元の値に付加する。
     * 既に指定されたCSSクラス名が含まれている場合は付加しない。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param cssClass 追記するCSSクラス名
     */
    public static void editClassAttribute(PageContext pageContext, HtmlAttributes attributes, String cssClass) {
        String original = TagUtil.getOriginalAttribute(attributes, HtmlAttribute.CLASS, " ");
        for (String clazz : SPACE_PATTERN.split(original)) {
            if (cssClass.equals(clazz)) {
                return;
            }
        }
        attributes.put(HtmlAttribute.CLASS, original + cssClass);
    }

    /**
     * 元の属性値の末尾にセパレータを付加した値を取得する。<br>
     * このメソッドは、属性に値を追記する場合に使用する。
     * @param attributes 属性
     * @param attribute 値を取得する属性
     * @param separator セパレータ
     * @return 元の属性値の末尾にセパレータを付加した値。属性値が未指定の場合は空文字
     */
    public static String getOriginalAttribute(HtmlAttributes attributes, HtmlAttribute attribute, String separator) {
        String value = attributes.get(attribute);
        if (!StringUtil.isNullOrEmpty(value)) {
            value = value.trim();
            if (!value.endsWith(separator)) {
                value += separator;
            }
        }
        return value != null ? value : "";
    }

    /** フォームのname属性をページコンテキストに格納する際に使用するキー */
    private static final String KEY_FORM_NAMES = FW_PREFIX + "formNames";

    /** フォームコンテキストをページコンテキストに格納する際に使用するキー */
    private static final String KEY_FORM_CONTEXT = FW_PREFIX + "formContext";

    /**
     * ページコンテキストにフォームコンテキストを設定する。
     * <p/>
     * フォームのname属性が重複している場合は例外を送出する。
     *
     * @param pageContext ページコンテキスト
     * @param formContext フォームコンテキスト
     */
    public static void setFormContext(PageContext pageContext, FormContext formContext) {
        if (formContext != null) {
            Set<String> formNames = getFormNames(pageContext);
            String formName = formContext.getName();
            if (formNames.contains(formName)) {
                throw new IllegalArgumentException(
                    String.format("name attribute of form tag has duplicated. name = [%s]", formName));
            }
            formNames.add(formName);
        }
        pageContext.setAttribute(KEY_FORM_CONTEXT, formContext, PageContext.REQUEST_SCOPE);
    }

    /**
     * ページコンテキストからフォームのname属性を取得する。
     * @param pageContext ページコンテキスト
     * @return フォームのname属性
     */
    @SuppressWarnings("unchecked")
    private static Set<String> getFormNames(PageContext pageContext) {
        Set<String> formNames = (Set<String>) pageContext
                                        .getAttribute(KEY_FORM_NAMES, PageContext.REQUEST_SCOPE);
        if (formNames == null) {
            formNames = new HashSet<String>();
            pageContext.setAttribute(KEY_FORM_NAMES, formNames, PageContext.REQUEST_SCOPE);
        }
        return formNames;
    }

    /**
     * ページコンテキストからフォームコンテキストを取得する。
     * @param pageContext ページコンテキスト
     * @return フォームコンテキスト
     */
    public static FormContext getFormContext(PageContext pageContext) {
        return (FormContext) pageContext.getAttribute(KEY_FORM_CONTEXT, PageContext.REQUEST_SCOPE);
    }

    /**
     * フォームコンテキストに入力項目のname属性を設定する。<br>
     * JSPが入力画面の場合のみname属性を設定する。
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     */
    public static void setNameToFormContext(PageContext pageContext, HtmlAttributes attributes) {
        if (!isConfirmationPage(pageContext)) {
            getFormContext(pageContext).addInputName(attributes.<String>get(
                    HtmlAttribute.NAME));
        }
    }

    /**
     * フォームコンテキストにサブミット情報を設定する。
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param action サブミット時の動作
     * @param uri サブミット先のURI
     * @param allowDoubleSubmission 二重サブミットを許可するか否か
     * @param requestId サブミット時のリクエストID
     * @param displayMethod 表示制御方法
     */
    public static void setSubmissionInfoToFormContext(PageContext pageContext, HtmlAttributes attributes,
                                                        SubmissionAction action, String uri,
                                                        boolean allowDoubleSubmission,
                                                        String requestId, DisplayMethod displayMethod) {
        doSetSubmissionInfoToFormContext(pageContext, attributes, action, uri, allowDoubleSubmission, requestId, displayMethod, null, null);
    }

    /**
     * フォームコンテキストにサブミット情報を設定する。<br/>
     * ポップアップのオプション情報を指定できる。
     * 指定されたオプション情報がnullであった場合には、代わりにデフォルト値を使用する。
     * 引数のオプション情報がnullで、デフォルト値も登録されていない場合は、nullが使用される。
     * （オプションなし）
     *
     * @param pageContext           ページコンテキスト
     * @param attributes            属性
     * @param action                サブミット時の動作
     * @param uri                   サブミット先のURI
     * @param allowDoubleSubmission 二重サブミットを許可するか否か
     * @param requestId             サブミット時のリクエストID
     * @param displayMethod         表示制御方法
     * @param popupWindowName       ポップアップのウィンドウ名(nullの場合、デフォルト値を使用）
     * @param popupOption           ポップアップのオプション情報(nullの場合、デフォルト値を使用）
     * @see CustomTagConfig#getPopupOption()
     */
    public static void setSubmissionInfoToFormContext(PageContext pageContext, HtmlAttributes attributes,   // SUPPRESS CHECKSTYLE サブミット情報の生成処理を局所化するため。
                                                      SubmissionAction action, String uri,
                                                      boolean allowDoubleSubmission, String requestId,
                                                      DisplayMethod displayMethod,
                                                      String popupWindowName, String popupOption) {
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        if (popupWindowName == null) {
            popupWindowName = config.getPopupWindowName();
        }
        if (popupOption == null) {
            popupOption = config.getPopupOption();
        }
        doSetSubmissionInfoToFormContext(pageContext, attributes, action, uri, allowDoubleSubmission, requestId,
                                         displayMethod, popupWindowName, popupOption);
    }

    /**
     * フォームコンテキストにサブミット情報を設定する。
     * @param pageContext ページコンテキスト
     * @param attributes 属性
     * @param action サブミット時の動作
     * @param uri サブミット先のURI
     * @param allowDoubleSubmission 二重サブミットを許可するか否か
     * @param requestId サブミット時のリクエストID
     * @param displayMethod 表示制御方法
     * @param popupWindowName ポップアップのウィンドウ名
     * @param popupOption ポップアップのオプション情報
     */
    private static void doSetSubmissionInfoToFormContext(PageContext pageContext,   // SUPPRESS CHECKSTYLE サブミット情報の生成処理を局所化するため。
                                                         HtmlAttributes attributes,
                                                         SubmissionAction action, String uri,
                                                         boolean allowDoubleSubmission, String requestId,
                                                         DisplayMethod displayMethod,
                                                         String popupWindowName, String popupOption) {
        getFormContext(pageContext).addSubmissionInfo(
                action, attributes,
                uri, allowDoubleSubmission, requestId, popupWindowName, popupOption, displayMethod);
    }
    // 入力データの復元(value属性の取得)をサポートするメソッド

    /**
     * 単一値としてname属性に対応するvalue属性を変数スコープから取得する。
     * <pre>
     * 単一値でない場合はINFOレベルでログ出力しnullを返す。
     * 取得方法については、{@link #getValue(PageContext, String, boolean)}メソッドのJavaDocを参照。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param name name属性
     * @return value属性。存在しない場合はnull
     */
    public static Object getSingleValueOnScope(PageContext pageContext, String name) {
        return getSingleValue(pageContext, name, false);
    }

    /**
     * 単一値としてname属性に対応するvalue属性をリクエストパラメータ又は変数スコープから取得する。
     * <pre>
     * 単一値でない場合はINFOレベルでログ出力しnullを返す。
     * 取得方法については、{@link #getValue(PageContext, String, boolean)}メソッドのJavaDocを参照。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param name name属性
     * @return value属性。存在しない場合はnull
     */
    public static Object getSingleValue(PageContext pageContext, String name) {
        return getSingleValue(pageContext, name, true);
    }

    /**
     * 単一値としてname属性に対応するvalue属性を取得する。
     * <pre>
     * 単一値でない場合はINFOレベルでログ出力しnullを返す。
     * 取得方法については、{@link #getValue(PageContext, String, boolean)}メソッドのJavaDocを参照。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param name name属性
     * @param includeRequestParameter 取得先にリクエストパラメータを含める場合はtrue
     * @return value属性。存在しない場合はnull
     */
    private static Object getSingleValue(PageContext pageContext, String name, boolean includeRequestParameter) {

        Object value = getValue(pageContext, name, includeRequestParameter);
        if (value == null) {
            return null;
        }

        boolean isSingleValue = true;
        if (value.getClass().isArray()) {
            if (Array.getLength(value) != 1) {
                isSingleValue = false;
            } else {
                value = Array.get(value, 0);
            }
        } else if (value instanceof Collection<?>) {
            Collection<?> values = (Collection<?>) value;
            if (values.size() != 1) {
                isSingleValue = false;
            } else {
                value = values.iterator().next();
            }
        }

        if (!isSingleValue) {
            LOGGER.logInfo(String.format("value wasn't single value. name = [%s]", name));
            value = null;
        }

        return value;
    }

    /**
     * 多値としてname属性に対応するvalue属性を変数スコープから取得する。<br>
     * 取得方法については、{@link #getValue(PageContext, String, boolean)}メソッドのJavaDocを参照。
     * @param pageContext ページコンテキスト
     * @param name name属性
     * @return value属性。存在しない場合は空のリスト
     */
    public static Collection<?> getMultipleValuesOnScope(PageContext pageContext, String name) {
        return getMultipleValues(pageContext, name, false);
    }

    /**
     * 多値としてname属性に対応するvalue属性をリクエストパラメータ又は変数スコープから取得する。<br>
     * 取得方法については、{@link #getValue(PageContext, String, boolean)}メソッドのJavaDocを参照。
     * @param pageContext ページコンテキスト
     * @param name name属性
     * @return value属性。存在しない場合は空のリスト
     */
    public static Collection<?> getMultipleValues(PageContext pageContext, String name) {
        return getMultipleValues(pageContext, name, true);
    }

    /**
     * 多値としてname属性に対応するvalue属性をリクエストパラメータ又は変数スコープから取得する。<br>
     * 取得方法については、{@link #getValue(PageContext, String, boolean)}メソッドのJavaDocを参照。
     * @param pageContext ページコンテキスト
     * @param name name属性
     * @param includeRequestParameter 取得先にリクエストパラメータを含める場合はtrue
     * @return value属性。存在しない場合は空のリスト
     */
    private static Collection<?> getMultipleValues(PageContext pageContext, String name, boolean includeRequestParameter) {

        Object value = getValue(pageContext, name, includeRequestParameter);
        if (value == null) {
            return Collections.emptyList();
        }

        if (value.getClass().isArray()) {
            if (value instanceof Object[]) {
                return Arrays.asList((Object[]) value);
            } else {
                final int length = Array.getLength(value);
                final Object[] objects = new Object[length];
                for (int i = 0; i < length; i++) {
                    objects[i] = Array.get(value, i);
                }
                return Arrays.asList(objects);
            }
        } else if (value instanceof Collection<?>) {
            return (Collection<?>) value;
        } else {
            return Arrays.asList(value);
        }
    }

    /** 角括弧付プロパティのパターン */
    private static final Pattern BRACKET_PATTERN = Pattern.compile("^(.+?)\\[(.+?)\\]$");


    /**
     * name属性に対応するvalue属性を取得する。<br>
     * <br>
     * name属性は、EL式に似た下記のルールにより、オブジェクト階層のアクセスをサポートする。
     * <ul>
     * <li>オブジェクト又はMapのプロパティへのアクセスは、ドット区切りを指定する。</li>
     * <li>List又は配列の要素へのアクセスは、角括弧（括弧内にインデックスを表す数字）を指定する。</li>
     * </ul>
     * <br>
     * オブジェクト階層のルートにあたるオブジェクトは、下記の順に検索し、最初に見つかった値を使用する。
     * <ol>
     * <li>Servlet APIのページスコープ</li>
     * <li>Servlet APIのリクエストスコープ</li>
     * <li>Servlet APIのリクエストパラメータ(includeRequestParameterがtrueの場合のみ)</li>
     * <li>Servlet APIのセッションスコープ</li>
     * </ol>
     *
     * @param pageContext ページコンテキスト
     * @param name name属性
     * @param includeRequestParameter 取得先にリクエストパラメータを含める場合はtrue
     * @return value属性。存在しない場合はnull
     *
     * @see CustomTagConfig#setUseValueAsNullIfObjectExists(boolean)
     */
    public static Object getValue(PageContext pageContext, String name, boolean includeRequestParameter) {

        if (pageContext == null) {
            throw new IllegalArgumentException("pageContext is null.");
        }
        if (StringUtil.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("name is null or blank.");
        }

        CustomTagConfig config = getCustomTagConfig();

        try {

            String[] splitName = DOT_PATTERN.split(name);

            Object pageObj = getObjectOnScope(pageContext, splitName, PageContext.PAGE_SCOPE);
            Object pageValue = chooseObjectOrProperty(pageObj, splitName);
            if (config.getUseValueAsNullIfObjectExists()
                    && pageObj != null || pageValue != null) {
                return pageValue;
            }

            Object requestObj = getObjectOnScope(pageContext, splitName, PageContext.REQUEST_SCOPE);
            Object requestValue = chooseObjectOrProperty(requestObj, splitName);
            if (config.getUseValueAsNullIfObjectExists() && requestObj != null
                    || requestValue != null) {
                return requestValue;
            }

            if (includeRequestParameter) {
                Map<?, ?> params = pageContext.getRequest().getParameterMap();
                if (params.containsKey(name)) {
                    return params.get(name);
                }
            }

            if(pageContext.getSession() == null) {
                return null;
            }
            Object sessionObj = getObjectOnScope(pageContext, splitName, PageContext.SESSION_SCOPE);

            return chooseObjectOrProperty(sessionObj, splitName);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("failed to parse. name = [" + name + "]", e);
        }
    }

    /**
     * getValue() メソッドの返すオブジェクトについて、オブジェクトそのものまたはプロパティを選択して返す<br />
     * 基本的には指定されたスコープのname属性のプロパティを取得するが、
     * ただし、splitName が1つの場合、特別にオブジェクトそのものを返す。<br />
     *
     * @param obj プロパティを取得する元になるオブジェクト
     * @param splitName ドットで区切ったname属性の値
     * @return splitName が1つの場合、特別にオブジェクト、それ以外はオブジェクトから splitName の最後の文字列と一致するプロパティ
     */
    public static Object chooseObjectOrProperty(Object obj, String[] splitName) {
        if (obj == null) {
            return null;
        }

        if (splitName.length == 1) {
            return obj;
        } else {
            String name = splitName[splitName.length - 1];
            Matcher matcher = BRACKET_PATTERN.matcher(name);
            if (matcher.matches()) {
                return getElement(ObjectUtil.getProperty(obj, matcher.group(1)), Integer.valueOf(matcher.group(2)));
            } else {
                return ObjectUtil.getPropertyIfExists(obj, splitName[splitName.length - 1]);
            }
        }
    }

    /**
     * 指定されたスコープのname属性のプロパティを保持するオブジェクトを取得する。
     * @param pageContext ページコンテキスト
     * @param splitName ドットで区切ったname属性の値
     * @param scope スコープ
     * @return name属性のプロパティを保持するオブジェクト。存在しない場合はnull
     */
    private static Object getObjectOnScope(PageContext pageContext, String[] splitName, int scope) {
        Object obj;
        String name = splitName[0];
        Matcher rootMatcher = BRACKET_PATTERN.matcher(name);
        if (rootMatcher.matches()) {
            // List or Array
            obj = getElement(pageContext.getAttribute(rootMatcher.group(1), scope), Integer.valueOf(rootMatcher.group(2)));
        } else {
            // Object or Map
            obj = pageContext.getAttribute(name, scope);
        }

        // find leaf
        for (int i = 1; i < splitName.length - 1; i++) {
            if (obj == null) {
                return null;
            }
            Matcher nodeMatcher = BRACKET_PATTERN.matcher(splitName[i]);
            if (nodeMatcher.matches()) {
                // List or Array
                obj = getElement(ObjectUtil.getProperty(obj, nodeMatcher.group(1)), Integer.valueOf(nodeMatcher.group(2)));
            } else {
                // Object or Map
                obj = ObjectUtil.getPropertyIfExists(obj, splitName[i]);
            }
        }
        return obj;
    }

    /**
     * List又は配列の要素を取得する。
     * @param obj List又は配列
     * @param index 要素のインデックス
     * @return 要素
     */
    private static Object getElement(Object obj, int index) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            return index < list.size() ? list.get(index) : null;
        } else if (obj.getClass().isArray()) {
            return index < Array.getLength(obj) ? Array.get(obj, index) : null;
        } else {
            throw new IllegalArgumentException(String.format("invalid type. type = [%s]", obj.getClass()));
        }
    }

    /**
     * 選択項目において選択した値に、選択肢の値が含まれているかを判定する。
     * <pre>
     * 選択した値と選択肢の値は、ともに{@link Object#toString()}してから等しいか判定する。
     * 選択項目の選択状態を判定する際に使用する。
     * </pre>
     * @param values 選択した値
     * @param value 選択肢の値
     * @return 選択した値に選択肢の値が含まれている場合はtrue
     */
    public static boolean contains(Collection<?> values, Object value) {
        if ((values == null || values.isEmpty()) || value == null) {
            return false;
        }
        String strValue = value.toString();
        for (Object element : values) {
            if (element != null && strValue.equals(element.toString())) {
                return true;
            }
        }
        return false;
    }

    // エスケープ処理をサポートするメソッド

    /**
     * HTMLエスケープを行う。
     * <pre>
     * このメソッドは、{@link #escapeHtml(Object, boolean)}に処理を委譲し、
     * セキュリティ上問題のあるHTMLエスケープ処理のみを行う。
     * このメソッドでは、改行変換と半角スペース変換を行わない。
     *
     * このメソッドは、HTMLタグの属性値に対するHTMLエスケープに使用する。
     * </pre>
     * @param s 文字列
     * @return HTMLエスケープ後の文字列
     */
    @Published(tag = "architect")
    public static String escapeHtml(Object s) {
        return escapeHtml(s, false, null, null);
    }

    /**
     * HTMLエスケープを行う。
     * <pre>
     * セキュリティ上問題のある下記文字の変換を行う。
     *
     * {@literal
     * & -> &amp;
     * < -> &lt;
     * > -> &gt;
     * " -> &034;
     * ' -> &039;
     *
     * withHtmlFormatにtrueが指定された場合は、さらに下記の変換を行う。
     *
     * " "(半角スペース) -> &nbsp;
     * \n、\r、\r\n -> brタグ
     * }
     * </pre>
     * @param s 文字列
     * @param withHtmlFormat 改行変換と半角スペース変換を行う場合はtrue
     * @return HTMLエスケープ後の文字列
     */
    @Published(tag = "architect")
    public static String escapeHtml(Object s, boolean withHtmlFormat) {
        return escapeHtml(s, withHtmlFormat, null, null);
    }

    /**
     * HTMLエスケープを行う。
     * <pre>
     * セキュリティ上問題のある下記文字の変換を行う。
     *
     * {@literal
     * & -> &amp;
     * < -> &lt;
     * > -> &gt;
     * " -> &034;
     * ' -> &039;
     *
     * withHtmlFormatにtrueが指定された場合は、さらに下記の変換を行う。
     *
     * " "(半角スペース) -> &nbsp;
     * \n、\r、\r\n -> brタグ
     * }
     * </pre>
     * @param s 文字列
     * @param withHtmlFormat 改行変換と半角スペース変換を行う場合はtrue
     * @param safeTags       エスケープ対象外のタグ
     * @param safeAttributes
     *     エスケープ対象外のタグの中で使用することができる属性。
     *     (ここに無い属性が使用されていた場合は、エスケープ対象外のタグでも、
     *      エスケープされる。)
     * @return HTMLエスケープ後の文字列
     */
    @Published(tag = "architect")
    public static String escapeHtml(Object       s,
                                    boolean      withHtmlFormat,
                                    List<String> safeTags,
                                    List<String> safeAttributes) {
        return HtmlTagUtil.escapeHtml(s, withHtmlFormat, safeTags, safeAttributes);
    }

    /** 静的コンテンツのバージョンのリポジトリ及びGETパラメータのキー */
    private static final String STATIC_CONTENT_VERSION_KEY = "static_content_version";

    /**
     * 指定されたURIにGETパラメータで静的リソースのバージョンを付加する。
     * <p/>
     * 追加するGETパラメータの形式は以下の通り。
     * <pre>
     * KEY:{@link ExecutionContext#FW_PREFIX} + "static_content_version"
     * VALUE:システムリポジトリ({@link SystemRepository})より取得した静的リソースのバージョン(取得時のキーは、static_content_version)
     * </pre>
     * システムリポジトリから取得した静的リソースバージョンがnullや空文字列の場合には、GETパラメータは付加しない。
     *
     * 設定ファイル(configファイル)への静的リソースバージョンの設定例:
     * <pre>
     * {@code static_content_version=1.0}
     * </pre>
     *
     * @param uri URI
     * @return 静的リソースのバージョンをGETパラメータとして付加したURI
     */
    public static String addStaticContentVersion(String uri) {
        String staticContentVersion = SystemRepository.get(STATIC_CONTENT_VERSION_KEY);
        if (StringUtil.isNullOrEmpty(staticContentVersion)) {
            // 付加するGETパラメータの値がnullまたは空文字列の場合は、そのまま返却する。
            return uri;
        }
        StringBuilder sb = new StringBuilder(uri.length() + 50);
        sb.append(uri);
        sb.append(uri.contains("?") ? '&' : '?');
        sb.append(FW_PREFIX);
        sb.append(STATIC_CONTENT_VERSION_KEY);
        sb.append('=');
        sb.append(escapeHtml(staticContentVersion, false));
        return sb.toString();
    }


    /**
     * JavaScriptの文字列として使用する向けにエスケープを行う。
     * <pre>
     * {@literal
     * HTMLエスケープは下記の変換を行う。
     * " -> \"
     * ' -> \'
     * \ -> \\
     * }
     * </pre>
     * @param s 文字列
     * @return エスケープ後の文字列
     */
    public static String escapeJavaScriptString(Object s) {

        if (s == null) {
            return null;
        }

        char[] str = s.toString().toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            char c = str[i];
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\'': sb.append("\\'"); break;
                case '\\': sb.append("\\\\"); break;
                default : sb.append(c);
            }
        }
        return sb.toString();
    }

    // エラー表示をサポートするメソッド

    /**
     * リクエストスコープからメッセージリストを取得する。
     * @param pageContext ページコンテキスト
     * @return メッセージリストまたは<code>null</code>
     */
    public static List<Message> getMessages(PageContext pageContext) {
        Object errorObject = pageContext.getAttribute(THROWN_APPLICATION_EXCEPTION_KEY, PageContext.REQUEST_SCOPE);
        if (!(errorObject instanceof ApplicationException)) {
            return null;
        }
        return ((ApplicationException) errorObject).getMessages();
    }

    /**
     * リクエストスコープに設定されているメッセージリストからプロパティ名でメッセージを検索する。<br>
     * <br>
     * 検索対象のメッセージは、ValidationResultMessage型のみ。
     *
     * @param pageContext ページコンテキスト
     * @param name プロパティ名
     * @return メッセージ。見つからない場合はnull
     */
    public static Message findMessage(PageContext pageContext, String name) {
        Set<String> names = new HashSet<String>();
        names.add(name);
        return findMessage(pageContext, names);
    }

    /**
     * リクエストスコープに設定されているメッセージリストからプロパティ名でメッセージを検索する。<br>
     * <br>
     * 検索対象のメッセージは、ValidationResultMessage型のみ。
     *
     * @param pageContext ページコンテキスト
     * @param names プロパティ名
     * @return メッセージ。見つからない場合はnull
     */
    public static Message findMessage(PageContext pageContext, Set<String> names) {
        List<Message> messages = TagUtil.getMessages(pageContext);
        if (messages == null || (names == null || names.isEmpty())) {
            return null;
        }
        for (Message message : messages) {
            if (message instanceof ValidationResultMessage) {
                if (names.contains(((ValidationResultMessage) message).getPropertyName())) {
                    return message;
                }
            }
        }
        return null;
    }

    // 入力画面と確認画面のJSP共通化をサポートするメソッド

    /** JSPが生成する画面が確認画面であるか否かをリクエストスコープに設定する際に使用するキー */
    private static final String KEY_CONFIRMATION_PAGE = FW_PREFIX + "confirmationPage";

    /**
     * JSPが生成する画面が確認画面であることを設定する。<br>
     * このメソッド呼び出し後のJSPでは、入力項目のカスタムタグが確認画面用の出力を行う。
     * @param pageContext ページコンテキスト
     */
    public static void setConfirmationPage(PageContext pageContext) {
        pageContext.setAttribute(KEY_CONFIRMATION_PAGE, true, PageContext.REQUEST_SCOPE);
    }

    /**
     * JSPが生成する画面が入力画面であることを設定する。<br>
     * このメソッド呼び出し後のJSPでは、入力項目のカスタムタグが入力画面用の出力を行う。
     * @param pageContext ページコンテキスト
     */
    public static void setInputPage(PageContext pageContext) {
        pageContext.setAttribute(KEY_CONFIRMATION_PAGE, false, PageContext.REQUEST_SCOPE);
    }

    /**
     * JSPが生成する画面が確認画面であるか否かを判定する。
     * @param pageContext ページコンテキスト
     * @return 確認画面の場合はtrue、確認画面でない場合はfalse
     */
    public static boolean isConfirmationPage(PageContext pageContext) {
        Object isOutputOnly = pageContext.getAttribute(KEY_CONFIRMATION_PAGE, PageContext.REQUEST_SCOPE);
        return isOutputOnly != null ? Boolean.valueOf(isOutputOnly.toString()) : false;
    }

    // コード値表示をサポートするメソッド

    /**
     * コード値に対応するラベルを取得する。<br/>
     * <br/>
     * labelPatternにnullが指定された場合は、{@link CustomTagConfig#getCodeLabelPattern()}を使用する。
     * @param labelPattern ラベルを整形するパターン。
     *                      プレースホルダを下記に示す。
     *                      <pre>
     *                      $NAME$: コード値に対応するコード名称
     *                      $SHORTNAME$: コード値に対応するコードの略称
     *                      $OPTIONALNAME$: コード値に対応するコードのオプション名称
     *                      $OPTIONALNAME$を使用する場合は、optionColumnName引数の指定が必須となる。
     *                      $VALUE$: コード値
     *                      </pre>
     * @param codeId コードID
     * @param value コード値
     * @param optionColumnName 取得するオプション名称のカラム名
     * @return コード値に対応するラベル
     */
    public static String getCodeLabel(String labelPattern, String codeId, String value, String optionColumnName) {
        String label = labelPattern != null ? labelPattern : getCustomTagConfig().getCodeLabelPattern();
        if (label.contains("$NAME$")) {
            label = label.replace("$NAME$", CodeUtil.getName(codeId, value));
        }
        if (label.contains("$SHORTNAME$")) {
            label = label.replace("$SHORTNAME$", CodeUtil.getShortName(codeId, value));
        }
        if (label.contains("$OPTIONALNAME$")) {
            if (StringUtil.isNullOrEmpty(optionColumnName)) {
                throw new IllegalArgumentException(
                    "optionColumnName wasn't specified. optionColumnName is required if you use \"$OPTIONALNAME$\" placeholder.");
            }
            label = label.replace("$OPTIONALNAME$", CodeUtil.getOptionalName(codeId, value, optionColumnName));
        }
        return label.replace("$VALUE$", value);
    }

    /**
     * サブミットを行うタグの表示方法を示す{@link DisplayMethod}を取得する。</br>
     * </br>
     * {@link CustomTagConfig}にて指定された{@link DisplayControlChecker}リストを使用して表示制御の要否を判定する。</br>
     * </br>
     * いずれかの{@link DisplayControlChecker}において表示制御が必要と判定した場合は、displayMethod引数に指定された値をそのまま返す。
     * ただし、displayMethod引数にnullが指定された場合は、{@link CustomTagConfig}に指定されたデフォルトの{@link DisplayMethod}を返す。</br>
     * </br>
     * すべての{@link DisplayControlChecker}において表示制御が不要と判定した場合は{@link DisplayMethod#NORMAL}を返す。
     * </br>
     * また、リクエストIDがnullの場合も{@link DisplayMethod#NORMAL}を返す。
     *
     * @param requestId サブミットを行うタグにて指定されたリクエストID
     * @param displayMethod サブミットを行うタグに指定された表示方法を表す{@link DisplayMethod}。
     *
     * @return サブミットを行うタグの表示方法を表す{@link DisplayMethod}
     */
    static DisplayMethod getDisplayMethod(String requestId, DisplayMethod displayMethod) {

        if (requestId == null) {
            return DisplayMethod.NORMAL;
        }
        CustomTagConfig customTagConfig = getCustomTagConfig();
        if (displayMethod == null) {
            displayMethod = customTagConfig.getDisplayMethod();
        }
        if (displayMethod == DisplayMethod.NORMAL) {
            return DisplayMethod.NORMAL;
        }

        for (DisplayControlChecker displayControlChecker : getDisplayControlCheckers()) {
            if (displayControlChecker.needsDisplayControl(requestId)) {
                return displayMethod;
            }
        }

        return DisplayMethod.NORMAL;
    }

    /**
     * {@link CustomTagConfig}にdisplayControlCheckersが指定されていない場合のデフォルト値
     */
    private static final List<DisplayControlChecker> DEFAULT_DISPLAY_CONTROL_CHECKERS = new ArrayList<DisplayControlChecker>();

    static {
        DEFAULT_DISPLAY_CONTROL_CHECKERS.add(new PermissionDisplayControlChecker());
        DEFAULT_DISPLAY_CONTROL_CHECKERS.add(new ServiceAvailabilityDisplayControlChecker());
    }

    /**
     * サブミットを行うタグの表示制御を行う際に、チェックを行う{@link DisplayControlChecker}のリストを取得する。</br>
     * {@link CustomTagConfig}の設定に設定されなかった場合、
     * {@link PermissionDisplayControlChecker} と
     * {@link ServiceAvailabilityDisplayControlChecker}を返却する。
     *
     * @return サブミットを行うタグの表示制御を行う際に、チェックを行う{@link DisplayControlChecker}のリスト
     */
    private static List<DisplayControlChecker> getDisplayControlCheckers() {
        CustomTagConfig customTagConfig = TagUtil.getCustomTagConfig();
        List<DisplayControlChecker> displayControlCheckers = customTagConfig.getDisplayControlCheckers();
        return displayControlCheckers != null
                                      ? displayControlCheckers
                                      : DEFAULT_DISPLAY_CONTROL_CHECKERS;
    }

    /**
     * Javascript使用不可のフラグが設定されていればtrueを返す。
     *
     * このフラグは {@link KeitaiAccessHandler} によって設定される。
     *
     * @param pageContext ページコンテキスト
     * @return Javascript使用不可のフラグが設定されていればtrue
     * @see KeitaiAccessHandler
     */
    public static boolean jsSupported(PageContext pageContext) {
        return (TagUtil.getSingleValue(pageContext, KeitaiAccessHandler.JS_UNSUPPORTED_FLAG_NAME) == null);
    }

    /**
     * オブジェクトに紐付くキーと値を nablarch_hidden に保存する。<br />
     * 保存した値は戻り値の文字列が引数 name で指定したキーでリクエストパラメータに入っていれば、復元できる。<br />
     * <br />
     *
     * 値の保存は単純なカンマ区切りで行うため、値の中にカンマが含まれている場合は保持できない。
     *
     * @param pageContext ページコンテキスト
     * @param name 保存する名称(リクエストパラメータ)
     * @param valueObject 保存する値を保持するオブジェクト
     * @param keyNames 保存する値のキー名のリスト
     * @param namePrefix 保存する値を保持するオブジェクトを表すプレフィクス
     * @return 復元時に使用する、リクエストパラメータに保持する文字列
     */
    public static String storeKeyValueSetToHidden(PageContext pageContext, String name, Object valueObject,
            List<String> keyNames, String namePrefix) {
        FormContext formContext = getFormContext(pageContext);
        StringBuilder namesListStr = new StringBuilder();
        StringBuilder valuesListStr = new StringBuilder();
        boolean first = true;
        for (String keyName : keyNames) {
            if (!first) {
                namesListStr.append(",");
                valuesListStr.append(",");
            }
            namesListStr.append(namePrefix + "." + keyName);

            Object property = getPropertyFromObject(valueObject, keyName);

            valuesListStr.append(property == null ? "" : property);
            first = false;
        }

        // 1リクエストに1回だけ hidden の name 属性を追加
        String nameKey = TagUtil.PARAM_NAMES_KEY_PREFIX + name;
        String valueKey = TagUtil.PARAM_VALUES_KEY_PREFIX + name;
        if (pageContext.getAttribute(nameKey, PageContext.REQUEST_SCOPE) == null) {
            pageContext.setAttribute(nameKey, Boolean.TRUE, PageContext.REQUEST_SCOPE);

            // hidden に展開するキー名を追加
            formContext.addHiddenTagInfo(TagUtil.VAR_NAMES_KEY, name);
            formContext.addHiddenTagInfo(nameKey, namesListStr.toString());
        }

        formContext.addHiddenTagInfo(valueKey, valuesListStr.toString());

        String valueList = valuesListStr.toString();
        return valueList;
    }

    /**
     * JavaBeans形式のオブジェクトまたはMapからプロパティ名に合致するオブジェクトを取得する。
     *
     * @param valueObject 取得対象のオブジェクト
     * @param propertyName プロパティ名
     * @return プロパティ名に合致するオブジェクト
     */
    public static Object getPropertyFromObject(Object valueObject,
            String propertyName) {
        Object property;
        if (valueObject instanceof Map<?, ?>) {
            property = ((Map<?, ?>) valueObject).get(propertyName);
        } else {
            property = ObjectUtil.getPropertyIfExists(valueObject, propertyName);
        }
        return property;
    }

    /**
     * nablarch_hidden に保存したキーと値のセットを復元する。<br />
     *
     * @param request HttpRequest
     */
    public static void restoreKeyValueSetFromHidden(HttpRequest request) {
        String[] names = request.getParam(VAR_NAMES_KEY);
        if (names == null) {
            return;
        }
        // 保存されたキー名分ループ
        for (String name : names) {
            String[] keysParam = request
            .getParam(PARAM_NAMES_KEY_PREFIX
                    + name);
            // 保存された有効な値
            String[] validValues = request
            .getParam(PARAM_VALUES_KEY_PREFIX
                    + name);
            String[] values = request.getParam(name);

            if (keysParam == null || keysParam.length != 1 || validValues == null || values == null) {
                // 異常なリクエストや、値が送信されなかった場合は無視
                continue;
            }

            Set<String> validValuesSet = new HashSet<String>(Arrays.asList(validValues));

            List<String> keys = getCommaSeparatedValueAsList(keysParam[0]);

            // 復元後の値の一時保存
            List<List<String>> restoredValues = new ArrayList<List<String>>();
            for (int i = 0; i < keys.size(); i++) {
                restoredValues.add(new ArrayList<String>());
            }
            for (String valuesStr : values) {
                if (!validValuesSet.contains(valuesStr)) {
                    // hidden に入った有効な値以外は無視するリクエストは無視し、有効な値のみ処理
                    continue;
                }
                // 有効な値であった場合、分解してリクエストパラメータに追加
                List<String> valuesList = getCommaSeparatedValueAsList(valuesStr);
                if (valuesList.size() == keys.size()) {
                    request.getParam(name);
                    for (int i = 0; i < keys.size(); i++) {
                        restoredValues.get(i).add(valuesList.get(i));
                    }
                }
            }

            // リクエストパラメータに値を設定
            for (int i = 0; i < keys.size(); i++) {
                request.setParam(keys.get(i), StringUtil.toArray(restoredValues.get(i)));
            }
        }
    }

    /**
     * リクエストスコープまたはパラメータに入った値からマルチキーを復元する。
     *
     * @param pageContext ページコンテキスト
     * @param prefix プレフィクス
     * @param keyNames マルチキーのキー名のリスト
     * @return 復元したマルチキーのリスト
     */
    public static List<String> createCompositeKeyValueList(PageContext pageContext, String prefix, List<String> keyNames) {
        List<String> requestValues = null;

        List<Object> valueParamsList = new ArrayList<Object>();
        for (String key : keyNames) {

            Object value = getValue(pageContext, prefix + "." + key, true);
            if (value != null) {
                valueParamsList.add(value);
            }
        }

        if (valueParamsList.size() > 0) {
            if (valueParamsList.get(0) instanceof String[]) {
                // 値が文字列配列の場合の復元
                requestValues = TagUtil.convertStringArrayToRequestValue(valueParamsList);
            } else if (valueParamsList.get(0) instanceof String) {
                // 値が文字列の場合の復元
                requestValues = TagUtil.convertStringToRequestValue(valueParamsList);
            }
            // 値が文字列配列または文字列でなければなにもしない
        }

        if (requestValues == null) {
            // 1リクエストで2回以上この処理が走らないよう、対象がなければ空リストを作成。
            requestValues = new ArrayList<String>();
        }

        return requestValues;
    }

    /**
     * 文字列のパラメータリストから、リクエストパラメータ(値をカンマ区切りしたもの)のリストを復元する。
     *
     * @param valueParamsList 複合キーの各キーを持つ文字列配列
     * @return リクエストパラメータのリスト
     */
    static List<String> convertStringToRequestValue(
            List<Object> valueParamsList) {

        List<String> requestValues = new ArrayList<String>();
        // valueParamsList に配列じゃないもの、配列長が違うものがまぎれていないかチェック
        List<String> joined = new ArrayList<String>();

        for (Object valueParams : valueParamsList) {

            if (!(valueParams instanceof String)) {
                // 一つでも文字列じゃないものがまぎれていたら何もしない。
                return requestValues;
            }

            // 値をつぎはぎ
            joined.add((String) valueParams);

        }
        requestValues.add(StringUtil.join(",", joined));
        return requestValues;
    }

    /**
     * 文字列配列のパラメータリストから、リクエストパラメータ(値をカンマ区切りしたもの)のリストを復元する。 <br />
     *
     * 変換の例
     * <pre>
     *
     * 元の配列
     *
     *   {{ "0001", "0002", "0003" }
     *    { "1001", "1002", "1003" }
     *    { "2001", "2002", "2003" }}
     *
     * 復元後の配列(行列を入れ替えて、カンマ区切りにしたような状態になる)
     *   { "0001,1001,2001",
     *     "0002,1002,2002",
     *     "0003,1003,2003" }
     * </pre>
     *
     * @param valueParamsList 複合キーの各キーを持つ文字列配列
     * @return リクエストパラメータのリスト
     */
    static List<String> convertStringArrayToRequestValue(
            List<Object> valueParamsList) {
        List<String> requestValues = new ArrayList<String>();
        int len = ((String[]) valueParamsList.get(0)).length;

        // valueParamsList に配列じゃないもの、配列長が違うものがまぎれていないかチェック
        for (Object valueParams : valueParamsList) {

            if (!(valueParams instanceof String[])) {
                // 一つでも配列じゃないものがまぎれていたら何もしない。
                return requestValues;
            }
            String[] paramsArray = (String[]) valueParams;
            if (paramsArray.length != len) {
                // 配列長が違うものがまぎれていたら、なにもしない
                return requestValues;
            }
        }

        for (int i = 0; i < len; i++) {
            List<String> joined = new ArrayList<String>();
            for (Object valueParams : valueParamsList) {
                joined.add(((String[]) valueParams)[i]);
            }
            requestValues.add(StringUtil.join(",", joined));
        }
        return requestValues;
    }
}
