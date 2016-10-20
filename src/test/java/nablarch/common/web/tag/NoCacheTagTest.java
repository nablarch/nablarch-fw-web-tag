package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.servlet.jsp.tagext.Tag;

import nablarch.core.util.Builder;

import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class NoCacheTagTest extends TagTestSupport<NoCacheTag> {

    public NoCacheTagTest() {
        super(new NoCacheTag());
    }

    @Test
    public void testInformationPage() throws Exception {
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<meta http-equiv=\"pragma\" content=\"no-cache\">",
                "<meta http-equiv=\"cache-control\" content=\"no-cache\">",
                "<meta http-equiv=\"expires\" content=\"0\">")
                .replace(Builder.LS, TagUtil.getCustomTagConfig().getLineSeparator());
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(pageContext.getMockRes().getHeader("Expires").get(0), is("0"));
        assertThat(pageContext.getMockRes().getHeader("Cache-Control").get(0), is("no-store, no-cache, must-revalidate"));
        assertThat(pageContext.getMockRes().getHeader("Cache-Control").get(1), is("post-check=0, pre-check=0"));
        assertThat(pageContext.getMockRes().getHeader("Pragma").get(0), is("no-cache"));
    }
    
    @Test
    public void testConfirmationPage() throws Exception {
        
        TagUtil.setConfirmationPage(pageContext);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<meta http-equiv=\"pragma\" content=\"no-cache\">",
                "<meta http-equiv=\"cache-control\" content=\"no-cache\">",
                "<meta http-equiv=\"expires\" content=\"0\">")
                .replace(Builder.LS, TagUtil.getCustomTagConfig().getLineSeparator());
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(pageContext.getMockRes().getHeader("Expires").get(0), is("0"));
        assertThat(pageContext.getMockRes().getHeader("Cache-Control").get(0), is("no-store, no-cache, must-revalidate"));
        assertThat(pageContext.getMockRes().getHeader("Cache-Control").get(1), is("post-check=0, pre-check=0"));
        assertThat(pageContext.getMockRes().getHeader("Pragma").get(0), is("no-cache"));
    }
}
