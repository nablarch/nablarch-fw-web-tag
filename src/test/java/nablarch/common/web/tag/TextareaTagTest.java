package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.servlet.jsp.tagext.Tag;

import nablarch.core.util.Builder;

import org.junit.Test;

import java.util.Collections;

/**
 * @author Kiyohito Itoh
 */
public class TextareaTagTest extends TagTestSupport<TextareaTag> {

    public TextareaTagTest() {
        super(new TextareaTag());
    }

    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"aaaaa\nbbbbb\nccccc"});
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // focus
        TagTestUtil.setFocusAttributes(target);
        
        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);
        target.setDisabled(true);
        target.setReadonly(true);
        target.setOnselect("onselect_test");
        target.setOnchange("onchange_test");

        // HTML5
        target.setAutofocus(true);
        target.setPlaceholder("placeholder_test");
        target.setMaxlength(200);

        // nablarch
        target.setErrorCss("errorCss_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
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
                "maxlength=\"200\"",
                "disabled=\"disabled\"",
                "readonly=\"readonly\"",
                "rows=\"5\"",
                "cols=\"40\"",
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
                "autofocus=\"autofocus\"",
                "placeholder=\"placeholder_test\">").replace(Builder.LS, " ")
                 + "\n$value$</textarea>"
                .replace("$value$", "aaaaa\nbbbbb\nccccc");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"aaaaa\nbbbbb\nccccc" + TagTestUtil.HTML});
        
        // generic
        TagTestUtil.setGenericAttributesWithHtml(target);
        
        // focus
        TagTestUtil.setFocusAttributesWithHtml(target);
        
        // textarea
        target.setName("name_test" + TagTestUtil.HTML);
        target.setRows(5);
        target.setCols(40);
        target.setDisabled(true);
        target.setReadonly(true);
        target.setOnselect("onselect_test" + TagTestUtil.HTML);
        target.setOnchange("onchange_test" + TagTestUtil.HTML);

        // HTML5
        target.setAutofocus(true);
        target.setPlaceholder("placeholder_test" + TagTestUtil.HTML);
        target.setMaxlength(200);

        // nablarch
        target.setErrorCss("errorCss_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
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
                "maxlength=\"200\"",
                "disabled=\"disabled\"",
                "readonly=\"readonly\"",
                "rows=\"5\"",
                "cols=\"40\"",
                "onclick=\"onclick_test" + TagTestUtil.ESC_HTML + "\"",
                "ondblclick=\"ondblclick_test" + TagTestUtil.ESC_HTML + "\"",
                "onselect=\"onselect_test" + TagTestUtil.ESC_HTML + "\"",
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
                "autofocus=\"autofocus\"",
                "placeholder=\"placeholder_test" + TagTestUtil.ESC_HTML + "\">").replace(Builder.LS, " ")
                + "\n$value$</textarea>"
                .replace("$value$", "aaaaa\nbbbbb\nccccc" + TagTestUtil.ESC_HTML);
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    @Test
    public void testInputPageForDefault() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"aaaaa\nbbbbb\nccccc"});
        
        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
                "name=\"name_test\"",
                "rows=\"5\"",
                "cols=\"40\">").replace(Builder.LS, " ")
                + "\n$value$</textarea>"
                .replace("$value$", "aaaaa\nbbbbb\nccccc");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
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

        pageContext.getMockReq().getParams().put("name_test", new String[] {"🙈🙈🙈\n🙉🙉🙉\n🙊🙊🙊"});

        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
                "name=\"name_test\"",
                "rows=\"5\"",
                "cols=\"40\">").replace(Builder.LS, " ")
                + "\n$value$</textarea>"
                .replace("$value$", "🙈🙈🙈\n🙉🙉🙉\n🙊🙊🙊");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithoutValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
                "name=\"name_test\"",
                "rows=\"5\"",
                "cols=\"40\">").replace(Builder.LS, " ")
                + "\n</textarea>";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithArrayNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(pageContext.REQUEST_SCOPE)
                .put("name_test", new String[] {null});

        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
                "name=\"name_test\"",
                "rows=\"5\"",
                "cols=\"40\">").replace(Builder.LS, " ")
                + "\n</textarea>";
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithListNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(pageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
                "name=\"name_test\"",
                "rows=\"5\"",
                "cols=\"40\">").replace(Builder.LS, " ")
                + "\n</textarea>";
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithDefaultConfig() throws Exception {
        
        TagTestUtil.setUpDefaultConfigWithLS();
        TagTestUtil.setErrorMessages(pageContext);
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"aaaaa\nbbbbb\nccccc"});
        
        // generic
        target.setCssClass("cssClass_test");
        
        // textarea
        target.setName("entity.bbb");
        target.setRows(5);
        target.setCols(40);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
                "class=\"cssClass_test default_error\"",
                "name=\"entity.bbb\"",
                "rows=\"5\"",
                "cols=\"40\">").replace(Builder.LS, " ")
                + "\r$value$</textarea>"
                .replace("$value$", "aaaaa\nbbbbb\nccccc");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }
    
    @Test
    public void testInputPageForError() throws Exception {
        
        TagTestUtil.setErrorMessages(pageContext);
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"aaaaa\nbbbbb\nccccc"});
        
        // generic
        target.setCssClass("cssClass_test");
        
        // textarea
        target.setName("entity.bbb");
        target.setRows(5);
        target.setCols(40);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
                "class=\"cssClass_test nablarch_error\"",
                "name=\"entity.bbb\"",
                "rows=\"5\"",
                "cols=\"40\">").replace(Builder.LS, " ")
                + "\n$value$</textarea>"
                .replace("$value$", "aaaaa\nbbbbb\nccccc");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testInputPageForErrorUsingAlias() throws Exception {
        
        TagTestUtil.setErrorMessages(pageContext);
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"aaaaa\nbbbbb\nccccc"});
        
        // generic
        target.setCssClass("cssClass_test");
        
        // textarea
        target.setName("name_test");
        target.setNameAlias("entity.bbb");
        target.setRows(5);
        target.setCols(40);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<textarea",
                "class=\"cssClass_test nablarch_error\"",
                "name=\"name_test\"",
                "rows=\"5\"",
                "cols=\"40\">").replace(Builder.LS, " ")
                + "\n$value$</textarea>"
                .replace("$value$", "aaaaa\nbbbbb\nccccc");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testConfirmationPageForDefault() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"aaaaa\nbbbbb\nccccc"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "aaaaa<br />bbbbb<br />ccccc";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForDefaultWithHtml() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"aaaaa\nbbbbb\nccccc" + TagTestUtil.HTML});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // textarea
        target.setName("name_test" + TagTestUtil.HTML);
        target.setRows(5);
        target.setCols(40);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "aaaaa<br />bbbbb<br />ccccc" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT;
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }
    
    @Test
    public void testConfirmationPageWithoutValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagUtil.setConfirmationPage(pageContext);
        
        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);
        
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

        pageContext.getAttributes(pageContext.REQUEST_SCOPE)
                .put("name_test", new String[] {null});

        TagUtil.setConfirmationPage(pageContext);

        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);

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

        pageContext.getAttributes(pageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

        TagUtil.setConfirmationPage(pageContext);

        // textarea
        target.setName("name_test");
        target.setRows(5);
        target.setCols(40);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    /**
     * 本タグがFormタグ内に定義されていない場合（FormContextが設定されていない場合）に、

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
            assertThat(e.getMessage(), is("invalid location of the textarea tag. the textarea tag must locate in the form tag."));
        }
    }
    
}
