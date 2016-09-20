package nablarch.common.web.tag;

import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;

/**
 * サブミット制御(リンクとアクションの紐付け、二重サブミット防止)を行うaタグを出力するクラス。
 * @author Kiyohito Itoh
 */
public class SubmitLinkTag extends SubmitLinkTagSupport {
    
    /** 二重サブミットを許可するか否か */
    private boolean allowDoubleSubmission = true;
    
    /**
     * 二重サブミットを許可するか否かを設定する。<br>
     * デフォルトはtrue。
     * @param allowDoubleSubmission 許可する場合はtrue、許可しない場合はfalse
     */
    public void setAllowDoubleSubmission(boolean allowDoubleSubmission) {
        this.allowDoubleSubmission = allowDoubleSubmission;
    }
    
    @Override
    protected void setSubmissionInfoToFormContext(String requestId, String encodedUri, DisplayMethod displayMethod) {
        TagUtil.setSubmissionInfoToFormContext(
            pageContext, getAttributes(), getSubmissionAction(), encodedUri,
            allowDoubleSubmission, requestId, displayMethod);
    }
    
    /**
     * サブミット時の動作を取得する。
     * @return サブミット時の動作
     */
    protected SubmissionAction getSubmissionAction() {
        return SubmissionAction.TRANSITION;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "submitLink";
    }
}
