package nablarch.common.web.tag;

import nablarch.common.availability.ServiceAvailabilityUtil;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

/**
 * 開閉局情報に基づき、サブミットを行うタグの表示制御を行う必要があるか否かを判定するクラス。
 * 
 * @author Tomokazu Kagawa
 */
public class ServiceAvailabilityDisplayControlChecker implements DisplayControlChecker {

    /**
     * {@inheritDoc}
     */
    public boolean needsDisplayControl(String requestId) {
        try {
            return !ServiceAvailabilityUtil.isAvailable(requestId);
            
        } catch (RuntimeException e) {
            return handleError(e);
        } catch (Error e) {
            return handleError(e);
        }
    }
    
    /**
     * 開閉局ステータスチェック中に発生したエラーを処理する。
     * 
     * 捕捉した例外をINFOレベルのログに出力し、falseをリターンする。
     * 
     * @param e 起因例外
     * @return 必ずfalseを返却する。
     *          (ボタン・リンクの表示制御は行われず、通常と同様の表示となる。)
     */
    private boolean handleError(Throwable e) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.logInfo(
                "Failed to pre-check the availability of business services. "
              + "It is needed for determining appearance of some buttons and links. "
              + "(They are showed in ordinal appearance.)"
              , e
            );
        }
        return false;
    }
    
    /** ロガー */
    private static final Logger
    LOGGER = LoggerManager.get(ServiceAvailabilityDisplayControlChecker.class);
}
