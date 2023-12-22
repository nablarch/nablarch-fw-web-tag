package nablarch.common.web.tag;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.WebTestUtil;
import nablarch.core.ThreadContext;
import nablarch.core.util.Builder;
import nablarch.fw.web.i18n.ResourcePathRule;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Kiyohito Itoh
 */
public class ImgTagTest extends TagTestSupport<ImgTag> {

    public ImgTagTest() {
        super(new ImgTag());
    }

    @Before
    public void setup() {
        TagTestUtil.setUpDefaultConfig();
    }

    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // img
        target.setSrc("./R12345?a=b");
        target.setAlt("alt_test");
        target.setName("name_test");
        target.setLongdesc("longdesc_test");
        target.setHeight("height_test");
        target.setWidth("width_test");
        target.setUsemap("usemap_test");
        target.setIsmap("ismap_test");
        target.setAlign("align_test");
        target.setBorder("border_test");
        target.setHspace("hspace_test");
        target.setVspace("vspace_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<img",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "name=\"name_test\"",
                "src=\"./R12345?a=b" + WebTestUtil.ENCODE_URL_SUFFIX + "&nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\"",
                "usemap=\"usemap_test\"",
                "align=\"align_test\"",
                "longdesc=\"longdesc_test\"",
                "height=\"height_test\"",
                "width=\"width_test\"",
                "ismap=\"ismap_test\"",
                "border=\"border_test\"",
                "hspace=\"hspace_test\"",
                "vspace=\"vspace_test\"",
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
        
        // img
        target.setSrc("./R12345" + TagTestUtil.HTML);
        target.setAlt("alt_test" + TagTestUtil.HTML);
        target.setName("name_test" + TagTestUtil.HTML);
        target.setLongdesc("longdesc_test" + TagTestUtil.HTML);
        target.setHeight("height_test" + TagTestUtil.HTML);
        target.setWidth("width_test" + TagTestUtil.HTML);
        target.setUsemap("usemap_test" + TagTestUtil.HTML);
        target.setIsmap("ismap_test" + TagTestUtil.HTML);
        target.setAlign("align_test" + TagTestUtil.HTML);
        target.setBorder("border_test" + TagTestUtil.HTML);
        target.setHspace("hspace_test" + TagTestUtil.HTML);
        target.setVspace("vspace_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<img",
                "id=\"id_test" + TagTestUtil.ESC_HTML + "\"",
                "class=\"css_test" + TagTestUtil.ESC_HTML + "\"",
                "style=\"style_test" + TagTestUtil.ESC_HTML + "\"",
                "title=\"title_test" + TagTestUtil.ESC_HTML + "\"",
                "lang=\"lang_test" + TagTestUtil.ESC_HTML + "\"",
                "xml:lang=\"xmlLang_test" + TagTestUtil.ESC_HTML + "\"",
                "dir=\"dir_test" + TagTestUtil.ESC_HTML + "\"",
                "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                "src=\"./R12345" + TagTestUtil.ESC_HTML + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test" + TagTestUtil.ESC_HTML + "\"",
                "usemap=\"usemap_test" + TagTestUtil.ESC_HTML + "\"",
                "align=\"align_test" + TagTestUtil.ESC_HTML + "\"",
                "longdesc=\"longdesc_test" + TagTestUtil.ESC_HTML + "\"",
                "height=\"height_test" + TagTestUtil.ESC_HTML + "\"",
                "width=\"width_test" + TagTestUtil.ESC_HTML + "\"",
                "ismap=\"ismap_test" + TagTestUtil.ESC_HTML + "\"",
                "border=\"border_test" + TagTestUtil.ESC_HTML + "\"",
                "hspace=\"hspace_test" + TagTestUtil.ESC_HTML + "\"",
                "vspace=\"vspace_test" + TagTestUtil.ESC_HTML + "\"",
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
        
        // img
        target.setSrc("./R12345");
        target.setAlt("alt_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<img",
                "src=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    /**
     * サロゲートペアを扱うテストケース。
     * @throws Exception
     */
    @Test
    public void testInputPageForSurrogatepair() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // img
        target.setSrc("./R12345");
        target.setAlt("🙊🙊🙊_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<img",
                "src=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"🙊🙊🙊_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForSecure() throws Exception {
        
        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // img
        target.setAlt("alt_test");
        
        // secure属性がtrueの場合
        
        target.setSrc("/R12345");
        target.setSecure(true);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<img",
                "src=\"https://nablarch.co.jp:443" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        // secure属性がfalseの場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        target.setSrc("/R12345");
        target.setSecure(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<img",
                "src=\"http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        // secure属性がtrueの場合、かつsecurePortの指定がない場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagUtil.getCustomTagConfig().setSecurePort(-1);
        target.setSrc("/R12345");
        target.setSecure(true);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<img",
                "src=\"https://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));

        // secure属性がfalseの場合、かつportの指定がない場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagUtil.getCustomTagConfig().setPort(-1);
        target.setSrc("/R12345");
        target.setSecure(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<img",
                "src=\"http://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testInputPageForDefaultWithAbsolutePath() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // img
        target.setSrc("/R12345");
        target.setAlt("alt_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<img",
                "src=\"" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
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
        
        // img
        target.setSrc("./R12345");
        target.setAlt("alt_test");
        target.setName("name_test");
        target.setLongdesc("longdesc_test");
        target.setHeight("height_test");
        target.setWidth("width_test");
        target.setUsemap("usemap_test");
        target.setIsmap("ismap_test");
        target.setAlign("align_test");
        target.setBorder("border_test");
        target.setHspace("hspace_test");
        target.setVspace("vspace_test");

        target.doStartTag();

        // 例外がスローされなければ成功
        assertTrue(true);
    }

    /**
     * 言語ごとのリソース切り替え判定をコンテキストルートの付加より先に実施していることを確認するテスト。
     * ResourcePathRuleに渡されるパスにコンテキスト名が含まれていないことをアサートする。
     */
    @Test
    public void testInputPageForResourcePathRule() throws Exception {

        ThreadContext.setLanguage(Locale.JAPANESE);
        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        TagUtil.getCustomTagConfig().setResourcePathRule(new ResourcePathRule() {
            @Override
            public String getPathForLanguage(String path, HttpServletRequest request) {
                assertThat("コンテキストパスが付加されていないこと。", path, is("/R12345"));
                return path + "_" + ThreadContext.getLanguage();
            }
            @Override
            protected String createPathForLanguage(String pathFromContextRoot, String language) {
                return null;
            }
        });

        // img
        target.setSrc("/R12345");
        target.setAlt("alt_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<img",
                "src=\"" + WebTestUtil.CONTEXT_PATH + "/R12345_ja" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    /**
     *  "http"、"https" といったスキーマを付加する処理が言語ごとのリソース切り替えより後に行われていることを確認するテスト。
     */
    @Test
    public void testInputPageForResourcePathRuleForSecure() throws Exception {

        ThreadContext.setLanguage(Locale.JAPANESE);
        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        TagUtil.getCustomTagConfig().setResourcePathRule(new ResourcePathRule() {
            @Override
            public String getPathForLanguage(String path, HttpServletRequest request) {
                return path + "_" + ThreadContext.getLanguage();
            }
            @Override
            protected String createPathForLanguage(String pathFromContextRoot, String language) {
                return null;
            }
        });

        String actual;
        String expected;

        // img
        target.setAlt("alt_test");

        // secure属性がtrueの場合(コンテキストパスの先頭に"/"あり)
        target.setSrc("/R12345");
        target.setSecure(true);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<img",
                "src=\"https://nablarch.co.jp:443" + WebTestUtil.CONTEXT_PATH + "/R12345_ja" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        // secure属性がtrueの場合(コンテキストパスの先頭に"/"なし)

        pageContext.getMockReq().setContextPath("dummy");

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        target.setSrc("/R12345");
        target.setSecure(true);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<img",
                "src=\"https://nablarch.co.jp:443/dummy/R12345_ja" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
        
        // secure属性がfalseの場合

        pageContext.getMockReq().setContextPath(WebTestUtil.CONTEXT_PATH);

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        target.setSrc("/R12345");
        target.setSecure(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<img",
                "src=\"http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345_ja" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        // secure属性がtrueの場合、かつsecurePortの指定がない場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagUtil.getCustomTagConfig().setSecurePort(-1);
        target.setSrc("/R12345");
        target.setSecure(true);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<img",
                "src=\"https://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345_ja" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));

        // secure属性がfalseの場合、かつportの指定がない場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagUtil.getCustomTagConfig().setPort(-1);
        target.setSrc("/R12345");
        target.setSecure(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<img",
                "src=\"http://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345_ja" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0" + '"',
                "alt=\"alt_test\" />").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }
}
