package nablarch.common.web.tag;

import nablarch.common.permission.BasicPermission;
import nablarch.common.permission.Permission;
import nablarch.common.permission.PermissionUtil;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.Builder;
import nablarch.test.support.web.WebTestUtil;
import org.junit.Test;

import javax.servlet.jsp.tagext.Tag;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * {@link ButtonTag}及び{@link ButtonTagSupport}のテスト。
 * @author Kiyohito Itoh
 */
public class ButtonTagTest extends TagTestSupport<ButtonTag> {

    public ButtonTagTest() {
        super(new ButtonTag());
    }

    @Test
    public void testSpecifyInvalidAttribute() throws Exception {

        try {
            target.setType(null);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("type was invalid. type must specify the following values. values = [submit, button, reset] type = [null]"));
        }

        try {
            target.setType("btn");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("type was invalid. type must specify the following values. values = [submit, button, reset] type = [btn]"));
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

        // button
        target.setName("name_test");
        target.setValue("value_test");
        target.setType("submit");
        target.setDisabled(true);

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
                "<button",
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
                "autofocus=\"autofocus\"></button>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionInfo.SubmissionAction.TRANSITION));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // generic
        TagTestUtil.setGenericAttributesWithHtml(target);

        // focus
        TagTestUtil.setFocusAttributesWithHtml(target);

        // button
        target.setName("name_test" + TagTestUtil.HTML);
        target.setValue("value_test" + TagTestUtil.HTML);
        target.setType("submit");
        target.setDisabled(true);

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345" + TagTestUtil.HTML);
        target.setAllowDoubleSubmission(false);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<button",
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
                "autofocus=\"autofocus\"></button>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test" + TagTestUtil.ESC_HTML));
        assertThat(info.getUri(), is("./R12345" + TagTestUtil.HTML + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionInfo.SubmissionAction.TRANSITION));
    }

    @Test
    public void testInputPageForDefault() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // button
        target.setName("name_test");

        // nablarch
        target.setUri("./R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<button",
                "name=\"name_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></button>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionInfo.SubmissionAction.TRANSITION));
    }

    @Test
    public void testInputPageForInvalidLocation() throws Exception {

        // button
        target.setName("name_test");

        // nablarch
        target.setUri("./R12345");

        try {
            target.doStartTag();
            fail("must be thrown IllegalArgumentException.");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("invalid location of the button tag. the button tag must locate in the form tag."));
        }
    }

    @Test
    public void testInputPageForSecure() throws Exception {

        TagTestUtil.setUpDefaultConfig();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // button
        target.setName("name_test");

        // nablarch
        target.setUri("/R12345");

        // secure属性がtrueの場合

        target.setSecure(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<button",
                "name=\"name_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></button>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("https://nablarch.co.jp:443" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionInfo.SubmissionAction.TRANSITION));

        // secure属性がfalseの場合

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        target.setSecure(false);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<button",
                "name=\"name_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></button>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("http://nablarch.co.jp:8080" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionInfo.SubmissionAction.TRANSITION));

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
                "<button",
                "name=\"name_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></button>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("https://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionInfo.SubmissionAction.TRANSITION));

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
                "<button",
                "name=\"name_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></button>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("http://nablarch.co.jp" + WebTestUtil.CONTEXT_PATH + "/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionInfo.SubmissionAction.TRANSITION));
    }

    @Test
    public void testInputPageForDefaultWithAbsolutePath() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // button
        target.setName("name_test");

        // nablarch
        target.setUri("http://test.com/R12345");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<button",
                "name=\"name_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\"></button>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("http://test.com/R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionInfo.SubmissionAction.TRANSITION));
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
        String expected = "<button name=\"name_test_null_control\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御不要
        target.setName("name_test_null_nocontrol");
        target.setUri("/R00000");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<button name=\"name_test_null_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
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
        expected = "<button name=\"name_test_nodisplay_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
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
        expected = "<button name=\"name_test_normal_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_normal_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<button name=\"name_test_normal_control\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
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
        expected = "<button name=\"name_test_disabled_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_disabled_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<button name=\"name_test_disabled_control\" disabled=\"disabled\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);

    }

    /**
     * タグで個別にdisplayMethod属性が指定された場合
     * @throws Exception
     */
    @Test
    public void testDisplayConrtolWithCustomizedInfo() throws Exception {

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
        String expected = "<button name=\"name_test_nodisplay_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
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
        expected = "<button name=\"name_test_normal_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_normal_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<button name=\"name_test_normal_control\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
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
        expected = "<button name=\"name_test_disabled_nocontrol\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
        TagTestUtil.assertTag(actual, expected, " ");
        TagTestUtil.clearOutput(pageContext);
        // 表示制御必要
        target.setName("name_test_disabled_control");
        target.setUri("/R00001");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<button name=\"name_test_disabled_control\" disabled=\"disabled\" onclick=\"return window.nablarch_submit(event, this);\"></button>";
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
            assertThat(e.getMessage(), is("invalid location of the button tag. the button tag must locate in the form tag."));
        }
    }
}
