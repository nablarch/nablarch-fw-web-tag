package nablarch.common.web.tag;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.JspException;

/**
 * JSPが確認画面であることを示すクラス。<br>
 * このタグが指定されると、入力項目のカスタムタグは確認画面用の出力を行う。<br>
 * このタグに入力画面へのパスを指定することで、入力画面と確認画面を共通化する。
 * @author Kiyohito Itoh
 */
public class ConfirmationPageTag extends CustomTagSupport {

    /** フォワード先のパス */
    private String path;
    
    /**
     * フォワード先のパスを設定する。
     * @param path フォワード先のパス
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * JSPが確認画面であることを示すフラグをリクエストスコープに設定する。
     * パスが指定されている場合は、フォワードを行う。
     * {@link TagUtil#getResourcePathForLanguage(jakarta.servlet.jsp.PageContext, String)}メソッドを呼び出し、
     * 指定されたパスを言語対応のリソースパスに変換してからフォワードを行う。
     * </pre>
     */
    public int doStartTag() throws JspException {
        TagUtil.setConfirmationPage(pageContext);
        if (path != null) {
            try {
                pageContext.forward(TagUtil.getResourcePathForLanguage(pageContext, path));
            } catch (ServletException e) {
                throw new JspException(e);
            } catch (IOException e) {
                throw new JspException(e);
            }
        }
        return SKIP_BODY;
    }
}
