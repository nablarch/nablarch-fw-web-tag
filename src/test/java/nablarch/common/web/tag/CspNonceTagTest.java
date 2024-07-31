package nablarch.common.web.tag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.Tag;

import nablarch.fw.web.handler.SecureHandler;
import org.junit.Test;

/**
 * {@link CspNonceTag}のテスト。
 */
public class CspNonceTagTest extends TagTestSupport<CspNonceTag> {
    public CspNonceTagTest() {
        super(new CspNonceTag());
    }

    /**
     * nonceがリクエストスコープにない場合には、出力が空になることを確認する。
     *
     * @throws JspException
     */
    @Test
    public void noCspNonce() throws JspException {
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    /**
     * nonceがリクエストスコープにある場合には、nonceが出力されることを確認する。
     *
     * @throws JspException
     */
    @Test
    public void hasCspNonce() throws JspException {
        pageContext.setAttribute(SecureHandler.CSP_NONCE_KEY, "abcde", PageContext.REQUEST_SCOPE);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "abcde";
        TagTestUtil.assertTag(actual, expected, " ");
    }

    /**
     * nonceがリクエストスコープにあり、{@code sourceFormat}を{@code true}にした場合には、
     * nonce- prefix付きでnonceが出力されることを確認する。
     *
     * @throws JspException
     */
    @Test
    public void hasCspNonceSourceFormat() throws JspException {
        target.setSourceFormat(true);

        pageContext.setAttribute(SecureHandler.CSP_NONCE_KEY, "abcde", PageContext.REQUEST_SCOPE);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "nonce-abcde";
        TagTestUtil.assertTag(actual, expected, " ");
    }
}