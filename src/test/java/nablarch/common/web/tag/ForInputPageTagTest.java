package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import jakarta.servlet.jsp.tagext.Tag;

import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class ForInputPageTagTest extends TagTestSupport<ForInputPageTag> {
    
    public ForInputPageTagTest() {
        super(new ForInputPageTag());
    }
    
    @Test
    public void testInputPage() throws Exception {
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
    }
    
    @Test
    public void testConfirmationPage() throws Exception {
        
        TagUtil.setConfirmationPage(pageContext);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
    }
}
