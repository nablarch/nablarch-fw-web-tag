package nablarch.common.web.tag;

import nablarch.core.util.Builder;
import org.junit.Test;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * {@link FileTag}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class FileTagTest extends TagTestSupport<FileTag> {

    public FileTagTest() {
        super(new FileTag());
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
        target.setSize(8);

        // file
        target.setReadonly(true);
        target.setSize(8);
        target.setMaxlength(10);
        target.setOnselect("onselect_test");
        target.setOnchange("onchange_test");
        target.setAccept("accept_test");
        target.setErrorCss("errorCss_test");

        // HTML5
        target.setAutofocus(true);
        target.setMultiple(true);

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
                "type=\"file\"",               // type="file"
                "name=\"name_test\"",
                "value=\"\"",
                "size=\"8\"",
                "maxlength=\"10\"",
                "multiple=\"multiple\"",
                "disabled=\"disabled\"",
                "readonly=\"readonly\"",
                "accept=\"accept_test\"",      // accept
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onselect=\"onselect_test\"",
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
                "autofocus=\"autofocus\" />").replace(Builder.LS, " ");
        assertThat(actual, is(expected));

        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
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

        // file
        target.setReadonly(true);
        target.setSize(8);
        target.setMaxlength(10);
        target.setOnselect("onselect_test" + TagTestUtil.HTML);
        target.setOnchange("onchange_test" + TagTestUtil.HTML);
        target.setErrorCss("errorCss_test" + TagTestUtil.HTML);
        target.setAccept("accept_test" + TagTestUtil.HTML);

        // HTML5
        target.setAutofocus(true);
        target.setMultiple(true);

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
                "type=\"file\"",
                "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                "value=\"\"",
                "size=\"8\"",
                "maxlength=\"10\"",
                "multiple=\"multiple\"",
                "disabled=\"disabled\"",
                "readonly=\"readonly\"",
                "accept=\"accept_test" + TagTestUtil.ESC_HTML + "\"",
                "onclick=\"onclick_test" + TagTestUtil.ESC_HTML + "\"",
                "ondblclick=\"ondblclick_test" + TagTestUtil.ESC_HTML + "\"",
                "onselect=\"onselect_test" + TagTestUtil.ESC_HTML + "\"",
                "onchange=\"onchange_test" + TagTestUtil.ESC_HTML + "\"",   //accept
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

        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    @Test
    public void testInputPageForDefault() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"value_test"});

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"file\"",
                "name=\"name_test\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithoutValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"file\"",
                "name=\"name_test\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithDefaultConfig() throws Exception {

        TagTestUtil.setUpDefaultConfig();
        TagTestUtil.setErrorMessages(pageContext);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"value_test"});

        // generic
        target.setCssClass("cssClass_test");

        // input
        target.setName("entity.bbb");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "class=\"cssClass_test default_error\"",
                "type=\"file\"",
                "name=\"entity.bbb\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testInputPageForError() throws Exception {

        TagTestUtil.setErrorMessages(pageContext);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"value_test"});

        // generic
        target.setCssClass("cssClass_test");

        // input
        target.setName("entity.bbb");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"file\"",
                "name=\"entity.bbb\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testInputPageForErrorUsingAlias() throws Exception {

        TagTestUtil.setErrorMessages(pageContext);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"value_test"});

        // generic
        target.setCssClass("cssClass_test");

        // input
        target.setName("name_test");
        target.setNameAlias("entity.bbb");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"file\"",
                "name=\"name_test\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
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

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"file\"",
                "name=\"name_test\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
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

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"file\"",
                "name=\"name_test\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForDefault() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"value_test"});

        TagUtil.setConfirmationPage(pageContext);

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "value_test";
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForDefaultWithHtml() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"value_test" + TagTestUtil.HTML});

        TagUtil.setConfirmationPage(pageContext);

        // input
        target.setName("name_test" + TagTestUtil.HTML);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "value_test" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT;
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    @Test
    public void testConfirmationPageWithoutValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        TagUtil.setConfirmationPage(pageContext);

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageWithArrayNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", new String[]{null});

        TagUtil.setConfirmationPage(pageContext);

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
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

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
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
            assertThat(e.getMessage(), is("invalid location of the file tag. the file tag must locate in the form tag."));
        }
    }
}
