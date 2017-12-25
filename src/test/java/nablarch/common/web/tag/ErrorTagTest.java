package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Locale;

import javax.servlet.jsp.tagext.Tag;

import nablarch.core.ThreadContext;

import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class ErrorTagTest extends TagTestSupport<ErrorTag> {
    
    public ErrorTagTest() {
        super(new ErrorTag());
    }
    
    @Test
    public void testSpecifyInvalidType() throws Exception {
        
        try {
            target.setMessageFormat(null);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("messageFormat was invalid. messageFormat must specify the following values. values = [div, span] messageFormat = [null]"));
        }
        
        try {
            target.setMessageFormat("divv");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("messageFormat was invalid. messageFormat must specify the following values. values = [div, span] messageFormat = [divv]"));
        }
    }
    
    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);
        
        // nablarch
        target.setName("entity.aaa.xxx");
        target.setErrorCss("errorCss_test");
        target.setMessageFormat("span");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "<span class=\"errorCss_test\">AAA1を入力して下さい。</span>";
        assertThat(actual, is(expected));
    }
    
    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);
        
        // nablarch
        target.setName("entity.aaa.xxx");
        target.setErrorCss("errorCss_test" + TagTestUtil.HTML);
        target.setMessageFormat("span");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "<span class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">AAA1を入力して下さい。</span>";
        assertThat(actual, is(expected));
    }

    @Test
    public void testInputPageWithDefaultConfig() throws Exception {
        
        TagTestUtil.setUpDefaultConfig();
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);
        
        // nablarch
        target.setName("entity.aaa.xxx");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "<span class=\"default_error\">AAA1を入力して下さい。</span>";
        assertThat(actual, is(expected));
    }
    
    @Test
    public void testInputPageForDefault() throws Exception {
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);
        
        // nablarch
        target.setName("entity.aaa.xxx");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "<div class=\"nablarch_error\">AAA1を入力して下さい。</div>";
        assertThat(actual, is(expected));
    }

    /**
     * サロゲートペアを扱うテストケース。
     * @throws Exception
     */
    @Test
    public void testInputPageForSurrogatepair() throws Exception {

        ThreadContext.setLanguage(Locale.JAPANESE);

        TagTestUtil.setErrorMessages(pageContext);

        // nablarch
        target.setName("entity.surrogatepair");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "<div class=\"nablarch_error\">🙊🙊🙊を入力して下さい。</div>";
        assertThat(actual, is(expected));
    }

    @Test
    public void testInputPageForNoError() throws Exception {
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);
        
        // nablarch
        target.setName("entity.aa.xxx");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        assertThat(actual, is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void testDoCatch() throws Throwable {

        target.doCatch(new NullPointerException());
    }
}
