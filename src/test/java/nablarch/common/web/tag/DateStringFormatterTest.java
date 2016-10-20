package nablarch.common.web.tag;

import static org.junit.Assert.assertEquals;

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

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * {@link DateStringFormatter}テスト。
 * @author Kiyohito Itoh
 */
public class DateStringFormatterTest {

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

    @BeforeClass
    public static void classSetup() {
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("systemTimeProvider", new FixedSystemDate());
                return data;
            }
        });
    }

    @Test
    public void testFormat() {

        ThreadContext.setLanguage(Locale.ENGLISH);

        DateStringFormatter formatter = new DateStringFormatter();
        MockPageContext context = new MockPageContext();

        // 日付フォーマットがタイムゾーンに依存しないことを確認するための設定
        ThreadContext.setTimeZone(TimeZone.getTimeZone("Brasilia Time, Brazil/East"));
        ThreadContext.setLanguage(Locale.JAPANESE);

        assertEquals("2012/10/10", formatter.format(context, "dateImput", "20121010", "yyyy/MM/dd"));
        assertEquals("2012/10/10", formatter.format(context, "dateImput", "2012/10/10", "yyyy/MM/dd"));
        assertEquals("14 11 2012", formatter.format(context, "dateImput", "14 11 2012", "dd MMM yyyy"));
        assertEquals("14 Nov 2012", formatter.format(context, "dateImput", "14 Nov 2012", "dd MMM yyyy|en"));
        assertEquals("14 Nov 2012", formatter.format(context, "dateImput", "14Nov2012", "dd MMM yyyy|en"));
        assertEquals("14 Nov 2012", formatter.format(context, "dateImput", "20121114", "dd MMM yyyy|en"));
    }
}
