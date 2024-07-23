package nablarch.common.web.tag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;

import nablarch.common.permission.BasicPermission;
import nablarch.common.permission.Permission;
import nablarch.common.permission.PermissionUtil;
import nablarch.common.web.handler.MockPageContext;
import nablarch.common.web.handler.WebTestUtil;
import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.Builder;
import org.junit.Test;

/**
 * {@link SubmitLinkTag}及び{@link SubmitLinkTagSupport}のテスト。
 * @author Kiyohito Itoh
 */
public class SubmitLinkTagTest extends TagTestSupport<SubmitLinkTag> {

    public SubmitLinkTagTest() {
        super(new SubmitLinkTag());
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

        // a
        target.setName("name_test");
        target.setShape("shape_test");
        target.setCoords("coords_test");

        // nablarch
        target.setUri("/R12345");
        target.setAllowDoubleSubmission(false);
        target.setSecure(false);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<a",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "name=\"name_test\"",
                "href=\"" + "http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "shape=\"shape_test\"",
                "coords=\"coords_test\"",
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
                "onblur=\"onblur_test\"></a>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
    }

    /**
     * 不正なURIを指定した場合のケース。
     *
     * リクエストIDがURIから取得出来ないため、{@link JspException}が送出される。
     * @throws Exception
     */
    @Test(expected = JspException.class)
    public void testInvalidUri() throws Exception {

        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // a
        target.setName("name_test");
        target.setShape("shape_test");
        target.setCoords("coords_test");

        // nablarch
        target.setUri(null);

        target.doStartTag();
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

        // a
        target.setName("name_test" + TagTestUtil.HTML);
        target.setShape("shape_test" + TagTestUtil.HTML);
        target.setCoords("coords_test" + TagTestUtil.HTML);

        // nablarch
        target.setUri("./R12345" + TagTestUtil.HTML);
        target.setAllowDoubleSubmission(false);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<a",
                "id=\"id_test" + TagTestUtil.ESC_HTML + "\"",
                "class=\"css_test" + TagTestUtil.ESC_HTML + "\"",
                "style=\"style_test" + TagTestUtil.ESC_HTML + "\"",
                "title=\"title_test" + TagTestUtil.ESC_HTML + "\"",
                "lang=\"lang_test" + TagTestUtil.ESC_HTML + "\"",
                "xml:lang=\"xmlLang_test" + TagTestUtil.ESC_HTML + "\"",
                "dir=\"dir_test" + TagTestUtil.ESC_HTML + "\"",
                "accesskey=\"accesskey_test" + TagTestUtil.ESC_HTML + "\"",
                "tabindex=\"3\"",
                "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                "href=\"" + "./R12345" + TagTestUtil.ESC_HTML + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "shape=\"shape_test" + TagTestUtil.ESC_HTML + "\"",
                "coords=\"coords_test" + TagTestUtil.ESC_HTML + "\"",
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
                "onblur=\"onblur_test" + TagTestUtil.ESC_HTML + "\"></a>").replace(Builder.LS, " ");
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

