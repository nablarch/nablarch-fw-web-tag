package nablarch.common.web.tag;

import javax.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.WebTestUtil;
import nablarch.core.util.Builder;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Kiyohito Itoh
 */
public class LinkTagTest extends TagTestSupport<LinkTag> {

    @Before
    public void setup() {
        TagTestUtil.setUpDefaultConfig();
    }

    public LinkTagTest() {
        super(new LinkTag());
    }
    
    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // link
        target.setCharset("charset_test");
        target.setHref("./R12345");
        target.setHreflang("hreflang_test");
        target.setType("type_test");
        target.setRel("rel_test");
        target.setRev("rev_test");
        target.setMedia("media_test");
        target.setTarget("target_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<link",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "type=\"type_test\"",
                "target=\"target_test\"",
                "charset=\"charset_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "hreflang=\"hreflang_test\"",
                "rel=\"rel_test\"",
                "rev=\"rev_test\"",
                "media=\"media_test\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // generic
        TagTestUtil.setGenericAttributesWithHtml(target);
        
        // link
        target.setCharset("charset_test" + TagTestUtil.HTML);
        target.setHref("./R12345" + TagTestUtil.HTML);
        target.setHreflang("hreflang_test" + TagTestUtil.HTML);
        target.setType("type_test" + TagTestUtil.HTML);
        target.setRel("rel_test" + TagTestUtil.HTML);
        target.setRev("rev_test" + TagTestUtil.HTML);
        target.setMedia("media_test" + TagTestUtil.HTML);
        target.setTarget("target_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<link",
                "id=\"id_test" + TagTestUtil.ESC_HTML + "\"",
                "class=\"css_test" + TagTestUtil.ESC_HTML + "\"",
                "style=\"style_test" + TagTestUtil.ESC_HTML + "\"",
                "title=\"title_test" + TagTestUtil.ESC_HTML + "\"",
                "lang=\"lang_test" + TagTestUtil.ESC_HTML + "\"",
                "xml:lang=\"xmlLang_test" + TagTestUtil.ESC_HTML + "\"",
                "dir=\"dir_test" + TagTestUtil.ESC_HTML + "\"",
                "type=\"type_test" + TagTestUtil.ESC_HTML + "\"",
                "target=\"target_test" + TagTestUtil.ESC_HTML + "\"",
                "charset=\"charset_test" + TagTestUtil.ESC_HTML + "\"",
                "href=\"./R12345" + TagTestUtil.ESC_HTML + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "hreflang=\"hreflang_test" + TagTestUtil.ESC_HTML + "\"",
                "rel=\"rel_test" + TagTestUtil.ESC_HTML + "\"",
                "rev=\"rev_test" + TagTestUtil.ESC_HTML + "\"",
                "media=\"media_test" + TagTestUtil.ESC_HTML + "\"",
                "onclick=\"onclick_test" + TagTestUtil.ESC_HTML + "\"",
                "ondblclick=\"ondblclick_test" + TagTestUtil.ESC_HTML + "\"",
                "onmousedown=\"onmousedown_test" + TagTestUtil.ESC_HTML + "\"",
                "onmouseup=\"onmouseup_test" + TagTestUtil.ESC_HTML + "\"",
                "onmouseover=\"onmouseover_test" + TagTestUtil.ESC_HTML + "\"",
                "onmousemove=\"onmousemove_test" + TagTestUtil.ESC_HTML + "\"",
                "onmouseout=\"onmouseout_test" + TagTestUtil.ESC_HTML + "\"",
                "onkeypress=\"onkeypress_test" + TagTestUtil.ESC_HTML + "\"",
                "onkeydown=\"onkeydown_test" + TagTestUtil.ESC_HTML + "\"",
                "onkeyup=\"onkeyup_test" + TagTestUtil.ESC_HTML + "\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForDefault() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // link
        target.setHref("./R12345");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<link",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + "\" />").replace(Builder.LS, " ") + "";
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForSecure() throws Exception {
        
        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // secure属性がtrueの場合
        
        target.setHref("/R12345");
        target.setSecure(true);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<link",
                "href=\"https://nablarch.co.jp:443" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        // secure属性がfalseの場合
        
        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        target.setHref("/R12345");
        target.setSecure(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<link",
                "href=\"http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        // secure属性がtrueの場合、かつsecurePortの指定がない場合
        
        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagUtil.getCustomTagConfig().setSecurePort(-1);
        target.setHref("/R12345");
        target.setSecure(true);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<link",
                "href=\"https://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + TagUtil.addStaticContentVersion(
                        "") + "\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        // secure属性がfalseの場合、かつportの指定がない場合
        
        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagUtil.getCustomTagConfig().setPort(-1);
        target.setHref("/R12345");
        target.setSecure(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<link",
                "href=\"http://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testInputPageForDefaultWithAbsolutePath() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // link
        target.setHref("/R12345");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<link",
                "href=\"" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }   
    
    /**
     * 本タグがFormタグ内に定義されていない場合（FormContextが設定されていない場合）に、
     * IllegalArgumentExceptionがスローされないことのテスト。
     */
    @Test
    public void testNotChildOfForm() throws Exception {
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // link
        target.setCharset("charset_test");
        target.setHref("./R12345");
        target.setHreflang("hreflang_test");
        target.setType("type_test");
        target.setRel("rel_test");
        target.setRev("rev_test");
        target.setMedia("media_test");
        target.setTarget("target_test");

        target.doStartTag();
        
        // 例外がスローされなければ成功
        assertTrue(true);
    }

}
