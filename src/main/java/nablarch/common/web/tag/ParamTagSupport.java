package nablarch.common.web.tag;

import java.util.Collection;

import javax.servlet.jsp.JspException;

import nablarch.core.util.StringUtil;

/**
 * パラメータを指定するクラスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class ParamTagSupport extends CustomTagSupport {

    /** パラメータの名前 */
    private String paramName;

    /** 値を取得するための名前 */
    private String name;

    /** 値 */
    private Object value;

    /**
     * パラメータの名前を設定する。
     * @param paramName パラメータの名前
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
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
     * {@inheritDoc}
     * <pre>
     * name属性とvalue属性のどちらも指定がない場合、どちらも指定されている場合は例外を送出する。
     * name属性が指定された場合に、name属性に対応する値が取得できない場合は値に空文字を指定する。
     * name属性に対応する値又はvalue属性の値を、paramName属性で指定されたパラメータ名を使用してサブミットに含める。
     * </pre>
     */
    public int doStartTag() throws JspException {

        if ((value == null && StringUtil.isNullOrEmpty(name))
                || (value != null && !StringUtil.isNullOrEmpty(name))) {
            throw new IllegalStateException(
                String.format("name and value was invalid. "
                            + "must specify either name or value. "
                            + "name = [%s], value = [%s]", name, value));
        }

        if (value != null) {
            addParam(paramName, StringUtil.toString(value));
        } else {
            Collection<?> values = TagUtil.getMultipleValues(pageContext, name);
            if (values.isEmpty()) {
                addParam(paramName, "");
            } else {
                for (Object value : values) {
                    addParam(paramName, value != null ? StringUtil.toString(value) : "");
                }
            }
        }
        
        return SKIP_BODY;
    }

    /**
     * パラメータを追加する。<br/>
     * 同じパラメータ名に対して複数の値が指定された場合はこのメソッドが複数回呼ばれる。
     * @param name パラメータ名
     * @param value 値
     */
    protected abstract void addParam(String name, String value);
}
