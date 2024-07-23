package nablarch.common.web.tag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.WebTestUtil;
import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.core.util.Builder;
import org.junit.Test;

public class PopupSubmitTagTest extends TagTestSupport<PopupSubmitTag> {

    public PopupSubmitTagTest() {
        super(new PopupSubmitTag());
    }

    @Test
    public void testInputPageForDefault() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // input
        target.setName("name_test");
        target.setPopupWindowName("window_name");
        
        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");
        target.setSrc("src_value");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        target.setPopupOption("width=400, height=300");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                "onclick=\"return window.nablarch_submit(event, this);\"",
                "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
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
        target.setPopupWindowName("window_name");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");
        target.setSrc("src_value");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        target.setPopupOption("width=400, height=300");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
        assertThat(formContext.getInlineSubmissionScripts().size(), is(1));
        List<String> inlineSubmissionScripts = formContext.getInlineSubmissionScripts();
        assertThat(inlineSubmissionScripts.get(0), is("document.querySelector(\"input[name='name_test']\").onclick = window.nablarch_submit;"));
    }

    /**
     * サロゲートペアを扱うテストケース
     * @throws Exception
     */
    @Test
    public void testInputPageForSurrogatepair() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("🙊🙊🙊_test");
        target.setPopupWindowName("window_name");

        // submit,button,image
        target.setType("submit");
        target.setValue("🙈🙈🙈_test");
        target.setSrc("🙉🙉🙉_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        target.setPopupOption("width=400, height=300");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"🙊🙊🙊_test\"",
                "value=\"🙈🙈🙈_test\"",
                "src=\"🙉🙉🙉_test?nablarch_static_content_version=1.0.0\"",
                "onclick=\"return window.nablarch_submit(event, this);\"",
                "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("🙊🙊🙊_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("🙊🙊🙊_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
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
        target.setPopupWindowName("window_name");
        target.setOnclick("onclick_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");
        target.setSrc("src_value");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        target.setPopupOption("width=400, height=300");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                "onclick=\"onclick_test\"",
                "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

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
                        "value=\"value_test\"",
                        "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        // suppressCallNablarchSubmitをtrueにした場合
        TagTestUtil.clearOutput(pageContext);
        TagTestUtil.setUpDefaultConfig();
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
                        "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        // suppressCallNablarchSubmitをtrueにした場合（CSP）
        TagTestUtil.clearOutput(pageContext);
        TagTestUtil.setUpDefaultConfig();
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        // input
        target.setName("name_test");
        target.setPopupWindowName("window_name");
        target.setOnclick("onclick_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");
        target.setSrc("src_value");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        target.setPopupOption("width=400, height=300");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
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
        target.setPopupWindowName("window_name");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");
        target.setSrc("src_value");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        target.setPopupOption("width=400, height=300");
        target.setSuppressCallNablarchSubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                        "<input",
                        "type=\"submit\"",
                        "name=\"name_test\"",
                        "value=\"value_test\"",
                        "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

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
                        "value=\"value_test\"",
                        "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

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
                        "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

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
                        "src=\"src_value?nablarch_static_content_version=1.0.0\"",
                        "onclick=\"onclick_test\"",
                        "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));
    }

    /**
     * popupOptionプロパティが明示的に指定されていない場合、
     * デフォルト値のwindowOptionが設定されること。
     */
    @Test
    public void testDefaultWindowOptions() throws JspException {
        // デフォルト値を設定
        TagTestUtil.setUpDefaultWithPopupOption();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("name_test");

        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        //target.setPopupOption("width=400, height=300");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\"",
                "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=500, height=400"));
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
            assertThat(e.getMessage(), is("invalid location of the popupSubmit tag. the popupSubmit tag must locate in the form tag."));
        }
    }
    
}