        // a
        target.setName("name_test");

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></a>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
    }

    /**
     * CSP対応用のnonceをリクエストスコープに保存した時に、スクリプトが直接aタグのonclick属性に
     * 出力されるのではなく、フォームコンテキストにためこまれることを確認する
     */
    @Test
    public void testInputPageForHasCspNonce() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        // a
        target.setName("name_test");

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"></a>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        assertThat(formContext.getInlineSubmissionScripts().size(), is(1));
        List<String> inlineSubmissionScripts = formContext.getInlineSubmissionScripts();
        assertThat(inlineSubmissionScripts.get(0), is("document.querySelector(\"a[name='name_test']\").onclick = window.nablarch_submit;"));

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

        // a
        target.setName("𪛔𪛉𠀜");

        // title
        target.setTitle("😸😸😸");

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<a",
                "title=\"😸😸😸\"",
                "name=\"𪛔𪛉𠀜\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></a>"
        ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("𪛔𪛉𠀜"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("𪛔𪛉𠀜"));
    }

    @Test
    public void testInputPageForInvalidLocation() throws Exception {

        // a
        target.setName("name_test");

        // nablarch
        target.setUri("./R12345");

        try {
            target.doStartTag();
            fail("must be thrown IllegalStateException.");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("invalid location of the submitLink tag. the submitLink tag must locate in the form tag."));
        }
    }

    @Test
    public void testInputPageForSecure() throws Exception {

        TagTestUtil.setUpDefaultWithDisplayControlSettings();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // a
        target.setName("name_test");

        // nablarch
        target.setUri("/R12345");

        // secure属性がtrueの場合

        target.setSecure(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"https://nablarch.co.jp:443" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></a>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        // secure属性がfalseの場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        target.setSecure(false);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></a>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

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
                "<a",
                "name=\"name_test\"",
                "href=\"https://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></a>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

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
                "<a",
                "name=\"name_test\"",
                "href=\"http://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></a>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForDefaultWithAbsolutePath() throws Exception {

        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // a
        target.setName("name_test");

        // nablarch
        target.setUri("http://test.com/R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"" + "http://test.com/R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></a>"
                ).replace(Builder.LS, " ");
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
    public void testDisplayConrtolWithCustomTagConfigInfo() throws Exception {
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
        String expected = "<a name=\"name_test_null_control\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00001_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御不要
        target.setName("name_test_null_nocontrol");
        target.setUri("/R00000");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<a name=\"name_test_null_nocontrol\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00000_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
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
        expected = "<a name=\"name_test_nodisplay_nocontrol\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00000_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
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
        expected = "<a name=\"name_test_normal_nocontrol\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00000_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_normal_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<a name=\"name_test_normal_control\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00001_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
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
        expected = "<a name=\"name_test_disabled_nocontrol\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00000_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_disabled_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);

    }

    /**
     * タグで個別にdisplayMethod属性が指定された場合
     * @throws Exception
     */
    @Test
    public void testDisplayControlWithCustomizedInfo() throws Exception {
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
        String expected = "<a name=\"name_test_nodisplay_nocontrol\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00000_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
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
        expected = "<a name=\"name_test_normal_nocontrol\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00000_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_normal_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<a name=\"name_test_normal_control\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00001_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
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
        expected = "<a name=\"name_test_disabled_nocontrol\" href=\"" + WebTestUtil.CONTEXT_PATH + "/R00000_encode_suffix\" onclick=\"return window.nablarch_submit(event, this);\"></a>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_disabled_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
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

        // a
        target.setName("name_test");
        target.setOnclick("onclick_test");

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"onclick_test\"></a>"
        ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* CSP対応用のnonceを含めている場合 */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"onclick_test\"></a>"
        ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* suppressCallNablarchSubmitをtrueにした場合 */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // nablarch
        target.setSuppressCallNablarchSubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"onclick_test\"></a>"
        ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* suppressCallNablarchSubmitをtrueにした場合（CSP対応用のnonceを含めている） */

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
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"onclick_test\"></a>"
        ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
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

        // a
        target.setName("name_test");

        // nablarch
        target.setUri("./R12345");
        target.setSuppressCallNablarchSubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"></a>"
        ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* CSP対応用のnonceを含めている場合 */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"></a>"
        ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* onclickを指定した場合はそのまま出力される */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // a
        target.setOnclick("onclick_test");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"onclick_test\"></a>"
        ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
        // スクリプトは生成されない
        assertThat(formContext.getInlineSubmissionScripts().isEmpty(), is(true));

        /* onclickを指定した場合はそのまま出力される（CSP対応用のnonceを含めている） */

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        // nonce
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<a",
                "name=\"name_test\"",
                "href=\"./R12345" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                "onclick=\"onclick_test\"></a>"
        ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.TRANSITION));
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
            assertThat(e.getMessage(), is("invalid location of the submitLink tag. the submitLink tag must locate in the form tag."));
        }
    }

    /**
     * 「非活性」項目を、JSPインクルードを使用して描画できること。
     */
    @Test
    public void testRenderDisabledUsingJspInclude() throws JspException {
        final Map<String, Object> attributesLog = new HashMap<String, Object>();
        class MyPageContext extends MockPageContext {
            String relativeUrlPath = "";
            @Override
            public void include(String relativeUrlPath) throws ServletException, IOException {
                this.relativeUrlPath = relativeUrlPath;
            }

            @Override
            public ServletRequest getRequest() {
                return new HttpServletRequestWrapper((HttpServletRequest) super.getRequest()){
                    public void setAttribute(String name, Object o) {
                        super.setAttribute(name, o);
                        attributesLog.put(name, o);
                    }
                };
            }
        }

        TagTestUtil.setUpDefaultWithJspForDisabled();

        FormContext formContext = TagTestUtil.createFormContext();
        MyPageContext pageContext = new MyPageContext();
        TagUtil.setFormContext(pageContext, formContext);
        target.setPageContext(pageContext);
        target.setId("test_id");
        target.setStyle("test style");
        target.setCssClass("test class");

        // 準備
        CustomTagConfig customTagConfig = TagUtil.getCustomTagConfig();

        SortedSet<String> requestIds = new TreeSet<String>();
        requestIds.add("R00000");
        Permission permission = new BasicPermission(requestIds);
        PermissionUtil.setPermission(permission);

        ServiceAvailableMock availableMock = (ServiceAvailableMock) SystemRepository.getObject("serviceAvailability");
        availableMock.setAvailable(true);

        // DISABLEDが機能していることの確認
        customTagConfig.setDisplayMethod("DISABLED");
        // 表示制御必要
        target.setBodyContent(new MockBodyContent("body"));
        target.setName("name_test_disabled_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(BodyTag.EVAL_BODY_BUFFERED));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(pageContext.relativeUrlPath, is("disabled.jsp"));

        // nablarch_css_class が属性に設定される。
        assertThat((String) attributesLog.get("nablarch_link_attributes_class"), is("test class"));
        assertThat((String) attributesLog.get("nablarch_link_attributes_id"), is("test_id"));
        assertThat((String) attributesLog.get("nablarch_link_attributes_style"), is("test style"));

        TagTestUtil.clearOutput(pageContext);

    }

}
