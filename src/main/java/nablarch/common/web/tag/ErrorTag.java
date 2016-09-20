package nablarch.common.web.tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.jsp.JspException;

import nablarch.core.message.Message;

/**
 * エラーメッセージを出力するクラス。<br>
 * エラーの原因となった入力項目の近くにエラーメッセージを個別に表示する場合に使用する。
 * @author Kiyohito Itoh
 */
public class ErrorTag extends CustomTagSupport {

    /** メッセージ表示時に使用するフォーマットの種類 */
    public static final Set<String> MESSAGE_FORMATS = new HashSet<String>(Arrays.asList("span", "div"));
    
    /** エラーメッセージを表示する入力項目のname属性 */
    private String name;
    
    /** エラーレベルのメッセージに使用するCSSクラス名 */
    private String errorCss;
    
    /** メッセージ表示時に使用するフォーマット */
    private String messageFormat;
    
    /**
     * エラーメッセージを表示する入力項目のname属性を設定する。
     * @param name エラーメッセージを表示する入力項目のname属性
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * エラーレベルのメッセージに使用するCSSクラス名を設定する。
     * @param errorCss エラーレベルのメッセージに使用するCSSクラス名
     */
    public void setErrorCss(String errorCss) {
        this.errorCss = errorCss;
    }
    
    /**
     * メッセージ表示時に使用するフォーマットを設定する。
     * <pre>
     * span(spanタグ)、div(divタグ)のいずれかを指定する。<br>
     * デフォルトはdiv。
     * </pre>
     * @param messageFormat メッセージ表示時に使用するフォーマット
     */
    public void setMessageFormat(String messageFormat) {
        if (messageFormat == null || !MESSAGE_FORMATS.contains(messageFormat)) {
            throw new IllegalArgumentException(
                String.format("messageFormat was invalid. messageFormat must specify the following values. values = %s messageFormat = [%s]",
                              MESSAGE_FORMATS, messageFormat));
        }
        this.messageFormat = messageFormat;
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * name属性に対応するエラーメッセージが存在しない場合は何も出力しない。
     * エラーメッセージを指定されたフォーマットで出力する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        Message message = TagUtil.findMessage(pageContext, name);
        if (message == null) {
            return SKIP_BODY;
        }
        
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        
        HtmlAttributes attributes = new HtmlAttributes();
        attributes.put(HtmlAttribute.CLASS,
                       errorCss != null ? errorCss : config.getErrorCss());
        
        String useMsgFormat = messageFormat != null ? messageFormat : config.getMessageFormat();
        StringBuilder sb = new StringBuilder();
        sb.append(TagUtil.createStartTag(useMsgFormat, attributes));
        sb.append(TagUtil.escapeHtml(message.formatMessage(), true));
        sb.append(TagUtil.createEndTag(useMsgFormat));
        
        TagUtil.print(pageContext, sb.toString());
        
        return SKIP_BODY;
    }
}
