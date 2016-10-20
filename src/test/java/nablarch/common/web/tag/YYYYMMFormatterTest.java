package nablarch.common.web.tag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import nablarch.common.web.handler.MockPageContext;
import nablarch.core.ThreadContext;
import nablarch.core.date.SystemTimeProvider;
import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link YYYYMMFormatter}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class YYYYMMFormatterTest {

    private static class FixedSystemDate implements SystemTimeProvider {
        private static final Timestamp TIMESTAMP;
        static {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 2012);
            calendar.set(Calendar.MONTH, 8);
            calendar.set(Calendar.DATE, 20);
            calendar.set(Calendar.HOUR_OF_DAY, 11);
            calendar.set(Calendar.MINUTE, 22);
            calendar.set(Calendar.SECOND, 33);
            calendar.set(Calendar.MILLISECOND, 444);
            TIMESTAMP = new Timestamp(calendar.getTimeInMillis());
        }
        @Override
        public Date getDate() {
            return TIMESTAMP;
        }
        @Override
        public Timestamp getTimestamp() {
            return TIMESTAMP;
        }
    }

    @Before
    public void setup() {
        TagUtil.getCustomTagConfig().setYyyymmPattern("yyyyMM");
        ThreadContext.clear();
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("systemTimeProvider", new FixedSystemDate());
                return data;
            }
        });
    }

    @After
    public void tearDown() {
        ThreadContext.clear();
    }

    /** テスト対象 */
    private YYYYMMFormatter sut = new YYYYMMFormatter();

    /** ページコンテキストのモック */
    private MockPageContext pageContext = new MockPageContext();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * デフォルトの言語(日本語)を使用してフォーマットすること。
     */
    @Test
    public void testFormat() {
        assertThat(sut.format(pageContext, "dateInput", "201210", "yyyy/MM"), is("2012/10"));
        assertThat(sut.format(pageContext, "dateInput", "2012/10", "yyyy/MM"), is("2012/10"));
    }

    /**
     * パターンに区切り文字が含まれていてもフォーマットできること。
     */
    @Test
    public void testFormat_withPatternSeparator() {
        assertThat(sut.format(pageContext, "dateInput", "201210", "yyyy/MM|"), is("2012/10"));
        assertThat(sut.format(pageContext, "dateInput", "201210", "yyyy/MM||"), is("2012/10"));
    }

    /**
     * パターンに言語が指定されている場合、
     * 指定された言語を使用してフォーマットすること。
     */
    @Test
    public void testFormat_withPatternLocale() {
        assertThat(sut.format(pageContext, "dateInput", "Nov 2012", "MMM yyyy|en"), is("Nov 2012"));
        assertThat(sut.format(pageContext, "dateInput", "Nov 2012", "MMM yyyy|en|"), is("Nov 2012"));
        assertThat(sut.format(pageContext, "dateInput", "Nov2012", "MMM yyyy|en"), is("Nov 2012"));
        assertThat(sut.format(pageContext, "dateInput", "201211", "MMM yyyy|en"), is("Nov 2012"));
    }

    /**
     * パターンにタイムゾーンが指定されていてもフォーマットできること。
     */
    @Test
    public void testFormat_withPatternTimeZone() {
        assertThat(sut.format(pageContext, "dateInput", "201210", "yyyy/MM||Brazil/East"), is("2012/10"));
    }

    /**
     * パターンに言語とタイムゾーンが指定されている場合、
     * 指定された言語を使用してフォーマットすること。
     */
    @Test
    public void testFormat_withPatternLocaleAndTimeZone() {
        assertThat(sut.format(pageContext, "dateInput", "Nov 2012", "MMM yyyy|en|Brazil/East"), is("Nov 2012"));
    }

    /**
     * スレッドコンテキストに言語が設定されている場合、
     * スレッドコンテキストの言語を使用してフォーマットすること。
     */
    @Test
    public void testFormat_dependThreadLocal() {
        ThreadContext.setTimeZone(TimeZone.getTimeZone("Brasilia Time, Brazil/East"));
        ThreadContext.setLanguage(Locale.ENGLISH);

        assertThat(sut.format(pageContext, "dateInput", "Nov 2012", "MMM yyyy"), is("Nov 2012"));
    }

    /**
     * 不正なフォーマットが指定された場合、例外を送出すること。
     */
    @Test
    public void testFormat_error() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is("failed to parse string as date. string = [Nov 2012], format = [MMM yyyy]"));

        // 例外が発生すること
        sut.format(pageContext, "dateInput", "Nov 2012", "MMM yyyy");
    }

}
