package nablarch.common.web.tag;

import javax.servlet.jsp.JspException;

public class CspNonceTag extends HtmlTagSupport {
    private Boolean sourceFormat;

    public Boolean getSourceFormat() {
        return sourceFormat;
    }

    public void setSourceFormat(Boolean sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    @Override
    public int doStartTag() throws JspException {
        if (TagUtil.hasCspNonce(pageContext)) {
            String outputNonce;

            if (sourceFormat != null && sourceFormat) {
                outputNonce = "nonce-" + TagUtil.getCspNonce(pageContext);
            } else {
                outputNonce = TagUtil.getCspNonce(pageContext);
            }

            TagUtil.print(pageContext, TagUtil.escapeHtml(outputNonce));
        }

        return SKIP_BODY;
    }
}
