package nablarch.common.web.tag;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.hamcrest.Matchers;

import nablarch.common.permission.BasicPermission;
import nablarch.common.permission.Permission;
import nablarch.common.permission.PermissionUtil;
import nablarch.common.web.handler.MockPageContext;
import nablarch.common.web.handler.MockPageContext.MockJspWriter;
import nablarch.common.web.handler.WebTestUtil;
import nablarch.core.ThreadContext;
import nablarch.core.message.Message;
import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.Builder;
import nablarch.core.util.FileUtil;
import nablarch.core.util.FormatSpec;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.MockHttpRequest;
import nablarch.fw.web.servlet.WebFrontController;
import nablarch.test.support.log.app.OnMemoryLogWriter;
import nablarch.test.support.web.servlet.MockServletContext;
import nablarch.test.support.web.servlet.MockServletFilterConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class TagUtilTest {

    private MockPageContext pageContext;

    @Before
    public void setUp() throws Exception {
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("static_content_version", "1.0.0");
                data.put("webFrontController", new WebFrontController());
                data.put("stringResourceHolder", new TagTestUtil.MockMessageResource());
                return data;
            }
        });

        WebFrontController servletFilter = SystemRepository.get("webFrontController");
        servletFilter.setServletFilterConfig(new MockServletFilterConfig().setServletContext(new MockServletContext() {
            @Override
            public URL getResource(String arg0) throws MalformedURLException {
                return FileUtil.getResourceURL("classpath:" + arg0.substring(1));
            }
        }));
        pageContext = new MockPageContext();
    }

    @After
    public void tearDown() throws Exception {
        SystemRepository.clear();
        ((MockJspWriter) pageContext.getOut()).clearOutput();
    }

    @Test
    public void testCreateTagWithoutBody() {
        HtmlAttributes attributes = new HtmlAttributes();
        assertThat(TagUtil.createTagWithoutBody("br", attributes), is("<br />"));
        attributes.put(HtmlAttribute.NAME, "test_name");
        attributes.put(HtmlAttribute.VALUE, "test_value");
        assertThat(TagUtil.createTagWithoutBody("input", attributes), is("<input name=\"test_name\" value=\"test_value\" />"));
    }

    @Test
    public void testPrintForException() {
        JspWriter writer = new MockPageContext.MockJspWriter() {
            @Override
            public void print(String s) throws IOException {
                throw new IOException("test1");
            }
        };
        pageContext.setOut(writer);
        try {
            TagUtil.print(pageContext, null);
            fail("must throw JspException.");
        } catch (JspException e) {
            assertTrue(e.getCause() instanceof IOException);
            assertThat(e.getCause().getMessage(), is("test1"));
        }
    }

    @Test
    public void testEscapeHtml() {

        assertThat(TagUtil.escapeHtml("<\"'&  >", true), is("&lt;&#034;&#039;&amp;&nbsp;&nbsp;&gt;"));
        assertThat(TagUtil.escapeHtml("<\"'&  >", false), is("&lt;&#034;&#039;&amp;  &gt;"));
        assertThat(TagUtil.escapeHtml("<\"'&  >"), is("&lt;&#034;&#039;&amp;  &gt;"));

        assertNull(TagUtil.escapeHtml(null, true));
        assertNull(TagUtil.escapeHtml(null, false));
        assertNull(TagUtil.escapeHtml(null));
        assertThat(TagUtil.escapeHtml("", true), is(""));
        assertThat(TagUtil.escapeHtml("", false), is(""));
        assertThat(TagUtil.escapeHtml(""), is(""));
        assertThat(TagUtil.escapeHtml(" ", true), is("&nbsp;"));
        assertThat(TagUtil.escapeHtml(" ", false), is(" "));
        assertThat(TagUtil.escapeHtml(" "), is(" "));

        assertThat(TagUtil.escapeHtml("aaa\nbbb", true), is("aaa<br />bbb"));
        assertThat(TagUtil.escapeHtml("aaa\nbbb", false), is("aaa\nbbb"));
        assertThat(TagUtil.escapeHtml("aaa\nbbb"), is("aaa\nbbb"));

        assertThat(TagUtil.escapeHtml("aaa\rbbb", true), is("aaa<br />bbb"));
        assertThat(TagUtil.escapeHtml("aaa\rbbb\r", true), is("aaa<br />bbb<br />"));
        assertThat(TagUtil.escapeHtml("aaa\rbbb", false), is("aaa\rbbb"));
        assertThat(TagUtil.escapeHtml("aaa\rbbb\r", false), is("aaa\rbbb\r"));
        assertThat(TagUtil.escapeHtml("aaa\rbbb"), is("aaa\rbbb"));
        assertThat(TagUtil.escapeHtml("aaa\rbbb\r"), is("aaa\rbbb\r"));

        assertThat(TagUtil.escapeHtml("aaa\r\nbbb", true), is("aaa<br />bbb"));
        assertThat(TagUtil.escapeHtml("aaa\r\nbbb", false), is("aaa\r\nbbb"));
        assertThat(TagUtil.escapeHtml("aaa\r\nbbb"), is("aaa\r\nbbb"));

        assertThat(TagUtil.escapeHtml("aaa\nbbb\rccc\r\nddd", true), is("aaa<br />bbb<br />ccc<br />ddd"));
        assertThat(TagUtil.escapeHtml("aaa\nbbb\rccc\r\nddd", false), is("aaa\nbbb\rccc\r\nddd"));
        assertThat(TagUtil.escapeHtml("aaa\nbbb\rccc\r\nddd"), is("aaa\nbbb\rccc\r\nddd"));

        assertThat(TagUtil.escapeHtml("aaa\r\nbbb" + TagTestUtil.HTML, true), is("aaa<br />bbb" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT));
        assertThat(TagUtil.escapeHtml("aaa\r\nbbb" + TagTestUtil.HTML, false), is("aaa\r\nbbb" + TagTestUtil.ESC_HTML));
        assertThat(TagUtil.escapeHtml("aaa\r\nbbb" + TagTestUtil.HTML), is("aaa\r\nbbb" + TagTestUtil.ESC_HTML));

        assertThat(TagUtil.escapeHtml("aaa\\bbb", true), is("aaa\\bbb"));
        assertThat(TagUtil.escapeHtml("aaa\\bbb", false), is("aaa\\bbb"));
        assertThat(TagUtil.escapeHtml("aaa\\bbb"), is("aaa\\bbb"));
    }

    @Test
    public void testEscapeJavaScriptString() {
        assertNull(TagUtil.escapeJavaScriptString(null));
        assertThat(TagUtil.escapeJavaScriptString(""), is(""));
        assertThat(TagUtil.escapeJavaScriptString(" "), is(" "));
        assertThat(TagUtil.escapeJavaScriptString("\\"), is("\\\\"));
        assertThat(TagUtil.escapeJavaScriptString("\""), is("\\\""));
        assertThat(TagUtil.escapeJavaScriptString("'"), is("\\'"));
        assertThat(TagUtil.escapeJavaScriptString("\\\"'"), is("\\\\\\\"\\'"));
        assertThat(TagUtil.escapeJavaScriptString("\"'\\"), is("\\\"\\'\\\\"));
        assertThat(TagUtil.escapeJavaScriptString("'\\\""), is("\\'\\\\\\\""));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFormatValue() throws Exception {

        MockPageContext pageContext = new MockPageContext();
        String name = "name_test";

        // value: null
        assertNull(TagUtil.formatValue(pageContext, name, spec("date{yyyy/MM/dd}"), null));
        assertNull(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), null));
        assertNull(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), null));
        assertNull(TagUtil.formatValue(pageContext, name, spec("decimal{##.##}"), null));

        // value: ""
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), ""), is(""));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), ""), is(""));
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{##.##}"), ""), is(""));

        // format: null
        try {
            TagUtil.formatValue(pageContext, name, null, new Object());
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("formatSpec must not be null."));
        }

        // format: invalid blacket
        try {
            TagUtil.formatValue(pageContext, name, spec("dateString[yyyy/MM/dd]"), "20100701");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("valueFormat attribute was invalid. expected = [<data type>{<pattern>}] actual = [dateString[yyyy/MM/dd]], data type = [dateString[yyyy/MM/dd]]"));
        }
        try {
            TagUtil.formatValue(pageContext, name, spec("yyyymmdd[yyyy/MM/dd]"), "20100701");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("valueFormat attribute was invalid. expected = [<data type>{<pattern>}] actual = [yyyymmdd[yyyy/MM/dd]], data type = [yyyymmdd[yyyy/MM/dd]]"));
        }

        // format: invalid data type
        try {
            TagUtil.formatValue(pageContext, name, spec("time{yyyy/MM/dd}"), new Date());
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("valueFormat attribute was invalid. expected = [<data type>{<pattern>}] actual = [time{yyyy/MM/dd}], data type = [time]"));
        }

        // format: invalid type of value
        Date date = new Date();
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), date), is(date.toString()));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), date), is(date.toString()));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), new BigDecimal("0.0000000001")), is("0.0000000001"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), new BigDecimal("0.0000000001")), is("0.0000000001"));

        TimeZone tokyo = TimeZone.getTimeZone("Asia/Tokyo");

        TagTestUtil.setUpDefaultConfig();
        ThreadContext.setTimeZone(tokyo);
        ThreadContext.setLanguage(new Locale("ja"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(tokyo);
        date = sdf.parse("2010/08/11 19:10:59");

        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime"), date), is("2010:08:11 19:10:59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{|}"), date), is("2010:08:11 19:10:59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{|ja|Europe/Madrid}"), date), is("2010:08:11 12:10:59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{|it_IT|Europe/Madrid}"), date), is("2010:08:11 12:10:59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{yyyy/MM/dd}"), date), is("2010/08/11"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{yy-MM-dd HH_mm_ss}"), date), is("10-08-11 19_10_59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{yy-MM-dd HH_mm_ss|ja|Europe/Madrid}"), date), is("10-08-11 12_10_59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{yy-MM-dd HH_mm_ss||Europe/Madrid}"), date), is("10-08-11 12_10_59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" dateTime "), date), is("2010:08:11 19:10:59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" dateTime { |ja_JP| Europe/Madrid } "), date), is("2010:08:11 12:10:59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" dateTime { yyyy/MM/dd } "), date), is("2010/08/11"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" dateTime { yy-MM-dd HH_mm_ss } "), date), is("10-08-11 19_10_59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" dateTime { yy-MM-dd HH_mm_ss | ja_JP | Europe/Madrid } "), date), is("10-08-11 12_10_59"));


        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{|}"), 123.456), is("123.456"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{00,000.00000}"), 123.456), is("00,123.45600"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{00,000.00000|es}"), 123.456), is("00.123,45600"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{#####.00%}"), 0.998), is("99.80%"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{#####.00%|es}"), 0.998), is("99,80%"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{00000.00000}"), "789,123.456"), is("789123.45600"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{00000.00000|es}"), "789.123,456"), is("789123,45600"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" decimal { 00,000.00000 } "), 123.456), is("00,123.45600"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" decimal { 00,000.00000 | es } "), 123.456), is("00.123,45600"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" decimal { #####.00% } "), 0.998), is("99.80%"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" decimal { #####.00% | es } "), 0.998), is("99,80%"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" decimal { 00000.00000 } "), "789,123.456"), is("789123.45600"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" decimal { 00000.00000 | es } "), "789.123,456"), is("789123,45600"));

        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString"), "20100701"), is("2010-07-01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), "20100701"), is("2010/07/01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), "2010/07/01"), is("2010/07/01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/M/d}"), "201071"), is("2010/7/1"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/M/d}"), "2010/7/1"), is("2010/7/1"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{MM/dd/yyyy}"), "07012010"), is("07/01/2010"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{MM/dd/yyyy}"), "07/01/2010"), is("07/01/2010"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" dateString "), "20100701"), is("2010-07-01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" dateString { yyyy/MM/dd } "), "20100701"), is("2010/07/01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" dateString { yyyy/M/d } "), "201071"), is("2010/7/1"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd"), "20100701"), is("2010.07.01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), "20100701"), is("2010/07/01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), "2010/07/01"), is("2010/07/01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/M/d}"), "201071"), is("2010/7/1"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/M/d}"), "2010/7/1"), is("2010/7/1"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{MM/dd/yyyy}"), "07012010"), is("07/01/2010"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{MM/dd/yyyy}"), "07/01/2010"), is("07/01/2010"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" yyyymmdd "), "20100701"), is("2010.07.01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" yyyymmdd { yyyy/MM/dd } "), "20100701"), is("2010/07/01"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" yyyymmdd { yyyy/M/d } "), "201071"), is("2010/7/1"));

        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{||Europe/Madrid}"), date), is("2010:08:11 12:10:59"));

        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{yyyy年MMM月d日(E) a hh:mm|ja|America/New_York}"), date), is("2010年8月11日(水) 午前 06:10"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{yyyy年MMM月d日(E) a hh:mm|ja_JP|Asia/Tokyo}"), date), is("2010年8月11日(水) 午後 07:10"));

        ThreadContext.setLanguage(Locale.JAPANESE);
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{yyyy年MMM月d日(E) a hh:mm||Asia/Tokyo}"), date), is("2010年8月11日(水) 午後 07:10"));
        ThreadContext.setLanguage(Locale.ENGLISH);
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{yyyy年MMM月d日(E) a hh:mm||Asia/Tokyo}"), date), is("2010年Aug月11日(Wed) PM 07:10"));

        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{MMM. d(E), yyyy a hh:mm|en|America/New_York}"), date), is("Aug. 11(Wed), 2010 AM 06:10"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{MMM. d(E), yyyy a hh:mm|en_US|Asia/Tokyo}"), date), is("Aug. 11(Wed), 2010 PM 07:10"));

        // フォーマットの区切り文字を変更した場合
        TagUtil.getCustomTagConfig().setPatternSeparator("#");
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateTime{yy-MM-dd HH_mm_ss#ja_JP#Europe/Madrid}"), date), is("10-08-11 12_10_59"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" dateTime { yy-MM-dd HH_mm_ss # ja # Europe/Madrid } "), date), is("10-08-11 12_10_59"));

        // フォーマットの区切り文字を変更した場合
        TagUtil.getCustomTagConfig().setPatternSeparator("@");
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{00000.00000@es}"), "789.123,456"), is("789123,45600"));
        assertThat(TagUtil.formatValue(pageContext, name, spec(" decimal { 00000.00000 @ es } "), "789.123,456"), is("789123,45600"));

        // 日付文字列フォーマットのテスト
        // DBの値(=値の取得先がリクエストスパラメータでない)
        // DBの値はyyyyMMdd形式の文字列。

        String value = "20110928";
        pageContext = new MockPageContext();
        pageContext.setAttribute(name, value, PageContext.REQUEST_SCOPE);
        TagUtil.getCustomTagConfig().setPatternSeparator("|");

        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), value), is("2011/09/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{MM/dd/yyyy}"), value), is("09/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/M/d}"), value), is("2011/9/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{M/d/yyyy}"), value), is("9/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), value), is("2011/09/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{MM/dd/yyyy}"), value), is("09/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/M/d}"), value), is("2011/9/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{M/d/yyyy}"), value), is("9/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{MMM/d/yyyy|en}"), value), is("Sep/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy年MMM月d日(E)|ja_JP|Asia/Tokyo}"), value), is("2011年9月28日(水)"));



        value = "20110928";
        pageContext = new MockPageContext();
        pageContext.setAttribute(name, value, PageContext.SESSION_SCOPE);
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), value), is("2011/09/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{MM/dd/yyyy}"), value), is("09/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/M/d}"), value), is("2011/9/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{M/d/yyyy}"), value), is("9/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), value), is("2011/09/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{MM/dd/yyyy}"), value), is("09/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/M/d}"), value), is("2011/9/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{M/d/yyyy}"), value), is("9/28/2011"));
        // 日付文字列フォーマットのテスト
        // フォーマット通りの入力値(=値の取得先がリクエストスパラメータである)

        value = "2011/09/28";
        pageContext = new MockPageContext();
        pageContext.getRequest().getParameterMap().put(name, value);
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), value), is("2011/09/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), value), is("2011/09/28"));

        value = "09/28/2011";
        pageContext = new MockPageContext();
        pageContext.getRequest().getParameterMap().put(name, value);
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{MM/dd/yyyy}"), value), is("09/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{MM/dd/yyyy}"), value), is("09/28/2011"));

        // 日付文字列フォーマットのテスト
        // 年月日区切り文字が省略された入力値(=値の取得先がリクエストスパラメータである)

        value = "20110928";
        pageContext = new MockPageContext();
        pageContext.getRequest().getParameterMap().put(name, value);
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{yyyy/MM/dd}"), value), is("2011/09/28"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{yyyy/MM/dd}"), value), is("2011/09/28"));

        value = "09282011";
        pageContext = new MockPageContext();
        pageContext.getRequest().getParameterMap().put(name, value);
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{MM/dd/yyyy}"), value), is("09/28/2011"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{MM/dd/yyyy}"), value), is("09/28/2011"));

        // 日付文字列フォーマットのテスト
        // リクエストパラメータとリクエストスコープで名前が重複した場合

        pageContext = new MockPageContext();
        pageContext.getRequest().getParameterMap().put(name, "01312012");
        pageContext.setAttribute(name, "20120131", PageContext.REQUEST_SCOPE);
        assertThat(TagUtil.formatValue(pageContext, name, spec("dateString{MM/dd/yyyy}"), "20120131"), is("01/31/2012"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("yyyymmdd{MM/dd/yyyy}"), "20120131"), is("01/31/2012"));

        ThreadContext.setLanguage(Locale.GERMAN);
        assertThat(TagUtil.formatValue(pageContext, "number", spec("decimal{###,###}"), "1234"), is("1.234"));


        ThreadContext.clear();
        assertThat(TagUtil.formatValue(pageContext, "number", spec("decimal{###,###}"), "1234"), is("1,234"));
    }

    /**
     * デフォルトロケールで日付をフォーマットできること。
     * @throws Exception
     */
    @Test
    public void testFormatDate_default() throws Exception {
        ThreadContext.clear();

        Date date = new SimpleDateFormat("yyyyMMdd").parse("20161231");
        assertThat(TagUtil.formatDate(date, "dd MMM yyyy"), is("31 12 2016"));
        assertThat(TagUtil.formatDate(date, "dd MMM yyyy|"), is("31 12 2016"));
        assertThat(TagUtil.formatDate(date, "dd MMM yyyy||"), is("31 12 2016"));
    }

    /**
     * スレッドコンテキストの言語で日付をフォーマットできること。
     *
     * @throws Exception
     */
    @Test
    public void testFormatDate_threadContext() throws Exception {
        ThreadContext.setLanguage(Locale.ENGLISH);

        Date date = new SimpleDateFormat("yyyyMMdd").parse("20161231");
        assertThat(TagUtil.formatDate(date, "dd MMM yyyy"), is("31 Dec 2016"));
        assertThat(TagUtil.formatDate(date, "dd MMM yyyy|"), is("31 Dec 2016"));
        assertThat(TagUtil.formatDate(date, "dd MMM yyyy||"), is("31 Dec 2016"));
    }

    /**
     * パターン文字列に設定された言語で日付をフォーマットできること。
     *
     * @throws Exception
     */
    @Test
    public void testFormatDate_patternHolder() throws Exception {
        ThreadContext.clear();

        Date date = new SimpleDateFormat("yyyyMMdd").parse("20161231");
        assertThat(TagUtil.formatDate(date, "dd MMM yyyy|en"), is("31 Dec 2016"));
        assertThat(TagUtil.formatDate(date, "dd MMM yyyy|en|"), is("31 Dec 2016"));
    }

    /**
     * デフォルトロケール、タイムゾーンで日時をフォーマットできること。
     * @throws Exception
     */
    @Test
    public void testFormatDateTime_default() throws Exception {
        ThreadContext.clear();

        Date date = new SimpleDateFormat("yyyyMMddhhmmss").parse("20161231123456");
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss"), is("31 12 2016 12:34:56"));
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss|"), is("31 12 2016 12:34:56"));
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss||"), is("31 12 2016 12:34:56"));
    }

    /**
     * スレッドコンテキストの言語、タイムゾーンで日時をフォーマットできること。
     *
     * @throws Exception
     */
    @Test
    public void testFormatDateTime_threadContext() throws Exception {
        ThreadContext.setLanguage(Locale.ENGLISH);
        ThreadContext.setTimeZone(TimeZone.getTimeZone("Brazil/East"));

        Date date = new SimpleDateFormat("yyyyMMddhhmmss").parse("20161231123456");
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss"), is("30 Dec 2016 01:34:56"));
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss|"), is("30 Dec 2016 01:34:56"));
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss||"), is("30 Dec 2016 01:34:56"));
    }

    /**
     * パターン文字列に設定された言語、タイムゾーンで日時をフォーマットできること。
     *
     * @throws Exception
     */
    @Test
    public void testFormatDateTime_patternHolder() throws Exception {
        ThreadContext.clear();

        Date date = new SimpleDateFormat("yyyyMMddhhmmss").parse("20161231123456");
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss|en"), is("31 Dec 2016 12:34:56"));
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss|en|"), is("31 Dec 2016 12:34:56"));
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss|en|Brazil/East"), is("30 Dec 2016 01:34:56"));
        assertThat(TagUtil.formatDateTime(date, "dd MMM yyyy hh:mm:ss||Brazil/East"), is("30 12 2016 01:34:56"));
    }

    private FormatSpec spec(String format) {
        return FormatSpec.valueOf(format, "|");
    }

    @Test
    public void testEncodeUri() {

        try {
            TagUtil.encodeUri(pageContext, null, Boolean.TRUE);
            fail("must be thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("uri is null."));
        }

        assertThat(TagUtil.encodeUri(pageContext, "http://test.com/", null), is("http://test.com/" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(TagUtil.encodeUri(pageContext, "https://test.com/", null), is("https://test.com/" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(TagUtil.encodeUri(pageContext, "/test", null), is(WebTestUtil.CONTEXT_PATH + "/test" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(TagUtil.encodeUri(pageContext, "./test", null), is("./test" + WebTestUtil.ENCODE_URL_SUFFIX));

        try {
            TagUtil.encodeUri(pageContext, "http://test.com/", Boolean.TRUE);
            fail("must be thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().indexOf("http://test.com/") != -1);
        }
        try {
            TagUtil.encodeUri(pageContext, "http://test.com/", Boolean.FALSE);
            fail("must be thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().indexOf("http://test.com/") != -1);
        }
        try {
            TagUtil.encodeUri(pageContext, "./test", Boolean.TRUE);
            fail("must be thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().indexOf("./test") != -1);
        }
        try {
            TagUtil.encodeUri(pageContext, "./test", Boolean.FALSE);
            fail("must be thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().indexOf("./test") != -1);
        }

        try {
            TagUtil.encodeUri(pageContext, "/test", Boolean.TRUE);
            fail("must be thrown IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().indexOf("host property") != -1);
        }

        TagTestUtil.setUpDefaultConfig();

        assertThat(TagUtil.encodeUri(pageContext, "/test", Boolean.TRUE),
                   is("https://nablarch.co.jp:443" + WebTestUtil.CONTEXT_PATH + "/test" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(TagUtil.encodeUri(pageContext, "/test", Boolean.FALSE),
                is("http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/test" + WebTestUtil.ENCODE_URL_SUFFIX));

        SystemRepository.clear();

        TagTestUtil.setUpDefaultConfigWithoutPort();

        assertThat(TagUtil.encodeUri(pageContext, "/test", Boolean.TRUE),
                   is("https://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/test" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(TagUtil.encodeUri(pageContext, "/test", Boolean.FALSE),
                is("http://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/test" + WebTestUtil.ENCODE_URL_SUFFIX));

        pageContext.getMockReq().setContextPath("");

        assertThat(TagUtil.encodeUri(pageContext, "/test", Boolean.TRUE),
                   is("https://nablarch.co.jp/test" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(TagUtil.encodeUri(pageContext, "/test", Boolean.FALSE),
                is("http://nablarch.co.jp/test" + WebTestUtil.ENCODE_URL_SUFFIX));

    }

    @SuppressWarnings("serial")
    @Test
    public void testGetSingleValue() {

        pageContext.setAttribute("array", new String[] {"aaa"});
        pageContext.setAttribute("collection", new ArrayList<String>() { { this.add("bbb"); } });

        assertThat(TagUtil.getSingleValue(pageContext, "array").toString(), is("aaa"));
        assertThat(TagUtil.getSingleValue(pageContext, "collection").toString(), is("bbb"));

        pageContext.setAttribute("array", new String[] {"aaa", "aaa2"});
        pageContext.setAttribute("collection", new ArrayList<String>() { { this.add("bbb"); this.add("bbb2"); } });

        OnMemoryLogWriter.clear();

        assertNull(TagUtil.getSingleValue(pageContext, "array"));
        assertThat(OnMemoryLogWriter.getMessages("writer.appLog").get(0),
                   containsString("value wasn't single value. name = [array]"));

        assertNull(TagUtil.getSingleValue(pageContext, "collection"));
        assertThat(OnMemoryLogWriter.getMessages("writer.appLog").get(1),
                   containsString("value wasn't single value. name = [collection]"));
    }
    
    /**
     * {@link TagUtil#getSingleValueOnScope(PageContext, String)}のテスト。
     */
    @SuppressWarnings({ "serial", "unchecked" })
    @Test
    public void testGetSingleValueOnScope() {
        // 変数スコープから取得できることを確認
        pageContext.setAttribute("array", new String[] {"aaa"});
        pageContext.setAttribute("collection", new ArrayList<String>() { { this.add("bbb"); } });
        assertThat(TagUtil.getSingleValueOnScope(pageContext, "array").toString(), is("aaa"));
        assertThat(TagUtil.getSingleValueOnScope(pageContext, "collection").toString(), is("bbb"));
        
        // リクエストパラメータから取得できないことを確認
        pageContext.getRequest().getParameterMap().put("param", new String[] {"test"});
        assertThat(TagUtil.getSingleValueOnScope(pageContext, "param"), is(nullValue()));

        // ログの確認
        pageContext.setAttribute("array", new String[] {"aaa", "aaa2"});
        pageContext.setAttribute("collection", new ArrayList<String>() { { this.add("bbb"); this.add("bbb2"); } });

        OnMemoryLogWriter.clear();

        assertNull(TagUtil.getSingleValueOnScope(pageContext, "array"));
        assertThat(OnMemoryLogWriter.getMessages("writer.appLog").get(0),
                   containsString("value wasn't single value. name = [array]"));

        assertNull(TagUtil.getSingleValueOnScope(pageContext, "collection"));
        assertThat(OnMemoryLogWriter.getMessages("writer.appLog").get(1),
                   containsString("value wasn't single value. name = [collection]"));
    }

    @SuppressWarnings({ "serial", "unchecked" })
    @Test
    public void testGetMultipleValues() {

        pageContext.setAttribute("string", "zzz");
        pageContext.setAttribute("array", new String[] {"aaa"});
        pageContext.setAttribute("collection", new ArrayList<String>() { { this.add("bbb"); } });

        List<String> values = (List<String>) TagUtil.getMultipleValues(pageContext, "string");
        assertThat(values.size(), is(1));
        assertThat(values.get(0), is("zzz"));

        values = (List<String>) TagUtil.getMultipleValues(pageContext, "array");
        assertThat(values.size(), is(1));
        assertThat(values.get(0), is("aaa"));

        values = (List<String>) TagUtil.getMultipleValues(pageContext, "collection");
        assertThat(values.size(), is(1));
        assertThat(values.get(0), is("bbb"));

        pageContext.setAttribute("array", new int[] {1, 11});
        pageContext.setAttribute("collection", new ArrayList<String>() { { this.add("bbb"); this.add("bbb2"); } });

        assertThat(TagUtil.getMultipleValues(pageContext, "array"), Matchers.<Object>contains(1, 11));

        values = (List<String>) TagUtil.getMultipleValues(pageContext, "collection");
        assertThat(values.size(), is(2));
        assertThat(values.get(0), is("bbb"));
        assertThat(values.get(1), is("bbb2"));
    }
    
    /**
     * {@link TagUtil#getMultipleValuesOnScope(PageContext, String)}のテスト。
     */
    @SuppressWarnings({ "serial", "unchecked" })
    @Test
    public void testGetMultipleValuesOnScope() {

        // 単一の値を取得できることを確認
        pageContext.setAttribute("string", "zzz");
        pageContext.setAttribute("array", new String[] {"aaa"});
        pageContext.setAttribute("collection", new ArrayList<String>() { { this.add("bbb"); } });

        List<String> values = (List<String>) TagUtil.getMultipleValues(pageContext, "string");
        assertThat(values.size(), is(1));
        assertThat(values.get(0), is("zzz"));

        values = (List<String>) TagUtil.getMultipleValues(pageContext, "array");
        assertThat(values.size(), is(1));
        assertThat(values.get(0), is("aaa"));

        values = (List<String>) TagUtil.getMultipleValues(pageContext, "collection");
        assertThat(values.size(), is(1));
        assertThat(values.get(0), is("bbb"));

        // 配列、コレクションの複数の値を取得できることを確認
        pageContext.setAttribute("array", new String[] {"aaa", "aaa2"});
        pageContext.setAttribute("collection", new ArrayList<String>() { { this.add("bbb"); this.add("bbb2"); } });

        values = (List<String>) TagUtil.getMultipleValuesOnScope(pageContext, "array");
        assertThat(values.size(), is(2));
        assertThat(values.get(0), is("aaa"));
        assertThat(values.get(1), is("aaa2"));

        values = (List<String>) TagUtil.getMultipleValuesOnScope(pageContext, "collection");
        assertThat(values.size(), is(2));
        assertThat(values.get(0), is("bbb"));
        assertThat(values.get(1), is("bbb2"));
        
        // リクエストパラメータの値を取得できないことを確認
        pageContext.getRequest().getParameterMap().put("param", new String[] {"test"});
        values = (List<String>) TagUtil.getMultipleValuesOnScope(pageContext, "param");
        assertThat(values.size(), is(0));
    }

    /**
     * sessionが存在しない場合にgetValueされた場合のテスト
     */
    @Test
    public void testGetValueInNoSession() {
        MockPageContext pageContext = new MockPageContext() {
            private boolean isSession(int scope) {
                return PageContext.SESSION_SCOPE == scope;
            }
            @Override
            public Object getAttribute(String name, int scope) {
                assertFalse("getAttributeのsessionScopeにアクセスしています。", isSession(scope));
                return super.getAttribute(name, scope);
            }
            @Override
            public void setAttribute(String name, Object value, int scope) {
                assertFalse("setAttributeのsessionScopeにアクセスしています。", isSession(scope));
                super.setAttribute(name, value, scope);
            }
            @Override
            public void removeAttribute(String name, int scope) {
                assertFalse("removeAttributeのsessionScopeにアクセスしています。", isSession(scope));
                super.getAttribute(name, scope);
            }
            @Override
            public Map<String, Object> getAttributes(int scope) {
                assertFalse("getAttributesのsessionScopeにアクセスしています。", isSession(scope));
                return super.getAttributes(scope);
            }
            @Override
            public HttpSession getSession() {
                return null;
            }
        };
        assertThat("nullが返却され、エラーにならない。", TagUtil.getValue(pageContext, "null", false), nullValue());
        assertThat("nullが返却され、エラーにならない。", TagUtil.getValue(pageContext, "null", true), nullValue());
    }
    
    @Test
    public void testGetValueForDuplicatedName() {

        // リクエストパラメータ、セッションスコープで名前が重複する場合
        pageContext.getMockReq().getParams().put("sample.string", new String[] {"value_test"});
        pageContext.setAttribute("sample", new Sample("sec_"), PageContext.SESSION_SCOPE);

        assertThat("取得先にリクエストパラメータを含めないとセッションスコープの値が取得される。",
                   TagUtil.getValue(pageContext, "sample.string", false).toString(), is("sec_string0"));
        assertThat("取得先にリクエストパラメータを含めるとリクエストスコープの値が取得される。",
                   Array.get(TagUtil.getValue(pageContext, "sample.string", true), 0).toString(), is("value_test"));

        // リクエストスコープ、リクエストパラメータ、セッションスコープで名前が重複する場合
        pageContext.setAttribute("sample", new Sample("req_"), PageContext.REQUEST_SCOPE);

        assertThat("取得先にリクエストパラメータを含めないとリクエストスコープの値が取得される。",
                   TagUtil.getValue(pageContext, "sample.string", false).toString(), is("req_string0"));
        assertThat("取得先にリクエストパラメータを含めてもリクエストスコープの値が取得される。",
                   TagUtil.getValue(pageContext, "sample.string", true).toString(), is("req_string0"));

        pageContext.setAttribute("sample", new Sample("pag_"), PageContext.PAGE_SCOPE);

        assertThat("取得先にリクエストパラメータを含めないとページスコープの値が取得される。",
                   TagUtil.getValue(pageContext, "sample.string", false).toString(), is("pag_string0"));
        assertThat("取得先にリクエストパラメータを含めてもページスコープの値が取得される。",
                   TagUtil.getValue(pageContext, "sample.string", true).toString(), is("pag_string0"));

    }

    /**
     * UseValueAsNullIfObjectExists が true の場合のテスト。
     */
    @Test
    public void testGetValueForUseValueAsNullIfObjectExists() {

        // オブジェクトが null の場合には null を使うよう設定
        TagUtil.getCustomTagConfig().setUseValueAsNullIfObjectExists(true);

        // セッションコンテキストにオブジェクトがある場合
        Sample sessionObj = new Sample("sec_");
        pageContext.setAttribute("sample", sessionObj, PageContext.SESSION_SCOPE);
        sessionObj.setString(null);
        assertThat("", TagUtil.getValue(pageContext, "sample.string", false), nullValue());
        sessionObj.setString("sessionObj");
        assertThat("", (String) TagUtil.getValue(pageContext, "sample.string", false), is("sessionObj"));

        // リクエストスコープではなく、リクエストパラメータが使用されないことの確認のために
        // 先にリクエストパラメータを設定。
        pageContext.getMockReq().getParams().put("sample.string", new String[] {"value_test"});

        // リクエストコンテキストにオブジェクトがある場合
        Sample requestObj = new Sample("req_");
        pageContext.setAttribute("sample", requestObj, PageContext.REQUEST_SCOPE);
        requestObj.setString(null);
        assertThat("", TagUtil.getValue(pageContext, "sample.string", false), nullValue());
        requestObj.setString("requestObj");
        assertThat("", (String) TagUtil.getValue(pageContext, "sample.string", false), is("requestObj"));

        // ページコンテキストにオブジェクトがある場合

        Sample pageObj = new Sample("page_");
        pageContext.setAttribute("sample", pageObj, PageContext.PAGE_SCOPE);
        pageObj.setString(null);
        assertThat("", TagUtil.getValue(pageContext, "sample.string", false), nullValue());
        pageObj.setString("pageObj");
        assertThat("", (String) TagUtil.getValue(pageContext, "sample.string", false), is("pageObj"));

    }

    @Test
    public void testGetValueForUseValueAsNullIfPropertyIsExists() {

        // オブジェクトが null の場合、 リクエストパラメータを使うよう設定
        TagUtil.getCustomTagConfig().setUseValueAsNullIfObjectExists(false);

        // セッションコンテキストにオブジェクトがある場合(これは設定によらず同じ動き)
        Sample sessionObj = new Sample("sec_");
        pageContext.setAttribute("sample", sessionObj, PageContext.SESSION_SCOPE);
        sessionObj.setString(null);
        assertThat("", TagUtil.getValue(pageContext, "sample.string", false), nullValue());
        sessionObj.setString("sessionObj");
        assertThat("", (String) TagUtil.getValue(pageContext, "sample.string", false), is("sessionObj"));
        // 値をクリア
        pageContext.setAttribute("sample", null, PageContext.SESSION_SCOPE);

        // リクエストスコープではなく、リクエストパラメータが使用されることの確認
        pageContext.getMockReq().getParams().put("sample.string", new String[] {"value_test"});

        // リクエストコンテキストにオブジェクトがある場合
        Sample requestObj = new Sample("req_");
        pageContext.setAttribute("sample", requestObj, PageContext.REQUEST_SCOPE);
        requestObj.setString(null);
        assertThat("", Array.get(TagUtil.getValue(pageContext, "sample.string", true), 0).toString() , is("value_test"));
        requestObj.setString("requestObj");
        assertThat("", (String) TagUtil.getValue(pageContext, "sample.string", false), is("requestObj"));
        // 値をクリア
        pageContext.setAttribute("sample", null, PageContext.REQUEST_SCOPE);

        // ページコンテキストにオブジェクトがある場合

        Sample pageObj = new Sample("page_");
        pageContext.setAttribute("sample", pageObj, PageContext.PAGE_SCOPE);
        pageObj.setString(null);
        assertThat("", Array.get(TagUtil.getValue(pageContext, "sample.string", true), 0).toString(), is("value_test"));
        pageObj.setString("pageObj");
        assertThat("", (String) TagUtil.getValue(pageContext, "sample.string", false), is("pageObj"));

    }
    @Test
    public void testContainsForInvalidArgs() {

        String value = "dummy";

        // values == null
        Collection<String> values = null;
        assertFalse(TagUtil.contains(values, value));

        // values == empty
        values = Collections.emptyList();
        assertFalse(TagUtil.contains(values, value));

        // value == null
        values = new ArrayList<String>();
        values.add("dummy");
        value = null;
        assertFalse(TagUtil.contains(values, value));
        
        assertFalse(TagUtil.contains(Arrays.asList(null, "abc", null), "aaa"));
        assertTrue(TagUtil.contains(Arrays.asList(null, "abc", null), "abc"));
    }

    @Test
    public void testFindMessageForInvalidArgs() {

        // messages == null
        Set<String> names = new HashSet<String>();
        names.add("dummy");
        assertNull(TagUtil.findMessage(pageContext, names));

        TagTestUtil.setErrorMessages(pageContext);

        // names == null
        names = null;
        assertNull(TagUtil.findMessage(pageContext, names));

        // names == empty
        names = Collections.emptySet();
        assertNull(TagUtil.findMessage(pageContext, names));
    }

    @Test
    public void testGetMessages() {

        // 空の場合
        List<Message> messages = TagUtil.getMessages(pageContext);
        assertNull(messages);

        // ApplicationExceptionに12件のメッセージが設定されている場合
        TagTestUtil.setErrorMessages(pageContext);
        messages = TagUtil.getMessages(pageContext);
        assertThat(messages.size(), is(13));

        // ApplicationException以外の例外が設定されている場合
        TagTestUtil.setErrorMessages(pageContext, new IllegalArgumentException("ignore"));
        messages = TagUtil.getMessages(pageContext);
        assertNull(messages);
    }

    @Test
    public void testGetOriginalAttribute() {
        HtmlAttribute attribute = HtmlAttribute.CLASS;
        HtmlAttributes attributes = new HtmlAttributes();
        assertThat(TagUtil.getOriginalAttribute(attributes, attribute, ";"), is(""));
        attributes.put(attribute, "class_test");
        assertThat(TagUtil.getOriginalAttribute(attributes, attribute, ";"), is("class_test;"));
        attributes.put(attribute, "class_test;");
        assertThat(TagUtil.getOriginalAttribute(attributes, attribute, ";"), is("class_test;"));
    }

    @Test
    public void testGetValue() {

        try {
            TagUtil.getValue(null, "test", true);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("pageContext is null."));
        }
        try {
            TagUtil.getValue(pageContext, null, true);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("name is null or blank."));
        }
        try {
            TagUtil.getValue(pageContext, "", true);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("name is null or blank."));
        }

        pageContext.setAttribute("sample", new Sample("sec_"), PageContext.SESSION_SCOPE);
        assertValues("sec_");

        pageContext.setAttribute("sample", new Sample("req_"), PageContext.REQUEST_SCOPE);
        assertValues("req_");

        pageContext.setAttribute("sample", new Sample("pag_"), PageContext.PAGE_SCOPE);
        assertValues("pag_");

        try {
            TagUtil.getValue(pageContext, "sample.list[key0].name", true);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("failed to parse. name = [sample.list[key0].name]"));
        }

        // over index
        assertNull(TagUtil.getValue(pageContext, "sample.array[3]", true));
        assertNull(TagUtil.getValue(pageContext, "sample.list[3]", true));

        // type mismatch for xxx[index]
        pageContext.setAttribute("notList", "notList");
        try {
            TagUtil.getValue(pageContext, "notList[1]", false);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("failed to parse. name = [notList[1]]"));
            assertThat(e.getCause().getMessage(), is("invalid type. type = [class java.lang.String]"));
        }

        assertNull(TagUtil.getValue(pageContext, "rootList[0]", true));

        // root is list
        String prefix = "rootList_";
        pageContext.setAttribute("rootList", new Sample(prefix).getList());
        assertThat(TagUtil.getValue(pageContext, "rootList[0]", true).toString(), is(prefix + "list0"));
        assertThat(TagUtil.getValue(pageContext, "rootList[1]", true).toString(), is(prefix + "list1"));
        assertThat(TagUtil.getValue(pageContext, "rootList[2]", true).toString(), is(prefix + "list2"));

        // root is array
        prefix = "rootArray_";
        pageContext.setAttribute("rootArray", new Sample(prefix).getArray());
        assertThat(TagUtil.getValue(pageContext, "rootArray[0]", true).toString(), is(prefix + "array0"));
        assertThat(TagUtil.getValue(pageContext, "rootArray[1]", true).toString(), is(prefix + "array1"));
        assertThat(TagUtil.getValue(pageContext, "rootArray[2]", true).toString(), is(prefix + "array2"));

        // root is map
        prefix = "rootMap";
        pageContext.setAttribute("rootMap", new Sample(prefix).getMap());
        assertThat(TagUtil.getValue(pageContext, "rootMap.key0", true).toString(), is(prefix + "map0"));
        assertThat(TagUtil.getValue(pageContext, "rootMap.key1", true).toString(), is(prefix + "map1"));
        assertThat(TagUtil.getValue(pageContext, "rootMap.key2", true).toString(), is(prefix + "map2"));

        // get from sessionScopeMap
        /*
        Map<String, Object> sessionScopeMap = new HashMap<String, Object>();
        sessionScopeMap.put("sessionScopeMapTest", "sessionScopeMapTestValue");
        pageContext.setAttribute("nablarch_session", sessionScopeMap, PageContext.REQUEST_SCOPE);
        assertThat(TagUtil.getValue(pageContext, "sessionScopeMapTest", true).toString(), is("sessionScopeMapTestValue"));
        */
    }

    private void assertValues(String prefix) {
        assertThat(TagUtil.getValue(pageContext, "sample.string", true).toString(), is(prefix + "string0"));
        assertThat(TagUtil.getValue(pageContext, "sample.array[0]", true).toString(), is(prefix + "array0"));
        assertThat(TagUtil.getValue(pageContext, "sample.array[1]", true).toString(), is(prefix + "array1"));
        assertThat(TagUtil.getValue(pageContext, "sample.array[2]", true).toString(), is(prefix + "array2"));
        assertThat(TagUtil.getValue(pageContext, "sample.list[0]", true).toString(), is(prefix + "list0"));
        assertThat(TagUtil.getValue(pageContext, "sample.list[1]", true).toString(), is(prefix + "list1"));
        assertThat(TagUtil.getValue(pageContext, "sample.list[2]", true).toString(), is(prefix + "list2"));
        assertThat(TagUtil.getValue(pageContext, "sample.map.key0", true).toString(), is(prefix + "map0"));
        assertThat(TagUtil.getValue(pageContext, "sample.map.key1", true).toString(), is(prefix + "map1"));
        assertThat(TagUtil.getValue(pageContext, "sample.map.key2", true).toString(), is(prefix + "map2"));
        assertThat(TagUtil.getValue(pageContext, "sample.userList[0].name", true).toString(), is(prefix + "userList0"));
        assertThat(TagUtil.getValue(pageContext, "sample.userList[0].age", true).toString(), is("0"));
        assertThat(TagUtil.getValue(pageContext, "sample.userList[1].name", true).toString(), is(prefix + "userList1"));
        assertThat(TagUtil.getValue(pageContext, "sample.userList[1].age", true).toString(), is("1"));
        assertThat(TagUtil.getValue(pageContext, "sample.userList[2].name", true).toString(), is(prefix + "userList2"));
        assertThat(TagUtil.getValue(pageContext, "sample.userList[2].age", true).toString(), is("2"));
        assertThat(TagUtil.getValue(pageContext, "sample.userMap.key0.name", true).toString(), is(prefix + "userMap0"));
        assertThat(TagUtil.getValue(pageContext, "sample.userMap.key0.age", true).toString(), is("0"));
        assertThat(TagUtil.getValue(pageContext, "sample.userMap.key1.name", true).toString(), is(prefix + "userMap1"));
        assertThat(TagUtil.getValue(pageContext, "sample.userMap.key1.age", true).toString(), is("10"));
        assertThat(TagUtil.getValue(pageContext, "sample.userMap.key2.name", true).toString(), is(prefix + "userMap2"));
        assertThat(TagUtil.getValue(pageContext, "sample.userMap.key2.age", true).toString(), is("20"));

    }

    public static final class Sample {
        private String string;
        private String[] array;
        private List<String> list;
        private Map<String, String> map;
        private List<User> userList;
        private Map<String, User> userMap;
        public Sample(String prefix) {
            string = prefix + "string0";
            int size = 3;
            array = new String[size];
            list = new ArrayList<String>();
            map = new HashMap<String, String>();
            userList = new ArrayList<User>();
            userMap = new HashMap<String, User>();
            for (int i = 0; i < size; i++) {
                array[i] = prefix + "array" + i;
                list.add(prefix + "list" + i);
                map.put("key" + i, prefix+ "map" + i);
                userList.add(new User(prefix + "userList" + i, i));
                userMap.put("key" + i, new User(prefix + "userMap" + i, i * 10));
            }
        }
        public String getString() {
            return string;
        }
        public void setString(String string) {
            this.string = string;
        }
        public String[] getArray() {
            return array;
        }
        public void setArray(String[] array) {
            this.array = array;
        }
        public List<String> getList() {
            return list;
        }
        public void setList(List<String> list) {
            this.list = list;
        }
        public Map<String, String> getMap() {
            return map;
        }
        public void setMap(Map<String, String> map) {
            this.map = map;
        }
        public List<User> getUserList() {
            return userList;
        }
        public void setUserList(List<User> userList) {
            this.userList = userList;
        }
        public Map<String, User> getUserMap() {
            return userMap;
        }
        public void setUserMap(Map<String, User> userMap) {
            this.userMap = userMap;
        }
    }

    public static final class User {
        private String name;
        private Integer age;
        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Integer getAge() {
            return age;
        }
        public void setAge(Integer age) {
            this.age = age;
        }
    }

    @Test
    public void testEditClassAttributeWithDuplicatedCssClass() throws Exception {

        TextTag target = new TextTag();
        target.setPageContext(pageContext);

        TagTestUtil.setErrorMessages(pageContext);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"value_test"});

        // generic
        target.setCssClass("nablarch_error");

        // input
        target.setName("entity.bbb");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "class=\"nablarch_error\"",
                "type=\"text\"",
                "name=\"entity.bbb\"",
                "value=\"value_test\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testEditClassAttributeWithSeparator() throws Exception {

        TextTag target = new TextTag();
        target.setPageContext(pageContext);

        TagTestUtil.setErrorMessages(pageContext);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"value_test"});

        // generic
        target.setCssClass("nablarch_error ");// end with space for css class separator

        // input
        target.setName("entity.bbb");

        // nablarch
        target.setErrorCss("errorCss_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "class=\"nablarch_error errorCss_test\"",
                "type=\"text\"",
                "name=\"entity.bbb\"",
                "value=\"value_test\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testGetDisplayMethod(){

        TagTestUtil.setUpDefaultWithDisplayControlSettings();

        ServiceAvailableMock availableMock = (ServiceAvailableMock) SystemRepository.getObject("serviceAvailability");
        availableMock.setAvailable(false);

        // #######################
        // リクエストIDがnullの場合
        // #######################
        assertThat(TagUtil.getDisplayMethod(null, DisplayMethod.DISABLED), is(DisplayMethod.NORMAL));

        CustomTagConfig customTagConfig = TagUtil.getCustomTagConfig();

        // ##############################################
        // CustomTagConfigのdisplayMethod設定網羅
        // ##############################################
        // デフォルト値使用
        assertThat(TagUtil.getDisplayMethod("DUMMY", null), is(DisplayMethod.NORMAL));
        // 表示方法を指定
        customTagConfig.setDisplayMethod("NODISPLAY");
        assertThat(TagUtil.getDisplayMethod("DUMMY", null), is(DisplayMethod.NODISPLAY));
        customTagConfig.setDisplayMethod("DISABLED");
        assertThat(TagUtil.getDisplayMethod("DUMMY", null), is(DisplayMethod.DISABLED));
        customTagConfig.setDisplayMethod("NORMAL");
        assertThat(TagUtil.getDisplayMethod("DUMMY", null), is(DisplayMethod.NORMAL));

        // ###############################
        // displayMethod引数を網羅
        // ###############################
        customTagConfig.setDisplayMethod("NODISPLAY");
        assertThat(TagUtil.getDisplayMethod("test", null), is(DisplayMethod.NODISPLAY));
        // CustomTagCofigの設定値より、引数のdisplayMethodが優先される
        customTagConfig.setDisplayMethod("NORMAL");
        assertThat(TagUtil.getDisplayMethod("test", DisplayMethod.NODISPLAY), is(DisplayMethod.NODISPLAY));
        customTagConfig.setDisplayMethod("NODISPLAY");
        assertThat(TagUtil.getDisplayMethod("test", DisplayMethod.NORMAL), is(DisplayMethod.NORMAL));
        customTagConfig.setDisplayMethod("NODISPLAY");
        assertThat(TagUtil.getDisplayMethod("test", DisplayMethod.DISABLED), is(DisplayMethod.DISABLED));

        // ###############################
        // 開閉局と認可の組み合わせ
        // ###############################
        // 準備
        SortedSet<String> requestIds = new TreeSet<String>();
        requestIds.add("R00000");
        Permission permission = new BasicPermission(requestIds);
        PermissionUtil.setPermission(permission);
        customTagConfig.setDisplayMethod("NODISPLAY");

        // 開閉局： true、認可：true
        availableMock.setAvailable(true);
        assertThat(TagUtil.getDisplayMethod("R00000", null), is(DisplayMethod.NORMAL));
        // 開閉局： true、認可：false
        availableMock.setAvailable(true);
        assertThat(TagUtil.getDisplayMethod("R00001", null), is(DisplayMethod.NODISPLAY));
        // 開閉局： false、認可：true
        availableMock.setAvailable(false);
        assertThat(TagUtil.getDisplayMethod("R00000", null), is(DisplayMethod.NODISPLAY));
        // 開閉局： false、認可：false
        availableMock.setAvailable(false);
        assertThat(TagUtil.getDisplayMethod("R00001", null), is(DisplayMethod.NODISPLAY));

        // #######################################################
        // CustomTagConfigのdisplayControlCheckersプロパティ網羅
        // #######################################################
        // null指定(デフォルトのdisplayControlCheckers)が使用される。
        customTagConfig.setDisplayControlCheckers(null);
        assertThat(TagUtil.getDisplayMethod("R00000", null), is(DisplayMethod.NODISPLAY));
        // 1つのcheckerのみ
        List<DisplayControlChecker> checkerList = new ArrayList<DisplayControlChecker>();
        checkerList.add(new PermissionDisplayControlChecker());
        customTagConfig.setDisplayControlCheckers(checkerList);
        availableMock.setAvailable(false);
        assertThat(TagUtil.getDisplayMethod("R00000", null), is(DisplayMethod.NORMAL));
        assertThat(TagUtil.getDisplayMethod("R00001", null), is(DisplayMethod.NODISPLAY));

        PermissionUtil.setPermission(null);
        assertThat(TagUtil.getDisplayMethod("R00001", null), is(DisplayMethod.NORMAL));

    }

    /**
     * {@link TagUtil#storeKeyValueSetToHidden(PageContext, String, Object, List, String)}
     * で、オブジェクトがMapの場合
     */
    @Test
    public void testStoreKeyValueSetToHiddenMap() {
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("name", "name");
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        List<String> keys = new ArrayList<String>() {
            {
                add("val1");
                add("val2");
            }

        };


        String valueKey;

        obj.put("val1", "value01");
        obj.put("val2", "value02");
        valueKey= TagUtil.storeKeyValueSetToHidden(pageContext, "test.val", obj, keys, "test");
        assertThat(valueKey, is("value01,value02"));
        obj.put("val1", "value11");
        obj.put("val2", "value12");
        valueKey= TagUtil.storeKeyValueSetToHidden(pageContext, "test.val", obj, keys, "test");
        assertThat(valueKey, is("value11,value12"));

        formContext = TagUtil.getFormContext(pageContext);
        List<HtmlAttributes> hiddenTagInfoList = formContext.getHiddenTagInfoList();
        {
            HtmlAttributes hiddenTagInfo = new HtmlAttributes();
            hiddenTagInfo.put(HtmlAttribute.TYPE, "hidden");
            hiddenTagInfo.put(HtmlAttribute.NAME, TagUtil.VAR_NAMES_KEY);
            hiddenTagInfo.put(HtmlAttribute.VALUE, "test.val");
            assertThat(hiddenTagInfo.toHTML("input"), is(hiddenTagInfoList.get(0).toHTML("input")));
        }
        {
            HtmlAttributes hiddenTagInfo = new HtmlAttributes();
            hiddenTagInfo.put(HtmlAttribute.TYPE, "hidden");
            hiddenTagInfo.put(HtmlAttribute.NAME, TagUtil.PARAM_NAMES_KEY_PREFIX + "test.val");
            hiddenTagInfo.put(HtmlAttribute.VALUE, "test.val1,test.val2");
            assertThat(hiddenTagInfo.toHTML("input"), is(hiddenTagInfoList.get(1).toHTML("input")));
        }
        {
            HtmlAttributes hiddenTagInfo = new HtmlAttributes();
            hiddenTagInfo.put(HtmlAttribute.TYPE, "hidden");
            hiddenTagInfo.put(HtmlAttribute.NAME, TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val");
            hiddenTagInfo.put(HtmlAttribute.VALUE, "value01,value02");
            assertThat(hiddenTagInfo.toHTML("input"), is(hiddenTagInfoList.get(2).toHTML("input")));
        }
        {
            HtmlAttributes hiddenTagInfo = new HtmlAttributes();
            hiddenTagInfo.put(HtmlAttribute.TYPE, "hidden");
            hiddenTagInfo.put(HtmlAttribute.NAME, TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val");
            hiddenTagInfo.put(HtmlAttribute.VALUE, "value11,value12");
            assertThat(hiddenTagInfo.toHTML("input"), is(hiddenTagInfoList.get(3).toHTML("input")));
        }

    }

    /**
     * {@link TagUtil#storeKeyValueSetToHidden(PageContext, String, Object, List, String)}
     * で、オブジェクトがFormの場合
     */
    @Test
    public void testStoreKeyValueSetToHiddenForm() {
        CompositeKeyExampleForm2 obj = new CompositeKeyExampleForm2();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        List<String> keys = new ArrayList<String>() {
            {
                add("key1");
                add("key2");
            }

        };


        String valueKey;

        obj.setKey1("value01");
        obj.setKey2("value02");
        valueKey = TagUtil.storeKeyValueSetToHidden(pageContext, "test.val", obj, keys, "test");
        assertThat(valueKey, is("value01,value02"));
        obj.setKey1("value11");
        obj.setKey2("value12");
        valueKey= TagUtil.storeKeyValueSetToHidden(pageContext, "test.val", obj, keys, "test");
        assertThat(valueKey, is("value11,value12"));

        formContext = TagUtil.getFormContext(pageContext);
        List<HtmlAttributes> hiddenTagInfoList = formContext.getHiddenTagInfoList();
        {
            HtmlAttributes hiddenTagInfo = new HtmlAttributes();
            hiddenTagInfo.put(HtmlAttribute.TYPE, "hidden");
            hiddenTagInfo.put(HtmlAttribute.NAME, TagUtil.VAR_NAMES_KEY);
            hiddenTagInfo.put(HtmlAttribute.VALUE, "test.val");
            assertThat(hiddenTagInfo.toHTML("input"), is(hiddenTagInfoList.get(0).toHTML("input")));
        }
        {
            HtmlAttributes hiddenTagInfo = new HtmlAttributes();
            hiddenTagInfo.put(HtmlAttribute.TYPE, "hidden");
            hiddenTagInfo.put(HtmlAttribute.NAME, TagUtil.PARAM_NAMES_KEY_PREFIX + "test.val");
            hiddenTagInfo.put(HtmlAttribute.VALUE, "test.key1,test.key2");
            assertThat(hiddenTagInfo.toHTML("html"), is(hiddenTagInfoList.get(1).toHTML("html")));
        }
        {
            HtmlAttributes hiddenTagInfo = new HtmlAttributes();
            hiddenTagInfo.put(HtmlAttribute.TYPE, "hidden");
            hiddenTagInfo.put(HtmlAttribute.NAME, TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val");
            hiddenTagInfo.put(HtmlAttribute.VALUE, "value01,value02");
            assertThat(hiddenTagInfo.toHTML("input"), is(hiddenTagInfoList.get(2).toHTML("html")));
        }
        {
            HtmlAttributes hiddenTagInfo = new HtmlAttributes();
            hiddenTagInfo.put(HtmlAttribute.TYPE, "hidden");
            hiddenTagInfo.put(HtmlAttribute.NAME, TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val");
            hiddenTagInfo.put(HtmlAttribute.VALUE, "value11,value12");
            assertThat(hiddenTagInfo.toHTML("input"), is(hiddenTagInfoList.get(3).toHTML("input")));
        }

    }


    /**
     * {@link TagUtil#restoreKeyValueSetFromHidden(HttpRequest)}
     * のテスト
     */
    @Test
    public void testRestoreKeyValueSetFromHidden() {
        {
            // 正常系
            HttpRequest request = new MockHttpRequest();
            request.setParam(TagUtil.VAR_NAMES_KEY, "test.val");
            request.setParam(TagUtil.PARAM_NAMES_KEY_PREFIX + "test.val", "test.val1,test.val2");
            request.setParam(TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val", "value01,value02", "value11,value12", "value21,value22");
            request.setParam("test.val", "value01,value02", "value11,value12");

            TagUtil.restoreKeyValueSetFromHidden(request);

            assertThat(request.getParam("test.val1"), is(new String[]{"value01", "value11"}));
            assertThat(request.getParam("test.val2"), is(new String[]{"value02", "value12"}));
        }
        {
            // 正常系 そもそも値が飛んでこなかった場合
            HttpRequest request = new MockHttpRequest();
            request.setParam(TagUtil.VAR_NAMES_KEY, "test.val");
            request.setParam(TagUtil.PARAM_NAMES_KEY_PREFIX + "test.val", "test.val1,test.val2");
            request.setParam(TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val", "value01,value02", "value11,value12", "value21,value22");

            TagUtil.restoreKeyValueSetFromHidden(request);

            assertThat(request.getParam("test.val1"), nullValue());
            assertThat(request.getParam("test.val2"), nullValue());
        }
        {
            // 異常系 hidden にあるパラメータ名のリストが取れない場合
            HttpRequest request = new MockHttpRequest();
            request.setParam(TagUtil.VAR_NAMES_KEY, "test.val");
            request.setParam(TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val", "value01,value02", "value11,value12", "value21,value22");
            request.setParam("test.val", "value01,value02", "value11,value12");

            TagUtil.restoreKeyValueSetFromHidden(request);

            assertThat(request.getParam("test.val1"), nullValue());
            assertThat(request.getParam("test.val2"), nullValue());
        }
        {
            // 異常系 hidden にあるキーが取れない場合
            HttpRequest request = new MockHttpRequest();
            request.setParam(TagUtil.PARAM_NAMES_KEY_PREFIX + "test.val", "test.val1,test.val2");
            request.setParam(TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val", "value01,value02", "value11,value12", "value21,value22");
            request.setParam("test.val", "value01,value02", "value11,value12");

            TagUtil.restoreKeyValueSetFromHidden(request);

            assertThat(request.getParam("test.val1"), nullValue());
            assertThat(request.getParam("test.val2"), nullValue());
        }
        {
            // 異常系 hidden にあるキーがなぜか2つある場合
            HttpRequest request = new MockHttpRequest();
            request.setParam(TagUtil.VAR_NAMES_KEY, "test.val");
            request.setParam(TagUtil.PARAM_NAMES_KEY_PREFIX + "test.val", "test.val1,test.val2", "test.val1,test.val2");
            request.setParam(TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val", "value01,value02", "value11,value12", "value21,value22");
            request.setParam("test.val", "value01,value02", "value11,value12");

            TagUtil.restoreKeyValueSetFromHidden(request);

            assertThat(request.getParam("test.val1"), nullValue());
            assertThat(request.getParam("test.val2"), nullValue());
        }
        {
            // 異常系 hidden にある候補リストが取れない場合
            HttpRequest request = new MockHttpRequest();
            request.setParam(TagUtil.VAR_NAMES_KEY, "test.val");
            request.setParam(TagUtil.PARAM_NAMES_KEY_PREFIX + "test.val", "test.val1,test.val2");
            request.setParam("test.val", "value01,value02", "value11,value12");

            TagUtil.restoreKeyValueSetFromHidden(request);

            assertThat(request.getParam("test.val1"), nullValue());
            assertThat(request.getParam("test.val2"), nullValue());
        }
        {
            // 異常系 候補リストにない値が飛んできた場合(候補にないのは無視する)
            HttpRequest request = new MockHttpRequest();
            request.setParam(TagUtil.VAR_NAMES_KEY, "test.val");
            request.setParam(TagUtil.PARAM_NAMES_KEY_PREFIX + "test.val", "test.val1,test.val2");
            request.setParam(TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val", "value01,value02", "value11,value12", "value21,value22");
            // ↓の"value11,value02" と , "value11,value12,value13" の組み合わせは無効
            request.setParam("test.val", "value01,value02", "value11,value02", "value11,value12,value13", "value11,value12");

            TagUtil.restoreKeyValueSetFromHidden(request);

            assertThat(request.getParam("test.val1"), is(new String[]{"value01", "value11"}));
            assertThat(request.getParam("test.val2"), is(new String[]{"value02", "value12"}));
        }
        {
            // 異常系 値のリストと名前のリストの長さが違うものは無視される(ありえないかも？)
            HttpRequest request = new MockHttpRequest();
            request.setParam(TagUtil.VAR_NAMES_KEY, "test.val");
            request.setParam(TagUtil.PARAM_NAMES_KEY_PREFIX + "test.val", "test.val1,test.val2");
            request.setParam(TagUtil.PARAM_VALUES_KEY_PREFIX + "test.val", "value01,value02", "value11,value12,value13", "value21,value22");
            // ↓の"value11,value02" と , "value11,value12,value13" の組み合わせは無効
            request.setParam("test.val", "value01,value02", "value11,value02", "value11,value12,value13", "value11,value12");

            TagUtil.restoreKeyValueSetFromHidden(request);

            assertThat(request.getParam("test.val1"), is(new String[]{"value01"}));
            assertThat(request.getParam("test.val2"), is(new String[]{"value02"}));
        }
    }

    /**
     * {@link TagUtil#createCompositeKeyValueList(PageContext, String, List)}のテスト。
     */
    @Test
    public void testCreateCompositeKeyValueList() {
        {
            // リクエストスコープからキーを取って、CompositeKey クラスを使わないFormでチェックボックスの場合。
            CompositeKeyExampleForm1 form = new CompositeKeyExampleForm1();

            List<String> keyNames = new ArrayList<String>(){{ add("key1"); add("key2"); }};
            form.setKey1(new String[] {"value11", "value12", "value13"});
            form.setKey2(new String[] {"value21", "value22", "value23"});

            pageContext.setAttribute("form", form);

            List<String> expected = new ArrayList<String>(){{ add("value11,value21"); add("value12,value22"); add("value13,value23"); }};
            assertThat(TagUtil.createCompositeKeyValueList(pageContext, "form", keyNames), is(expected));
        }
        {
            // リクエストスコープからキーを取って、CompositeKey クラスを使わない Form でラジオボタンの場合。
               CompositeKeyExampleForm2 form = new CompositeKeyExampleForm2();

               List<String> keyNames = new ArrayList<String>(){{ add("key1"); add("key2"); }};
               form.setKey1("value1");
               form.setKey2("value2");

               pageContext.setAttribute("form", form);

               List<String> expected = new ArrayList<String>(){{ add("value1,value2");}};
               assertThat(TagUtil.createCompositeKeyValueList(pageContext, "form", keyNames), is(expected));
        }
        {
            // リクエストスコープからキーを取って、Mapの場合
            Map<String, Object> m = new HashMap<String, Object>();

            m.put("key1", new String[] {"value11", "value12", "value13"});
            m.put("key2", new String[] {"value21", "value22", "value23"});
            pageContext.setAttribute("form", m);

            List<String> keyNames = new ArrayList<String>(){{ add("key1"); add("key2"); }};

            List<String> expected = new ArrayList<String>(){{ add("value11,value21"); add("value12,value22"); add("value13,value23"); }};
            assertThat(TagUtil.createCompositeKeyValueList(pageContext, "form", keyNames), is(expected));
        }
        {
            // リクエストスコープからキーを取って、Mapの場合
            Map<String, Object> m = new HashMap<String, Object>();

            m.put("key1", new String[] {"value11", "value12", "value13"});
            m.put("key2", new String[] {"value21", "value22", "value23"});
            pageContext.setAttribute("form", m);

            List<String> keyNames = new ArrayList<String>(){{ add("key1"); add("key2"); }};

            List<String> expected = new ArrayList<String>(){{ add("value11,value21"); add("value12,value22"); add("value13,value23"); }};
            assertThat(TagUtil.createCompositeKeyValueList(pageContext, "form", keyNames), is(expected));
        }
        {
            // マルチキーのキー名リストが空の時
            Map<String, Object> m = new HashMap<String, Object>();

            m.put("key1", new String[] {"value11", "value12", "value13"});
            m.put("key2", new Long[] {10l, 11l, 12l});
            pageContext.setAttribute("form", m);

            List<String> keyNames = new ArrayList<String>();

            List<String> expected = new ArrayList<String>();
            assertThat(TagUtil.createCompositeKeyValueList(pageContext, "form", keyNames), is(expected));
        }
        {
            // パラメータの値が String でも String[] でもないとき
            Map<String, Object> m = new HashMap<String, Object>();

            m.put("key1", 1L);
            m.put("key2", new Long[] {10l, 11l, 12l});
            pageContext.setAttribute("form", m);

            List<String> keyNames = new ArrayList<String>(){{ add("key1"); add("key2"); }};

            List<String> expected = new ArrayList<String>();
            assertThat(TagUtil.createCompositeKeyValueList(pageContext, "form", keyNames), is(expected));
        }
    }

    public static class CompositeKeyExampleForm1 {
        private String[] key1;
        private String[] key2;

        public String[] getKey1() {
            return key1;
        }

        public void setKey1(String[] key1) {
            this.key1 = key1;
        }

        public String[] getKey2() {
            return key2;
        }

        public void setKey2(String[] key2) {
            this.key2 = key2;
        }
    }

    public static class CompositeKeyExampleForm2 {
        private String key1;
        private String key2;

        public String getKey1() {
            return key1;
        }

        public void setKey1(String key1) {
            this.key1 = key1;
        }

        public String getKey2() {
            return key2;
        }

        public void setKey2(String key2) {
            this.key2 = key2;
        }

    }


    @Test
    public void testConvertStringArrayToRequestValue() {
        {
            // 正常系
            List<Object> valueParamsList = new ArrayList<Object>();

            valueParamsList.add(new String[] {"0001", "0002", "0003"});
            valueParamsList.add(new String[] {"1001", "1002", "1003"});
            valueParamsList.add(new String[] {"2001", "2002", "2003"});

            List<String> expected = new ArrayList<String>();

            expected.add("0001,1001,2001");
            expected.add("0002,1002,2002");
            expected.add("0003,1003,2003");
            assertThat(TagUtil.convertStringArrayToRequestValue(valueParamsList), is(expected));
        }
        {
            // 異常系 ひとつ文字列配列じゃない
            List<Object> valueParamsList = new ArrayList<Object>();

            valueParamsList.add(new String[] {"0001", "0002", "0003"});
            valueParamsList.add("1001,1002,1003");
            valueParamsList.add(new String[] {"2001", "2002", "2003"});

            List<String> expected = new ArrayList<String>();

            assertThat(TagUtil.convertStringArrayToRequestValue(valueParamsList), is(expected));
        }
        {
            // 異常系 ひとつ長さが違う
            List<Object> valueParamsList = new ArrayList<Object>();

            valueParamsList.add(new String[] {"0001", "0002", "0003"});
            valueParamsList.add(new String[] {"1001", "1002", "1003", "1004"});
            valueParamsList.add(new String[] {"2001", "2002", "2003"});

            List<String> expected = new ArrayList<String>();

            assertThat(TagUtil.convertStringArrayToRequestValue(valueParamsList), is(expected));
        }
    }

    @Test
    public void testConvertStringToRequestValue() {

        {
            // 正常系
            List<Object> valueParamsList = new ArrayList<Object>();

            valueParamsList.add("0001");
            valueParamsList.add("1001");
            valueParamsList.add("2001");

            List<String> expected = new ArrayList<String>();

            expected.add("0001,1001,2001");
            assertThat(TagUtil.convertStringToRequestValue(valueParamsList), is(expected));
        }
    }
    {
        // 異常系 ひとつ文字列じゃない
        List<Object> valueParamsList = new ArrayList<Object>();

        valueParamsList.add("0001");
        valueParamsList.add(new String[] {"1001"});
        valueParamsList.add("2001");

        List<String> expected = new ArrayList<String>();

        assertThat(TagUtil.convertStringToRequestValue(valueParamsList), is(expected));
    }

    /**
     * URIにGETパラメータとしてタイムスタンプを付加するメソッドのテスト
     */
    @Test
    public void testAddStaticContentVersion() throws Exception {

        // 元URIにGETパラメータが存在しない場合
        String actual2 = TagUtil.addStaticContentVersion("http://localhost.com");
        assertThat("タグ設定初期化時のシステムタイムスタンプが使用されること", actual2,
                is("http://localhost.com?nablarch_static_content_version=1.0.0"));

        // 元URIにGETパラメータが存在している場合
        assertThat(TagUtil.addStaticContentVersion("http://localhost.com?a=b"),
                is("http://localhost.com?a=b&nablarch_static_content_version=1.0.0"));

        // 静的コンテンツのバージョンを空文字列に変更
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                return new HashMap<String, Object>() {{
                    put("static_content_version", "");
                }};
            }
        });
        String actual = TagUtil.addStaticContentVersion("http://localhost.com");
        assertThat("静的リソースのURIに付加するGETパラメータ値がnullの場合、URIに変更が加えられないこと。",
                actual, is("http://localhost.com"));

    }

    /**
     * 指数表現を含む数値のテスト
     */
    @Test
    public void testFormatHugeExponentialValue() {
        MockPageContext pageContext = new MockPageContext();
        String name = "name_test";

        // huge exponetial value
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{#}"), "9e99999999"), is("9e99999999"));
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{#}"), "-9E-99999999"), is("-9E-99999999"));
        // non exponential value.
        assertThat(TagUtil.formatValue(pageContext, name, spec("decimal{#}"), "abc"), is("abc"));
    }

}
