package nablarch.common.web.tag;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

/**
 * カスタムタグのベースクラス。
 * @author Kiyohito Itoh
 */
public abstract class CustomTagSupport extends TagSupport implements TryCatchFinally {
    
    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(CustomTagSupport.class);
    
    /**
     * {@inheritDoc}<br>
     * 例外をINFOレベルでログ出力する。
     */
    public void doCatch(Throwable e) throws Throwable {
        LOGGER.logInfo("exception occurred.", e);
        throw e;
    }
    /** {@inheritDoc} */
    public void doFinally() {
        // nothing to do.
    }
}
