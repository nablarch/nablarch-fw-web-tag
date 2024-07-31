package nablarch.common.web.tag;

import java.util.List;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.WebTestUtil;
import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.core.util.Builder;
import nablarch.fw.web.handler.SecureHandler;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * {@link DownloadSubmitTag}のテスト。
 * @author Kiyohito Itoh
 */
public class DownloadSubmitTagTest extends TagTestSupport<DownloadSubmitTag> {

    public DownloadSubmitTagTest() {
        super(new DownloadSubmitTag());
    }

    @Before
    public void setup() {
        TagTestUtil.setUpDefaultConfig();
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

        target.setSrc("download_src_value");

        // HTML5
        target.setAutofocus(true);

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
                "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                "onclick=\"return window.nablarch_submit(event, this);\"",
                "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));
    }

    /**
     * CSP対応用のnonceをリクエストスコープに保存した時に、スクリプトが直接inputタグのonclick属性に
     * 出力されるのではなく、フォームコンテキストにためこまれることを確認する
     */
    @Test
    public void testInputPageForHasCspNonce() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContextByName("test_form1");
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(SecureHandler.CSP_NONCE_KEY, "abcde", PageContext.REQUEST_SCOPE);

        // input
        target.setName("name_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        target.setSrc("download_src_value");

        // HTML5
        target.setAutofocus(true);

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
                "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));
        List<String> inlineSubmissionScripts = formContext.getInlineSubmissionScripts();
        assertThat(inlineSubmissionScripts.size(), is(1));
        assertThat(inlineSubmissionScripts.get(0), is("document.querySelector(\"form[name='test_form1'] input[name='name_test']\").onclick = window.nablarch_submit;"));
    }

    /**
     * サロゲートペアを扱うテストケース。
     * @throws Exception
     */
    @Test
    public void testInputPageForSurrogatepair() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("😸😸😸");

        // submit,button,image
        target.setType("submit");
        target.setValue("🙊🙈🙉");

        target.setSrc("download_src_value");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"😸😸😸\"",
                "value=\"🙊🙈🙉\"",
                "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                "onclick=\"return window.nablarch_submit(event, this);\"",
                "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("😸😸😸"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("😸😸😸"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));
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

        target.setSrc("download_src_value");

        // HTML5
        target.setAutofocus(true);

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
                        "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));        // スクリプトは生成されない
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* CSP対応用のnonceを含めている場合 */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(SecureHandler.CSP_NONCE_KEY, "abcde", PageContext.REQUEST_SCOPE);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));        // スクリプトは生成されない
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* suppressDefaultSubmitをtrueにした場合 */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // nablarch
        target.setSuppressDefaultSubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));        // スクリプトは生成されない
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* suppressDefaultSubmitをtrueにした場合（CSP対応用のnonceを含めている） */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(SecureHandler.CSP_NONCE_KEY, "abcde", PageContext.REQUEST_SCOPE);

        // nablarch
        target.setSuppressDefaultSubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));        // スクリプトは生成されない
        // スクリプトは生成されない
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

        target.setSrc("download_src_value");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        target.setSuppressDefaultSubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* CSP対応用のnonceを含めている場合 */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(SecureHandler.CSP_NONCE_KEY, "abcde", PageContext.REQUEST_SCOPE);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* onclickを指定した場合はそのまま出力される */

        TagTestUtil.clearOutput(pageContext);
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
                        "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* onclickを指定した場合はそのまま出力される（CSP対応用のnonceを含めている） */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(SecureHandler.CSP_NONCE_KEY, "abcde", PageContext.REQUEST_SCOPE);

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
                        "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));
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
            assertThat(e.getMessage(), is("invalid location of the downloadSubmit tag. the downloadSubmit tag must locate in the form tag."));
        }
    }
}
