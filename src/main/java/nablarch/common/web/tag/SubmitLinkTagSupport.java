package nablarch.common.web.tag;

import nablarch.common.util.WebRequestUtil;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import java.io.IOException;

/**
 * サブミット制御を行うaタグを出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class SubmitLinkTagSupport extends FocusAttributesTagSupport implements BodyTag {

    /** サブミット先のURI */
    private String uri;

    /** URIをhttpsにするか否か */
    private Boolean secure = null;

    /** {@link BodyContent} */
    private BodyContent bodyContent;

    /** カスタムタグが生成するsubmit関数の出力を抑制するか否か。抑制する場合は{@code true} */
    private boolean suppressCallNablarchSubmit = false;

    /**
     * サブミット先のURIを設定する。
     * @param uri サブミット先のURI
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * URIをhttpsにするか否かを設定する。
     * @param secure httpsにする場合はtrue、しない場合はfalse。
     */
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    /**
     * カスタムタグが生成するsubmit関数の出力を抑制するか否かを設定する。
     * 抑制する場合は{@code true}。
     *
     * @param suppressCallNablarchSubmit カスタムタグが生成するsubmit関数の出力を抑制するか否か
     */
    public void setSuppressCallNablarchSubmit(boolean suppressCallNablarchSubmit) {
        this.suppressCallNablarchSubmit = suppressCallNablarchSubmit;
    }

    /**
     * XHTMLのname属性を設定する。
     * @param name XHTMLのname属性
     */
    public void setName(String name) {
        getAttributes().put(HtmlAttribute.NAME, name);
    }

    /**
     * XHTMLのshape属性を設定する。
     * @param shape XHTMLのshape属性
     */
    public void setShape(String shape) {
        getAttributes().put(HtmlAttribute.SHAPE, shape);
    }

    /**
     * XHTMLのcoords属性を設定する。
     * @param coords XHTMLのcoords属性
     */
    public void setCoords(String coords) {
        getAttributes().put(HtmlAttribute.COORDS, coords);
    }

    /** 表示制御を行う場合の表示方法 */
    private DisplayMethod displayMethod;

    /**
     * 表示制御を行う場合の表示方法を設定する。
     *
     * @param displayMethod 表示制御を行う場合の表示方法
     */
    public void setDisplayMethod(String displayMethod) {

        this.displayMethod = DisplayMethod.getDisplayMethod(displayMethod);
    }

    /**
     * {@inheritDoc}
     *
     * <pre>
     * onclick属性にサブミット制御を行うJavaScript関数を設定した開始タグを出力する。
     * href属性に指定されたサブミット先のURIを設定する。
     * 属性はHTMLエスケープして出力する。
     * 認可や開閉局の状態に応じて、タグの表示方法を切り替える。切り替え方法は非表示、非活性、通常表示のいずれかである。
     * ここで非活性とは、リンクを解除してラベルのみを表示することである。
     * </pre>
     */
    public int doStartTag() throws JspException {
        if (!TagUtil.jsSupported(pageContext)) {
           throw new JspException(
             "Without javascript, <n:submitLink> tags will not work properly."
           + " Use <n:submit type='submit'> tags if you needs some parameter transferred in request body,"
           + " or use <n:a> tags if you want to send a simple GET request."
           );
        }

        checkChildElementsOfForm();

        String tagName = "a";
        String requestId = WebRequestUtil.getRequestId(uri);
        if (requestId == null) {
            // サブミット制御では、リクエストIDがnullになるuriを許容しない。
            throw new JspException("REQUEST_ID was not in the requestPath."
                    + " Make sure that a REQUEST_ID is included in the requestPath"
                    + " or use <n:a> a tag if you want to send a GET request."
                    + " uri = [" + uri + "]");
        }
        String encodedUri = TagUtil.encodeUri(pageContext, uri, secure);
        getAttributes().put(HtmlAttribute.HREF, encodedUri);
        DisplayMethod displayMethodResult = TagUtil.getDisplayMethod(requestId, displayMethod);
        setSubmissionInfoToFormContext(requestId, encodedUri, displayMethodResult);

        // サブミット情報を追加した後にスクリプトの生成を行う
        TagUtil.registerOnclickForSubmission(pageContext, tagName, getAttributes(), suppressCallNablarchSubmit);

        switch (displayMethodResult) {
        case DISABLED:
            return isSubmitLinkDisabledJspSpecified()
                    ? EVAL_BODY_BUFFERED
                    : EVAL_BODY_INCLUDE;
        case NODISPLAY:
            return SKIP_BODY;
        default:
            TagUtil.print(pageContext, TagUtil.createStartTag(tagName, getAttributes()));
            return EVAL_BODY_INCLUDE;
        }
    }

    /** {@inheritDoc} */
    public void doInitBody() throws JspException {
    }

    /** {@inheritDoc} */
    public void setBodyContent(BodyContent bodyContent) {
        this.bodyContent = bodyContent;
    }

    /** ボディ部をJSPに引き継ぐ為のキー */
    private static final String BODY_CONTENT_KEY = "nablarch_link_body";

    /** 属性をJSPに引き継ぐ際に使用するのキーのプレフィクス */
    private static final String LINK_ATTRIBUTES_KEY_PREFIX = "nablarch_link_attributes_";

    /**
     * フォームコンテキスにサブミット情報を設定する。
     * @param requestId リクエストID
     * @param encodedUri サブミット先のURI(URLエンコード済み)
     * @param displayMethod 表示制御方法
     */
    protected abstract void setSubmissionInfoToFormContext(String requestId, String encodedUri, DisplayMethod displayMethod);

    /**
     * {@inheritDoc}
     * <pre>
     * 閉じタグを出力する。
     * </pre>
     */
    public int doEndTag() throws JspException {
        DisplayMethod displayMethod = TagUtil.getFormContext(pageContext)
                                              .getCurrentSubmissionInfo()
                                              .getDisplayMethod();
        switch (displayMethod) {
        case NORMAL:
            TagUtil.print(pageContext, TagUtil.createEndTag("a"));
            break;
        case DISABLED:
            if (isSubmitLinkDisabledJspSpecified()) {
                renderCustomJspForDisabled();
            }
            break;
        default:
            break;
        }
        TagUtil.getFormContext(pageContext).setCurrentSubmissionInfo(null);
        return EVAL_PAGE;
    }

    /**
     * 「非活性」時に使用するJSPが設定されているかどうかを判定する。
     *
     * @return 設定されている場合、真
     */
    private static boolean isSubmitLinkDisabledJspSpecified() {
        return TagUtil.getCustomTagConfig().isSubmitLinkDisabledJspSpecified();
    }


    /**
     * インクルードするJSPのURLを取得する。
     *
     * @return URL
     */
    private static String getJspUrlToInclude() {
        return TagUtil.getCustomTagConfig().getSubmitLinkDisabledJsp();
    }

    /**
     * 非活性描画用JSPを描画する。
     *
     * @throws JspException 予期しない例外
     */
    private void renderCustomJspForDisabled() throws JspException {
        String body = bodyContent.getString();
        pageContext.getRequest().setAttribute(BODY_CONTENT_KEY, body);
        
        for (HtmlAttribute attribute : getAttributes().keys()) {
            // nablarch_link_attributes_<タグの属性名> で全ての属性をリクエストスコープにセットする。
            pageContext.getRequest().setAttribute(
                    LINK_ATTRIBUTES_KEY_PREFIX + attribute.getXHtmlName(),
                    getAttributes().get(attribute));
        }
        try {
            String url = getJspUrlToInclude();
            pageContext.include(url);
        } catch (IOException e) {
            throw new JspException(e);
        } catch (ServletException e) {
            throw new JspException(e);
        } finally {
            for (HtmlAttribute attribute : getAttributes().keys()) {
                // nablarch_link_attributes_<タグの属性名> で全ての属性をリクエストスコープから取り除く
                pageContext.getRequest().removeAttribute(
                        LINK_ATTRIBUTES_KEY_PREFIX + attribute.getXHtmlName());
            }
            pageContext.getRequest().removeAttribute(BODY_CONTENT_KEY);
        }
    }
}
