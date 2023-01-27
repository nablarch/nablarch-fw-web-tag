package nablarch.common.web.tag;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;

import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.Tag;

import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class SetTagTest extends TagTestSupport<SetTag> {

    public SetTagTest() {
        super(new SetTag());
    }
    
    @Test
    public void testInputPageUsingName() throws Exception {
        
        String[] value = new String[] {"value_test"};
        pageContext.getMockReq().getParams().put("entity.bbb", value);
        
        // nablarch
        target.setName("entity.bbb");
        target.setVar("var_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE).toString(),
                   is("value_test"));
    }

    /**
     * サロゲートペアを扱うテストケース
     *
     * @throws Exception
     */
    @Test
    public void testInputPageUsingNameSurrogatepair() throws Exception {

        String[] value = new String[] {"🙊🙈🙉"};
        pageContext.getMockReq().getParams().put("entity.bbb", value);

        // nablarch
        target.setName("entity.bbb");
        target.setVar("𪛔𪛉𠀜");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(pageContext.getAttribute("𪛔𪛉𠀜", PageContext.REQUEST_SCOPE).toString(),
                is("🙊🙈🙉"));
    }

    @Test
    public void testInputPageUsingNameByNoSingleValue() throws Exception {
        
        String[] value = new String[] {"value_test"};
        pageContext.getMockReq().getParams().put("entity.bbb", value);
        
        // nablarch
        target.setName("entity.bbb");
        target.setVar("var_test");
        target.setBySingleValue(false);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        String[] actualValue = (String[]) pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE);
        assertThat(actualValue[0], is("value_test"));
    }
    
    @Test
    public void testInputPageUsingValue() throws Exception {
        
        // nablarch
        target.setValue("value_test");
        target.setVar("var_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE).toString(), is("value_test"));
    }
    
    @Test
    public void testInputPageInvalidAttribute() throws Exception {
        
        // nablarch
        // name = null and value = null
        target.setVar("var_test");
        
        try {
            target.doStartTag();
            fail("must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                       is("name and value was invalid. must specify either name or value. var = [var_test], name = [null], value = [null]"));
        }
        
        // nablarch
        target.setName("entity.bbb");
        target.setValue("value_test");
        
        try {
            target.doStartTag();
            fail("must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                       is("name and value was invalid. must specify either name or value. var = [var_test], name = [entity.bbb], value = [value_test]"));
        }
        
        // nablarch
        // invalid scope
        try {
            target.setScope("session");
            fail("must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                       is("scopeName was invalid. scopeName must specify the following values. values = [\"page\", \"request\"] scopeName = [session]"));
        }
    }
    
    @Test
    public void testInputPageUsingNameWithHtml() throws Exception {
        
        pageContext.getMockReq().getParams().put("entity.bbb" + TagTestUtil.HTML, new String[] {"value_test" + TagTestUtil.HTML});
        
        // nablarch
        target.setName("entity.bbb" + TagTestUtil.HTML);
        target.setVar("var_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(pageContext.getAttribute("var_test" + TagTestUtil.HTML, PageContext.REQUEST_SCOPE).toString(),
                   is("value_test" + TagTestUtil.HTML));
    }

    @Test
    public void testInputPageArrayWithNull() throws Exception {
        pageContext.getMockReq()
                   .getParams()
                   .put("entity.bbb", new String[] {null});

        // nablarch
        target.setName("entity.bbb");
        target.setVar("var_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE),
                is(nullValue()));

    }
    
    @Test
    public void testInputPageListWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("entity.bbb", Collections.singleton(null));

        // nablarch
        target.setName("entity.bbb");
        target.setVar("var_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE),
                is(nullValue()));

    }

    @Test
    public void testConfirmationPageUsingName() throws Exception {

        pageContext.getMockReq().getParams().put("entity.bbb", new String[] {"value_test"});
        
        // nablarch
        target.setName("entity.bbb");
        target.setVar("var_test");
        
        TagUtil.setConfirmationPage(pageContext);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE).toString(),
                   is("value_test"));
    }
    
    @Test
    public void testInputPageUsingNameWithoutValue() throws Exception {
        
        // nablarch
        target.setName("entity.bbb");
        target.setVar("var_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertNull(pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE));
    }

    /**
     * スコープ指定が正しく動作すること。
     */
    @Test
    public void testInputPageUsingScope() throws Exception {
        
        // デフォルトの場合
        target.setVar("default_test");
        target.setValue("default_value");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertNull(pageContext.getAttribute("default_test", PageContext.PAGE_SCOPE));
        assertThat(pageContext.getAttribute("default_test", PageContext.REQUEST_SCOPE).toString(), is("default_value"));

        // スコープにpageを指定した場合
        target.setVar("page_test");
        target.setValue("page_value");
        target.setScope("page");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(pageContext.getAttribute("page_test", PageContext.PAGE_SCOPE).toString(), is("page_value"));
        assertNull(pageContext.getAttribute("page_test", PageContext.REQUEST_SCOPE));

        // スコープにrequestを指定した場合
        target.setVar("request_test");
        target.setValue("request_value");
        target.setScope("request");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertNull(pageContext.getAttribute("request_test", PageContext.PAGE_SCOPE));
        assertThat(pageContext.getAttribute("request_test", PageContext.REQUEST_SCOPE).toString(), is("request_value"));
    }

    /**
     * setタグはいつでも上書き設定されること。
     */
    @Test
    public void testOverride() throws Exception {
        
        String[] value = new String[] {"value_test", "value_test2", null, null, "value_test3", null};
        
        for (int i = 0; i < value.length; i++) {
            
            pageContext.getMockReq().getParams().put("entity.bbb", new String[] { value[i] });
            
            target.setName("entity.bbb");
            target.setVar("var_test");
            
            assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
            assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
            
            Object o = pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE);
            if (value[i] != null) {
                assertThat(o.toString(), is(value[i]));
            } else {
                assertNull(o);
            }
        }
    }
    
    @Test
    public void testConfirmationPageArrayWithNull() throws Exception {
        pageContext.getMockReq()
                   .getParams()
                   .put("entity.bbb", new String[] {null});
        TagUtil.setConfirmationPage(pageContext);

        // nablarch
        target.setName("entity.bbb");
        target.setVar("var_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE),
                is(nullValue()));

    }

    @Test
    public void testConfirmationPageListWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("entity.bbb", Collections.singleton(null));
        TagUtil.setConfirmationPage(pageContext);

        // nablarch
        target.setName("entity.bbb");
        target.setVar("var_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(pageContext.getAttribute("var_test", PageContext.REQUEST_SCOPE),
                is(nullValue()));

    }
}
