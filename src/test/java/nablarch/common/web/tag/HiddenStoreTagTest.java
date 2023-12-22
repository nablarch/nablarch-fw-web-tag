package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.Tag;

import nablarch.core.util.Builder;

import org.junit.Test;

/**
 * {@link HiddenStoreTag}のテストクラス。
 */
public class HiddenStoreTagTest extends TagTestSupport<HiddenStoreTag> {

    public HiddenStoreTagTest() {
        super(new HiddenStoreTag());
    }

    /**
     * リクエストスコープに設定されている値がvalue属性に出力されることを確認。
     *
     * @throws Exception
     */
    @Test
    public void test_requestScope() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.setAttribute("name_test", "scope_value", PageContext.REQUEST_SCOPE);
        pageContext.getMockReq().getParams().put("name_test", new String[]{"request_parameter_value"});

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"hidden\"",
                "name=\"name_test\"",
                "value=\"scope_value\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    /**
     * サロゲートペアを扱うテストケース。
     * @throws Exception
     */
    @Test
    public void test_requestScopeSurrogatepair() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.setAttribute("name_test", "🙊🙊🙊", PageContext.REQUEST_SCOPE);
        pageContext.getMockReq().getParams().put("name_test", new String[]{"request_parameter_value"});

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"hidden\"",
                "name=\"name_test\"",
                "value=\"🙊🙊🙊\"",
                "/>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    /**
     * リクエストスコープに値が設定されておらず、
     * 別のスコープに値が設定されている場合にタグが出力されないことを確認
     *
     * @throws Exception
     */
    @Test
    public void test_sessionScope() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.setAttribute("name_test", "scope_value", PageContext.SESSION_SCOPE);

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(formContext.getInputNames().size(), is(0));
    }

    /**
     * リクエストスコープに値が設定されておらず、
     * リクエストパラメータに値が設定されている場合にタグが出力されないことを確認
     *
     * @throws Exception
     */
    @Test
    public void test_requestParameter() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("name_test", new String[]{"request_parameter_value"});

        // input
        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
        assertThat(formContext.getInputNames().size(), is(0));
    }

    /**
     * Formタグ内に定義されていない場合（FormContextが設定されていない場合）に、
     * 例外が送出されることを確認。
     */
    @Test
    public void test_notChildOfForm() throws Exception {

        pageContext.setAttribute("name_test", "value_test");

        // input
        target.setName("name_test");

        try {
            target.doStartTag();
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("invalid location of the hiddenStore tag. the hiddenStore tag must locate in the form tag."));
        }
    }
}
