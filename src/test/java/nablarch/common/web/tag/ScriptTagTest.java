package nablarch.common.web.tag;

import jakarta.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.WebTestUtil;
import nablarch.core.util.Builder;
import nablarch.fw.web.handler.KeitaiAccessHandler;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * @author Kiyohito Itoh
 */
public class ScriptTagTest extends TagTestSupport<ScriptTag> {
    
    public ScriptTagTest() {
        super(new ScriptTag());
    }

    @Before
    public void setup() {
        TagTestUtil.setUpDefaultConfig();
    }
        
    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // script
        target.setId("id_test");
        target.setCharset("charset_test");
        target.setType("type_test");
        target.setLanguage("language_test");
        target.setSrc("./R12345");
        target.setDefer("defer_test");
        target.setXmlSpace("xmlSpace_test");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<script",
                "id=\"id_test\"",
                "type=\"type_test\"",
                "src=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0\"",
                "charset=\"charset_test\"",
                "language=\"language_test\"",
                "defer=\"defer_test\"",
                "xml:space=\"xmlSpace_test\"></script>").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    
    @Test
    public void testScriptTagSuppressedEntirelyIfJsUnsuppotedFlagSet() throws Exception {
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // script
        target.setId("id_test");
        target.setCharset("charset_test");
        target.setType("type_test");
        target.setLanguage("language_test");
        target.setSrc("./R12345");
        target.setDefer("defer_test");
        target.setXmlSpace("xmlSpace_test");
        
        pageContext.setAttribute(KeitaiAccessHandler.JS_UNSUPPORTED_FLAG_NAME, "true"); // JS出力を抑制
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";  // なにもでない。
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");        
    }
    
    
    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // script
        target.setId("id_test" + TagTestUtil.HTML);
        target.setCharset("charset_test" + TagTestUtil.HTML);
        target.setType("type_test" + TagTestUtil.HTML);
        target.setLanguage("language_test" + TagTestUtil.HTML);
        target.setSrc("./R12345" + TagTestUtil.HTML);
        target.setDefer("defer_test" + TagTestUtil.HTML);
        target.setXmlSpace("xmlSpace_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<script",
                "id=\"id_test" + TagTestUtil.ESC_HTML + "\"",
                "type=\"type_test" + TagTestUtil.ESC_HTML + "\"",
                "src=\"./R12345" + TagTestUtil.ESC_HTML + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0\"",
                "charset=\"charset_test" + TagTestUtil.ESC_HTML + "\"",
                "language=\"language_test" + TagTestUtil.ESC_HTML + "\"",
                "defer=\"defer_test" + TagTestUtil.ESC_HTML + "\"",
                "xml:space=\"xmlSpace_test" + TagTestUtil.ESC_HTML + "\"></script>").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForDefault() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // script
        target.setType("type_test");
        target.setSrc("./R12345");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<script",
                "type=\"type_test\"",
                "src=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0\"></script>").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    /**
     * サロゲートペアを扱うテストケース
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForSurrogatepair() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // script
        target.setType("🙊🙊🙊_test");
        target.setSrc("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<script",
                "type=\"🙊🙊🙊_test\"",
                "src=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0\"></script>").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForSecure() throws Exception {
        
        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // script
        target.setType("type_test");
        
        // secure属性がtrueの場合
        
        target.setSrc("/R12345");
        target.setSecure(true);
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<script",
                "type=\"type_test\"",
                "src=\"https://nablarch.co.jp:443" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0"
                        + "\"></script>").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        // secure属性がfalseの場合
        
        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        target.setSrc("/R12345");
        target.setSecure(false);
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<script",
                "type=\"type_test\"",
                "src=\"http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0"
                        + "\"></script>").replace(Builder.LS, " ");
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
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<script",
                "type=\"type_test\"",
                "src=\"https://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0"
                        + "\"></script>").replace(Builder.LS, " ");
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
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<script",
                "type=\"type_test\"",
                "src=\"http://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0"
                        + "\"></script>").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testInputPageForDefaultWithAbsolutePath() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // script
        target.setType("type_test");
        target.setSrc("/R12345");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<script",
                "type=\"type_test\"",
                "src=\"" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "?nablarch_static_content_version=1.0.0"
                        + "\"></script>").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testBodyPrefixAndSuffix() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // script(src属性を指定しない)
        target.setType("type_test");
        
        // CustomTagConfigのデフォルト値の場合
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        
        String startTag = Builder.lines(
                "<script",
                "type=\"type_test\">").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        
        String excludeStartTag = Builder.lines(
                "<!--",
                "",
                "-->",
                "</script>");
        String[] expected = excludeStartTag.split(Builder.LS);
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], expected[i - 1], " ");
        }
        
        // CustomTagConfigのデフォルト値を変更した場合
        TagTestUtil.clearOutput(pageContext);
        
        TagUtil.getCustomTagConfig().setScriptBodyPrefix("<!--");
        TagUtil.getCustomTagConfig().setScriptBodySuffix("// -->");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        
        startTag = Builder.lines(
                "<script",
                "type=\"type_test\">").replace(Builder.LS, " ");
        System.out.println(actual);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        
        excludeStartTag = Builder.lines(
                "<!--",
                "",
                "// -->",
                "</script>");
        expected = excludeStartTag.split(Builder.LS);
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], expected[i - 1], " ");
        }
        
        
        // 元に戻す
        CustomTagConfig defaultConfig = new CustomTagConfig();
        TagUtil.getCustomTagConfig().setScriptBodyPrefix(defaultConfig.getScriptBodyPrefix());
        TagUtil.getCustomTagConfig().setScriptBodySuffix(defaultConfig.getScriptBodySuffix());
        
    }
}
