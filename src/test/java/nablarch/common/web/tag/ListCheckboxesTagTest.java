package nablarch.common.web.tag;

import nablarch.core.util.Builder;
import org.junit.Test;

import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.Tag;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 * @author Kiyohito Itoh
 */
public class ListCheckboxesTagTest extends TagTestSupport<ListCheckboxesTag> {

    public ListCheckboxesTagTest() {
        super(new ListCheckboxesTag());
    }
    
    @Test
    public void testInvalidListFormat() throws Exception {
        try {
            target.setListFormat(null);
            fail("must be thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            String expected = "listFormat was invalid. listFormat must specify the following values. values = [br, div, span, ul, ol, sp] listFormat = [null]";
            assertThat(e.getMessage(), is(expected));
        }
        try {
            target.setListFormat("DIV");
            fail("must be thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            String expected = "listFormat was invalid. listFormat must specify the following values. values = [br, div, span, ul, ol, sp] listFormat = [DIV]";
            assertThat(e.getMessage(), is(expected));
        }
    }
    
    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"G001", "G003"});
        
        TagTestUtil.setListWithStringId(pageContext);
        
        // generic
        TagTestUtil.setGenericAttributesForInputs(target);
        target.setTabindex(4);
        
        // focus
        TagTestUtil.setFocusAttributesForInputs(target);
        
