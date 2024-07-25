package nablarch.common.web.tag;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import nablarch.common.util.WebRequestUtil;
import nablarch.fw.web.handler.KeitaiAccessHandler;
import nablarch.fw.web.servlet.UriUtil;

/**
 * サブミット制御を行うinputタグ(type="submit","button","image")を出力するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class SubmitTagSupport extends InputTagSupport {

    /** type属性の値 */
    private static final List<String> TYPE = Arrays.asList("submit", "button", "image");
    
    /** サブミット先のURI */
    private String uri;
    
    /** URIをhttpsにするか否か */
    private Boolean secure = null;

    /** カスタムタグが生成するデフォルトのsubmit関数呼び出しを抑制するか否か。抑制する場合は{@code true} */
    private boolean suppressDefaultSubmit = false;
    
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
     * カスタムタグが生成するデフォルトのsubmit関数呼び出しを抑制するか否かを設定する。
     * 抑制する場合は{@code true}。
     *
     * @param suppressDefaultSubmit カスタムタグが生成するデフォルトのsubmit関数呼び出しを抑制するか否か
     */
    public void setSuppressDefaultSubmit(boolean suppressDefaultSubmit) {
        this.suppressDefaultSubmit = suppressDefaultSubmit;
    }

    /**
     * XHTMLのtype属性を設定する。
     * @param type XHTMLのtype属性
     */
    public void setType(String type) {
        if (type == null || !TYPE.contains(type)) {
            throw new IllegalArgumentException(
                    String.format("type was invalid. type must specify the following values. values = %s type = [%s]",
                                  TYPE, type));
        }
        getAttributes().put(HtmlAttribute.TYPE, type);
    }
    
    /**
     * XHTMLのvalue属性を設定する。
     * @param value XHTMLのvalue属性
     */
    public void setValue(String value) {
        getAttributes().put(HtmlAttribute.VALUE, value);
    }
    
    /**
     * XHTMLのsrc属性を設定する。
     * @param src XHTMLのsrc属性
     */
    public void setSrc(String src) {
        getAttributes().put(HtmlAttribute.SRC, src);
    }
    
    /**
     * XHTMLのalt属性を設定する。
     * @param alt XHTMLのalt属性
     */
    public void setAlt(String alt) {
        getAttributes().put(HtmlAttribute.ALT, alt);
    }
    
    /**
     * XHTMLのusemap属性を設定する。
     * @param usemap XHTMLのusemap属性
     */
    public void setUsemap(String usemap) {
        getAttributes().put(HtmlAttribute.USEMAP, usemap);
    }
    
    /**
     * XHTMLのalign属性を設定する。
     * @param align XHTMLのalign属性
     */
    public void setAlign(String align) {
        getAttributes().put(HtmlAttribute.ALIGN, align);
    }
    
    /** 表示制御を行う場合の表示方法 */
    private DisplayMethod displayMethod;

    /**
     * 表示制御を行う場合の表示方法を設定する。
     * 
     * @param displayMethod 表示制御を行う場合の表示方法指定
     */
    public void setDisplayMethod(String displayMethod) {

        this.displayMethod = DisplayMethod.getDisplayMethod(displayMethod);
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * onclick属性にサブミット制御を行うJavaScript関数を設定したinputタグを出力する。
     * 属性はHTMLエスケープして出力する。
     * 認可や開閉局の状態に応じて、タグの表示方法を切り替える。切り替え方法は非表示、非活性、通常表示のいずれかである。
     * 
     * なお、Javascriptが使用できない端末では、以下のような挙動となる。
     * <pre>
     * - type属性がimage/buttonではPOSTの送信は不可能なので、JspExceptionを送出する。
     * - type属性がsubmitの場合は、uri属性およびsubmit_name属性を保持する
     *   ダミーのPOST変数を展開する。
     *   この値は、サーバ側で {@link KeitaiAccessHandler} によりそれぞれリクエストパスと
     *   リクエストパラメータに差し替えられる。
     * </pre>
     */
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();

        String tagName = "input";
        String requestId = WebRequestUtil.getRequestId(uri);
        String encodedUri = TagUtil.encodeUri(pageContext, uri, secure);
        String submitName = getAttributes().get(HtmlAttribute.NAME);

        DisplayMethod displayMethodResult = TagUtil.getDisplayMethod(requestId, displayMethod);
        setSubmissionInfoToFormContext(requestId, encodedUri, displayMethodResult);

        // サブミット情報を追加した後にスクリプトの生成を行う
        TagUtil.registerOnclickForSubmission(pageContext, tagName, getAttributes(), suppressDefaultSubmit);

        if (DisplayMethod.DISABLED == displayMethodResult) {
            getAttributes().put(HtmlAttribute.DISABLED, true);
        } else if (DisplayMethod.NODISPLAY == displayMethodResult) {
            return SKIP_BODY;
        }
        
        if (!TagUtil.jsSupported(pageContext)) {
            String type = getAttributes().get(HtmlAttribute.TYPE);
            // type='submit' ボタンで無ければPOSTできないのでエラーにする。
            if (!"submit".equals(type)) {
                throw new JspException(
                  "Without javascript, <n:submit> tags will not work properly "
                + " if its type is " + type + "."
                + " Use 'submit' type instead."
                );
            }
            // ダミー変数を展開
            getAttributes().put(
                HtmlAttribute.NAME
              , KeitaiAccessHandler.URI_OVERRIDE_PRAM_PREFIX + submitName + "|" + createOverrideUri(encodedUri)
            );
        }

        TagUtil.print(pageContext, TagUtil.createTagWithoutBody(tagName, getAttributes()));
        return EVAL_BODY_INCLUDE;
    }

    /**
     * オーバーライド用のURIを作成する。
     * <p/>
     * 指定されたURIがコンテキストルートからのパスの場合は、コンテキストパスを取り除く。
     * 絶対URLまたは相対パスの場合は、コンテキストからのパスに変換する。
     * 
     * @param encodedUri エンコード済みのURI
     * @return オーバーライド用のURI
     */
    protected String createOverrideUri(String encodedUri) {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        if (encodedUri.startsWith("/")) { // コンテキストルートからのパスの場合
            int contextPathLength = req.getContextPath().length();
            if (contextPathLength == 1) {
                return encodedUri;
            }
            return encodedUri.substring(contextPathLength);
        } else { // 絶対URLまたは相対パスの場合
            return UriUtil.convertToPathFromContextRoot(encodedUri, req);
        }
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
     */
    public int doEndTag() throws JspException {
        TagUtil.getFormContext(pageContext).setCurrentSubmissionInfo(null);
        return EVAL_PAGE;
    }
}
