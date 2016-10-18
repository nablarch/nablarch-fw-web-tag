package nablarch.common.web.tag;

import nablarch.core.ThreadContext;
import nablarch.core.util.ObjectUtil;
import nablarch.test.support.SystemRepositoryResource;
import nablarch.test.support.message.MockStringResourceHolder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * {@link MessageTag}のテスト。
 * @author Kiyohito Itoh
 */
public class MessageTagTest extends TagTestSupport<MessageTag> {

    @Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource("nablarch/common/web/tag/message-tag-test.xml");

    private static final String[][] MESSAGES = {
        { "TEST0001", "ja", "テストメッセージ。0[{0}],1[{1}],2[{2}],3[{3}],4[{4}],5[{5}],6[{6}],7[{7}],8[{8}],9[{9}]" , "en", "TEST_MESSAGE.9[{9}],0[{0}],1[{1}],2[{2}],3[{3}],4[{4}],5[{5}],6[{6}],7[{7}],8[{8}]" },
        { "TEST0002", "ja", "<div id=\"id\" style=''style''>\nテスト\n</div>" , "en", "<div id=\"id\" style=''style''>\nTEST\n</div>" },
    };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    	repositoryResource.getComponentByType(MockStringResourceHolder.class).setMessages(MESSAGES);
    }

    public MessageTagTest() {
        super(new MessageTag());
    }

    /**
     * 言語指定に応じたメッセージの出力をテストする。
     */
    @Test
    public void testOutputForLanguage() throws Exception {

        String actual;

        target.setMessageId("TEST0001");
        setOptions(target);

        /************************************************************
        language属性の指定がない場合。
        ************************************************************/

        // 日本語
        TagTestUtil.clearOutput(pageContext);
        ThreadContext.setLanguage(new Locale("ja"));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("テストメッセージ。0[test0],1[test1],2[test2],3[test3],4[test4],5[test5],6[test6],7[test7],8[test8],9[test9]"));

        // 英語
        TagTestUtil.clearOutput(pageContext);
        ThreadContext.setLanguage(new Locale("en"));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("TEST_MESSAGE.9[test9],0[test0],1[test1],2[test2],3[test3],4[test4],5[test5],6[test6],7[test7],8[test8]"));

        /************************************************************
        language属性を指定した場合。
        ************************************************************/

        ThreadContext.setLanguage(new Locale("ja")); // スレッドコンテキストは日本語とする。

        // 日本語
        TagTestUtil.clearOutput(pageContext);

        target.setLanguage("ja");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("テストメッセージ。0[test0],1[test1],2[test2],3[test3],4[test4],5[test5],6[test6],7[test7],8[test8],9[test9]"));

        // 英語
        TagTestUtil.clearOutput(pageContext);

        target.setLanguage("en");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("TEST_MESSAGE.9[test9],0[test0],1[test1],2[test2],3[test3],4[test4],5[test5],6[test6],7[test7],8[test8]"));
    }

    /**
     * HTMLエスケープ指定に応じたメッセージの出力をテストする。
     */
    @Test
    public void testOutputForHtmlEscape() throws Exception {

        String actual;

        target.setMessageId("TEST0002");

        /************************************************************
        htmlEscape属性の指定がない場合。
        ************************************************************/

        ThreadContext.setLanguage(new Locale("ja")); // スレッドコンテキストは日本語とする。

        TagTestUtil.clearOutput(pageContext);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("&lt;div&nbsp;id=&#034;id&#034;&nbsp;style=&#039;style&#039;&gt;<br />テスト<br />&lt;/div&gt;"));

        /************************************************************
        htmlEscape属性を指定した場合。
        ************************************************************/

        ThreadContext.setLanguage(new Locale("en")); // スレッドコンテキストは英語とする。

        // htmlEscape属性 = true
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(true);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("&lt;div&nbsp;id=&#034;id&#034;&nbsp;style=&#039;style&#039;&gt;<br />TEST<br />&lt;/div&gt;"));

        // htmlEscape属性 = false
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(false);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("<div id=\"id\" style='style'>\nTEST\n</div>"));

        // htmlEscape属性 = true かつ withHtmlFormat属性 = true
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(true);
        target.setWithHtmlFormat(true);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("&lt;div&nbsp;id=&#034;id&#034;&nbsp;style=&#039;style&#039;&gt;<br />TEST<br />&lt;/div&gt;"));

        // htmlEscape属性 = true かつ withHtmlFormat属性 = false
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(true);
        target.setWithHtmlFormat(false);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("&lt;div id=&#034;id&#034; style=&#039;style&#039;&gt;\nTEST\n&lt;/div&gt;"));

        // htmlEscape属性 = false かつ withHtmlFormat属性 = true
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(false);
        target.setWithHtmlFormat(true);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("<div id=\"id\" style='style'>\nTEST\n</div>"));
    }

    /**
     * 言語指定に応じたメッセージの変数設定をテストする。
     */
    @Test
    public void testValForLanguage() throws Exception {

        String actual;

        target.setMessageId("TEST0001");
        target.setVar("testMsg");
        setOptions(target);

        /************************************************************
        language属性の指定がない場合。
        ************************************************************/

        // 日本語
        TagTestUtil.clearOutput(pageContext);
        ThreadContext.setLanguage(new Locale("ja"));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("テストメッセージ。0[test0],1[test1],2[test2],3[test3],4[test4],5[test5],6[test6],7[test7],8[test8],9[test9]"));

        // 英語
        TagTestUtil.clearOutput(pageContext);
        ThreadContext.setLanguage(new Locale("en"));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("TEST_MESSAGE.9[test9],0[test0],1[test1],2[test2],3[test3],4[test4],5[test5],6[test6],7[test7],8[test8]"));

        /************************************************************
        language属性を指定した場合。
        ************************************************************/

        ThreadContext.setLanguage(new Locale("ja")); // スレッドコンテキストは日本語とする。

        // 日本語
        TagTestUtil.clearOutput(pageContext);

        target.setLanguage("ja");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("テストメッセージ。0[test0],1[test1],2[test2],3[test3],4[test4],5[test5],6[test6],7[test7],8[test8],9[test9]"));

        // 英語
        TagTestUtil.clearOutput(pageContext);

        target.setLanguage("en");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("TEST_MESSAGE.9[test9],0[test0],1[test1],2[test2],3[test3],4[test4],5[test5],6[test6],7[test7],8[test8]"));
    }

    /**
     * HTMLエスケープ指定に応じたメッセージの変数設定をテストする。
     * 常にHTMLエスケープされない。
     */
    @Test
    public void testValForHtmlEscape() throws Exception {

        String actual;

        target.setVar("testMsg");
        target.setMessageId("TEST0002");

        /************************************************************
        htmlEscape属性の指定がない場合。
        ************************************************************/

        ThreadContext.setLanguage(new Locale("ja")); // スレッドコンテキストは日本語とする。

        TagTestUtil.clearOutput(pageContext);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("<div id=\"id\" style='style'>\nテスト\n</div>"));

        /************************************************************
        htmlEscape属性を指定した場合。
        ************************************************************/

        ThreadContext.setLanguage(new Locale("en")); // スレッドコンテキストは英語とする。

        // htmlEscape属性 = true
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(true);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("<div id=\"id\" style='style'>\nTEST\n</div>"));

        // htmlEscape属性 = false
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(false);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("<div id=\"id\" style='style'>\nTEST\n</div>"));

        // htmlEscape属性 = true かつ withHtmlFormat属性 = true
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(true);
        target.setWithHtmlFormat(true);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("<div id=\"id\" style='style'>\nTEST\n</div>"));

        // htmlEscape属性 = true かつ withHtmlFormat属性 = false
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(true);
        target.setWithHtmlFormat(false);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("<div id=\"id\" style='style'>\nTEST\n</div>"));

        // htmlEscape属性 = false かつ withHtmlFormat属性 = true
        TagTestUtil.clearOutput(pageContext);

        target.setHtmlEscape(false);
        target.setWithHtmlFormat(true);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(pageContext.getAttribute("testMsg", PageContext.REQUEST_SCOPE).toString(),
                   is("<div id=\"id\" style='style'>\nTEST\n</div>"));
    }

    private void setOptions(MessageTag tag) {
        for (int i = 0; i < 10; i++) {
            ObjectUtil.setProperty(tag, "option" + i, "test" + i);
        }
    }
}
