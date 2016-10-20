package nablarch.common.web.tag;

import nablarch.core.util.Builder;
import org.junit.Test;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class ListSelectTagTest extends TagTestSupport<ListSelectTag> {

    public ListSelectTagTest() {
        super(new ListSelectTag());
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
        TagTestUtil.setGenericAttributes(target);
        
        // select
        target.setName("name_test");
        target.setSize(10);
        target.setMultiple(true);
        target.setDisabled(true);
        target.setTabindex(3);
        target.setOnfocus("onfocus_test");
        target.setOnblur("onblur_test");
        target.setOnchange("onchange_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        target.setElementLabelPattern("$VALUE$ - $LABEL$");
        target.setListFormat("div");
        target.setWithNoneOption(true);
        target.setNoneOptionLabel("選択なし");
        target.setErrorCss("errorCss_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String startTag = Builder.lines(
                "<select",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "tabindex=\"3\"",
                "name=\"name_test\"",
                "size=\"10\"",
                "multiple=\"multiple\"",
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
                "autofocus=\"autofocus\">").replace(Builder.LS, " ");
        String excludeStartTag = Builder.lines(
                "<option value=\"\">選択なし</option>",
                "<option value=\"G000\">G000 - グループ0</option>",
                "<option value=\"G001\" selected=\"selected\">G001 - グループ1</option>",
                "<option value=\"G002\">G002 - グループ2</option>",
                "<option value=\"G003\" selected=\"selected\">G003 - グループ3</option>",
                "<option value=\"G004\">G004 - グループ4</option></select>");
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"G001" + TagTestUtil.HTML, "G003" + TagTestUtil.HTML});
        
        TagTestUtil.setListWithHtml(pageContext);
        
        // generic
        TagTestUtil.setGenericAttributesWithHtml(target);
        
        // select
        target.setName("name_test" + TagTestUtil.HTML);
        target.setSize(10);
        target.setMultiple(true);
        target.setDisabled(true);
        target.setTabindex(3);
        target.setOnfocus("onfocus_test" + TagTestUtil.HTML);
        target.setOnblur("onblur_test" + TagTestUtil.HTML);
        target.setOnchange("onchange_test" + TagTestUtil.HTML);

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        target.setElementLabelPattern("$VALUE$ - $LABEL$");
        target.setListFormat("div");
        target.setWithNoneOption(true);
        target.setNoneOptionLabel("選択なし" + TagTestUtil.HTML);
        target.setErrorCss("errorCss_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String startTag = Builder.lines(
                "<select",
                "id=\"id_test" + TagTestUtil.ESC_HTML + "\"",
                "class=\"css_test" + TagTestUtil.ESC_HTML + "\"",
                "style=\"style_test" + TagTestUtil.ESC_HTML + "\"",
                "title=\"title_test" + TagTestUtil.ESC_HTML + "\"",
                "lang=\"lang_test" + TagTestUtil.ESC_HTML + "\"",
                "xml:lang=\"xmlLang_test" + TagTestUtil.ESC_HTML + "\"",
                "dir=\"dir_test" + TagTestUtil.ESC_HTML + "\"",
                "tabindex=\"3\"",
                "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                "size=\"10\"",
                "multiple=\"multiple\"",
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
                "autofocus=\"autofocus\">").replace(Builder.LS, " ");
        String excludeStartTag = Builder.lines(
                "<option value=\"\">選択なし" + TagTestUtil.ESC_HTML + "</option>",
                "<option value=\"G000" + TagTestUtil.ESC_HTML + "\">G000" + TagTestUtil.ESC_HTML + " - グループ0" + TagTestUtil.ESC_HTML + "</option>",
                "<option value=\"G001" + TagTestUtil.ESC_HTML + "\" selected=\"selected\">G001" + TagTestUtil.ESC_HTML + " - グループ1" + TagTestUtil.ESC_HTML + "</option>",
                "<option value=\"G002" + TagTestUtil.ESC_HTML + "\">G002" + TagTestUtil.ESC_HTML + " - グループ2" + TagTestUtil.ESC_HTML + "</option>",
                "<option value=\"G003" + TagTestUtil.ESC_HTML + "\" selected=\"selected\">G003" + TagTestUtil.ESC_HTML + " - グループ3" + TagTestUtil.ESC_HTML + "</option>",
                "<option value=\"G004" + TagTestUtil.ESC_HTML + "\">G004" + TagTestUtil.ESC_HTML + " - グループ4" + TagTestUtil.ESC_HTML + "</option></select>");
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    @Test
    public void testInputPageForCheckedWithParamValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"3"});
        
        TagTestUtil.setListWithIntegerId(pageContext);
        
        // select
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String startTag = "<select name=\"name_test\">";
        String excludeStartTag = Builder.lines(
                "<option value=\"0\">グループ0</option>",
                "<option value=\"1\">グループ1</option>",
                "<option value=\"2\">グループ2</option>",
                "<option value=\"3\" selected=\"selected\">グループ3</option>",
                "<option value=\"4\">グループ4</option></select>");
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForCheckedWithScopeValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("name_test", Integer.valueOf(3));
        
        TagTestUtil.setListWithIntegerId(pageContext);
        
        // select
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String startTag = "<select name=\"name_test\">";
        String excludeStartTag = Builder.lines(
                "<option value=\"0\">グループ0</option>",
                "<option value=\"1\">グループ1</option>",
                "<option value=\"2\">グループ2</option>",
                "<option value=\"3\" selected=\"selected\">グループ3</option>",
                "<option value=\"4\">グループ4</option></select>");
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForDefaultWithNullList() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"G003"});
        
        TagTestUtil.setList(pageContext, null);
        
        // select
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        
        assertThat(actual, is(""));
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithoutValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagTestUtil.setListWithStringId(pageContext);
        
        // select
        target.setName("name_test");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String startTag = "<select name=\"name_test\">";
        String excludeStartTag = Builder.lines(
                "<option value=\"G000\">グループ0</option>",
                "<option value=\"G001\">グループ1</option>",
                "<option value=\"G002\">グループ2</option>",
                "<option value=\"G003\">グループ3</option>",
                "<option value=\"G004\">グループ4</option></select>");
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithDefaultConfig() throws Exception {
        
        TagTestUtil.setUpDefaultConfig();
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"G003"});
        
        TagTestUtil.setListWithStringId(pageContext);
        TagTestUtil.setErrorMessages(pageContext);
        
        // generic
        target.setCssClass("cssClass_test");
        
        // select
        target.setName("entity.bbb");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String startTag = Builder.lines(
                "<select",
                "class=\"cssClass_test default_error\"",
                "name=\"entity.bbb\">").replace(Builder.LS, " ");
        String excludeStartTag = Builder.lines(
                "<option class=\"default_error\" value=\"G000\">default_グループ0</option>",
                "<option class=\"default_error\" value=\"G001\">default_グループ1</option>",
                "<option class=\"default_error\" value=\"G002\">default_グループ2</option>",
                "<option class=\"default_error\" value=\"G003\" selected=\"selected\">default_グループ3</option>",
                "<option class=\"default_error\" value=\"G004\">default_グループ4</option></select>");
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testInputPageWithNull() throws Exception {
        
        TagTestUtil.setUpDefaultConfig();

        /**********************************************************
        ラベルにnullを含む場合
        **********************************************************/

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"G003"});
        
        TagTestUtil.setListWithLabelNull(pageContext);
        TagTestUtil.setErrorMessages(pageContext);
        
        // generic
        target.setCssClass("cssClass_test");
        
        // select
        target.setName("entity.bbb");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        target.setElementLabelPattern("$VALUE$:$LABEL$");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String startTag = Builder.lines(
                "<select",
                "class=\"cssClass_test default_error\"",
                "name=\"entity.bbb\">").replace(Builder.LS, " ");
        String excludeStartTag = Builder.lines(
                "<option class=\"default_error\" value=\"G000\">G000:</option>",
                "<option class=\"default_error\" value=\"G001\">G001:</option>",
                "<option class=\"default_error\" value=\"G002\">G002:</option>",
                "<option class=\"default_error\" value=\"G003\" selected=\"selected\">G003:</option>",
                "<option class=\"default_error\" value=\"G004\">G004:</option></select>");
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));

        /**********************************************************
        値にnullを含む場合
        **********************************************************/

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"G003"});
        
        TagTestUtil.setListWithValueNull(pageContext);
        TagTestUtil.setErrorMessages(pageContext);
        
        // generic
        target.setCssClass("cssClass_test");
        
        // select
        target.setName("entity.bbb");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        target.setElementLabelPattern("$VALUE$:$LABEL$");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);

        startTag = Builder.lines(
                "<select",
                "class=\"cssClass_test default_error\"",
                "name=\"entity.bbb\">").replace(Builder.LS, " ");
        excludeStartTag = Builder.lines(
                "<option class=\"default_error\" value=\"\">:</option>",
                "<option class=\"default_error\" value=\"G001\">G001:グループ1</option>",
                "<option class=\"default_error\" value=\"G002\">G002:グループ2</option>",
                "<option class=\"default_error\" value=\"G003\" selected=\"selected\">G003:グループ3</option>",
                "<option class=\"default_error\" value=\"G004\">G004:グループ4</option></select>");
        splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testInputPageForError() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"G003"});
        
        TagTestUtil.setListWithStringId(pageContext);
        TagTestUtil.setErrorMessages(pageContext);
        
        // generic
        target.setCssClass("cssClass_test");
        
        // select
        target.setName("entity.bbb");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String startTag = Builder.lines(
                "<select",
                "class=\"cssClass_test nablarch_error\"",
                "name=\"entity.bbb\">").replace(Builder.LS, " ");
        String excludeStartTag = Builder.lines(
                "<option class=\"nablarch_error\" value=\"G000\">グループ0</option>",
                "<option class=\"nablarch_error\" value=\"G001\">グループ1</option>",
                "<option class=\"nablarch_error\" value=\"G002\">グループ2</option>",
                "<option class=\"nablarch_error\" value=\"G003\" selected=\"selected\">グループ3</option>",
                "<option class=\"nablarch_error\" value=\"G004\">グループ4</option></select>");
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testInputPageForErrorUsingAlias() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"G003"});
        
        TagTestUtil.setListWithStringId(pageContext);
        TagTestUtil.setErrorMessages(pageContext);
        
        // generic
        target.setCssClass("cssClass_test");
        
        // select
        target.setName("name_test");
        target.setNameAlias("entity.bbb");
        
        // nablarch
        target.setListName("groups");
        target.setElementLabelProperty("name");
        target.setElementValueProperty("groupId");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);

        String startTag = Builder.lines(
                "<select",
                "class=\"cssClass_test nablarch_error\"",
                "name=\"name_test\">").replace(Builder.LS, " ");
        String excludeStartTag = Builder.lines(
                "<option class=\"nablarch_error\" value=\"G000\">グループ0</option>",
                "<option class=\"nablarch_error\" value=\"G001\">グループ1</option>",
                "<option class=\"nablarch_error\" value=\"G002\">グループ2</option>",
                "<option class=\"nablarch_error\" value=\"G003\" selected=\"selected\">グループ3</option>",
                "<option class=\"nablarch_error\" value=\"G004\">グループ4</option></select>");
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = excludeStartTag.split(Builder.LS);
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        for (int i = 1; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i - 1], " ");
        }
        
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
        String startTag = "<select name=\"name_test\">";
        String excludeStartTag = "<option value=\"1\">0.0000000001</option></select>";

        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        TagTestUtil.assertTag(splitActual[1], excludeStartTag, " ");

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
        String startTag = "<select name=\"name_test\">";
        String excludeStartTag = "<option value=\"0.0000000001\">testName</option></select>";

        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        TagTestUtil.assertTag(splitActual[0], startTag, " ");
        TagTestUtil.assertTag(splitActual[1], excludeStartTag, " ");

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
            assertThat(e.getMessage(), is("invalid location of the select tag. the select tag must locate in the form tag."));
        }
    }
}
