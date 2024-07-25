package nablarch.common.web.tag;

import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspException;

import nablarch.common.util.WebRequestUtil;

/**
 * サブミット制御を行うbuttonタグを出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class ButtonTagSupport extends FocusAttributesTagSupport {

    /** type属性の値 */
    private static final List<String> TYPE = Arrays.asList("submit", "button", "reset");

    /** サブミット先のURI */
    private String uri;

    /** URIをhttpsにするか否か */
    private Boolean secure = null;

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
     * XHTMLのvalue属性を設定する。
     * @param value XHTMLのvalue属性
     */
    public void setValue(String value) {
        getAttributes().put(HtmlAttribute.VALUE, value);
    }

    /**
     * XHTMLのtype属性を設定する。
     * @param type XHTMLのtype属性
     */
    public void setType(String type) {
        if (type == null || !TYPE.contains(type)) {
            throw new IllegalArgumentException(
                String.format("type was invalid. type must specify the following values. values = %s type = [%s]", TYPE, type));
        }
        getAttributes().put(HtmlAttribute.TYPE, type);
    }

    /**
     * XHTMLのdisabled属性を設定する。
     * @param disabled XHTMLのdisabled属性
     */
    public void setDisabled(boolean disabled) {
        getAttributes().put(HtmlAttribute.DISABLED, disabled);
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
     * HTML5のautofocus属性を設定する。
     * @param autofocus HTML5のautofocus属性
     */
    public void setAutofocus(boolean autofocus) {
        getAttributes().put(HtmlAttribute.AUTOFOCUS, autofocus);
    }

    /**
     * {@inheritDoc}
     * <pre>
     * onclick属性にサブミット制御を行うJavaScript関数を設定した開始タグを出力する。
     * 属性はHTMLエスケープして出力する。
     * 認可や開閉局の状態に応じて、タグの表示方法を切り替える。切り替え方法は非表示、非活性、通常表示のいずれかである。
     * </pre>
     */
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();

        String tagName = "button";
        String requestId = WebRequestUtil.getRequestId(uri);
        String encodedUri = TagUtil.encodeUri(pageContext, uri, secure);

        DisplayMethod displayMethodResult = TagUtil.getDisplayMethod(requestId, displayMethod);
        setSubmissionInfoToFormContext(requestId, encodedUri, displayMethodResult);

        // サブミット情報を追加した後にスクリプトの生成を行う
        TagUtil.registerOnclickForSubmission(pageContext, tagName, getAttributes(), suppressCallNablarchSubmit);

        if (DisplayMethod.DISABLED == displayMethodResult) {
            getAttributes().put(HtmlAttribute.DISABLED, true);
        } else if (DisplayMethod.NODISPLAY == displayMethodResult) {
            return SKIP_BODY;
        }

        TagUtil.print(pageContext, TagUtil.createStartTag(tagName, getAttributes()));

        return EVAL_BODY_INCLUDE;
    }

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
        DisplayMethod displayMethodResult = TagUtil.getFormContext(pageContext)
                                                   .getCurrentSubmissionInfo()
                                                   .getDisplayMethod();
        if (displayMethodResult != DisplayMethod.NODISPLAY) {
            TagUtil.print(pageContext, TagUtil.createEndTag("button"));
        }
        TagUtil.getFormContext(pageContext).setCurrentSubmissionInfo(null);
        return EVAL_PAGE;
    }
}
