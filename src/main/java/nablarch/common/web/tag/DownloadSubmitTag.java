package nablarch.common.web.tag;

import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;

/**
 * ダウンロードを行うinputタグ(type="submit","button","image")を出力するクラス。
 * <p/>
 * {@link SubmitTag}と異なる点を下記に示す。
 * <ul>
 * <li>新しいFormを作成し、作成したFormを使用してサブミットを行う。</li>
 * </ul>
 * 
 * @author Kiyohito Itoh
 */
public class DownloadSubmitTag extends SubmitTag {
    @Override
    protected SubmissionAction getSubmissionAction() {
        return SubmissionAction.DOWNLOAD;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "downloadSubmit";
    }
}
