package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import nablarch.core.util.Builder;

import org.junit.Test;

import java.util.Collections;

/**
 * @author Kiyohito Itoh
 */
public class CheckboxTagTest extends TagTestSupport<CheckboxTag> {

    public CheckboxTagTest() {
        super(new CheckboxTag());
    }
    
    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"value_test"});
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // focus
        TagTestUtil.setFocusAttributes(target);
        
        // input
        target.setName("name_test");
        target.setDisabled(true);
        target.setOnchange("onchange_test");
        
        // checkbox
        target.setValue("value_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");
        target.setOffLabel("offLabel_test");
        target.setOffValue("offValue_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
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
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "checked=\"checked\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onchange=\"onchange_test\"",
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
                "autofocus=\"autofocus\" /><label for=\"id_test\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("offValue_test"));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"value_test" + TagTestUtil.HTML});
        
        // generic
        TagTestUtil.setGenericAttributesWithHtml(target);
        
        // focus
        TagTestUtil.setFocusAttributesWithHtml(target);
        
        // input
        target.setName("name_test" + TagTestUtil.HTML);
        target.setDisabled(true);
        target.setOnchange("onchange_test" + TagTestUtil.HTML);
        
        // checkbox
        target.setValue("value_test" + TagTestUtil.HTML);

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test" + TagTestUtil.HTML);
        target.setErrorCss("errorCss_test" + TagTestUtil.HTML);
        target.setOffLabel("offLabel_test" + TagTestUtil.HTML);
        target.setOffValue("offValue_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
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
                "type=\"checkbox\"",
                "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                "value=\"value_test" + TagTestUtil.ESC_HTML + "\"",
                "checked=\"checked\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test" + TagTestUtil.ESC_HTML + "\"",
                "ondblclick=\"ondblclick_test" + TagTestUtil.ESC_HTML + "\"",
                "onchange=\"onchange_test" + TagTestUtil.ESC_HTML + "\"",
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
                "autofocus=\"autofocus\" /><label for=\"id_test" + TagTestUtil.ESC_HTML + "\">label_test" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test" + TagTestUtil.HTML));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("offValue_test" + TagTestUtil.HTML));
    }
    
    @Test
    public void testInputPageForDefault() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"unknown"});
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setLabel("label_test");
        target.setOffLabel("offLabel_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"1\" /><label for=\"nablarch_checkbox1\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));
    }
    
    @Test
    public void testInputPageWithNotUseOffValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"unknown"});
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setLabel("label_test");
        target.setOffLabel("offLabel_test");
        target.setUseOffValue(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"1\" /><label for=\"nablarch_checkbox1\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(0));
    }
    
    @Test
    public void testInputPageForNoLabel() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"unknown"});
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setOffLabel("offLabel_test");
        
        // ラベルに空文字を設定した場合
        target.setLabel("");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"1\" />").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));

        // ラベルにnullを設定した場合
        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        target.setLabel(null);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"1\" />").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));
    }
    
    @Test
    public void testInputPageForChecked() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", Integer.valueOf(10));
        
        // input
        target.setName("name_test");
        
        // checkbox
        target.setValue("10");
        
        // nablarch
        target.setLabel("label_test");
        target.setOffValue("offValue_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"10\"",
                "checked=\"checked\" /><label for=\"nablarch_checkbox1\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("offValue_test"));
    }
    
    @Test
    public void testInputPageForMultiple() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"unknown"});
        
        // input
        target.setName("name_test");
        
        // checkbox
        target.setValue("value_test");
        
        // nablarch
        target.setLabel("label_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"value_test\" /><label for=\"nablarch_checkbox1\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
        
        TagTestUtil.clearOutput(pageContext, false);
        
        // 2nd
        target = new CheckboxTag();
        target.setPageContext(pageContext);

        // input
        target.setName("name_test2");
        
        // checkbox
        target.setValue("value_test2");
        
        // nablarch
        target.setLabel("label_test2");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox2\"",
                "type=\"checkbox\"",
                "name=\"name_test2\"",
                "value=\"value_test2\" /><label for=\"nablarch_checkbox2\">label_test2</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithDefaultConfig() throws Exception {
        
        TagTestUtil.setUpDefaultConfig();
        TagTestUtil.setErrorMessages(pageContext);
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"unknown"});
        
        // generic
        target.setCssClass("cssClass_test");
        
        // input
        target.setName("entity.bbb");
        
        // nablarch
        target.setLabel("label_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "class=\"cssClass_test default_error\"",
                "type=\"checkbox\"",
                "name=\"entity.bbb\"",
                "value=\"default_onValue_test\" /><label class=\"default_error\" for=\"nablarch_checkbox1\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }
    
    @Test
    public void testInputPageForError() throws Exception {
        
        TagTestUtil.setErrorMessages(pageContext);
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"unknown"});
        
        // generic
        target.setCssClass("cssClass_test");
        
        // input
        target.setName("entity.bbb");
        
        // checkbox
        target.setValue("value_test");
        
        // nablarch
        target.setLabel("label_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"checkbox\"",
                "name=\"entity.bbb\"",
                "value=\"value_test\" /><label class=\"nablarch_error\" for=\"nablarch_checkbox1\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }
    
    @Test
    public void testInputPageForErrorUsingAlias() throws Exception {
        
        TagTestUtil.setErrorMessages(pageContext);
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"unknown"});
        
        // generic
        target.setCssClass("cssClass_test");
        
        // input
        target.setName("name_test");
        target.setNameAlias("entity.bbb");
        
        // checkbox
        target.setValue("value_test");
        
        // nablarch
        target.setLabel("label_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"value_test\" /><label class=\"nablarch_error\" for=\"nablarch_checkbox1\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithArrayNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", new String[]{null});

        // input
        target.setName("name_test");

        // checkbox
        target.setValue("10");

        // nablarch
        target.setLabel("label_test");
        target.setOffValue("offValue_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"10\" /><label for=\"nablarch_checkbox1\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithListNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", Collections.singletonList(null));

        // input
        target.setName("name_test");

        // checkbox
        target.setValue("10");

        // nablarch
        target.setLabel("label_test");
        target.setOffValue("offValue_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"10\" /><label for=\"nablarch_checkbox1\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForChecked() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"1"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setLabel("label_test");
        target.setOffLabel("offLabel_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "label_test";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForCheckedWithNotUseOffValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"1"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setLabel("label_test");
        target.setUseOffValue(false);
        target.setOffLabel("offLabel_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "label_test";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testConfirmationPageForUnchecked() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"0"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setLabel("label_test");
        target.setOffLabel("offLabel_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "offLabel_test";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageNoOffLabel() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"0"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setLabel("label_test");
        target.setUseOffValue(true);
        
        // 空文字を設定した場合
        target.setOffLabel("");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        // nullを設定した場合
        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        target.setOffLabel(null);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForUncheckedWithNotUseOffValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"0"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setLabel("label_test");
        target.setUseOffValue(false);
        target.setOffLabel("offLabel_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "offLabel_test";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForCheckedWithHtml() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"value_test" + TagTestUtil.HTML});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test" + TagTestUtil.HTML);
        
        // checkbox
        target.setValue("value_test" + TagTestUtil.HTML);
        
        // nablarch
        target.setLabel("label_test" + TagTestUtil.HTML);
        target.setOffLabel("offLabel_test" + TagTestUtil.HTML);
        target.setOffValue("offValue_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "label_test" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT;
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    @Test
    public void testConfirmationPageForUncheckedWithHtml() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.ESC_HTML, new String[] {"offValue_test" + TagTestUtil.HTML});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test" + TagTestUtil.HTML);

        // checkbox
        target.setValue("value_test" + TagTestUtil.HTML);
        
        // nablarch
        target.setLabel("label_test" + TagTestUtil.HTML);
        target.setOffLabel("offLabel_test" + TagTestUtil.HTML);
        target.setOffValue("offValue_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "offLabel_test" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT;
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.ESC_HTML));
    }

    @Test
    public void testConfirmationPageWithArrayNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", new String[]{null});

        TagUtil.setConfirmationPage(pageContext);

        // input
        target.setName("name_test");

        // nablarch
        target.setLabel("label_test");
        target.setOffLabel("offLabel_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "offLabel_test";
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageWithListNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", Collections.singletonList(null));

        TagUtil.setConfirmationPage(pageContext);

        // input
        target.setName("name_test");

        // nablarch
        target.setLabel("label_test");
        target.setOffLabel("offLabel_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "offLabel_test";
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
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
            assertThat(e.getMessage(), is("invalid location of the checkbox tag. the checkbox tag must locate in the form tag."));
        }
    }
}
