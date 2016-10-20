package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.MockPageContext;

import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class ConfirmationPageTagTest extends TagTestSupport<ConfirmationPageTag> {
    
    public ConfirmationPageTagTest() {
        super(new ConfirmationPageTag());
    }
    
    @Test
    public void testExceptionOnForward() throws Exception {
        
        target.setPath("test");
        
        target.setPageContext(new MockPageContext() {
            public void forward(String relativeUrlPath)
                    throws ServletException, IOException {
                throw new ServletException("test1");
            }
        });
        
        try {
            target.doStartTag();
            fail("must throw ServletException.");
        } catch (JspException e) {
            assertTrue(e.getCause() instanceof ServletException);
            assertThat(e.getCause().getMessage(), is("test1"));
        }
        
        target.setPageContext(new MockPageContext() {
            public void forward(String relativeUrlPath)
                    throws ServletException, IOException {
                throw new IOException("test2");
            }
        });
        
        try {
            target.doStartTag();
            fail("must throw ServletException.");
        } catch (JspException e) {
            assertTrue(e.getCause() instanceof IOException);
            assertThat(e.getCause().getMessage(), is("test2"));
        }
    }
    
    @Test
    public void testAllSetting() throws Exception {
        
        target.setPath("./W001.jsp");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        assertTrue(TagUtil.isConfirmationPage(pageContext));
        assertThat(pageContext.getForwardPath(), is("./W001.jsp"));
    }
    
    @Test
    public void testDefault() throws Exception {
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        assertTrue(TagUtil.isConfirmationPage(pageContext));
        assertNull(pageContext.getForwardPath());
    }
}
