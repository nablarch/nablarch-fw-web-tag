package nablarch.common.web.tag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.junit.Test;

public class CspNonceTagTest extends TagTestSupport<CspNonceTag> {
    public CspNonceTagTest() {
        super(new CspNonceTag());
    }

    @Test
    public void noCspNonce() throws JspException {
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void hasCspNonce() throws JspException {
        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "abcde";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    @Test
    public void hasCspNonceSourceFormat() throws JspException {
        target.setSourceFormat(true);

        pageContext.setAttribute(CustomTagConfig.CSP_NONCE_KEY, "abcde");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "nonce-abcde";
        TagTestUtil.assertTag(actual, expected, " ");
    }
}