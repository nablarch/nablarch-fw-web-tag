package nablarch.common.web.tag;

import jakarta.servlet.jsp.JspException;

/**
 * 入力画面向けの表示内容を出力するクラス。
 * <pre>
 * 入力画面と確認画面を共通化したJSPにおいて使用する。
 * </pre>
 * @author Kiyohito Itoh
 */
public class ForInputPageTag extends CustomTagSupport {

    /**
     * {@inheritDoc}<br>
     * 入力画面の場合のみこのタグのボディを評価する。
     */
    public int doStartTag() throws JspException {
        return TagUtil.isConfirmationPage(pageContext) ? SKIP_BODY : EVAL_BODY_INCLUDE;
    }
}
