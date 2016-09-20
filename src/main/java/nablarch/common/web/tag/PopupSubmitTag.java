package nablarch.common.web.tag;

import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;

/**
 * ポップアップを行うinputタグ(type="submit","button","image")を出力するクラス。
 * <p/>
 * {@link SubmitTag}と異なる点を下記に示す。
 * <ul>
 * <li>新しい画面をオープンし、オープンした画面に対してサブミットを行う。</li>
 * <li>ポップアップを繰り返し使用できるように、二重サブミットは常に許可する。</li>
 * </ul>
 * @author Kiyohito Itoh
 */
public class PopupSubmitTag extends SubmitTagSupport {

    /** ポップアップのウィンドウ名 */
    private String popupWindowName;

    /** ポップアップのオプション情報 */
    private String popupOption;

    /**
     * ポップアップのウィンドウ名を設定する。
     * @param popupWindowName ポップアップのウィンドウ名
     */
    public void setPopupWindowName(String popupWindowName) {
        this.popupWindowName = popupWindowName;
    }

    /**
     * ポップアップのオプション情報を設定する。
     * @param popupOption ポップアップのオプション情報
     */
    public void setPopupOption(String popupOption) {
        this.popupOption = popupOption;
    }
    
    @Override
    protected void setSubmissionInfoToFormContext(String requestId, String encodedUri, DisplayMethod displayMethod) {
        TagUtil.setSubmissionInfoToFormContext(
                pageContext, getAttributes(), SubmissionAction.POPUP, encodedUri,
                true, requestId, displayMethod, popupWindowName, popupOption);
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "popupSubmit";
    }
}
