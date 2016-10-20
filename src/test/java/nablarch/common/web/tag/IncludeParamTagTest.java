package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.jsp.tagext.Tag;

import org.junit.Test;

/**
 * {@link IncludeParamTag}のテスト。
 * @author Kiyohito Itoh
 */
public class IncludeParamTagTest extends TagTestSupport<IncludeParamTag> {

    public IncludeParamTagTest() {
        super(new IncludeParamTag());
    }

    /**
     * 不正なロケーションで使用された場合のテスト。
     */
    @Test
    public void testInvalidLocation() throws Exception {

        IncludeContext.setIncludeContext(pageContext, null);

        try {
            target.doStartTag();
            fail("must throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(),
                       is("invalid location of the includeParam tag. "
                        + "the includeParam tag must locate in the include tag."));
        }
    }

    /**
     * name属性を指定した場合のテスト。
     */
    @Test
    public void testInputPageUsingName() throws Exception {

        IncludeContext includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"value_test"});

        // nablarch
        target.setName("entity.bbb");
        target.setParamName("paramName_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(includeContext.getParams().size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").get(0), is("value_test"));
    }

    /**
     * name属性に紐づく値がBigDecimalの場合、指数表記にならないこと。
     * @throws Exception
     */
    @Test
    public void testInputPageUsingNameBigDecimal() throws Exception {

        IncludeContext includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);
        pageContext.setAttribute("decimal", new BigDecimal("0.0000000001"));

        // nablarch
        target.setName("decimal");
        target.setParamName("paramName_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(includeContext.getParams().size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").get(0), is("0.0000000001"));
    }

    /**
     * value属性を指定した場合のテスト。
     */
    @Test
    public void testInputPageUsingValue() throws Exception {

        IncludeContext includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);

        // nablarch
        target.setValue("value_test");
        target.setParamName("paramName_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(includeContext.getParams().size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").get(0), is("value_test"));
    }

    /**
     * value属性がBigDecimalの場合、指数表記にならないこと
     */
    @Test
    public void testInputPageUsingValueBigDecimal() throws Exception {

        IncludeContext includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);

        // nablarch
        target.setValue(new BigDecimal("0.0000000001"));
        target.setParamName("paramName_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(includeContext.getParams().size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").get(0), is("0.0000000001"));
    }

    /**
     * name属性とvalue属性の指定が不正な場合のテスト。
     */
    @Test
    public void testInputPageInvalidAttribute() throws Exception {

        IncludeContext includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);

        // nablarch
        // name = null and value = null
        target.setParamName("paramName_test");

        try {
            target.doStartTag();
            fail("must throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(),
                       is("name and value was invalid. must specify either name or value. name = [null], value = [null]"));
        }

        // nablarch
        target.setName("entity.bbb");
        target.setValue("value_test");

        try {
            target.doStartTag();
            fail("must throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(),
                       is("name and value was invalid. must specify either name or value. name = [entity.bbb], value = [value_test]"));
        }
    }

    @Test
    public void testInputPageUsingNameWithHtml() throws Exception {

        IncludeContext includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);

        pageContext.getMockReq().getParams().put("entity.bbb" + TagTestUtil.HTML, new String[] {"value_test" + TagTestUtil.HTML});
        
        // nablarch
        target.setName("entity.bbb" + TagTestUtil.HTML);
        target.setParamName("paramName_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(includeContext.getParams().size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test" + TagTestUtil.HTML).size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test" + TagTestUtil.HTML).get(0), is("value_test" + TagTestUtil.HTML));
    }

    @Test
    public void testConfirmationPageUsingName() throws Exception {

        IncludeContext includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"value_test"});

        // nablarch
        target.setName("entity.bbb");
        target.setParamName("paramName_test");

        TagUtil.setConfirmationPage(pageContext);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(includeContext.getParams().size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").get(0), is("value_test"));
    }

    @Test
    public void testInputPageUsingNameWithoutValue() throws Exception {

        IncludeContext includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);

        // nablarch
        target.setName("entity.bbb");
        target.setParamName("paramName_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(includeContext.getParams().size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").get(0), is(""));
    }

    /**
     * 多値でも使用できること。
     * 配列またはCollection限定。
     */
    @Test
    public void testConfirmationPageUsingNameForMultiValues() throws Exception {

        IncludeContext includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);

        String actual;
        String expected;

        /**********************************************************
        配列の場合
        **********************************************************/
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"test1", "test2", "test3"});

        // nablarch
        target.setName("entity.bbb");
        target.setParamName("paramName_test");

        TagUtil.setConfirmationPage(pageContext);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(includeContext.getParams().size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").size(), is(3));
        for (int i = 0; i < 3; i++) {
            assertThat(includeContext.getParams().get("paramName_test").get(i), is("test" + (i + 1)));
        }

        /**********************************************************
        リストの場合
        **********************************************************/

        includeContext = new IncludeContext();
        IncludeContext.setIncludeContext(pageContext, includeContext);

        pageContext.setAttribute("entity",
                new HashMap<String, Object>() {{ put("bbb", Arrays.asList(new String[] {"val1", "val2", "val3"}));}});

        // nablarch
        target.setName("entity.bbb");
        target.setParamName("paramName_test");

        TagUtil.setConfirmationPage(pageContext);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(includeContext.getParams().size(), is(1));
        assertThat(includeContext.getParams().get("paramName_test").size(), is(3));
        for (int i = 0; i < 3; i++) {
            assertThat(includeContext.getParams().get("paramName_test").get(i), is("val" + (i + 1)));
        }
    }
}
