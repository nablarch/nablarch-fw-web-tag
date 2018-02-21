package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class WriteTagTest extends TagTestSupport<WriteTag> {
    @Before
    public void setupThreadContext() throws Exception {
        ThreadContext.setLanguage(Locale.JAPANESE);
    }

    @After
    public void clearThreadContext() throws Exception {
        ThreadContext.clear();
    }

    public WriteTagTest() {
        super(new WriteTag());
    }

    public static final class Entity {
        private String bbb;
        private Date date;
        private BigDecimal decimal;
        public Entity(String bbb) {
            this.bbb = bbb;
        }
        public Entity(BigDecimal decimal) {
            this.decimal = decimal;
        }
        public String getBbb() {
            return bbb;
        }
        public void setBbb(String bbb) {
            this.bbb = bbb;
        }
        public Date getDate() {
            return date;
        }
        public void setDate(Date date) {
            this.date = date;
        }
        public BigDecimal getDecimal() {
            return decimal;
        }
        public void setDecimal(BigDecimal decimal) {
            this.decimal = decimal;
        }
    }
    
    @Test
    public void testInputPage() throws Exception {

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity("value_test"));
        
        // nablarch
        target.setName("entity.bbb");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "value_test";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    /**
     * サロゲートペアを扱うテストケース
     *
     * @throws Exception
     */
    @Test
    public void testInputPageSurrogatepair() throws Exception {

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity("🙊🙊🙊"));

        // nablarch
        target.setName("entity.bbb");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "🙊🙊🙊";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testYyyyMm() throws Exception {
        TagUtil.getCustomTagConfig().setYyyymmPattern("yyyy/MM");
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity("201007"));
        // nablarch
        target.setName("entity.bbb");
        target.setValueFormat("yyyymm{yyyy/MM}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "2010/07";
        TagTestUtil.assertTag(actual, expected, " ");
        
        
        TagTestUtil.clearOutput(pageContext);
        
        target.setName("entity.bbb");
        target.setValueFormat("yyyymm{MMM.yyyy|en}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "Jul.2010";
        TagTestUtil.assertTag(actual, expected, " ");
        
        TagTestUtil.clearOutput(pageContext);
        
        target.setName("entity.bbb");
        target.setValueFormat("yyyymm{yyyy年MMM月|ja_JP|Asia/Tokyo}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "2010年7月";
        TagTestUtil.assertTag(actual, expected, " ");
    }
    
    
    @Test
    public void testYyyymmdd() throws Exception {
        TagUtil.getCustomTagConfig().setYyyymmPattern("yyyy/MM");
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity("20110928"));
        // nablarch
        target.setName("entity.bbb");
        target.setValueFormat("yyyymmdd{MMM/d/yyyy|en}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "Sep/28/2011";
        TagTestUtil.assertTag(actual, expected, " ");
        
        
        TagTestUtil.clearOutput(pageContext);
        
        target.setName("entity.bbb");
        target.setValueFormat("yyyymmdd{yyyy年MMM月d日(E)|ja_JP|Asia/Tokyo}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "2011年9月28日(水)";
        TagTestUtil.assertTag(actual, expected, " ");
    }
    
    

    @Test
    public void testInputPageWithoutValue() throws Exception {
        // ウィンドウスコープの値も出力できる。
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"value_from_param"});
        
        // nablarch
        target.setName("entity.bbb");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "value_from_param";
        TagTestUtil.assertTag(actual, expected, " ");
        
        
        TagTestUtil.clearOutput(pageContext);
        
        // ただし、ページスコープ、リクエストスコープの内容が優先される。
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity("value_from_request"));        
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = "value_from_request";
        TagTestUtil.assertTag(actual, expected, " ");
        
        TagTestUtil.clearOutput(pageContext);        
        pageContext.setAttribute("entity", new Entity("value_from_page"));
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = "value_from_page";
        TagTestUtil.assertTag(actual, expected, " ");        
    }

    /**
     * スコープでサロゲートペアを扱うテストケース
     *
     * @throws Exception
     */
    @Test
    public void testInputPageWithoutSurrogatepairValue() throws Exception {
        // ウィンドウスコープの値も出力できる。
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"🙈🙈🙈_from_param"});

        // nablarch
        target.setName("entity.bbb");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "🙈🙈🙈_from_param";
        TagTestUtil.assertTag(actual, expected, " ");


        TagTestUtil.clearOutput(pageContext);

        // ただし、ページスコープ、リクエストスコープの内容が優先される。
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity("🙉🙉🙉_from_request"));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "🙉🙉🙉_from_request";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);
        pageContext.setAttribute("entity", new Entity("🙊🙊🙊_from_page"));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "🙊🙊🙊_from_page";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testInputPageWithFormat() throws Exception {
        
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity("20100701"));
        
        // nablarch
        target.setName("entity.bbb");
        target.setValueFormat("yyyymmdd{yyyy/M/d}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "2010/7/1";
        TagTestUtil.assertTag(actual, expected, " ");
    }
    
    @Test
    public void testInputPageWithNullValueFormat() throws Exception {
        
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(""));
        
        // nablarch
        target.setName("entity.date");
        
        // date
        target.setValueFormat("date{yyyy/M/d}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        TagTestUtil.clearOutput(pageContext);
        
        // nablarch
        target.setName("entity.bbb");
        target.setValueFormat("yyyymmdd{yyyy/M/d}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        TagTestUtil.clearOutput(pageContext);
        
        // decimal
        target.setValueFormat("decimal{####}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        TagTestUtil.clearOutput(pageContext);
    }
    
    @Test
    public void testInputPageWithBlankValueFormat() throws Exception {
        
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(""));
        
        // nablarch
        target.setName("entity.bbb");
        target.setValueFormat("yyyymmdd{yyyy/M/d}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        TagTestUtil.clearOutput(pageContext);
        
        // decimal
        target.setValueFormat("decimal{####}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        TagTestUtil.clearOutput(pageContext);
    }
    
    @Test
    public void testInputPageWithCustomFormat() throws Exception {

        TagTestUtil.setUpCustomFormatter();
        
        pageContext.getAttributes(PageContext.SESSION_SCOPE).put("entity", new Entity("20100701"));
        
        // nablarch
        target.setName("entity.bbb");
        target.setValueFormat("custom{test}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "@@@20100701@@@";
        TagTestUtil.assertTag(actual, expected, " ");
        
        SystemRepository.clear();
    }

    @Test
    public void testInputPageWithHtml() throws Exception {

        pageContext.getAttributes(PageContext.SESSION_SCOPE).put("entity", new Entity("value_test" + TagTestUtil.HTML));
        
        // nablarch
        target.setName("entity.bbb");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "value_test" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT;
        TagTestUtil.assertTag(actual, expected, " ");
    }
    
    @Test
    public void testInputPageForNoEscape() throws Exception {

        pageContext.getAttributes(PageContext.SESSION_SCOPE).put("entity", new Entity("value_test" + TagTestUtil.HTML));
        
        // nablarch
        target.setName("entity.bbb");
        target.setHtmlEscape(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "value_test" + TagTestUtil.HTML;
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testInputPageForNoHtmlFormat() throws Exception {

        pageContext.getAttributes(PageContext.SESSION_SCOPE).put("entity", new Entity("value_test" + TagTestUtil.HTML));
        
        // nablarch
        target.setName("entity.bbb");
        target.setWithHtmlFormat(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "value_test" + TagTestUtil.ESC_HTML;
        TagTestUtil.assertTag(actual, expected, " ");
    }

    /**
     * 入力画面で、値がBigDecimal型の場合に指数表記にならないこと。
     * @throws Exception
     */
    @Test
    public void testInputPageBigDecimalValue() throws Exception {

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new BigDecimal("0.0000000001")));

        // nablarch
        target.setName("entity.decimal");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "0.0000000001";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    /**
     * 入力画面で配列の要素がnullの場合は空文字列が出力されること
     * @throws Exception
     */
    @Test
    public void testInputPageArrayWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("array", new String[] {null});

        target.setName("array");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
    }
    
    /**
     * 入力画面でリストの要素がnullの場合は空文字列が出力されること
     * @throws Exception
     */
    @Test
    public void testInputPageListWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("list", Collections.singletonList(null));

        target.setName("list");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
    }

    /**
     * 入力画面で、値がBigDecimal型かつエスケープしない場合に指数表記にならないこと。
     * @throws Exception
     */
    @Test
    public void testInputPageBigDecimalValueNotEscape() throws Exception {

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new BigDecimal("0.0000000001")));

        // nablarch
        target.setName("entity.decimal");
        target.setHtmlEscape(false);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "0.0000000001";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testConfirmationPage() throws Exception {
        
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity("value_test"));
        
        // nablarch
        target.setName("entity.bbb");
        
        TagUtil.setConfirmationPage(pageContext);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "value_test";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    /**
     * 確認画面で、値がBigDecimal型の場合に指数表記にならないこと。
     * @throws Exception
     */
    @Test
    public void testConfirmationPageBigDecimalValue() throws Exception {

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new BigDecimal("0.0000000001")));

        // nablarch
        target.setName("entity.decimal");

        TagUtil.setConfirmationPage(pageContext);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "0.0000000001";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    /**
     * 入力画面で、値がBigDecimal型かつエスケープしない場合に指数表記にならないこと。
     * @throws Exception
     */
    @Test
    public void testConfirmationPageBigDecimalValueNotEscape() throws Exception {

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new BigDecimal("0.0000000001")));

        // nablarch
        target.setName("entity.decimal");
        target.setHtmlEscape(false);

        TagUtil.setConfirmationPage(pageContext);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "0.0000000001";
        TagTestUtil.assertTag(actual, expected, " ");
    }
    
    /**
     * 確認画面で配列の要素がnullの場合は空文字列が出力されること
     * @throws Exception
     */
    @Test
    public void testConfirmationPageArrayWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("array", new String[] {null});

        target.setName("array");
        
        TagUtil.setConfirmationPage(pageContext);

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
    }

    /**
     * 確認画面でリストの要素がnullの場合は空文字列が出力されること
     * @throws Exception
     */
    @Test
    public void testConfirmationPageListWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("list", Collections.singletonList(null));

        target.setName("list");

        TagUtil.setConfirmationPage(pageContext);
        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
    }

    @Test
    public void value属性があればその値を出力すること() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity("201007"));
        target.setValue("valueAttribute");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "valueAttribute";
        TagTestUtil.assertTag(actual, expected, " ");
    }
}
