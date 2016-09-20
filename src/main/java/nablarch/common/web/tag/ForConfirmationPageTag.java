package nablarch.common.web.tag;

import javax.servlet.jsp.JspException;

/**
 * 確認画面向けの表示内容を出力するクラス。
 * <pre>
 * 入力画面と確認画面を共通化したJSPにおいて使用する。
 * </pre>
 * @author Kiyohito Itoh
 */
public class ForConfirmationPageTag extends CustomTagSupport {

    /**
     * {@inheritDoc}<br>
     * 確認画面の場合のみこのタグのボディを評価する。
     */
    public int doStartTag() throws JspException {
        return TagUtil.isConfirmationPage(pageContext) ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }
}
