package nablarch.common.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import nablarch.core.util.StringUtil;

/**
 * 変数に値を設定するクラス。
 * @author Kiyohito Itoh
 */
public class SetTag extends CustomTagSupport {

    /** リクエストスコープに格納する際に使用する変数名 */
    private String var;
    
    /** 値を取得するための名前 */
    private String name;
    
    /** name属性に対応する値を単一値として取得するか否か。 */
    private boolean bySingleValue = true;
    
    /** 値 */
    private Object value;
    
    /** 変数を格納するスコープ */
    private int scope = PageContext.REQUEST_SCOPE;
    
    /**
     * リクエストスコープに格納する際に使用する変数名を設定する。
     * @param var リクエストスコープに格納する際に使用する変数名
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /**
     * 値を取得するための名前を設定する。
     * @param name 値を取得するための名前
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 値を設定する。
     * @param value 値
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * name属性に対応する値を単一値として取得するか否かを設定する。<br>
     * デフォルトはtrue。
     * @param bySingleValue name属性に対応する値を単一値として取得する場合はtrue。
     */
    public void setBySingleValue(boolean bySingleValue) {
        this.bySingleValue = bySingleValue;
    }
    
    /**
     * 変数を格納するスコープを設定する。
     * <pre>
     * 指定できるスコープを下記に示す。
     * 
     * page: ページスコープ
     * request: リクエストスコープ
     * 
     * デフォルトはリクエストスコープ。
     * </pre>
     * @param scopeName スコープ名
     */
    public void setScope(String scopeName) {
        if ("page".equals(scopeName)) {
            scope = PageContext.PAGE_SCOPE;
        } else if ("request".equals(scopeName)) {
            scope = PageContext.REQUEST_SCOPE;
        } else {
            throw new IllegalArgumentException(
                    String.format("scopeName was invalid. scopeName must specify the following values. values = %s scopeName = [%s]",
                                  "[\"page\", \"request\"]", scopeName));
        }
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * name属性とvalue属性のどちらも指定がない場合、どちらも指定されている場合は例外を送出する。
     * name属性に対応する値又はvalue属性の値を、var属性で指定された属性名で指定されたスコープに設定する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        if ((value == null && StringUtil.isNullOrEmpty(name))
                || (value != null && !StringUtil.isNullOrEmpty(name))) {
            throw new IllegalArgumentException(
                String.format("name and value was invalid. must specify either name or value. var = [%s], name = [%s], value = [%s]",
                              var, name, value));
        }
        
        Object obj;
        if (value != null) {
            obj = value;
        } else {
            obj = bySingleValue ? TagUtil.getSingleValue(pageContext, name)
                                : TagUtil.getValue(pageContext, name, true);
        }
        pageContext.setAttribute(var, obj, scope);
        
        return SKIP_BODY;
    }
}
