package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import javax.servlet.jsp.tagext.Tag;

import nablarch.core.ThreadContext;
import nablarch.core.util.Builder;
import nablarch.test.support.SystemRepositoryResource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class CodeCheckboxTagTest extends TagTestSupport<CodeCheckboxTag> {

    public CodeCheckboxTagTest() {
        super(new CodeCheckboxTag());
    }

    @Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource("nablarch/common/web/tag/code-tag-test.xml");

    private static final String[][] CODE_NAMES = {
        { "0001", "01", "2", "en", "Male", "M", "01:Male", "0001-01-en" },
        { "0001", "01", "1", "ja", "男性", "男", "01:Male", "0001-01-ja" },
        { "0001", "02", "1", "en", "Female", "F", "02:Female", "0001-02-en" },
        { "0001", "02", "2", "ja", "女性", "女", "02:Female", "0001-02-ja" },
        { "0002", "01", "1", "en", "Initial State", "Initial","", "0002-01-en" },
        { "0002", "01", "1", "ja", "初期状態", "初期","", "0002-01-ja" },
        { "0002", "02", "2", "en", "Waiting For Batch Start", "Waiting","", "0002-02-en" },
        { "0002", "02", "2", "ja", "処理開始待ち", "待ち", "","0002-02-ja" },
        { "0002", "03", "3", "en", "Batch Running", "Running","", "0002-03-en" },
        { "0002", "03", "3", "ja", "処理実行中", "実行", "処理実行中","0002-03-ja" },
        { "0002", "04", "4", "en", "Batch Execute Completed Checked", "Completed","", "0002-04-en" },
        { "0002", "04", "4", "ja", "処理実行完了", "完了","", "0002-04-ja" },
        { "0002", "05", "5", "en", "Batch Result Checked", "Checked","", "0002-05-en" },
        { "0002", "05", "5", "ja", "処理結果確認完了", "確認","", "0002-05-ja" },
       { "0003", "01<table id=\"id\" style='style'>", "1", "ja", "初期状態<table id=\"id\" style='style'>", "初期<table id=\"id\" style='style'>", "","0002-01-ja<table id=\"id\" style='style'>" },
       { "0003", "02<table id=\"id\" style='style'>", "2", "ja", "処理開始待ち<table id=\"id\" style='style'>", "待ち<table id=\"id\" style='style'>", "","0002-02-ja<table id=\"id\" style='style'>" },
        { "0003", "03<table id=\"id\" style='style'>", "3", "ja", "処理実行中<table id=\"id\" style='style'>", "実行<table id=\"id\" style='style'>", "","0002-03-ja<table id=\"id\" style='style'>" },
       { "0003", "04<table id=\"id\" style='style'>", "4", "ja", "処理実行完了<table id=\"id\" style='style'>", "完了<table id=\"id\" style='style'>","", "0002-04-ja<table id=\"id\" style='style'>" },
        { "0003", "05<table id=\"id\" style='style'>", "5", "ja", "処理結果確認完了<table id=\"id\" style='style'>", "確認<table id=\"id\" style='style'>","", "0002-05-ja<table id=\"id\" style='style'>" },
        { "0005", "0", "2", "en", "No, thank you.", "NO", "0:NO", "0005-0-en" },
        { "0005", "0", "2", "ja", "いいえいいえ", "いいえ", "0:NO", "0005-0-ja" },
        { "0005", "1", "1", "en", "Yes, off course!", "YES", "1:YES", "0005-1-en" },
        { "0005", "1", "1", "ja", "はいはい", "はい", "1:YES", "0005-1-ja" },
        { "0006", "0<table id=\"id\" style='style'>", "2", "en", "No, thank you.<table id=\"id\" style='style'>", "NO<table id=\"id\" style='style'>", "0:NO", "0006-0-en<table id=\"id\" style='style'>" },
        { "0006", "0<table id=\"id\" style='style'>", "2", "ja", "いいえいいえ<table id=\"id\" style='style'>", "いいえ<table id=\"id\" style='style'>", "0:NO", "0006-0-ja<table id=\"id\" style='style'>" },
        { "0006", "1<table id=\"id\" style='style'>", "1", "en", "Yes, off course!<table id=\"id\" style='style'>", "YES<table id=\"id\" style='style'>", "1:YES", "0006-1-en<table id=\"id\" style='style'>" },
        { "0006", "1<table id=\"id\" style='style'>", "1", "ja", "はいはい<table id=\"id\" style='style'>", "はい<table id=\"id\" style='style'>", "1:YES", "0006-1-ja<table id=\"id\" style='style'>" },
        { "0007", "Y", "1", "ja", "Y0007", "はい", "1:YES", "0007-Y-ja" },
        { "0007", "N", "2", "ja", "N0007", "いいえ", "0:NO", "0007-N-ja" },
        { "0008", "Y", "1", "ja", "Y0008", "はい", "1:YES", "0008-Y-ja" },
        { "0008", "N", "2", "ja", "N0008", "いいえ", "0:NO", "0008-N-ja" },
        { "0008", "P", "2", "ja", "P0008", "いいえ", "0:NO", "0008-P-ja" },
        { "0009", "0", "2", "ja", "","いいえ", "0:NO", "0005-0-ja" },
        { "0009", "1", "1", "ja", "","はい", "1:YES", "0005-1-ja" },
    };

    private static final String[][] CODE_PATTERNS = {
    	{ "0001", "01", "1", "0", "0" },
    	{ "0001", "02", "1", "0", "0" },
    	{ "0002", "01", "1", "0", "0" },
    	{ "0002", "02", "1", "0", "0" },
    	{ "0002", "03", "0", "1", "0" },
    	{ "0002", "04", "0", "1", "0" },
    	{ "0002", "05", "1", "0", "0" },
    	{ "0003", "01<table id=\"id\" style='style'>", "1", "0", "0" },
    	{ "0003", "02<table id=\"id\" style='style'>", "1", "0", "0" },
    	{ "0003", "03<table id=\"id\" style='style'>", "0", "1", "0" },
    	{ "0003", "04<table id=\"id\" style='style'>", "0", "1", "0" },
    	{ "0003", "05<table id=\"id\" style='style'>", "1", "0", "0" },
    	{ "0005", "1", "0", "0", "0" },
    	{ "0005", "0", "0", "0", "0" },
    	{ "0006", "1<table id=\"id\" style='style'>", "0", "0", "0" },
    	{ "0006", "0<table id=\"id\" style='style'>", "0", "0", "0" },
    	{ "0007", "Y", "0", "0", "0" },
    	{ "0007", "N", "0", "0", "0" },
    	{ "0008", "Y", "0", "0", "0" },
    	{ "0008", "N", "0", "0", "0" },
    	{ "0008", "P", "0", "0", "0" },
    	{ "0009", "1", "0", "0", "0" },
    	{ "0009", "0", "0", "0", "0" },
    };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    	super.setUp();
    	repositoryResource.getComponentByType(MockCodeManager.class).setCodePatterns(CODE_PATTERNS);
    	repositoryResource.getComponentByType(MockCodeManager.class).setCodeNames(CODE_NAMES);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testInputPageForAllSetting() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        // generic
        TagTestUtil.setGenericAttributes(target);

        // focus
        TagTestUtil.setFocusAttributes(target);

        // input
        target.setName("name_test");
        target.setDisabled(true);
        target.setOnchange("onchange_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setCodeId("0005");
        target.setOptionColumnName("OPTION01");
        target.setLabelPattern("$VALUE$:$NAME$-$SHORTNAME$-$OPTIONALNAME$");
        target.setOffCodeValue("0");
        target.setValue("1");

        String temp = Builder.lines(
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
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" /><label for=\"id_test\">%s</label>").replace(Builder.LS, " ");

        // チェックありの場合

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"1"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String expected = Builder.lines(String.format(temp, "1", "checked=\"checked\"", "1:はいはい-はい-0005-1-ja"))
                                 .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));

        // チェックなしの場合
        TagTestUtil.clearOutput(pageContext);

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"0"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);

        expected = Builder.lines(String.format(temp, "1", "unchecked", "1:はいはい-はい-0005-1-ja"))
                                       .replace("unchecked ", "")
                                       .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        // generic
        TagTestUtil.setGenericAttributesWithHtml(target);

        // focus
        TagTestUtil.setFocusAttributesWithHtml(target);

        // input
        target.setName("name_test" + TagTestUtil.HTML);
        target.setDisabled(true);
        target.setOnchange("onchange_test" + TagTestUtil.HTML);

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setCodeId("0006");
        target.setOptionColumnName("OPTION01");
        target.setLabelPattern("$VALUE$:$NAME$-$SHORTNAME$-$OPTIONALNAME$");
        target.setOffCodeValue("0" + TagTestUtil.HTML);
        target.setValue("1" + TagTestUtil.HTML);

        String temp = Builder.lines(
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
                "onblur=\"onblur_test" + TagTestUtil.ESC_HTML + "\"",
                "autofocus=\"autofocus\" /><label for=\"id_test" + TagTestUtil.ESC_HTML + "\">%s</label>").replace(Builder.LS, " ");

        // チェックありの場合

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"1" + TagTestUtil.HTML});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String expected = Builder.lines(String.format(temp, "1" + TagTestUtil.ESC_HTML,
                                                            "checked=\"checked\"",
                                                            "1" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT
                                                          + ":はいはい" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT
                                                          + "-はい" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT
                                                          + "-0006-1-ja" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT))
                                 .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test" + TagTestUtil.HTML));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0" + TagTestUtil.HTML));

        // チェックなしの場合
        TagTestUtil.clearOutput(pageContext);

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"0" + TagTestUtil.HTML});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);

        expected = Builder.lines(String.format(temp, "1" + TagTestUtil.ESC_HTML,
                                                     "unchecked",
                                                     "1" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT
                                                   + ":はいはい" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT
                                                   + "-はい" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT
                                                   + "-0006-1-ja" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT))
                                       .replace("unchecked ", "")
                                       .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test" + TagTestUtil.HTML));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0" + TagTestUtil.HTML));
    }

    @Test
    public void testInputPageForDefault() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        // input
        target.setName("name_test");

        // nablarch
        target.setCodeId("0005");

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox1\">%s</label>").replace(Builder.LS, " ");

        // チェックありの場合

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"1"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String expected = Builder.lines(String.format(temp, "1", "checked=\"checked\"", "はいはい"))
                                 .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));

        // チェックなしの場合
        TagTestUtil.clearOutput(pageContext);

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"0"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);

        expected = Builder.lines(String.format(temp, "1", "unchecked", "はいはい"))
                                       .replace("unchecked ", "")
                                       .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));
    }

    @Test
    public void testInputPageWithoutValue() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        // input
        target.setName("name_test");

        // nablarch
        target.setCodeId("0005");

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox1\">%s</label>").replace(Builder.LS, " ");

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String expected = Builder.lines(String.format(temp, "1", "unchecked", "はいはい"))
                                 .replace("unchecked ", "")
                                 .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));
    }

    @Test
    public void testInputPageWithDefaultConfig() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        CustomTagConfig config = TagUtil.getCustomTagConfig();
        config.setCodeLabelPattern("$SHORTNAME$-$VALUE$");
        config.setCheckboxOnValue("0");
        config.setCheckboxOffValue("1");

        ThreadContext.setLanguage(Locale.JAPANESE);

        // input
        target.setName("name_test");

        // nablarch
        target.setCodeId("0005");

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_checkbox1\">%s</label>").replace(Builder.LS, " ");

        // チェックありの場合

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"0"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String expected = Builder.lines(String.format(temp, "0", "checked=\"checked\"", "いいえ-0"))
                                 .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("1"));

        // チェックなしの場合
        TagTestUtil.clearOutput(pageContext);

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"1"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);

        expected = Builder.lines(String.format(temp, "0", "unchecked", "いいえ-0"))
                                       .replace("unchecked ", "")
                                       .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("1"));

        CustomTagConfig newConfig = new CustomTagConfig();
        config.setCodeLabelPattern(newConfig.getCodeLabelPattern());
        config.setCheckboxOnValue(newConfig.getCheckboxOnValue());
        config.setCheckboxOffValue(newConfig.getCheckboxOffValue());
    }

    @Test
    public void testInputPageForError() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        TagTestUtil.setErrorMessages(pageContext);

        // input
        target.setName("entity.bbb");

        // nablarch
        target.setCodeId("0005");
        target.setCssClass("cssClass_test");

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"checkbox\"",
                "name=\"entity.bbb\"",
                "value=\"%s\"",
                "%s /><label class=\"nablarch_error\" for=\"nablarch_checkbox1\">%s</label>").replace(Builder.LS, " ");

        // チェックありの場合

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"1"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String expected = Builder.lines(String.format(temp, "1", "checked=\"checked\"", "はいはい"))
                                 .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("entity.bbb"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_entity.bbb"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));

        // チェックなしの場合
        TagTestUtil.clearOutput(pageContext);

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"0"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);

        expected = Builder.lines(String.format(temp, "1", "unchecked", "はいはい"))
                                       .replace("unchecked ", "")
                                       .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("entity.bbb"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_entity.bbb"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));
    }

    @Test
    public void testInputPageForErrorUsingAlias() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        TagTestUtil.setErrorMessages(pageContext);

        // input
        target.setName("name_test");

        // nablarch
        target.setCodeId("0005");
        target.setCssClass("cssClass_test");
        target.setNameAlias("entity.bbb");

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_checkbox1\"",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"checkbox\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label class=\"nablarch_error\" for=\"nablarch_checkbox1\">%s</label>").replace(Builder.LS, " ");

        // チェックありの場合

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"1"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String expected = Builder.lines(String.format(temp, "1", "checked=\"checked\"", "はいはい"))
                                 .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));

        // チェックなしの場合
        TagTestUtil.clearOutput(pageContext);

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"0"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);

        expected = Builder.lines(String.format(temp, "1", "unchecked", "はいはい"))
                                       .replace("unchecked ", "")
                                       .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.NAME), is("nablarch_cbx_off_param_name_test"));
        assertThat(formContext.getHiddenTagInfoList().get(0).<String>get(HtmlAttribute.VALUE), is("0"));
    }

    @Test
    public void testConfirmationPageForDefault() throws Exception {

        TagUtil.setConfirmationPage(pageContext);
        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        // input
        target.setName("name_test");

        // nablarch
        target.setCodeId("0005");

        // チェックありの場合

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"1"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "はいはい";
        TagTestUtil.assertTag(actual, expected, " ");

        // チェックなしの場合
        TagTestUtil.clearOutput(pageContext);

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"0"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "いいえいいえ";
        TagTestUtil.assertTag(actual, expected, " ");

    }

    @Test
    public void testConfirmationPageForDefaultWithHtml() throws Exception {

        TagUtil.setConfirmationPage(pageContext);
        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        // input
        target.setName("name_test" + TagTestUtil.HTML);

        // nablarch
        target.setCodeId("0006");
        target.setOffCodeValue("0" + TagTestUtil.HTML);
        target.setValue("1" + TagTestUtil.HTML);

        // チェックありの場合

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"1" + TagTestUtil.HTML});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "はいはい" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT;
        TagTestUtil.assertTag(actual, expected, " ");

        // チェックなしの場合
        TagTestUtil.clearOutput(pageContext);

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"0" + TagTestUtil.HTML});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "いいえいいえ" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT;
        TagTestUtil.assertTag(actual, expected, " ");

    }

    @Test
    public void testOffCodeValue() throws Exception {

        TagUtil.setConfirmationPage(pageContext);
        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        // input
        target.setName("name_test");

        // チェックなしのコード値が検索により見つかる場合

        target.setCodeId("0007");
        target.setValue("Y");
        target.setOffCodeValue(null);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"N"});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "N0007";
        TagTestUtil.assertTag(actual, expected, " ");

        // チェックなしのコード値が検索により見つからない場合(検索結果件数3件)

        target.setCodeId("0008");
        target.setValue("Y");
        target.setOffCodeValue(null);

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"N"});

        try {
            assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
            fail();
        } catch (IllegalArgumentException e) {
            // デフォルト値が使用される。
            assertThat(e.getMessage(), is("name was not found. code id = 0008"));
        }

        target.setOffCodeValue("P"); // offCodeValue属性を指定する。

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "P0008";
        TagTestUtil.assertTag(actual, expected, " ");

        // チェックなしのコード値が検索により見つからない場合(value属性の値が含まれない)

        target.setCodeId("0007");
        target.setValue("P");
        target.setOffCodeValue(null);

        TagTestUtil.clearOutput(pageContext);
        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        pageContext.getMockReq().getParams().put("name_test", new String[] {"N"});

        try {
            assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
            fail();
        } catch (IllegalArgumentException e) {
            // デフォルト値が使用される。
            assertThat(e.getMessage(), is("name was not found. code id = 0007"));
        }
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
            assertThat(e.getMessage(), is("invalid location of the codeCheckbox tag. the codeCheckbox tag must locate in the form tag."));
        }
    }
}
