package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Locale;

import javax.servlet.jsp.tagext.Tag;

import nablarch.core.ThreadContext;
import nablarch.core.message.ApplicationException;
import nablarch.core.util.Builder;

import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class ErrorsTagTest extends TagTestSupport<ErrorsTag> {
    
    public ErrorsTagTest() {
        super(new ErrorsTag());
    }
    
    @Test
    public void testSpecifyInvalidAttribute() throws Exception {
        
        try {
            target.setFilter(null);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("filter was invalid. filter must specify the following values. values = [all, global] filter = [null]"));
        }
        
        try {
            target.setFilter("valid");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("filter was invalid. filter must specify the following values. values = [all, global] filter = [valid]"));
        }
    }
    
    @Test
    public void testForNullOrEmpty() throws Exception {
        
        TagTestUtil.setErrorMessages(pageContext, null);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        TagTestUtil.setErrorMessages(pageContext, new ApplicationException());
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
    }
    
    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);
        
        // nablarch
        target.setCssClass("css_test");
        target.setInfoCss("infoCss_test");
        target.setWarnCss("warnCss_test");
        target.setErrorCss("errorCss_test");
        target.setFilter("all");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<ul class=\"css_test\">",
                "<li class=\"infoCss_test\">XXXのため登録できません。</li>",
                "<li class=\"warnCss_test\">YYYにより既に<br />更新されています。</li>",
                "<li class=\"errorCss_test\">ZZZは既に削除されています。</li>",
                "<li class=\"errorCss_test\">AAAを入力して下さい。</li>",
                "<li class=\"errorCss_test\">BBBは半角英数で入力して下さい。</li>",
                "<li class=\"errorCss_test\">CCCは4桁以下で入力して下さい。</li>",
                "<li class=\"errorCss_test\">AAA1を入力して下さい。</li>",
                "<li class=\"errorCss_test\">AAA2を入力して下さい。</li>",
                "<li class=\"errorCss_test\">AAA3を入力して下さい。</li>",
                "<li class=\"errorCss_test\">XXXを入力して下さい。</li>",
                "<li class=\"errorCss_test\">YYYを入力して下さい。</li>",
                "<li class=\"errorCss_test\">ZZZを入力して下さい。</li>",
                "</ul>").replace(Builder.LS, TagUtil.getCustomTagConfig().getLineSeparator());
        TagTestUtil.assertTag(actual, expected, TagUtil.getCustomTagConfig().getLineSeparator());
    }
    

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);
        
        // nablarch
        target.setCssClass("css_test" + TagTestUtil.HTML);
        target.setInfoCss("infoCss_test" + TagTestUtil.HTML);
        target.setWarnCss("warnCss_test" + TagTestUtil.HTML);
        target.setErrorCss("errorCss_test" + TagTestUtil.HTML);
        target.setFilter("all");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<ul class=\"css_test" + TagTestUtil.ESC_HTML + "\">",
                "<li class=\"infoCss_test" + TagTestUtil.ESC_HTML + "\">XXXのため登録できません。</li>",
                "<li class=\"warnCss_test" + TagTestUtil.ESC_HTML + "\">YYYにより既に<br />更新されています。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">ZZZは既に削除されています。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">AAAを入力して下さい。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">BBBは半角英数で入力して下さい。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">CCCは4桁以下で入力して下さい。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">AAA1を入力して下さい。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">AAA2を入力して下さい。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">AAA3を入力して下さい。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">XXXを入力して下さい。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">YYYを入力して下さい。</li>",
                "<li class=\"errorCss_test" + TagTestUtil.ESC_HTML + "\">ZZZを入力して下さい。</li>",
                "</ul>").replace(Builder.LS, TagUtil.getCustomTagConfig().getLineSeparator());
        TagTestUtil.assertTag(actual, expected, TagUtil.getCustomTagConfig().getLineSeparator());
    }

    @Test
    public void testInputPageForDefault() throws Exception {
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<ul class=\"nablarch_errors\">",
                "<li class=\"nablarch_info\">XXXのため登録できません。</li>",
                "<li class=\"nablarch_warn\">YYYにより既に<br />更新されています。</li>",
                "<li class=\"nablarch_error\">ZZZは既に削除されています。</li>",
                "<li class=\"nablarch_error\">AAAを入力して下さい。</li>",
                "<li class=\"nablarch_error\">BBBは半角英数で入力して下さい。</li>",
                "<li class=\"nablarch_error\">CCCは4桁以下で入力して下さい。</li>",
                "<li class=\"nablarch_error\">AAA1を入力して下さい。</li>",
                "<li class=\"nablarch_error\">AAA2を入力して下さい。</li>",
                "<li class=\"nablarch_error\">AAA3を入力して下さい。</li>",
                "<li class=\"nablarch_error\">XXXを入力して下さい。</li>",
                "<li class=\"nablarch_error\">YYYを入力して下さい。</li>",
                "<li class=\"nablarch_error\">ZZZを入力して下さい。</li>",
                "</ul>").replace(Builder.LS, TagUtil.getCustomTagConfig().getLineSeparator());
        TagTestUtil.assertTag(actual, expected, TagUtil.getCustomTagConfig().getLineSeparator());
        
        TagTestUtil.clearOutput(pageContext);
        
        // cssClass = null
        target.setCssClass(null);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<ul class=\"\">",
                "<li class=\"nablarch_info\">XXXのため登録できません。</li>",
                "<li class=\"nablarch_warn\">YYYにより既に<br />更新されています。</li>",
                "<li class=\"nablarch_error\">ZZZは既に削除されています。</li>",
                "<li class=\"nablarch_error\">AAAを入力して下さい。</li>",
                "<li class=\"nablarch_error\">BBBは半角英数で入力して下さい。</li>",
                "<li class=\"nablarch_error\">CCCは4桁以下で入力して下さい。</li>",
                "<li class=\"nablarch_error\">AAA1を入力して下さい。</li>",
                "<li class=\"nablarch_error\">AAA2を入力して下さい。</li>",
                "<li class=\"nablarch_error\">AAA3を入力して下さい。</li>",
                "<li class=\"nablarch_error\">XXXを入力して下さい。</li>",
                "<li class=\"nablarch_error\">YYYを入力して下さい。</li>",
                "<li class=\"nablarch_error\">ZZZを入力して下さい。</li>",
                "</ul>").replace(Builder.LS, TagUtil.getCustomTagConfig().getLineSeparator());
        TagTestUtil.assertTag(actual, expected, TagUtil.getCustomTagConfig().getLineSeparator());
    }

    @Test
    public void testInputPageForDefaultWithCustomLineSeparator() throws Exception {
        
        TagTestUtil.setUpDefaultConfigWithLS();
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<ul class=\"nablarch_errors\">",
                "<li class=\"nablarch_info\">XXXのため登録できません。</li>",
                "<li class=\"nablarch_warn\">YYYにより既に<br />更新されています。</li>",
                "<li class=\"nablarch_error\">ZZZは既に削除されています。</li>",
                "<li class=\"nablarch_error\">AAAを入力して下さい。</li>",
                "<li class=\"nablarch_error\">BBBは半角英数で入力して下さい。</li>",
                "<li class=\"nablarch_error\">CCCは4桁以下で入力して下さい。</li>",
                "<li class=\"nablarch_error\">AAA1を入力して下さい。</li>",
                "<li class=\"nablarch_error\">AAA2を入力して下さい。</li>",
                "<li class=\"nablarch_error\">AAA3を入力して下さい。</li>",
                "<li class=\"nablarch_error\">XXXを入力して下さい。</li>",
                "<li class=\"nablarch_error\">YYYを入力して下さい。</li>",
                "<li class=\"nablarch_error\">ZZZを入力して下さい。</li>",
                "</ul>").replaceAll(Builder.LS, "\r");
        TagTestUtil.assertTag(actual, expected, "\r");
    }

    @Test
    public void testInputPageForFilter() throws Exception {
        
        ThreadContext.setLanguage(Locale.JAPANESE);
        
        TagTestUtil.setErrorMessages(pageContext);

        // nablarch
        target.setFilter("global");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<ul class=\"nablarch_errors\">",
                "<li class=\"nablarch_info\">XXXのため登録できません。</li>",
                "<li class=\"nablarch_warn\">YYYにより既に<br />更新されています。</li>",
                "<li class=\"nablarch_error\">ZZZは既に削除されています。</li>",
                "<li class=\"nablarch_error\">XXXを入力して下さい。</li>",
                "<li class=\"nablarch_error\">YYYを入力して下さい。</li>",
                "<li class=\"nablarch_error\">ZZZを入力して下さい。</li>",
                "</ul>").replace(Builder.LS, TagUtil.getCustomTagConfig().getLineSeparator());
        TagTestUtil.assertTag(actual, expected, TagUtil.getCustomTagConfig().getLineSeparator());
        
        TagTestUtil.clearOutput(pageContext);
        
        TagTestUtil.setErrorMessages(pageContext, false);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, TagUtil.getCustomTagConfig().getLineSeparator());
    }
}
