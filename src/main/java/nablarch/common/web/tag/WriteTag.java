package nablarch.common.web.tag;

import nablarch.core.util.StringUtil;

import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspException;

/**
 * 名前に対応する値を出力するクラス。
 * このタグで出力された値は必ずHTMLエスケープされる。
 * 
 * @see RawWriteTag
 * @see PrettyPrintTag
 * @author Kiyohito Itoh
 */
public class WriteTag extends CustomTagSupport {

    /** 出力対象の名前 */
    private String name;
    
    /** HTMLエスケープをするか否か */
    private boolean htmlEscape = true;
    
    /** 
     *  HTMLエスケープの対象とならずにHTMLタグとしてそのまま出力するタグ名のリスト
     *  (半角スペース区切り)
     *  htmlEscapeの値がfalseであった場合は、単に無視される。
     */
    private List<String> safeTags = null;
    
    /** 
     *  HTMLエスケープの対象外のHTMLタグの中で使用することができる属性値のリスト。
     *  (半角スペース区切り)
     *  htmlEscapeの値がfalseであった場合は、単に無視される。
     */
    private List<String> safeAttributes = null;
    
    /** HTMLフォーマット(改行と半角スペースの変換)をするか否か */
    private boolean withHtmlFormat = true;
    
    /** 出力時のフォーマット */
    private String valueFormat;
    
    /**
     * 出力対象の名前を設定する。
     * @param name 出力対象の名前
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * HTMLエスケープをするか否かを設定する。<br>
     * デフォルトはtrue。
     * @param htmlEscape HTMLエスケープをする場合はtrue、しない場合はfalse。
     */
    void setHtmlEscape(boolean htmlEscape) {
        this.htmlEscape = htmlEscape;
    }
    
    /**
     *  HTMLエスケープの対象とならずにHTMLタグとしてそのまま出力するタグ名のリスト
     *  を設定する。
     * @param tagNames HTMLエスケープの対象外のタグリスト (半角スペース区切り)
     */
    void setSafeTags(String[] tagNames) {
        this.safeTags = Arrays.asList(tagNames);
    }
    
    /**
     * HTMLエスケープの対象外のHTMLタグの中で使用することができる属性値のリスト
     * を設定する。
     * @param attributes 属性値のリスト (半角スペース区切り)
     */
    void setSafeAttributes(String[] attributes) {
        this.safeAttributes = Arrays.asList(attributes);
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
     * 出力時のフォーマットを設定する。
     * <pre>
     * フォーマットは、"データタイプ{パターン}"形式で指定する。
     * 
     * フレームワークがデフォルトでサポートしているフォーマットを下記に示す。
     * 
     * dateString:
     *   日付文字列のフォーマット。
     *   値は、yyyyMMdd形式の文字列を指定する。
     *   パターンには、java.text.SimpleDateFormatが規定している構文を指定する。
     *   {@link CustomTagConfig}を使用してパターンのデフォルト値を設定することができる。
     *   例：dateString{yyyy/MM/dd}
     * 
     * dateTime:
     *   日時のフォーマット。
     *   値は、java.util.Date型を指定する。
     *   パターンには、java.text.SimpleDateFormatが規定している構文を指定する。
     *   {@link CustomTagConfig}を使用してパターンのデフォルト値を設定することができる。
     *   例：datetime{yy/MM/dd HH:mm:ss}
     * 
     * decimal:
     *   10進数のフォーマット。
     *   値は、java.lang.Number型又は数字の文字列を指定する。
     *   数字の文字列の場合は、カンマを取り除いた後でフォーマットする。
     *   パターンには、java.text.DecimalFormatが規定している構文を指定する。
     *   例：decimal{###.##%}
     * </pre>
     * @param valueFormat 出力時のフォーマット
     */
    public void setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * name属性に対応する値を出力する。
     * name属性に対応する値は、変数スコープのみから取得する。(リクエストパラメータは取得先に含まない)
     * name属性に対応する値が取得できない場合は何も出力しない。
     * format属性が指定されている場合は、name属性に対応する値をフォーマットする。
     * </pre>
     */
    public int doStartTag() throws JspException {
        Object value = TagUtil.getSingleValue(pageContext, name);
        if (value != null) {
            if (valueFormat != null) {
                value = TagUtil.formatValue(pageContext, name, TagUtil.createFormatSpec(valueFormat), value);
            }
            String output = htmlEscape
                          ? TagUtil.escapeHtml(value, withHtmlFormat, safeTags, safeAttributes)
                          : StringUtil.toString(value);
            TagUtil.print(pageContext, output);
        }
        return SKIP_BODY;
    }
}
