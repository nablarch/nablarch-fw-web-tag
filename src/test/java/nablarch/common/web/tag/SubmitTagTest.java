package nablarch.common.web.tag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import nablarch.common.permission.BasicPermission;
import nablarch.common.permission.Permission;
import nablarch.common.permission.PermissionUtil;
import nablarch.common.web.handler.WebTestUtil;
import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.Builder;
import nablarch.fw.web.handler.KeitaiAccessHandler;
import nablarch.test.support.web.servlet.MockServletRequest;
import org.junit.Test;

/**
 * {@link SubmitTag}及び{@link SubmitTagSupport}のテスト。
 * @author Kiyohito Itoh
 */
public class SubmitTagTest extends TagTestSupport<SubmitTag> {

    public SubmitTagTest() {
        super(new SubmitTag());
    }

    @Test
    public void testSpecifyInvalidAttribute() throws Exception {

        try {
            target.setType(null);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("type was invalid. type must specify the following values. values = [submit, button, image] type = [null]"));
        }

        try {
            target.setType("btn");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("type was invalid. type must specify the following values. values = [submit, button, image] type = [btn]"));
        }
    }

    @Test
    public void testInputPageForAllSetting() throws Exception {

        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // generic
        TagTestUtil.setGenericAttributes(target);

        // focus
        TagTestUtil.setFocusAttributes(target);

        // input
        target.setName("name_test");
        target.setDisabled(true);

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");
        target.setSrc("src_test");
        target.setAlt("alt_test");
        target.setUsemap("usemap_test");
        target.setAlign("align_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("/R12345");
        target.setAllowDoubleSubmission(false);
        target.setSecure(false);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "disabled=\"disabled\"",
                "src=\"src_test?nablarch_static_content_version=1.0.0\"",
                "alt=\"alt_test\"",
                "usemap=\"usemap_test\"",
                "align=\"align_test\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\"",
                "onfocus=\"onfocus_test\"",
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" />").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {

        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // generic
        TagTestUtil.setGenericAttributesWithHtml(target);

        // focus
        TagTestUtil.setFocusAttributesWithHtml(target);

        // input
        target.setName("name_test" + TagTestUtil.HTML);
        target.setDisabled(true);

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test" + TagTestUtil.HTML);
        target.setSrc("src_test" + TagTestUtil.HTML);
        target.setAlt("alt_test" + TagTestUtil.HTML);
        target.setUsemap("usemap_test" + TagTestUtil.HTML);
        target.setAlign("align_test" + TagTestUtil.HTML);

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345"+ TagTestUtil.HTML);
        target.setAllowDoubleSubmission(false);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test" + TagTestUtil.ESC_HTML + "\"",
                "class=\"css_test" + TagTestUtil.ESC_HTML + "\"",
                "style=\"style_test" + TagTestUtil.ESC_HTML + "\"",
                "title=\"title_test" + TagTestUtil.ESC_HTML + "\"",
                "lang=\"lang_test" + TagTestUtil.ESC_HTML + "\"",
                "xml:lang=\"xmlLang_test" + TagTestUtil.ESC_HTML + "\"",
                "dir=\"dir_test" + TagTestUtil.ESC_HTML + "\"",
                "accesskey=\"accesskey_test" + TagTestUtil.ESC_HTML + "\"",
                "tabindex=\"3\"",
                "type=\"submit\"",
                "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                "value=\"value_test" + TagTestUtil.ESC_HTML + "\"",
                "disabled=\"disabled\"",
                "src=\"src_test" + TagTestUtil.ESC_HTML + "?nablarch_static_content_version=1.0.0\"",
                "alt=\"alt_test" + TagTestUtil.ESC_HTML + "\"",
                "usemap=\"usemap_test" + TagTestUtil.ESC_HTML + "\"",
                "align=\"align_test" + TagTestUtil.ESC_HTML + "\"",
                "onclick=\"onclick_test" + TagTestUtil.ESC_HTML + "\"",
                "ondblclick=\"ondblclick_test" + TagTestUtil.ESC_HTML + "\"",
                "onmousedown=\"onmousedown_test" + TagTestUtil.ESC_HTML + "\"",
                "onmouseup=\"onmouseup_test" + TagTestUtil.ESC_HTML + "\"",
                "onmouseover=\"onmouseover_test" + TagTestUtil.ESC_HTML + "\"",
                "onmousemove=\"onmousemove_test" + TagTestUtil.ESC_HTML + "\"",
                "onmouseout=\"onmouseout_test" + TagTestUtil.ESC_HTML + "\"",
                "onkeypress=\"onkeypress_test" + TagTestUtil.ESC_HTML + "\"",
                "onkeydown=\"onkeydown_test" + TagTestUtil.ESC_HTML + "\"",
                "onkeyup=\"onkeyup_test" + TagTestUtil.ESC_HTML + "\"",
                "onfocus=\"onfocus_test" + TagTestUtil.ESC_HTML + "\"",
                "onblur=\"onblur_test" + TagTestUtil.ESC_HTML + "\"",
                "autofocus=\"autofocus\" />").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test" + TagTestUtil.ESC_HTML));
        assertThat(info.getUri(), is("./R12345" + TagTestUtil.HTML + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
    }

    @Test
    public void testInputPageForDefault() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("name_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
    }

    /**
     * CSP対応用のnonceをリクエストスコープに保存した時に、スクリプトが直接inputタグのonclick属性に
     * 出力されるのではなく、フォームコンテキストにためこまれることを確認する
     */
    @Test
    public void testInputPageForHasCspNonce() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        // input
        target.setName("name_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        assertThat(formContext.getInlineSubmissionScripts().size(), is(1));
        List<String> inlineSubmissionScripts = formContext.getInlineSubmissionScripts();
        assertThat(inlineSubmissionScripts.get(0), is("document.querySelector(\"input[name='name_test']\").onclick = window.nablarch_submit;"));
    }

    /**
     * サロゲートペアを扱うテストケース
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForSurrogatepair() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("𪛔𪛉𠀜");

        // submit,button,image
        target.setType("submit");
        target.setValue("🙈🙈🙈");

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"𪛔𪛉𠀜\"",
                "value=\"🙈🙈🙈\"",
                "onclick=\"return window.nablarch_submit(event, this);\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("𪛔𪛉𠀜"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("𪛔𪛉𠀜"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
    }

    @Test
    public void testInputPageForInvalidLocation() throws Exception {

        // input
        target.setName("name_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        // nablarch
        target.setUri("./R12345");

        try {
            target.doStartTag();
            fail("must be thrown IllegalArgumentException.");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("invalid location of the submit tag. the submit tag must locate in the form tag."));
        }
    }

    @Test
    public void testInputPageForSecure() throws Exception {

        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("name_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        // nablarch
        target.setUri("/R12345");

        // secure属性がtrueの場合

        target.setSecure(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("https://nablarch.co.jp:443" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));

        // secure属性がfalseの場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        target.setSecure(false);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));

        // secure属性がtrueの場合、かつsecurePortの指定がない場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        TagUtil.getCustomTagConfig().setSecurePort(-1);
        target.setSecure(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("https://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));

        // secure属性がfalseの場合、かつportの指定がない場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        TagUtil.getCustomTagConfig().setPort(-1);
        target.setSecure(false);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("http://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
    }

    /**
     * onclick属性を指定した時に、CSPのnonceの有無に関わらず指定した属性値がそのまま出力されることを確認する。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForOnclick() throws Exception {
        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("name_test");
        target.setOnclick("onclick_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "onclick=\"onclick_test\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + nablarch.test.support.web.WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        // CSP対応用のnonceを含めている場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "onclick=\"onclick_test\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + nablarch.test.support.web.WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは登録されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        // suppressCallNablarchSubmitをtrueにした場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // nablarch
        target.setSuppressCallNablarchSubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "onclick=\"onclick_test\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + nablarch.test.support.web.WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは登録されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        // suppressCallNablarchSubmitをtrueにした場合（CSP）

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        // nablarch
        target.setSuppressCallNablarchSubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "onclick=\"onclick_test\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + nablarch.test.support.web.WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは登録されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));
    }

    /**
     * SuppressCallNablarchSubmit属性に{@code true}を指定した時に、CSPのnonceの有無に関わらず
     * サブミット用のスクリプトが出力されなくなることを確認する。
     */
    @Test
    public void testInputPageForSuppressCallNablarchSubmit() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("name_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        // nablarch
        target.setUri("./R12345");
        target.setSuppressCallNablarchSubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        assertThat(formContext.getInputNames().isEmpty(), is(true));

        // CSP対応用のnonceを含めている場合

        TagTestUtil.clearOutput(pageContext);
        TagTestUtil.setUpDefaultConfig();
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは生成されない
        assertThat(formContext.getInputNames().isEmpty(), is(true));

        // onclickを指定した場合はそのまま出力される
        TagTestUtil.clearOutput(pageContext);
        TagTestUtil.setUpDefaultConfig();
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setOnclick("onclick_test");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "onclick=\"onclick_test\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは生成されない
        assertThat(formContext.getInputNames().isEmpty(), is(true));

        // onclickを指定した場合はそのまま出力される（CSP）
        TagTestUtil.clearOutput(pageContext);
        TagTestUtil.setUpDefaultConfig();
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "onclick=\"onclick_test\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは生成されない
        assertThat(formContext.getInputNames().isEmpty(), is(true));
    }

    @Test
    public void testInputPageForDefaultWithAbsolutePath() throws Exception {

        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("name_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        // nablarch
        target.setUri("http://test.com/R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("http://test.com/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
    }

    /**
     * {@link CustomTagConfig}の設定が有効に反映されていることをテストする。
     * @throws Exception
     */
    @Test
    public void testDisplayControlWithCustomTagConfigInfo() throws Exception {
        TagTestUtil.setUpDefaultWithDisplayControlSettings();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // 準備
        CustomTagConfig customTagConfig = TagUtil.getCustomTagConfig();

        SortedSet<String> requestIds = new TreeSet<String>();
        requestIds.add("/R00000");
        Permission permission = new BasicPermission(requestIds);
        PermissionUtil.setPermission(permission);

        ServiceAvailableMock availableMock = (ServiceAvailableMock) SystemRepository.getObject("serviceAvailability");
        availableMock.setAvailable(true);

        // デフォルト
        // 表示制御必要
        target.setName("name_test_null_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "<input name=\"name_test_null_control\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御不要
        target.setName("name_test_null_nocontrol");
        target.setUri("/R00000");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_null_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);

        // NODISPLAYが機能していることの確認。
        customTagConfig.setDisplayMethod("NODISPLAY");
        // 表示制御不要
        target.setName("name_test_nodisplay_nocontrol");
        target.setUri("/R00000");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_nodisplay_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_nodisplay_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);

        // NORMALが機能していることの確認
        customTagConfig.setDisplayMethod("NORMAL");
        // 表示制御不要
        target.setName("name_test_normal_nocontrol");
        target.setUri("/R00000");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_normal_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_normal_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_normal_control\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);

        // DISABLEDが機能していることの確認
        customTagConfig.setDisplayMethod("DISABLED");
        // 表示制御不要
        target.setName("name_test_disabled_nocontrol");
        target.setUri("/R00000");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_disabled_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_disabled_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_disabled_control\" disabled=\"disabled\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);

    }

    /**
     * タグで個別にdisplayMethod属性が指定された場合
     * @throws Exception
     */
    @Test
    public void testSubmissionDisplayWithCustomizedInfo() throws Exception {
        TagTestUtil.setUpDefaultWithDisplayControlSettings();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        try {
            target.setDisplayMethod("hoge");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("displayMethod was invalid. displayMethod must specify the following values. values = [NODISPLAY, DISABLED, NORMAL] displayMethod = [hoge]"));
        }

        // 準備
        CustomTagConfig customTagConfig = TagUtil.getCustomTagConfig();
        customTagConfig.setDisplayMethod("NODISPLAY");

        SortedSet<String> requestIds = new TreeSet<String>();
        requestIds.add("/R00000");
        Permission permission = new BasicPermission(requestIds);
        PermissionUtil.setPermission(permission);

        ServiceAvailableMock availableMock = (ServiceAvailableMock) SystemRepository.getObject("serviceAvailability");
        availableMock.setAvailable(true);

        // NODISPLAYが機能していることの確認。
        customTagConfig.setDisplayMethod("NORMAL");
        target.setDisplayMethod("NODISPLAY");
        // 表示制御不要
        target.setName("name_test_nodisplay_nocontrol");
        target.setUri("/R00000");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "<input name=\"name_test_nodisplay_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_nodisplay_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);

        // NORMALが機能していることの確認
        customTagConfig.setDisplayMethod("NODISPLAY");
        target.setDisplayMethod("NORMAL");
        // 表示制御不要
        target.setName("name_test_normal_nocontrol");
        target.setUri("/R00000");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_normal_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_normal_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_normal_control\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);

        // DISABLEDが機能していることの確認
        customTagConfig.setDisplayMethod("NODISPLAY");
        target.setDisplayMethod("DISABLED");
        // 表示制御不要
        target.setName("name_test_disabled_nocontrol");
        target.setUri("/R00000");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_disabled_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_disabled_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<input name=\"name_test_disabled_control\" disabled=\"disabled\" onclick=\"return window.nablarch_submit(event, this);\" />";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
    }

    /**
     * 本タグがFormタグ内に定義されていない場合（FormContextが設定されていない場合）に、
     * IllegalStateExceptionがスローされることのテスト。
     */
    @Test
    public void testNotChildOfForm() throws Exception {

        pageContext.getMockReq().getParams().put("name_test", new String[] {"value_test"});

        // input
        target.setName("name_test");

        try {
            target.doStartTag();
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("invalid location of the submit tag. the submit tag must locate in the form tag."));
        }
    }

    @Test
    public void testJsUnsupported() throws Exception {

        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // JS未サポートの設定
        pageContext.setAttribute(KeitaiAccessHandler.JS_UNSUPPORTED_FLAG_NAME, Boolean.TRUE, PageContext.REQUEST_SCOPE);

        target.setName("register");
        target.setType("submit");
        target.setValue("登録");
        target.setUri("/action/MenuAction/WRAA0001");

        // type='submit' ボタンで無ければPOSTできないのでエラーとなること。

        target.setType("button");
        try {
            target.doStartTag();
            fail("type='submit' ボタンで無ければPOSTできないのでエラーとなること。");
        } catch (JspException e) {
            assertThat(e.getMessage(), is("Without javascript, <n:submit> tags will not work properly "
                                        + " if its type is button."
                                        + " Use 'submit' type instead."));
        }

        target.setType("image");
        try {
            target.doStartTag();
            fail("type='submit' ボタンで無ければPOSTできないのでエラーとなること。");
        } catch (JspException e) {
            assertThat(e.getMessage(), is("Without javascript, <n:submit> tags will not work properly "
                                        + " if its type is image."
                                        + " Use 'submit' type instead."));
        }

        // オーバーライド用URIがコンテキストルートからのパスに変換されて設定されること。

        MockServletRequest request = (MockServletRequest) pageContext.getRequest();

        // コンテキストルートからのパス指定(コンテキスト指定あり)

        TagTestUtil.clearOutput(pageContext);

        request.setContextPath("/context_name");
        request.setRequestURI("/context_name/action/MenuAction/WRZZ9999");
        target.setName("register");
        target.setValue("登録");
        target.setType("submit");
        target.setUri("/action/MenuAction/WRAA1111");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"nablarch_uri_override_register|/action/MenuAction/WRAA1111" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "value=\"登録\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        // コンテキストルートからのパス指定(コンテキスト指定なし)

        TagTestUtil.clearOutput(pageContext);

        request.setContextPath("");
        request.setRequestURI("/action/MenuAction/WRZZ9999");
        target.setName("register");
        target.setValue("登録");
        target.setType("submit");
        target.setUri("/action/MenuAction/WRAA1111");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"nablarch_uri_override_register|/action/MenuAction/WRAA1111" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "value=\"登録\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        request.setContextPath("/");
        request.setRequestURI("/action/MenuAction/WRZZ9999");
        target.setName("register");
        target.setValue("登録");
        target.setType("submit");
        target.setUri("/action/MenuAction/WRAA1111");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"nablarch_uri_override_register|//action/MenuAction/WRAA1111" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "value=\"登録\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        // 絶対URL指定

        TagTestUtil.clearOutput(pageContext);

        request.setContextPath("/context_name");
        request.setRequestURI("/context_name/action/MenuAction/WRZZ9999");
        target.setName("register");
        target.setValue("登録");
        target.setType("submit");
        target.setUri("https://test.co.jp:8888/context_name/action/MenuAction/WRAA2222");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"nablarch_uri_override_register|/action/MenuAction/WRAA2222" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "value=\"登録\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        // 相対URL指定

        TagTestUtil.clearOutput(pageContext);

        request.setContextPath("/context_name");
        request.setRequestURI("/context_name/action/MenuAction/WRZZ9999");
        target.setName("register");
        target.setValue("登録");
        target.setType("submit");
        target.setUri("../OtherAction/WRAA3333");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"nablarch_uri_override_register|/action/MenuAction/../OtherAction/WRAA3333" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "value=\"登録\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
    }
}