        // input
        target.setName("name_test");
        target.setDisabled(true);
        target.setOnchange("onchange_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        target.setElementLabelPattern("$VALUE$ - $LABEL$");
        target.setListFormat("div");
        target.setErrorCss("errorCss_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<div><input",
                "id=\"nablarch_checkbox%s\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "tabindex=\"4\"", // 出力されるすべてのinputタグに同一のtabindexが出力されることを確認する
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s",
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
                "onblur=\"onblur_test\" %s/><label for=\"nablarch_checkbox%s\">%s</label></div>").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, "autofocus=\"autofocus\" ", i + "", "G000&nbsp;-&nbsp;グループ0"),
                String.format(temp, ++i + "", "G001", checked, "", i + "", "G001&nbsp;-&nbsp;グループ1"),
                String.format(temp, ++i + "", "G002", unchecked, "", i + "", "G002&nbsp;-&nbsp;グループ2"),
                String.format(temp, ++i + "", "G003", checked, "", i + "", "G003&nbsp;-&nbsp;グループ3"),
                String.format(temp, ++i + "", "G004", unchecked, "", i + "", "G004&nbsp;-&nbsp;グループ4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"G001" + TagTestUtil.HTML, "G003" + TagTestUtil.HTML});
        
        TagTestUtil.setListWithHtml(pageContext);
        
        // generic
        TagTestUtil.setGenericAttributesWithHtmlForInputs(target);
        target.setTabindex(4);
        
        // focus
        TagTestUtil.setFocusAttributesWithHtmlForInputs(target);
        
        // input
        target.setName("name_test" + TagTestUtil.HTML);
        target.setDisabled(true);
        target.setOnchange("onchange_test" + TagTestUtil.HTML);

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        target.setElementLabelPattern("$VALUE$ - $LABEL$");
        target.setListFormat("div");
        target.setErrorCss("errorCss_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<div><input",
                "id=\"nablarch_checkbox%s\"",
                "class=\"css_test" + TagTestUtil.ESC_HTML + "\"",
                "style=\"style_test" + TagTestUtil.ESC_HTML + "\"",
                "title=\"title_test" + TagTestUtil.ESC_HTML + "\"",
                "lang=\"lang_test" + TagTestUtil.ESC_HTML + "\"",
                "xml:lang=\"xmlLang_test" + TagTestUtil.ESC_HTML + "\"",
                "dir=\"dir_test" + TagTestUtil.ESC_HTML + "\"",
                "tabindex=\"4\"", // 出力されるすべてのinputタグに同一のtabindexが出力されることを確認する
                "type=\"checkbox\"",
                "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                "value=\"%s\"",
                "%s",
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
                "onblur=\"onblur_test" + TagTestUtil.ESC_HTML + "\" %s/><label for=\"nablarch_checkbox%s\">%s</label></div>").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000" + TagTestUtil.ESC_HTML, unchecked, "autofocus=\"autofocus\" ", i + "", "G000" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;-&nbsp;グループ0" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT),
                String.format(temp, ++i + "", "G001" + TagTestUtil.ESC_HTML, checked, "", i + "", "G001" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;-&nbsp;グループ1" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT),
                String.format(temp, ++i + "", "G002" + TagTestUtil.ESC_HTML, unchecked, "", i + "", "G002" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;-&nbsp;グループ2" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT),
                String.format(temp, ++i + "", "G003" + TagTestUtil.ESC_HTML, checked, "", i + "", "G003" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;-&nbsp;グループ3" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT),
                String.format(temp, ++i + "", "G004" + TagTestUtil.ESC_HTML, unchecked, "", i + "", "G004" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;-&nbsp;グループ4" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    @Test
    public void testInputPageForDefault() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"G001", "G003"});
        
        TagTestUtil.setListWithStringId(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox%s\">%s</label><br />").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"),
                String.format(temp, ++i + "", "G001", checked, i + "", "グループ1"),
                String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"),
                String.format(temp, ++i + "", "G003", checked, i + "", "グループ3"),
                String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    /**
     * サロゲートペアを扱うテストケース。
     * @throws Exception
     */
    @Test
    public void testInputPageForSurrogatepair() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("🙊🙈🙉", new String[] {"G001", "G003"});

        TagTestUtil.setListWithSurrogatepairStringId(pageContext);

        // input
        target.setName("🙊🙈🙉");

        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "type=\"checkbox\"",
                "name=\"🙊🙈🙉\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox%s\">%s</label><br />").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, i + "", "🙊🙊🙊0"),
                String.format(temp, ++i + "", "G001", checked, i + "", "🙈🙈🙈1"),
                String.format(temp, ++i + "", "G002", unchecked, i + "", "🙉🙉🙉2"),
                String.format(temp, ++i + "", "G003", checked, i + "", "🙊🙈🙉3"),
                String.format(temp, ++i + "", "G004", unchecked, i + "", "😸😸😸4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("🙊🙈🙉"));
    }

    @Test
    public void testInputPageForCheckedWithParamValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"1", "3"});
        
        TagTestUtil.setListWithIntegerId(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox%s\">%s</label><br />").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "0", unchecked, i + "", "グループ0"),
                String.format(temp, ++i + "", "1", checked, i + "", "グループ1"),
                String.format(temp, ++i + "", "2", unchecked, i + "", "グループ2"),
                String.format(temp, ++i + "", "3", checked, i + "", "グループ3"),
                String.format(temp, ++i + "", "4", unchecked, i + "", "グループ4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testInputPageForCheckedWithScopeValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", new Integer[] {1, 3});
        
        TagTestUtil.setListWithIntegerId(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox%s\">%s</label><br />").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "0", unchecked, i + "", "グループ0"),
                String.format(temp, ++i + "", "1", checked, i + "", "グループ1"),
                String.format(temp, ++i + "", "2", unchecked, i + "", "グループ2"),
                String.format(temp, ++i + "", "3", checked, i + "", "グループ3"),
                String.format(temp, ++i + "", "4", unchecked, i + "", "グループ4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testInputPageWithoutValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagTestUtil.setListWithStringId(pageContext);
        
        // input
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox%s\">%s</label><br />").replace(Builder.LS, " ");
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"),
                String.format(temp, ++i + "", "G001", unchecked, i + "", "グループ1"),
                String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"),
                String.format(temp, ++i + "", "G003", unchecked, i + "", "グループ3"),
                String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithDefaultConfig() throws Exception {

        TagTestUtil.setUpDefaultConfig();
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"G001", "G003"});
        
        TagTestUtil.setListWithStringId(pageContext);
        TagTestUtil.setErrorMessages(pageContext);
        
        // generic
        target.setCssClass("cssClass_test");
        
        // input
        target.setName("entity.bbb");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        
        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "class=\"cssClass_test default_error\"",
                "type=\"checkbox\"",
                "name=\"entity.bbb\"",
                "value=\"%s\"",
                "%s /><label class=\"default_error\" for=\"nablarch_checkbox%s\">%s</label>&nbsp;").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, i + "", "default_グループ0"),
                String.format(temp, ++i + "", "G001", checked, i + "", "default_グループ1"),
                String.format(temp, ++i + "", "G002", unchecked, i + "", "default_グループ2"),
                String.format(temp, ++i + "", "G003", checked, i + "", "default_グループ3"),
                String.format(temp, ++i + "", "G004", unchecked, i + "", "default_グループ4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testInputPageArrayWithNull() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {null});

        TagTestUtil.setListWithStringId(pageContext);

        // input
        target.setName("name_test");

        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox%s\">%s</label><br />").replace(Builder.LS, " ");
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"),
                String.format(temp, ++i + "", "G001", unchecked, i + "", "グループ1"),
                String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"),
                String.format(temp, ++i + "", "G003", unchecked, i + "", "グループ3"),
                String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageListWithNull() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

        TagTestUtil.setListWithStringId(pageContext);

        // input
        target.setName("name_test");

        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox%s\">%s</label><br />").replace(Builder.LS, " ");
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"),
                String.format(temp, ++i + "", "G001", unchecked, i + "", "グループ1"),
                String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"),
                String.format(temp, ++i + "", "G003", unchecked, i + "", "グループ3"),
                String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testInputPageForError() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"G001", "G003"});
        
        TagTestUtil.setListWithStringId(pageContext);
        TagTestUtil.setErrorMessages(pageContext);
        
        // generic
        target.setCssClass("cssClass_test");
        
        // input
        target.setName("entity.bbb");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        // default
        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"checkbox\"",
                "name=\"entity.bbb\"",
                "value=\"%s\"",
                "%s /><label class=\"nablarch_error\" for=\"nablarch_checkbox%s\">%s</label>").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"), "<br />",
                String.format(temp, ++i + "", "G001", checked, i + "", "グループ1"), "<br />",
                String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"), "<br />",
                String.format(temp, ++i + "", "G003", checked, i + "", "グループ3"), "<br />",
                String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"), "<br />"
                )
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));
        
        TagTestUtil.clearOutput(pageContext);
        
        // br
        target.setListFormat("br");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        TagTestUtil.assertTag(actual, expected, " ");
        
        TagTestUtil.clearOutput(pageContext);
        
        // div
        target.setListFormat("div");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        i = 0;
        expected = Builder.lines(
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"), "</div>",
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "G001", checked, i + "", "グループ1"), "</div>",
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"), "</div>",
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "G003", checked, i + "", "グループ3"), "</div>",
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"), "</div>"
                )
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        TagTestUtil.clearOutput(pageContext);
        
        // span
        target.setListFormat("span");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        i = 0;
        expected = Builder.lines(
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"), "</span>",
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "G001", checked, i + "", "グループ1"), "</span>",
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"), "</span>",
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "G003", checked, i + "", "グループ3"), "</span>",
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"), "</span>"
                )
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        TagTestUtil.clearOutput(pageContext);
        
        // ul
        target.setListFormat("ul");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        i = 0;
        expected = Builder.lines(
                "<ul class=\"nablarch_error\">",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G001", checked, i + "", "グループ1"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G003", checked, i + "", "グループ3"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"), "</li>",
                "</ul>"
                )
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        TagTestUtil.clearOutput(pageContext);
        
        // ol
        target.setListFormat("ol");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        i = 0;
        expected = Builder.lines(
                "<ol class=\"nablarch_error\">",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G001", checked, i + "", "グループ1"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G003", checked, i + "", "グループ3"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"), "</li>",
                "</ol>"
                )
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        TagTestUtil.clearOutput(pageContext);
        
        // space
        target.setListFormat("sp");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        i = 0;
        expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"), "&nbsp;",
                String.format(temp, ++i + "", "G001", checked, i + "", "グループ1"), "&nbsp;",
                String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"), "&nbsp;",
                String.format(temp, ++i + "", "G003", checked, i + "", "グループ3"), "&nbsp;",
                String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"), "&nbsp;"
                )
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        TagTestUtil.clearOutput(pageContext);
    }

    @Test
    public void testInputPageForErrorUsingAlias() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"G001", "G003"});
        
        TagTestUtil.setListWithStringId(pageContext);
        TagTestUtil.setErrorMessages(pageContext);
        
        // generic
        target.setCssClass("cssClass_test");
        
        // input
        target.setName("name_test");
        target.setNameAlias("entity.bbb");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox%s\"",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label class=\"nablarch_error\" for=\"nablarch_checkbox%s\">%s</label><br />").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "G000", unchecked, i + "", "グループ0"),
                String.format(temp, ++i + "", "G001", checked, i + "", "グループ1"),
                String.format(temp, ++i + "", "G002", unchecked, i + "", "グループ2"),
                String.format(temp, ++i + "", "G003", checked, i + "", "グループ3"),
                String.format(temp, ++i + "", "G004", unchecked, i + "", "グループ4"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    /**
     * ラベルがBigDecimal型の場合に指数表記にならないこと。
     * @throws Exception
     */
    @Test
    public void testInputPageBigDecimalLabel() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"1"});
        pageContext.setAttribute("groups", new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("groupId", 1);
                put("name", new BigDecimal("0.0000000001"));
            }});
        }});

        target.setName("name_test");
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"1\"",
                "checked=\"checked\" /><label for=\"nablarch_checkbox1\">0.0000000001</label><br />").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        assertThat(formContext.getInputNames(), hasItem("name_test"));
    }

    /**
     * 値がBigDecimal型の場合に指数表記にならないこと。
     * @throws Exception
     */
    @Test
    public void testInputPageBigDecimalValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"0.0000000001"});
        pageContext.setAttribute("groups", new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("groupId", new BigDecimal("0.0000000001"));
                put("name", "testName");
            }});
        }});

        target.setName("name_test");
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"0.0000000001\"",
                "checked=\"checked\" /><label for=\"nablarch_checkbox1\">testName</label><br />").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        assertThat(formContext.getInputNames(), hasItem("name_test"));
    }
    
    @Test
    public void testConfirmationPageForDefault() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"G001", "G003"});
        
        TagTestUtil.setListWithStringId(pageContext);
        
        TagUtil.setConfirmationPage(pageContext);
        
        // select
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");

        // br(default)
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "グループ1<br />",
                "グループ3<br />").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        TagTestUtil.clearOutput(pageContext);
        
        // div
        target.setListFormat("div");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<div>グループ1</div>",
                "<div>グループ3</div>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        TagTestUtil.clearOutput(pageContext);
        
        // span
        target.setListFormat("span");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<span>グループ1</span>",
                "<span>グループ3</span>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        TagTestUtil.clearOutput(pageContext);
        
        // ul
        target.setListFormat("ul");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<ul>",
                "<li>グループ1</li>",
                "<li>グループ3</li>",
                "</ul>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        TagTestUtil.clearOutput(pageContext);
        
        // ul
        target.setListFormat("ol");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<ol>",
                "<li>グループ1</li>",
                "<li>グループ3</li>",
                "</ol>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        TagTestUtil.clearOutput(pageContext);
        
        // sp
        target.setListFormat("sp");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "グループ1&nbsp;",
                "グループ3&nbsp;").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForDefaultWithHtml() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"G001" + TagTestUtil.HTML, "G003" + TagTestUtil.HTML});
        
        TagTestUtil.setListWithHtml(pageContext);
        
        TagUtil.setConfirmationPage(pageContext);
        
        // select
        target.setName("name_test" + TagTestUtil.HTML);
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");

        // br(default)
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "グループ1" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "<br />",
                "グループ3" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "<br />").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
        
        TagTestUtil.clearOutput(pageContext);
        
        // div
        target.setListFormat("div");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<div>グループ1" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</div>",
                "<div>グループ3" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</div>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
        
        TagTestUtil.clearOutput(pageContext);
        
        // span
        target.setListFormat("span");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<span>グループ1" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</span>",
                "<span>グループ3" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</span>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
        
        TagTestUtil.clearOutput(pageContext);
        
        // ul
        target.setListFormat("ul");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<ul>",
                "<li>グループ1" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>",
                "<li>グループ3" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>",
                "</ul>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
        
        TagTestUtil.clearOutput(pageContext);
        
        // ul
        target.setListFormat("ol");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<ol>",
                "<li>グループ1" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>",
                "<li>グループ3" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>",
                "</ol>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
        
        TagTestUtil.clearOutput(pageContext);
        
        // sp
        target.setListFormat("sp");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "グループ1" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;",
                "グループ3" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }
    
    @Test
    public void testConfirmationPageWithoutValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagTestUtil.setListWithStringId(pageContext);
        
        TagUtil.setConfirmationPage(pageContext);
        
        // select
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        target.setListFormat("ul");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageWithArrayNull() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {null});

        TagTestUtil.setListWithStringId(pageContext);

        TagUtil.setConfirmationPage(pageContext);

        // select
        target.setName("name_test");

        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        target.setListFormat("ul");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageWithListNull() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

        TagTestUtil.setListWithStringId(pageContext);

        TagUtil.setConfirmationPage(pageContext);

        // select
        target.setName("name_test");

        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        target.setListFormat("ul");

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
            assertThat(e.getMessage(), is("invalid location of the checkboxes tag. the checkboxes tag must locate in the form tag."));
        }
    }

}
