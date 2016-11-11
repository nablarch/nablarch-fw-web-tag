package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Locale;

import javax.servlet.jsp.PageContext;
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
public class CodeRadioButtonsTagTest extends TagTestSupport<CodeRadioButtonsTag> {

    public CodeRadioButtonsTagTest() {
        super(new CodeRadioButtonsTag());
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
        TagTestUtil.setUpCodeTagTestBeforeClass();
    }

    @Before
    public void setUp() throws Exception {
    	super.setUp();
    	repositoryResource.getComponentByType(MockCodeManager.class).setCodePatterns(CODE_PATTERNS);
    	repositoryResource.getComponentByType(MockCodeManager.class).setCodeNames(CODE_NAMES);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        TagTestUtil.tearDownConnectionAfterClass();
    }

    @Test
    public void testInvalidListFormat() throws Exception {

        TagTestUtil.setUpCodeTagTest();

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

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"03"});

        // generic
        TagTestUtil.setGenericAttributesForInputs(target);
        target.setTabindex(2);

        // focus
        TagTestUtil.setFocusAttributesForInputs(target);

        // input
        target.setName("name_test");
        target.setDisabled(true);
        target.setOnchange("onchange_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setCodeId("0002");
        target.setPattern("PATTERN2");
        target.setOptionColumnName("OPTION01");
        target.setLabelPattern("$VALUE$:$NAME$-$SHORTNAME$-$OPTIONALNAME$");
        target.setListFormat("div");
        target.setErrorCss("errorCss_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<div><input",
                "id=\"nablarch_radio%s\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "tabindex=\"2\"", // 出力されるすべてのinputタグに同一のtabindexが出力されることを確認する
                "type=\"radio\"",
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
                "onblur=\"onblur_test\" %s/><label for=\"nablarch_radio%s\">%s</label></div>").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "03", checked, "autofocus=\"autofocus\" ", i + "", "03:処理実行中-実行-0002-03-ja"),
                String.format(temp, ++i + "", "04", unchecked, "", i + "", "04:処理実行完了-完了-0002-04-ja"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"03" + TagTestUtil.HTML});

        // generic
        TagTestUtil.setGenericAttributesWithHtmlForInputs(target);
        target.setTabindex(2);

        // focus
        TagTestUtil.setFocusAttributesWithHtmlForInputs(target);

        // input
        target.setName("name_test" + TagTestUtil.HTML);
        target.setDisabled(true);
        target.setOnchange("onchange_test" + TagTestUtil.HTML);

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setCodeId("0003");
        target.setPattern("PATTERN2");
        target.setOptionColumnName("OPTION01");
        target.setLabelPattern("$VALUE$:$NAME$-$SHORTNAME$-$OPTIONALNAME$");
        target.setListFormat("div");
        target.setErrorCss("errorCss_test" + TagTestUtil.HTML);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<div><input",
                "id=\"nablarch_radio%s\"",
                "class=\"css_test" + TagTestUtil.ESC_HTML + "\"",
                "style=\"style_test" + TagTestUtil.ESC_HTML + "\"",
                "title=\"title_test" + TagTestUtil.ESC_HTML + "\"",
                "lang=\"lang_test" + TagTestUtil.ESC_HTML + "\"",
                "xml:lang=\"xmlLang_test" + TagTestUtil.ESC_HTML + "\"",
                "dir=\"dir_test" + TagTestUtil.ESC_HTML + "\"",
                "tabindex=\"2\"", // 出力されるすべてのinputタグに同一のtabindexが出力されることを確認する
                "type=\"radio\"",
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
                "onblur=\"onblur_test" + TagTestUtil.ESC_HTML + "\" %s/><label for=\"nablarch_radio%s\">%s</label></div>").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";

        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "03" + TagTestUtil.ESC_HTML, checked, "autofocus=\"autofocus\" ", i + "", "03" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + ":処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "-実行" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "-0002-03-ja" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + ""),
                String.format(temp, ++i + "", "04" + TagTestUtil.ESC_HTML, unchecked, "", i + "", "04" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + ":処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "-完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "-0002-04-ja" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + ""))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    @Test
    public void testInputPageForDefault() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"03"});

        // input
        target.setName("name_test");

        // nablarch
        target.setCodeId("0002");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_radio%s\"",
                "type=\"radio\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_radio%s\">%s</label><br />").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"),
                String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"),
                String.format(temp, ++i + "", "03", checked, i + "", "処理実行中"),
                String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"),
                String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithoutValue() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("name_test");

        // nablarch
        target.setCodeId("0002");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_radio%s\"",
                "type=\"radio\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_radio%s\">%s</label><br />").replace(Builder.LS, " ");
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"),
                String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"),
                String.format(temp, ++i + "", "03", unchecked, i + "", "処理実行中"),
                String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"),
                String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithDefaultConfig() throws Exception {

        TagTestUtil.setUpDefaultConfig();
        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"03"});

        TagTestUtil.setErrorMessages(pageContext);

        // generic
        target.setCssClass("cssClass_test");

        // input
        target.setName("entity.bbb");

        // nablarch
        target.setCodeId("0002");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<span class=\"default_error\"><input",
                "id=\"nablarch_radio%s\"",
                "class=\"cssClass_test default_error\"",
                "type=\"radio\"",
                "name=\"entity.bbb\"",
                "value=\"%s\"",
                "%s /><label class=\"default_error\" for=\"nablarch_radio%s\">%s</label></span>").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "01", unchecked, i + "", "default_初期状態"),
                String.format(temp, ++i + "", "02", unchecked, i + "", "default_処理開始待ち"),
                String.format(temp, ++i + "", "03", checked, i + "", "default_処理実行中"),
                String.format(temp, ++i + "", "04", unchecked, i + "", "default_処理実行完了"),
                String.format(temp, ++i + "", "05", unchecked, i + "", "default_処理結果確認完了"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("entity.bbb"));
    }

    @Test
    public void testInputPageWithArrayNull() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {null});

        // input
        target.setName("name_test");

        // nablarch
        target.setCodeId("0002");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_radio%s\"",
                "type=\"radio\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_radio%s\">%s</label><br />").replace(Builder.LS, " ");
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"),
                String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"),
                String.format(temp, ++i + "", "03", unchecked, i + "", "処理実行中"),
                String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"),
                String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithListNull() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

        // input
        target.setName("name_test");

        // nablarch
        target.setCodeId("0002");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_radio%s\"",
                "type=\"radio\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label for=\"nablarch_radio%s\">%s</label><br />").replace(Builder.LS, " ");
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"),
                String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"),
                String.format(temp, ++i + "", "03", unchecked, i + "", "処理実行中"),
                String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"),
                String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForError() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"03"});

        TagTestUtil.setErrorMessages(pageContext);

        // generic
        target.setCssClass("cssClass_test");

        // input
        target.setName("entity.bbb");

        // nablarch
        target.setCodeId("0002");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_radio%s\"",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"radio\"",
                "name=\"entity.bbb\"",
                "value=\"%s\"",
                "%s /><label class=\"nablarch_error\" for=\"nablarch_radio%s\">%s</label>").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"), "<br />",
                String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"), "<br />",
                String.format(temp, ++i + "", "03", checked, i + "", "処理実行中"), "<br />",
                String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"), "<br />",
                String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"), "<br />"
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
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"), "</div>",
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"), "</div>",
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "03", checked, i + "", "処理実行中"), "</div>",
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"), "</div>",
                "<div class=\"nablarch_error\">", String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"), "</div>"
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
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"), "</span>",
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"), "</span>",
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "03", checked, i + "", "処理実行中"), "</span>",
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"), "</span>",
                "<span class=\"nablarch_error\">", String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"), "</span>"
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
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "03", checked, i + "", "処理実行中"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"), "</li>",
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
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "03", checked, i + "", "処理実行中"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"), "</li>",
                "<li class=\"nablarch_error\">", String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"), "</li>",
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
                String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"), "&nbsp;",
                String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"), "&nbsp;",
                String.format(temp, ++i + "", "03", checked, i + "", "処理実行中"), "&nbsp;",
                String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"), "&nbsp;",
                String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"), "&nbsp;"
                )
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testInputPageForErrorUsingAlias() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"03"});

        TagTestUtil.setErrorMessages(pageContext);

        // generic
        target.setCssClass("cssClass_test");

        // input
        target.setName("name_test");
        target.setNameAlias("entity.bbb");

        // nablarch
        target.setCodeId("0002");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);

        String temp = Builder.lines(
                "<input",
                "id=\"nablarch_radio%s\"",
                "class=\"cssClass_test nablarch_error\"",
                "type=\"radio\"",
                "name=\"name_test\"",
                "value=\"%s\"",
                "%s /><label class=\"nablarch_error\" for=\"nablarch_radio%s\">%s</label><br />").replace(Builder.LS, " ");
        String checked = "checked=\"checked\"";
        String unchecked = "unchecked";
        int i = 0;
        String expected = Builder.lines(
                String.format(temp, ++i + "", "01", unchecked, i + "", "初期状態"),
                String.format(temp, ++i + "", "02", unchecked, i + "", "処理開始待ち"),
                String.format(temp, ++i + "", "03", checked, i + "", "処理実行中"),
                String.format(temp, ++i + "", "04", unchecked, i + "", "処理実行完了"),
                String.format(temp, ++i + "", "05", unchecked, i + "", "処理結果確認完了"))
                .replace("unchecked ", "")
                .replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForDefault() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {"03", "04"});

        TagUtil.setConfirmationPage(pageContext);

        // select
        target.setName("name_test");

        // nablarch
        target.setCodeId("0002");

        // br(default)
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "処理実行中<br />",
                "処理実行完了<br />").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        TagTestUtil.clearOutput(pageContext);

        // div
        target.setListFormat("div");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<div>処理実行中</div>",
                "<div>処理実行完了</div>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        TagTestUtil.clearOutput(pageContext);

        // span
        target.setListFormat("span");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<span>処理実行中</span>",
                "<span>処理実行完了</span>").replace(Builder.LS, "");
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
                "<li>処理実行中</li>",
                "<li>処理実行完了</li>",
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
                "<li>処理実行中</li>",
                "<li>処理実行完了</li>",
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
                "処理実行中&nbsp;",
                "処理実行完了&nbsp;").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForDefaultWithHtml() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"03" + TagTestUtil.HTML, "04" + TagTestUtil.HTML});

        TagUtil.setConfirmationPage(pageContext);

        // select
        target.setName("name_test" + TagTestUtil.HTML);

        // nablarch
        target.setCodeId("0003");

        // br(default)
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "<br />",
                "処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "<br />").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));

        TagTestUtil.clearOutput(pageContext);

        // div
        target.setListFormat("div");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<div>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</div>",
                "<div>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</div>").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));

        TagTestUtil.clearOutput(pageContext);

        // span
        target.setListFormat("span");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = Builder.lines(
                "<span>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</span>",
                "<span>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</span>").replace(Builder.LS, "");
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
                "<li>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>",
                "<li>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>",
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
                "<li>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>",
                "<li>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>",
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
                "処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;",
                "処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;").replace(Builder.LS, "");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    @Test
    public void testConfirmationPageWithoutValue() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        TagUtil.setConfirmationPage(pageContext);

        // select
        target.setName("name_test");

        // nablarch
        target.setCodeId("0002");
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

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        TagUtil.setConfirmationPage(pageContext);

        pageContext.getMockReq().getParams().put("name_test", new String[] {null});

        // select
        target.setName("name_test");

        // nablarch
        target.setCodeId("0002");
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

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        TagUtil.setConfirmationPage(pageContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

        // select
        target.setName("name_test");

        // nablarch
        target.setCodeId("0002");
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
            assertThat(e.getMessage(), is("invalid location of the codeRadioButtons tag. the codeRadioButtons tag must locate in the form tag."));
        }
    }
}
