package nablarch.common.web.tag;

import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;

/**
 * ダウンロードを行うbuttonタグを出力するクラス。
 * <p/>
 * {@link ButtonTag}と異なる点を下記に示す。
 * <ul>
 * <li>新しいFormを作成し、作成したFormを使用してサブミットを行う。</li>
 * </ul>
 * @author Kiyohito Itoh
 */
public class DownloadButtonTag extends ButtonTag {
    @Override
    protected SubmissionAction getSubmissionAction() {
        return SubmissionAction.DOWNLOAD;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "downloadButton";
    }
}
