package nablarch.common.web.tag;

import java.util.Arrays;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import nablarch.core.message.Message;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.MessageUtil;
import nablarch.core.util.I18NUtil;

/**
 * メッセージを出力するクラス。
 * <p/>
 * {@link MessageUtil}を使用してメッセージを取得する。
 * @author Kiyohito Itoh
 */
public class MessageTag extends CustomTagSupport {

    /** メッセージフォーマットに使用するオプション引数のサイズ */
    private static final int OPTION_SIZE = 10;

    /** メッセージID */
    private String messageId;

    /** メッセージフォーマットに使用するオプション引数 */
    private final Object[] options = new Object[OPTION_SIZE];

    {
        Arrays.fill(options, ""); // optionsの要素を空文字で初期化する。
    }

    /** メッセージの言語 */
    private Locale language;

    /** リクエストスコープに格納する際に使用する変数名 */
    private String var;

    /** HTMLエスケープをするか否か */
    private boolean htmlEscape = true;

    /** HTMLフォーマット(改行と半角スペースの変換)をするか否か */
    private boolean withHtmlFormat = true;

    /**
     * メッセージIDを設定する。
     * @param messageId メッセージID
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * メッセージフォーマットに使用するインデックスが0のオプション引数を設定する。
     * @param option インデックスが0のオプション引数
     */
    public void setOption0(Object option) {
        options[0] = option;
    }

    /**
     * メッセージフォーマットに使用するインデックスが1のオプション引数を設定する。
     * @param option インデックスが1のオプション引数
     */
    public void setOption1(Object option) {
        options[1] = option;
    }

    /**
     * メッセージフォーマットに使用するインデックスが2のオプション引数を設定する。
     * @param option インデックスが2のオプション引数
     */
    public void setOption2(Object option) {
        options[2] = option;
    }

    /**
     * メッセージフォーマットに使用するインデックスが3のオプション引数を設定する。
     * @param option インデックスが3のオプション引数
     */
    public void setOption3(Object option) {
        options[3] = option;
    }

    /**
     * メッセージフォーマットに使用するインデックスが4のオプション引数を設定する。
     * @param option インデックスが4のオプション引数
     */
    public void setOption4(Object option) {
        options[4] = option;
    }

    /**
     * メッセージフォーマットに使用するインデックスが5のオプション引数を設定する。
     * @param option インデックスが5のオプション引数
     */
    public void setOption5(Object option) {
        options[5] = option;
    }

    /**
     * メッセージフォーマットに使用するインデックスが6のオプション引数を設定する。
     * @param option インデックスが6のオプション引数
     */
    public void setOption6(Object option) {
        options[6] = option;
    }

    /**
     * メッセージフォーマットに使用するインデックスが7のオプション引数を設定する。
     * @param option インデックスが7のオプション引数
     */
    public void setOption7(Object option) {
        options[7] = option;
    }

    /**
     * メッセージフォーマットに使用するインデックスが8のオプション引数を設定する。
     * @param option インデックスが8のオプション引数
     */
    public void setOption8(Object option) {
        options[8] = option;
    }

    /**
     * メッセージフォーマットに使用するインデックスが9のオプション引数を設定する。
     * @param option インデックスが9のオプション引数
     */
    public void setOption9(Object option) {
        options[9] = option;
    }

    /**
     * メッセージの言語を設定する。
     * @param language メッセージの取得先となる言語
     */
    public void setLanguage(String language) {
        this.language = I18NUtil.createLocale(language);
    }

    /**
     * リクエストスコープに格納する際に使用する変数名を設定する。
     * @param var リクエストスコープに格納する際に使用する変数名
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * HTMLエスケープをするか否かを設定する。<br/>
     * デフォルトはtrue。
     * @param htmlEscape HTMLエスケープをする場合はtrue、しない場合はfalse。
     */
    public void setHtmlEscape(boolean htmlEscape) {
        this.htmlEscape = htmlEscape;
    }

    /**
     * HTMLフォーマット(改行と半角スペースの変換)をするか否かを設定する。<br>
     * HTMLフォーマットはHTMLエスケープをする場合のみ有効となる。
     * @param withHtmlFormat HTMLフォーマット(改行と半角スペースの変換)をする場合はtrue、しない場合はfalse。
     */
    public void setWithHtmlFormat(boolean withHtmlFormat) {
        this.withHtmlFormat = withHtmlFormat;
    }

    /**
     * {@inheritDoc}
     * <pre>
     * 指定されたメッセージIDとオプション引数を使用してメッセージを取得する。
     * 
     * {@link #language}属性の指定がない場合は、{@link nablarch.core.ThreadContext#getLanguage()}
     * から取得した言語を使用してフォーマットしたメッセージを取得する。
     * 
     * {@link #var}属性が指定された場合は、フォーマットしたメッセージをリクエストスコープに設定し、処理を終了する。
     * メッセージをリクエストスコープに設定する場合はHTMLエスケープとHTMLフォーマットを行わない。
     * 
     * {@link #var}属性が指定されなかった場合は、メッセージを出力する。
     * メッセージを出力する場合はHTMLエスケープとHTMLフォーマットを行う。
     * </pre>
     */
    public int doStartTag() throws JspException {

        Message message = MessageUtil.createMessage(MessageLevel.INFO, messageId, options);
        String formattedMessage = language != null
                                      ? message.formatMessage(language)
                                      : message.formatMessage();
        if (var != null) {
            pageContext.setAttribute(var, formattedMessage, PageContext.REQUEST_SCOPE);
        } else { 
            TagUtil.print(pageContext, htmlEscape
                                           ? TagUtil.escapeHtml(formattedMessage, withHtmlFormat)
                                           : formattedMessage);
        }

        return SKIP_BODY;
    }
}
