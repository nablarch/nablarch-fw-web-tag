package nablarch.common.web.hiddenencryption;

import static nablarch.fw.ExecutionContext.FW_PREFIX;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.servlet.jsp.PageContext;

import nablarch.common.encryption.AesEncryptor;
import nablarch.common.encryption.Encryptor;
import nablarch.common.util.WebRequestUtil;
import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.Base64Util;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;

/**
 * hiddenタグの暗号化機能で使用するユーティリティ。
 * <pre>
 * このユーティリティでは、{@link Encryptor}インタフェースを実装したクラスに、暗号化と復号の処理を移譲する。
 * デフォルトでは{@link AesEncryptor}を使用する。
 * 暗号化と復号に使用するクラスを変更したい場合は、"hiddenEncryptor"という名前でリポジトリに登録する。
 * </pre>
 * @author Kiyohito Itoh
 */
public final class HiddenEncryptionUtil {
    
    /** 隠蔽コンストラクタ */
    private HiddenEncryptionUtil() {
    }
    
    /** {@link Encryptor}をリポジトリから取得する際に使用する名前 */
    private static final String HIDDEN_ENCRYPTOR_NAME = "hiddenEncryptor";
    
    /** リポジトリから取得できない場合に使用するデフォルトの{@link Encryptor} */
    private static final Encryptor<? extends Serializable> DEFAULT_HIDDEN_ENCRYPTOR = new AesEncryptor();
    
    /**
     * {@link Encryptor}が使用するコンテキスト情報をセッションに格納する際に使用するキー。
     * セッション管理機能の論理区分の最上位階層に設定する。
     */
    private static final String KEY_ENCRYPTION_CONTEXT = "/" + FW_PREFIX + "encryptionContext";
    
    /** 暗号化したhiddenタグの値を出力する際に使用する名前 */
    public static final String KEY_HIDDEN_NAME = FW_PREFIX + "hidden";
    
    /** サブミットされた要素を識別するために使用するパラメータの名前 */
    public static final String KEY_SUBMIT_NAME = FW_PREFIX + "submit";
    
    /** 暗号化したhiddenタグの値にリクエストIDを含める際に使用するname属性 */
    public static final String KEY_HIDDEN_REQUEST_IDS_NAME = FW_PREFIX + "hidden_requestIds";
    
    /** 暗号化したhiddenタグの値にサブミット情報を含める際に使用するname属性のプレフィックス */
    public static final String KEY_HIDDEN_SUBMIT_NAME_PREFIX = FW_PREFIX + "hidden_submit_";
    
    /** 現在のリクエストに対して、hiddenタグの暗号化が必要であることを示すリクエストパラメータ名 */
    public static final String KEY_NEEDS_ENCRYPTION = FW_PREFIX + "needs_hidden_encryption";
    
    /** ハッシュ値の生成に使用するアルゴリズム */
    private static final String HASH_ALGORITHM = "MD5";
    
    /** ハッシュ値のバイトサイズ */
    private static final int HASH_BYTE_LENGTH = 16;
    
    /** 暗号化と復号に使用する文字セット */
    private static final Charset ENCRYPTION_CHARSET = Charset.forName("UTF-8");
    
    /**
     * hiddenタグの値を復号する。
     * <pre>
     * 復号では、暗号化時に埋め込んだハッシュ値とリクエストID文字列を使用して改竄チェックを行う。
     * 暗号化の仕様については、{@link #encryptHiddenValues(PageContext, List, Map)}を参照。
     * </pre>
     * @param context {@link ExecutionContext}
     * @param value hiddenタグの値
     * @return hiddenタグの値を復号した結果
     * 
     * @throws TamperingDetectedException 改竄を検知した場合。
     * @throws KeyEncryptionContextNotFoundException 暗号化キーをセッションから取得出来なかった場合。
     * 
     */
    public static Map<String, List<String>> decryptHiddenValues(ExecutionContext context, String value) 
            throws TamperingDetectedException, KeyEncryptionContextNotFoundException {
        
        byte[] allBytes;
        try {
            allBytes = Base64Util.decode(value);
        } catch (IllegalArgumentException e) {
            throw new TamperingDetectedException("base64 decoding failed.", e);
        }
        
        try {
            Serializable encryptionContext = (Serializable) context.getSessionScopedVar(KEY_ENCRYPTION_CONTEXT);
            if (encryptionContext == null) {
                throw new KeyEncryptionContextNotFoundException("key encryption context was not found." 
                        + " session key = [" + KEY_ENCRYPTION_CONTEXT + "]");
            } else {
                allBytes = getHiddenEncryptor().decrypt(encryptionContext, allBytes);
            }
        } catch (IllegalArgumentException e) {
            throw new TamperingDetectedException("decryption failed.", e);
        }
        
        if (allBytes.length <= HASH_BYTE_LENGTH) {
            throw new TamperingDetectedException("hash was invalid.");
        }
        
        byte[] hash = new byte[HASH_BYTE_LENGTH];
        byte[] valuesBytes = new byte[allBytes.length - hash.length];
        
        System.arraycopy(allBytes, 0, hash, 0, hash.length);
        System.arraycopy(allBytes, hash.length, valuesBytes, 0, valuesBytes.length);
        
        String values = StringUtil.toString(valuesBytes, ENCRYPTION_CHARSET);
        valuesBytes = StringUtil.getBytes(values, ENCRYPTION_CHARSET);
        
        if (!Arrays.equals(hash, hash(KEY_HIDDEN_NAME, valuesBytes))) {
            throw new TamperingDetectedException("hash was invalid.");
        }
        
        Map<String, List<String>> params = WebRequestUtil.convertToParamsMap(values);
        List<String> requestIds = params.remove(KEY_HIDDEN_REQUEST_IDS_NAME);
        if (!requestIds.contains(ThreadContext.getRequestId())) {
            throw new TamperingDetectedException("requestId was invalid.");
        }
        
        return params;
    }
    
