package nablarch.common.web.hiddenencryption;

import nablarch.core.util.annotation.Published;

/**
 * hiddenタグの暗号化機能で、キーコンテキストが取得できなかった場合に発生する例外。<br />
 * この例外が発生するのは、改竄以外にセッション有効期限切れのケースが考えられる。
 *
 * @author Koichi Asano 
 */
@Published(tag = "architect")
public class KeyEncryptionContextNotFoundException extends RuntimeException {
    /**
     * コンストラクタ。
     * @param message メッセージ
     */
    public KeyEncryptionContextNotFoundException(String message) {
        super(message);
    }
}
