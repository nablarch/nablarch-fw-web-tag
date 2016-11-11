package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Locale;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.MockPageContext;
import nablarch.core.ThreadContext;
import nablarch.test.support.SystemRepositoryResource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class CodeTagTest extends TagTestSupport<CodeTag> {

    public CodeTagTest() {
        super(new CodeTag());
    }

    public static final class Entity {
        private String[] bbb;
        public Entity(String[] bbb) {
            this.bbb = bbb;
        }
        public String[] getBbb() {
            return bbb;
        }
        public void setBbb(String[] bbb) {
            this.bbb = bbb;
        }
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

    @Before
    public void setUp() throws Exception {
    	super.setUp();
    	repositoryResource.getComponentByType(MockCodeManager.class).setCodePatterns(CODE_PATTERNS);
    	repositoryResource.getComponentByType(MockCodeManager.class).setCodeNames(CODE_NAMES);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        TagTestUtil.setUpCodeTagTestBeforeClass();
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

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new String[] {"03", "04"}));

        // nablarch
        target.setName("entity.bbb");
        target.setCodeId("0002");
        target.setPattern("PATTERN2");
        target.setOptionColumnName("OPTION01");
        target.setLabelPattern("$VALUE$:$NAME$-$SHORTNAME$-$OPTIONALNAME$");
        target.setListFormat("div");

        // div
        target.setListFormat("div");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "<div>03:処理実行中-実行-0002-03-ja</div>"
                + "<div>04:処理実行完了-完了-0002-04-ja</div>";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testAbbrName() throws Exception {
        TagTestUtil.setUpCodeTagTest();
        ThreadContext.setLanguage(Locale.JAPANESE);

        target.setOptionColumnName("OPTION01");
        target.setLabelPattern("$VALUE$:$NAME$-$SHORTNAME$-$OPTIONALNAME$");
        target.setListFormat("div");

        // 実行
        // 正常系
        target.setCodeId("0002");
        target.setPattern("PATTERN2");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "<div>03:処理実行中-実行-0002-03-ja</div>"
                + "<div>04:処理実行完了-完了-0002-04-ja</div>";
        TagTestUtil.assertTag(actual, expected, " ");

        // コードIDが同じでパターンが異なる。
        ((MockPageContext.MockJspWriter)pageContext.getOut()).clearOutput();
        target.setCodeId("0002");
        target.setPattern("PATTERN1");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<div>01:初期状態-初期-0002-01-ja</div>"
                + "<div>02:処理開始待ち-待ち-0002-02-ja</div>"
                + "<div>05:処理結果確認完了-確認-0002-05-ja</div>";
        TagTestUtil.assertTag(actual, expected, " ");

        // コードIDが異なる。
        ((MockPageContext.MockJspWriter)pageContext.getOut()).clearOutput();
        target.setCodeId("0001");
        target.setPattern("PATTERN1");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "<div>01:男性-男-0001-01-ja</div>"
                + "<div>02:女性-女-0001-02-ja</div>";
        TagTestUtil.assertTag(actual, expected, " ");

        // 該当するコードがない場合
        ((MockPageContext.MockJspWriter)pageContext.getOut()).clearOutput();
        target.setCodeId("0001");
        target.setPattern("PATTERN2");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
    }


    @Test
    public void testInputPageForDefault() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new String[] {"03", "04"}));

        // nablarch
        target.setName("entity.bbb");
        target.setCodeId("0002");

        // br(default)
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "処理実行中<br />"
                + "処理実行完了<br />";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // div
        target.setListFormat("div");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<div>処理実行中</div>"
                + "<div>処理実行完了</div>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // span
        target.setListFormat("span");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<span>処理実行中</span>"
                + "<span>処理実行完了</span>";

        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // ul
        target.setListFormat("ul");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<ul>"
                + "<li>処理実行中</li>"
                + "<li>処理実行完了</li>"
                + "</ul>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // ul
        target.setListFormat("ol");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<ol>"
                + "<li>処理実行中</li>"
                + "<li>処理実行完了</li>"
                + "</ol>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // sp
        target.setListFormat("sp");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "処理実行中&nbsp;"
                + "処理実行完了&nbsp;";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testInputPageForDefaultWithHtml() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new String[] {"03" + TagTestUtil.HTML, "04" + TagTestUtil.HTML}));

        // nablarch
        target.setName("entity.bbb");
        target.setCodeId("0003");

        // br(default)
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "<br />"
                + "処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "<br />";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // div
        target.setListFormat("div");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<div>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</div>"
                + "<div>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</div>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // span
        target.setListFormat("span");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<span>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</span>"
                + "<span>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</span>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // ul
        target.setListFormat("ul");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<ul>"
                + "<li>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>"
                + "<li>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>"
                + "</ul>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // ol
        target.setListFormat("ol");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<ol>"
                + "<li>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>"
                + "<li>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>"
                + "</ol>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // sp
        target.setListFormat("sp");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;"
                + "処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testInputPageWithRequestParameter() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"03", "04"});

        // nablarch
        target.setName("entity.bbb");
        target.setCodeId("0002");

        target.setListFormat("ul");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext),
                   is("<ul><li>処理実行中</li><li>処理実行完了</li></ul>"));
    }
    
    @Test
    public void testInputPageArrayWithNull() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        target.setName("array");
        target.setCodeId("0002");
        target.setListFormat("ul");

        pageContext.getAttributes(PageContext.PAGE_SCOPE).put("array", new String[] {null});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
    }
    
    @Test
    public void testInputPageListWithNull() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        target.setName("list");
        target.setCodeId("0002");
        target.setListFormat("ul");

        pageContext.getAttributes(PageContext.PAGE_SCOPE).put("list", Collections.singleton(null));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
    }

    @Test
    public void testConfirmationPageForDefault() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new String[] {"03", "04"}));

        TagUtil.setConfirmationPage(pageContext);

        // nablarch
        target.setName("entity.bbb");
        target.setCodeId("0002");

        // br(default)
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "処理実行中<br />"
                + "処理実行完了<br />";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // div
        target.setListFormat("div");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<div>処理実行中</div>"
                + "<div>処理実行完了</div>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // span
        target.setListFormat("span");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<span>処理実行中</span>"
                + "<span>処理実行完了</span>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // ul
        target.setListFormat("ul");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<ul>"
                + "<li>処理実行中</li>"
                + "<li>処理実行完了</li>"
                + "</ul>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // ul
        target.setListFormat("ol");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<ol>"
                + "<li>処理実行中</li>"
                + "<li>処理実行完了</li>"
                + "</ol>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // sp
        target.setListFormat("sp");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "処理実行中&nbsp;"
                + "処理実行完了&nbsp;";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testConfirmationPageForDefaultWithHtml() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new String[] {"03" + TagTestUtil.HTML, "04" + TagTestUtil.HTML}));

        TagUtil.setConfirmationPage(pageContext);

        // nablarch
        target.setName("entity.bbb");
        target.setCodeId("0003");

        // br(default)
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "<br />"
                + "処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "<br />";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // div
        target.setListFormat("div");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<div>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</div>"
                + "<div>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</div>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // span
        target.setListFormat("span");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<span>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</span>"
                + "<span>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</span>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // ul
        target.setListFormat("ul");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<ul>"
                + "<li>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>"
                + "<li>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>"
                + "</ul>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // ol
        target.setListFormat("ol");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "<ol>"
                + "<li>処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>"
                + "<li>処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "</li>"
                + "</ol>";
        TagTestUtil.assertTag(actual, expected, " ");

        TagTestUtil.clearOutput(pageContext);

        // sp
        target.setListFormat("sp");
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "処理実行中" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;"
                + "処理実行完了" + TagTestUtil.ESC_HTML_WITH_HTML_FORMAT + "&nbsp;";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void testConfirmationPageWithRequestValue() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        TagUtil.setConfirmationPage(pageContext);

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"03", "04"});

        // nablarch
        target.setName("entity.bbb");
        target.setCodeId("0002");

        target.setListFormat("ul");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext),
                   is("<ul><li>処理実行中</li><li>処理実行完了</li></ul>"));
    }

    @Test
    public void testConfirmationPageWithNullValue() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        TagUtil.setConfirmationPage(pageContext);

        // nablarch
        target.setName("entity.bbb");
        target.setCodeId("0002");

        target.setListFormat("ul");

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(null));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
    }

    @Test
    public void testConfirmationPageWithBlankValue() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);

        TagUtil.setConfirmationPage(pageContext);

        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("entity", new Entity(new String[] {"", ""}));

        // nablarch
        target.setName("entity.bbb");
        target.setCodeId("0002");

        target.setListFormat("ul");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
    }
    
    @Test
    public void testConfirmationPageArrayWithNull() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);
        TagUtil.setConfirmationPage(pageContext);

        target.setName("array");
        target.setCodeId("0002");
        target.setListFormat("ul");

        pageContext.getAttributes(PageContext.PAGE_SCOPE).put("array", new String[] {null});

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
    }

    @Test
    public void testConfirmationPageListWithNull() throws Exception {

        TagTestUtil.setUpCodeTagTest();

        ThreadContext.setLanguage(Locale.JAPANESE);
        TagUtil.setConfirmationPage(pageContext);

        target.setName("list");
        target.setCodeId("0002");
        target.setListFormat("ul");

        pageContext.getAttributes(PageContext.PAGE_SCOPE).put("list", Collections.singleton(null));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
    }
}
