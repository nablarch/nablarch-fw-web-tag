package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.junit.Test;

public class PrettyPrintTagTest extends TagTestSupport<PrettyPrintTag> {
    
    public static final class Bean {
        private String text;
        public Bean(String text) {
            this.text = text;
        }
        public String getText() {
            return text;
        }
        
    }

    public PrettyPrintTagTest() {
        super(new PrettyPrintTag());
    }

    @Test
    public void testWithDefaultPermittedTag() throws Exception {
        Bean bean = new Bean("<script>hoge</script>");
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("bean", bean);
        target.setName("bean.text");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        String actual = TagTestUtil.getOutput(pageContext);
        TagTestUtil.assertTag(actual, "&lt;script&gt;hoge&lt;/script&gt;", " ");
    }
}
