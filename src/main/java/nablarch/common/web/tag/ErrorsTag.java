package nablarch.common.web.tag;

import java.util.Arrays;
import java.util.List;
import javax.servlet.jsp.JspException;

import nablarch.core.message.Message;
import nablarch.core.util.StringUtil;
import nablarch.core.validation.ValidationResultMessage;

import static nablarch.fw.ExecutionContext.FW_PREFIX;

/**
 * 複数件のエラーメッセージを出力するクラス。<br>
 * 画面上部に一覧でエラーメッセージを表示する場合に使用する。
 * @author Kiyohito Itoh
 */
public class ErrorsTag extends CustomTagSupport {

    /** フィルタの値 */
    private static final List<String> FILTERS = Arrays.asList("all", "global");
    
    /** リスト表示においてulタグに使用するCSSクラス名 */
    private String cssClass = FW_PREFIX + "errors";
    
    /** 情報レベルのメッセージに使用するCSSクラス名 */
    private String infoCss = FW_PREFIX + "info";

    /** 警告レベルのメッセージに使用するCSSクラス名 */
    private String warnCss = FW_PREFIX + "warn";

    /** エラーレベルのメッセージに使用するCSSクラス名 */
    private String errorCss = FW_PREFIX + "error";

    /** リストに含めるメッセージのフィルタ条件 */
    private String filter = "all";
    /**
     * リスト表示においてulタグに使用するCSSクラス名を設定する。
     * @param cssClass リスト表示においてulタグに使用するCSSクラス名
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
    
    /**
     * 情報レベルのメッセージに使用するCSSクラス名を設定する。
     * @param infoCss 情報レベルのメッセージに使用するCSSクラス名
     */
    public void setInfoCss(String infoCss) {
        this.infoCss = infoCss;
    }

    /**
     * 警告レベルのメッセージに使用するCSSクラス名を設定する。
     * @param warnCss 警告レベルのメッセージに使用するCSSクラス名
     */
    public void setWarnCss(String warnCss) {
        this.warnCss = warnCss;
    }

    /**
     * エラーレベルのメッセージに使用するCSSクラス名を設定する。
     * @param errorCss エラーレベルのメッセージに使用するCSSクラス名
     */
    public void setErrorCss(String errorCss) {
        this.errorCss = errorCss;
    }

    /**
     * リストに含めるメッセージのフィルタ条件を設定する。
     * <pre>
     * 下記のいずれかを指定する。
     * all(全て表示)
     * global(ValidationResultMessageとプロパティ名が空文字のValidationResultMessageを除いたメッセージを表示)
     * 
     * デフォルトはall。
     * </pre>
     * @param filter リストに含めるメッセージのフィルタ条件
     */
    public void setFilter(String filter) {
        if (filter == null || !FILTERS.contains(filter)) {
            throw new IllegalArgumentException(
                String.format("filter was invalid. filter must specify the following values. values = %s filter = [%s]",
                              FILTERS, filter));
        }
        this.filter = filter;
    }
    /**
     * {@inheritDoc}
     * <pre>
     * エラーメッセージが存在しない場合は何も出力しない。
     * エラーメッセージをliタグに展開したulタグを出力する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        
        List<Message> messages = TagUtil.getMessages(pageContext);
        if (messages == null || messages.isEmpty()) {
            return SKIP_BODY;
        }
        
        StringBuilder elements = new StringBuilder();
        for (Message message : messages) {
            if (exclude(message)) {
                continue;
            }
            String messageLevelCssClass = getCssClass(message);
            HtmlAttributes attributes = new HtmlAttributes();
            attributes.put(HtmlAttribute.CLASS, messageLevelCssClass);
            elements.append(TagUtil.getCustomTagConfig().getLineSeparator()).append(
                TagUtil.createTagWithBody("li", attributes, TagUtil.escapeHtml(message.formatMessage(), true)));
        }
        
        if (elements.length() != 0) {
            StringBuilder list = new StringBuilder();
            list.append("<ul class=\"" + (cssClass != null ? TagUtil.escapeHtml(cssClass, false) : "") + "\">");
            list.append(elements);
            list.append(TagUtil.getCustomTagConfig().getLineSeparator() + "</ul>");
            TagUtil.print(pageContext, list.toString());
        }
        return SKIP_BODY;
    }
    
    /**
     * フィルタ条件を見て、リストに含めない場合はtrueを返す。
     * @param message メッセージ
     * @return リストに含めない場合はtrue、それ以外はfalse
     */
    private boolean exclude(Message message) {
        if (filter.equals("all")) {
            return false;
        }
        if (!(message instanceof ValidationResultMessage)) {
            return false;
        } else {
            return !StringUtil.isNullOrEmpty(((ValidationResultMessage) message).getPropertyName());
        }
    }
    
    /**
     * メッセージに対応する名前を取得する。
     * @param message メッセージ
     * @return バリデーション結果メッセージの場合はプロパティ名、それ以外はメッセージID
     */
    private String getName(Message message) {
        return message instanceof ValidationResultMessage ? ((ValidationResultMessage) message).getPropertyName() : message.getMessageId();
    }
    
    /**
     * メッセージに設定されたメッセージラベルに対応するCSSクラス名を取得する。
     * @param message メッセージ
     * @return メッセージに設定されたメッセージラベルに対応するCSSクラス名。メッセージラベルが設定されてない場合は空文字
     */
    private String getCssClass(Message message) {
        switch (message.getLevel()) {
            case INFO:
                return infoCss;
            case WARN:
                return warnCss;
            case ERROR:
                return errorCss;
            default:
                // 列挙型の値を増やさない限り到達不能。
                return "";
        }
    }
}
