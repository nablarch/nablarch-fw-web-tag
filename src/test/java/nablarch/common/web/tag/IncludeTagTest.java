package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.MockPageContext;

import org.junit.Test;

/**
 * {@link IncludeTag}のテスト。
 * @author Kiyohito Itoh
 */
public class IncludeTagTest extends TagTestSupport<IncludeTag> {

    public IncludeTagTest() {
        super(new IncludeTag());
    }

    /**
     * Servlet API呼び出し時に例外が発生した場合のテスト。
     * JspExceptionにラップして再送される。
     */
    @Test
    public void testExceptionOnInclude() throws Exception {

        target.setPath("test");

        target.setPageContext(new MockPageContext() {
            public void include(String relativeUrlPath) throws ServletException, IOException {
                throw new ServletException("test1");
            }
        });

        target.doStartTag();
        try {
            target.doEndTag();
            fail("must throw ServletException.");
        } catch (JspException e) {
            assertTrue(e.getCause() instanceof ServletException);
            assertThat(e.getCause().getMessage(), is("test1"));
        }

        target.setPageContext(new MockPageContext() {
            public void include(String relativeUrlPath) throws ServletException, IOException {
                throw new IOException("test2");
            }
        });

        target.doStartTag();
        try {
            target.doEndTag();
            fail("must throw ServletException.");
        } catch (JspException e) {
            assertTrue(e.getCause() instanceof IOException);
            assertThat(e.getCause().getMessage(), is("test2"));
        }
    }

    /**
     * すべての属性を指定する場合のテスト。
     */
    @Test
    public void testAllSetting() throws Exception {

        target.setPath("./W001.jsp");

        // パラメータなし

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        assertThat(pageContext.getIncludePath(), is("./W001.jsp"));

        // パラメータあり

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        addParam("aaa", "001");
        addParam("aaa", "002");
        addParam("bbb", "003");
        addParam("ccc", "0 0 4");
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        assertThat(pageContext.getIncludePath(), containsString("./W001.jsp?"));
        assertThat(pageContext.getIncludePath().split("&").length, is(4));
        assertThat(pageContext.getIncludePath(), containsString("aaa=001"));
        assertThat(pageContext.getIncludePath(), containsString("aaa=002"));
        assertThat(pageContext.getIncludePath(), containsString("bbb=003"));
        assertThat(pageContext.getIncludePath(), containsString("ccc=0+0+4"));
    }

    private void addParam(String name, String value) throws Exception {
        IncludeParamTag tag = new IncludeParamTag();
        tag.setPageContext(pageContext);
        tag.setParamName(name);
        tag.setValue(value);
        tag.doStartTag();
        tag.doEndTag();
    }
}
