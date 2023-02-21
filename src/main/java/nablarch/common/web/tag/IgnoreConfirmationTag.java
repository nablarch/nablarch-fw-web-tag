package nablarch.common.web.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import nablarch.fw.ExecutionContext;

/**
 * JSPの画面状態が確認画面である場合に、部分的に確認画面の画面状態を無効化するクラス。
 * <p/>
 * このタグにより囲まれた範囲にある入力項目のカスタムタグは、常に入力画面用の出力を行う。
 * このタグは内部状態の管理にリクエストスコープを使用するため、入れ子での使用を禁止する。
 * 
 * @author Kiyohito Itoh
 */
public class IgnoreConfirmationTag extends CustomTagSupport {

    /** 元の設定状態であるJSPの画面状態(入力画面または確認画面)をリクエストスコープに退避する際に使用するキー */
    private static final String KEY_ORIGINAL_CONFIRMATION_PAGE
            = ExecutionContext.FW_PREFIX + "inputPageContent.originalConfirmationPage";

    /**
     * {@inheritDoc}
     * <pre>
     * 元の設定状態であるJSPの画面状態(入力画面または確認画面)を退避し、
     * JSPが入力画面であることを示すフラグをリクエストスコープに設定する。
     * </pre>
     */
    public int doStartTag() throws JspException {

        if (removeOriginalConfirmationPage(pageContext) != null) {
            throw new IllegalStateException(
                "invalid location of the ignoreConfirmation tag. the ignoreConfirmation tag cannot be nested.");
        }

        setOriginalConfirmationPage(pageContext, TagUtil.isConfirmationPage(pageContext));
        TagUtil.setInputPage(pageContext);

        return EVAL_BODY_INCLUDE;
    }

    /**
     * {@inheritDoc}
     * <pre>
     * 退避しておいた元の設定状態であるJSPの画面状態(入力画面または確認画面)
     * をリクエストスコープに設定する。
     * </pre>
     */
    public int doEndTag() throws JspException {

        if (removeOriginalConfirmationPage(pageContext)) {
            TagUtil.setConfirmationPage(pageContext);
        } else {
            TagUtil.setInputPage(pageContext);
        }

        return EVAL_PAGE;
    }

    /**
     * 元の設定状態であるJSPの画面状態(入力画面または確認画面)をリクエストスコープに設定する。
     * @param pageContext ページコンテキスト
     * @param confirmationPage 確認画面の場合はtrue、確認画面でない場合はfalse
     */
    protected void setOriginalConfirmationPage(PageContext pageContext, Boolean confirmationPage) {
        pageContext.setAttribute(KEY_ORIGINAL_CONFIRMATION_PAGE, confirmationPage, PageContext.REQUEST_SCOPE);
    }

    /**
     * 元の設定状態であるJSPの画面状態(入力画面または確認画面)をリクエストスコープから削除する。
     * @param pageContext ページコンテキスト
     * @return 削除した画面状態。確認画面の場合はtrue、確認画面でない場合はfalse。未設定の場合はnull
     */
    protected Boolean removeOriginalConfirmationPage(PageContext pageContext) {
        Object obj = pageContext.getAttribute(KEY_ORIGINAL_CONFIRMATION_PAGE, PageContext.REQUEST_SCOPE);
        pageContext.removeAttribute(KEY_ORIGINAL_CONFIRMATION_PAGE, PageContext.REQUEST_SCOPE);
        return (Boolean) obj;
    }
}
