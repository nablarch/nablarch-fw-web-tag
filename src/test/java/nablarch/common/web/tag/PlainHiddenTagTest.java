package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import nablarch.core.util.Builder;

import org.junit.Test;

/**
 * {@link PlainHiddenTag}のテスト。
 * @author Kiyohito Itoh
 */
public class PlainHiddenTagTest extends TagTestSupport<PlainHiddenTag> {

    public PlainHiddenTagTest() {
        super(new PlainHiddenTag());
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

        // HTML5
        target.setAutofocus(true);

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
                "type=\"hidden\"",
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
                "autofocus=\"autofocus\" />").replace(Builder.LS, " ");
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

        // HTML5
        target.setAutofocus(true);

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
                "type=\"hidden\"",
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
                "type=\"hidden\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "/>").replace(Builder.LS, " ");
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

        pageContext.getMockReq().getParams().put("🙊🙊🙊_test", new String[] {"🙈🙈🙈_test"});

        // input
        target.setName("🙊🙊🙊_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"hidden\"",
                "name=\"🙊🙊🙊_test\"",
                "value=\"🙈🙈🙈_test\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("🙊🙊🙊_test"));
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
                "type=\"hidden\"",
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

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", new String[] {null});

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"hidden\"",
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
                "type=\"hidden\"",
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
        String expected = Builder.lines(
                "<input",
                "type=\"hidden\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
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
        String expected = Builder.lines(
                "<input",
                "type=\"hidden\"",
                "name=\"name_test\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageWithArrayNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", new String[] {null});

        TagUtil.setConfirmationPage(pageContext);

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"hidden\"",
                "name=\"name_test\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
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
        String expected = Builder.lines(
                "<input",
                "type=\"hidden\"",
                "name=\"name_test\"",
                "value=\"\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
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
        String expected = Builder.lines(
                "<input",
                "type=\"hidden\"",
                "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                "value=\"value_test" + TagTestUtil.ESC_HTML + "\" />").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    /**
     * 多値でも使用できること。
     * 配列またはCollection限定。
     */
    @Test
    public void testInputPageForMultiValues() throws Exception {

        FormContext formContext;

        /**********************************************************
        配列の場合
        **********************************************************/

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"data1", "data2", "data3"});

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        for (int i = 0; i < 3; i++) {
            String expected = Builder.lines(
                    "<input",
                    "type=\"hidden\"",
                    "name=\"name_test\"",
                    "value=\"data" + (i + 1) + "\"",
                    "/>").replace(Builder.LS, " ");
            TagTestUtil.assertTag(splitActual[i], expected, " ");
        }
        assertTrue(formContext.getInputNames().contains("name_test"));

        /**********************************************************
        リストの場合
        **********************************************************/

        TagTestUtil.clearOutput(pageContext);

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.setAttribute("name_test", Arrays.asList(new String[] {"val1", "val2", "val3"}));

        // input
        target.setName("name_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        for (int i = 0; i < 3; i++) {
            String expected = Builder.lines(
                    "<input",
                    "type=\"hidden\"",
                    "name=\"name_test\"",
                    "value=\"val" + (i + 1) + "\"",
                    "/>").replace(Builder.LS, " ");
            TagTestUtil.assertTag(splitActual[i], expected, " ");
        }

        assertTrue(formContext.getInputNames().contains("name_test"));
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
            assertThat(e.getMessage(), is("invalid location of the plainHidden tag. the plainHidden tag must locate in the form tag."));
        }
    }
}

