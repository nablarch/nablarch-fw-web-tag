package nablarch.common.web.tag;

import nablarch.core.util.annotation.Published;

/**
 * サブミットを行うタグの表示制御が必要か否かを判定するインタフェース。
 * 
 * @author Tomokazu Kagawa
 */
@Published(tag = "architect")
public interface DisplayControlChecker {

    /**
     * 表示制御を行う必要があるか否かを判定する。
     * 
     * @param requestId 該当のタグのサブミット先リクエストID
     * @return 表示制御を行う必要がある場合は、{@code true}
     */
    boolean needsDisplayControl(String requestId);
}
