package nablarch.common.web.tag;

import nablarch.core.ThreadContext;
import nablarch.core.util.Builder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.util.Collections;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 * @author Kiyohito Itoh
 */
public class TextTagTest extends TagTestSupport<TextTag> {

    public TextTagTest() {
        super(new TextTag());
    }

    @Before
    public void setupThreadContext() throws Exception {
        ThreadContext.setLanguage(Locale.JAPANESE);
    }
    
    @After
    public void clearThreadContext() throws Exception {
        ThreadContext.clear();
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
        
        // text
        target.setReadonly(true);
        target.setSize(8);
        target.setMaxlength(10);
        target.setOnselect("onselect_test");
        target.setOnchange("onchange_test");
        target.setErrorCss("errorCss_test");

        // HTML5
        target.setAutocomplete("off");
        target.setAutofocus(true);
        target.setPlaceholder("placeholder_test");

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
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "size=\"8\"",
                "maxlength=\"10\"",
                "disabled=\"disabled\"",
                "readonly=\"readonly\"",
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
                "autocomplete=\"off\"",
                "autofocus=\"autofocus\"",
                "placeholder=\"placeholder_test\" />").replace(Builder.LS, " ");
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
        
        // text
        target.setReadonly(true);
        target.setSize(8);
        target.setMaxlength(10);
        target.setOnselect("onselect_test" + TagTestUtil.HTML);
        target.setOnchange("onchange_test" + TagTestUtil.HTML);
        target.setErrorCss("errorCss_test" + TagTestUtil.HTML);

