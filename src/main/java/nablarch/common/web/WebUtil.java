package nablarch.common.web;

import java.util.Arrays;

import nablarch.core.message.ApplicationException;
import nablarch.core.message.Message;
import nablarch.core.util.annotation.Published;
import nablarch.core.validation.ValidationContext;
import nablarch.fw.ExecutionContext;

/**
 * Webアプリケーションの作成に必要となる共通機能を提供するユーティリティ。
 * @author Kiyohito Itoh
 */
@Published
public final class WebUtil {

    /** 隠蔽コンストラクタ */
    private WebUtil() {
    }

    /**
     * メッセージをユーザに通知する。
     * <pre>{@code
     * WebUtil.notifyMessages(context, MessageUtil.createMessage(MessageLevel.ERROR, "メッセージID"));
     * }</pre>
     * 既にメッセージが存在する場合は既存メッセージの末尾に追加する。<br />
     * 指定されたメッセージは n:errors タグを使用して出力する。<br />
     *
     * @see nablarch.core.message.MessageUtil
     * @see nablarch.core.message.MessageLevel
     * @param context 実行コンテキスト
     * @param messages メッセージ
     * @deprecated <a href="https://nablarch.github.io/docs/LATEST/doc/application_framework/application_framework/libraries/message.html#message-level" target='_blank'>メッセージレベルの使い分け</a>を参照
     */
    @Deprecated
    public static void notifyMessages(ExecutionContext context, Message... messages) {

        if (messages == null || messages.length == 0) {
            return;
        }

        ApplicationException ae = context.getApplicationException();
        if (ae == null) {
            ae = new ApplicationException();
            context.setRequestScopedVar(ExecutionContext.THROWN_APPLICATION_EXCEPTION_KEY, ae);
        }
        ae.addMessages(Arrays.asList(messages));
    }


    /**
     * 指定したプロパティに対応するキー(リクエストパラメータ名)がリクエストに存在するかどうか判定する。<br/>
     * 例えば、form.sampleというキーがリクエストに存在している場合、下記コードは{@code true}を返す。
     * <pre>{@code
     * WebUtil.containsPropertyKey(context, "form.sample"); //--> true
     * }</pre>
     *
     * @param context バリデーションコンテキスト
     * @param key プロパティに対応するキー
     * @return キーが存在する場合{@code true}
     */

    public static boolean containsPropertyKey(@SuppressWarnings("rawtypes") ValidationContext context,  String key) {
        return context.getParams().containsKey(key);
    }

    /**
     * 指定したキー（リクエストパラメータ名）に指定した値が含まれているか判定する。<br/>
     * 例えば、form.sampleというキーの値が"ABC"だったとき、下記コードは{@code true}を返す。
     * <pre>{@code
     * WebUtil.containsPropertyKeyValue(context, "form.sample", "ABC"); //--> true
     * }</pre>
     * 指定したキーと値の組み合わせがリクエストに存在しなかった場合は{@code false}を返す。
     *
     * @param context バリデーションコンテキスト
     * @param key プロパティに対応するキー
     * @param value プロパティの値
     * @return 指定した値が含まれている場合{@code true}
     */
    public static boolean containsPropertyKeyValue(@SuppressWarnings("rawtypes") ValidationContext context, String key, String value) {
        boolean ret = false;

        if (containsPropertyKey(context, key)) {
            for (String element : (String[]) context.getParams().get(key)) {
                if (element.equals(value)) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }
}
