package nablarch.common.web.tag;

import jakarta.servlet.jsp.JspException;

/**
 * セキュアハンドラがCSP向けのnonceを生成した場合にnonceを出力するクラス
 */
public class CspNonceTag extends HtmlTagSupport {
    /**
     * nonceを出力する際に nonce- をprefixとして付与するか否か。ポリシーのsrcとして使用することを想定
     */
    private Boolean sourceFormat = false;

    /**
     * nonceを出力する際に nonce- をprefixとして付与するか否か
     *
     * @return nonceを出力する際に nonce- をprefixとして付与するか否か
     */
    public Boolean getSourceFormat() {
        return sourceFormat;
    }

    /**
     * nonceを出力する際に nonce- をprefixとして付与するか否か。
     * デフォルトは{@code false}で出力しない
     *
     * @param sourceFormat nonceを出力する際に nonce- をprefixとして付与するか否か
     */
    public void setSourceFormat(Boolean sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    /**
     * {@inheritDoc}
     * <br>
     * セキュアハンドラがCSP向けのnonceを生成している場合（リクエストスコープにnonceが設定されている場合）に
     * nonceを出力する。{@code sourceFormat}プロパティが{@code true}の場合は、ポリシー向けに nonce- を
     * prefixとして付与して出力する
     */
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