        // HTML5
        target.setAutofocus(true);
        target.setPlaceholder("placeholder_test" + TagTestUtil.HTML);

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
                "type=\"text\"",
                "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                "value=\"value_test" + TagTestUtil.ESC_HTML + "\"",
                "size=\"8\"",
                "maxlength=\"10\"",
                "disabled=\"disabled\"",
                "readonly=\"readonly\"",
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
                "placeholder=\"placeholder_test" + TagTestUtil.ESC_HTML + "\" />").replace(Builder.LS, " ");
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
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
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
                "type=\"text\"",
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

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", new String[] {null});

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
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

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
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
                "type=\"text\"",
                "name=\"entity.bbb\"",
                "value=\"value_test\"",
                "autocomplete=\"off\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));

        // autocompleteDisableTarget = "password"の場合

        TagTestUtil.clearOutput(pageContext);
        TagUtil.getCustomTagConfig().setAutocompleteDisableTarget("password");
        target = new TextTag();
        target.setPageContext(pageContext);

        // generic
        target.setCssClass("cssClass_test");
        
        // input
        target.setName("entity.bbb");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<input",
                "class=\"cssClass_test default_error\"",
                "type=\"text\"",
                "name=\"entity.bbb\"",
                "value=\"value_test\"",
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
                "type=\"text\"",
                "name=\"entity.bbb\"",
                "value=\"value_test\"",
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
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    /**
     * 入力画面でYYYYMMDDフォーマットが指定されるケース。
     * デフォルトのロケールを使用してフォーマットすること。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultWithYYYYMMDDFormat() throws Exception {

        ThreadContext.clear();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"201071"});

        target.setName("name_test");
        target.setValueFormat("yyyymmdd{yyyy/M/d}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"2010/7/1\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(2));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("yyyymmdd{yyyy/M/d|ja}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
    }

    /**
     * 入力画面でYYYYMMDDフォーマットが指定されるケース。
     * スレッドコンテキストに設定された言語を使用してフォーマットすること。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultWithYYYYMMDDFormatDependThreadContext() throws Exception {
        ThreadContext.setLanguage(Locale.ENGLISH);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"201071"});

        target.setName("name_test");
        target.setValueFormat("yyyymmdd{yyyy/M/d}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"2010/7/1\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(2));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("yyyymmdd{yyyy/M/d|en}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
    }

    /**
     * 入力画面でYYYYMMDDフォーマットが指定されるケース。
     * 一つ目以外のオプションは除外されること。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultWithYYYYMMDDFormatIgnoreOption() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"14 Nov 2012"});

        target.setName("name_test");
        target.setValueFormat("yyyymmdd{dd MMM yyyy|en|Brazil/East}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"14 Nov 2012\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(2));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("yyyymmdd{dd MMM yyyy|en}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
    }

    /**
     * 入力画面でYYYYMMフォーマットが指定されるケース。
     * デフォルトのロケールを使用してフォーマットすること。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultWithYYYYMMFormat() throws Exception {

        ThreadContext.clear();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"20107"});

        target.setName("name_test");
        target.setValueFormat("yyyymm{yyyy/M}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"2010/7\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(2));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("yyyymm{yyyy/M|ja}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
    }

    /**
     * 入力画面でYYYYMMフォーマットが指定されるケース。
     * スレッドコンテキストに設定された言語を使用してフォーマットすること。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultWithYYYYMMFormatDependThreadContext() throws Exception {

        ThreadContext.setLanguage(Locale.ENGLISH);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"20107"});

        target.setName("name_test");
        target.setValueFormat("yyyymm{yyyy/M}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"2010/7\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(2));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("yyyymm{yyyy/M|en}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
    }

    /**
     * 入力画面でYYYYMMフォーマットが指定されるケース。
     * 一つ目以外のオプションは除外されること。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultWithYYYYMMFormatIgnoreOption() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"Nov 2012"});

        target.setName("name_test");
        target.setValueFormat("yyyymm{MMM yyyy|en|Brazil/East}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"Nov 2012\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(2));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("yyyymm{MMM yyyy|en}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
    }

    /**
     * 入力画面でdateStringフォーマットが指定されるケース。
     * デフォルトのロケールを使用してフォーマットすること。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultWithDateStringFormat() throws Exception {

        ThreadContext.clear();

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"201071"});

        target.setName("name_test");
        target.setValueFormat("dateString{yyyy/M/d}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"2010/7/1\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(2));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("dateString{yyyy/M/d|ja}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
    }

    /**
     * 入力画面でdateStringフォーマットが指定されるケース。
     * スレッドコンテキストに設定された言語を使用してフォーマットすること。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultWithDateStringFormatDependThreadContext() throws Exception {

        ThreadContext.setLanguage(Locale.ENGLISH);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"201071"});

        target.setName("name_test");
        target.setValueFormat("dateString{yyyy/M/d}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"2010/7/1\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(2));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("dateString{yyyy/M/d|en}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
    }

    /**
     * 入力画面でdateStringフォーマットが指定されるケース。
     * 一つ目以外のオプションは除外されること。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultWithDateStringFormatIgnoreOption() throws Exception {
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"14 Nov 2012"});

        target.setName("name_test");
        target.setValueFormat("dateString{dd MMM yyyy|en|Brazil/East}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"14 Nov 2012\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat(formContext.getHiddenTagInfoList().size(), is(2));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("dateString{dd MMM yyyy|en}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("name_test_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
    }

    /**
     * 入力が画面ですでにフォーマット情報のパラメータが存在するケース。
     *
     * @throws Exception
     */
    @Test
    public void testInputPageForDefaultExistsFormatSpec() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"20107"});
        pageContext.getMockReq().getParams().put("name_test_nablarch_formatSpec", new String[]{"yyyymm{yyyy/M|ja}"});

        target.setName("name_test");
        target.setValueFormat("yyyymm{yyyy/M}");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"text\"",
                "name=\"name_test\"",
                "value=\"2010/7\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(formContext.getInputNames(), hasItem("name_test"));
        assertThat("パラメータにフォーマット情報があるのでhiddenには追加されない。", formContext.getHiddenTagInfoList().size(), is(0));
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
    public void testConfirmationPageForDefaultWithFormat() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"201071"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setValueFormat("yyyymmdd{yyyy/M/d}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "2010/7/1";
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

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", new String[]{null});

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

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

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
    public void testConfirmationPageForDefaultWithNullValueFormat() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {null});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        target.setValueFormat("yyyymmdd{yyyy/M/d}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        TagTestUtil.clearOutput(pageContext);
        
        // decimal
        target.setValueFormat("decimal{####}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        assertFalse(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testConfirmationPageForDefaultWithBlankValueFormat() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {""});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setValueFormat("yyyymmdd{yyyy/M/d}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        TagTestUtil.clearOutput(pageContext);
        
        // decimal
        target.setValueFormat("decimal{####}");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
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
            assertThat(e.getMessage(), is("invalid location of the text tag. the text tag must locate in the form tag."));
        }
    }
    
}
