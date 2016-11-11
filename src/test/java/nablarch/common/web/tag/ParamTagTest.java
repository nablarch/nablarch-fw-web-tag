package nablarch.common.web.tag;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.jsp.tagext.Tag;

import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.IsCollectionContaining;

import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class ParamTagTest extends TagTestSupport<ParamTag> {

    private FormContext formContext;
    private SubmissionInfo currentSubmissionInfo;
    
    public ParamTagTest() {
        super(new ParamTag());
    }
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        initFormContext("dummy");
    }

    private HtmlAttributes createTagNameof(String name) {
        HtmlAttributes tag = new HtmlAttributes();
        tag.put(HtmlAttribute.NAME, name);
        return tag;
    }    
    
    private void initFormContext(String name) {
        formContext = new FormContext(name);
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("dummy"), "./D0001", true, "D0001", null, null, DisplayMethod.NORMAL);
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"), "/R0001", true, "R0001", null, null, DisplayMethod.NORMAL);
        currentSubmissionInfo = formContext.getCurrentSubmissionInfo();
        TagUtil.setFormContext(pageContext, formContext);
    }
    
    @Test
    public void testInvalidLocation() throws Exception {
        
        String expectedMsg = "invalid location of the param tag. the param tag must locate in the tag to submit(submit or submitLink or button).";
        
        TagUtil.setFormContext(pageContext, null);
        
        try {
            target.doStartTag();
            fail("must throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is(expectedMsg));
        }
        
        TagUtil.setFormContext(pageContext, new FormContext("test_form_name"));
        
        try {
            target.doStartTag();
            fail("must throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is(expectedMsg));
        }
    }
    
    @Test
    public void testInputPageUsingName() throws Exception {
        
        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"value_test"});
        
        // nablarch
        target.setName("entity.bbb");
        target.setParamName("paramName_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").get(0), is("value_test"));
    }

    /**
     * name属性に紐づく値がBigDecimalの場合、指数表記にならないこと。
     * @throws Exception
     */
    @Test
    public void testInputPageUsingNameBigDecimal() throws Exception {

        pageContext.setAttribute("decimal", new BigDecimal("0.0000000001"));
        // nablarch
        target.setName("decimal");
        target.setParamName("paramName_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").get(0), is("0.0000000001"));
    }

    @Test
    public void testArrayWithNullValue() throws Exception {
        pageContext.setAttribute("array", new String[] {null});
        target.setName("array");
        target.setParamName("array_param");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));

        assertThat(currentSubmissionInfo.getParamsMap(), hasEntry(is("array_param"), contains("")));
    }

    @Test
    public void testMultiArrayWithNullValue() throws Exception {
        pageContext.setAttribute("array", new String[] {"a", null, "c"});
        target.setName("array");
        target.setParamName("array_param");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));

        assertThat(currentSubmissionInfo.getParamsMap(), hasEntry(is("array_param"), contains("a", "", "c")));
    }

    @Test
    public void testListWithNullValue() throws Exception {
        pageContext.setAttribute("list", Collections.singletonList(null));
        target.setName("list");
        target.setParamName("list_param");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));

        assertThat(currentSubmissionInfo.getParamsMap(), hasEntry(is("list_param"), contains("")));
    }

    @Test
    public void testMultiListWithNullValue() throws Exception {
        pageContext.setAttribute("list", Arrays.asList("あ", null, "う"));
        target.setName("list");
        target.setParamName("list_param");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));

        assertThat(currentSubmissionInfo.getParamsMap(), hasEntry(is("list_param"), contains("あ", "", "う")));
    }

    @Test
    public void testScopeValueIsNull() throws Exception {
        pageContext.setAttribute("list", Arrays.asList("あ", null, "う"));
        target.setName("not_found");
        target.setParamName("param");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));

        assertThat(currentSubmissionInfo.getParamsMap(), hasEntry(is("param"), contains("")));
    }

    @Test
    public void testInputPageUsingValue() throws Exception {
        
        // nablarch
        target.setValue("value_test");
        target.setParamName("paramName_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").get(0), is("value_test"));
    }

    /**
     * value属性がBigDecimalの場合、指数表記にならないこと
     * @throws Exception
     */
    @Test
    public void testInputPageUsingValueBigDecimal() throws Exception {

        // nablarch
        target.setValue(new BigDecimal("0.0000000001"));
        target.setParamName("paramName_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").get(0), is("0.0000000001"));
    }
    
    @Test
    public void testInputPageInvalidAttribute() throws Exception {
        
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
        
        pageContext.getMockReq().getParams().put("entity.bbb" + TagTestUtil.HTML, new String[] {"value_test" + TagTestUtil.HTML});
        
        // nablarch
        target.setName("entity.bbb" + TagTestUtil.HTML);
        target.setParamName("paramName_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test" + TagTestUtil.HTML).size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test" + TagTestUtil.HTML).get(0), is("value_test" + TagTestUtil.HTML));
    }

    @Test
    public void testConfirmationPageUsingName() throws Exception {

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

        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").get(0), is("value_test"));
    }
    
    @Test
    public void testInputPageUsingNameWithoutValue() throws Exception {
        
        // nablarch
        target.setName("entity.bbb");
        target.setParamName("paramName_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").get(0), is(""));
    }


    /**
     * 多値でも使用できること。
     * 配列またはCollection限定。
     */
    @Test
    public void testConfirmationPageUsingNameForMultiValues() throws Exception {
        
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

        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").size(), is(3));
        for (int i = 0; i < 3; i++) {
            assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").get(i), is("test" + (i + 1)));
        }

        /**********************************************************
        リストの場合
        **********************************************************/
        
        initFormContext("dummy2");
        
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

        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").size(), is(3));
        for (int i = 0; i < 3; i++) {
            assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").get(i), is("val" + (i + 1)));
        }

        /**********************************************************
        リストの場合(値がnullの場合)
        **********************************************************/
        
        initFormContext("dummy3");
        
        pageContext.setAttribute("entity",
                new HashMap<String, Object>() {{ put("bbb", Arrays.asList(new String[] {"", null, ""}));}});
        
        // nablarch
        target.setName("entity.bbb");
        target.setParamName("paramName_test");
        
        TagUtil.setConfirmationPage(pageContext);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(currentSubmissionInfo.getParamsMap().size(), is(1));
        assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").size(), is(3));
        for (int i = 0; i < 3; i++) {
            assertThat(currentSubmissionInfo.getParamsMap().get("paramName_test").get(i), is(""));
        }
    }
}