    /**
     * 改竄検知に使用するハッシュ値を生成する。<br>
     * "name + value"からハッシュ値を生成する。
     * @param name name属性
     * @param value value属性
     * @return 改竄検知に使用するハッシュ値
     */
    private static byte[] hash(String name, byte[] value) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.reset();
            digest.update(value);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            // アルゴリズムが固定のため到達不能
            throw new IllegalStateException("MessageDigest initialization failed.", e);
        }
    }
    
    /**
     * hiddenタグの値を暗号化する。
     * <pre>
     * 暗号化は、下記の仕様で行う。
     * ・リクエストIDを連結しリクエストID文字列を作成する。
     *   リクエストID文字列は、復号時に値の置き換えによる改竄を検知するために、hiddenタグの値に追加する。
     * ・全てのhiddenタグの値を連結し、hidden文字列を作成する。
     * ・"nablarch_hidden"＋hidden文字列からハッシュ値を生成する。
     *   ハッシュ値は、復号時に値の書き換えによる改竄を検知するために使用する。
     *   "nablarch_hidden"は、暗号化した値をhiddenタグで出力する際に使用するname属性の値である。
     * ・ハッシュ値＋hidden文字列を暗号化した結果をBASE64でエンコードする。
     * </pre>
     * @param pageContext ページコンテキスト
     * @param requestIds リクエストID
     * @param values hiddenタグの値
     * @return hiddenタグの値を暗号化した結果
     */
    public static String encryptHiddenValues(PageContext pageContext, List<String> requestIds, Map<String, List<String>> values) {
        
        values.put(KEY_HIDDEN_REQUEST_IDS_NAME, requestIds);
        String value = WebRequestUtil.convertToParamsString(values);
        
        byte[] valueBytes = StringUtil.getBytes(value, ENCRYPTION_CHARSET);
        byte[] hash = hash(KEY_HIDDEN_NAME, valueBytes);
        byte[] allBytes = new byte[hash.length + valueBytes.length];
        
        System.arraycopy(hash, 0, allBytes, 0, hash.length);
        System.arraycopy(valueBytes, 0, allBytes, hash.length, valueBytes.length);
        
        return Base64Util.encode(getHiddenEncryptor().encrypt(getEncryptionContext(pageContext), allBytes));
    }
    
    /**
     * リポジトリから{@link Encryptor}を取得する。<br>
     * 存在しない場合はデフォルトの{@link AesEncryptor}を使用する。
     * @return {@link Encryptor}
     */
    @SuppressWarnings("unchecked")
    private static Encryptor<Serializable> getHiddenEncryptor() {
        Encryptor<Serializable> encryptor = (Encryptor<Serializable>) SystemRepository.getObject(HIDDEN_ENCRYPTOR_NAME);
        return encryptor != null ? encryptor : (Encryptor<Serializable>) DEFAULT_HIDDEN_ENCRYPTOR;
    }
    
    /**
     * セッションから{@link Encryptor}が使用するコンテキスト情報を取得する。<br>
     * セッションにコンテキスト情報が存在しない場合は生成する。
     * @param pageContext ページコンテキスト
     * @return {@link Encryptor}が使用するコンテキスト情報
     */
    private static Serializable getEncryptionContext(PageContext pageContext) {
        Serializable encryptionContext = (Serializable) pageContext.getAttribute(KEY_ENCRYPTION_CONTEXT, PageContext.SESSION_SCOPE);
        if (encryptionContext == null) {
            encryptionContext = getHiddenEncryptor().generateContext();
            pageContext.setAttribute(KEY_ENCRYPTION_CONTEXT, encryptionContext, PageContext.SESSION_SCOPE);
        }
        return encryptionContext;
    }
}
