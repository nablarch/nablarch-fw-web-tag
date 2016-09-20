package nablarch.common.web.tag;

import static nablarch.fw.ExecutionContext.FW_PREFIX;

import javax.servlet.jsp.JspException;

import nablarch.core.util.StringUtil;

/**
 * チェックなしに対する値をリクエストパラメータに設定するチェックボックスの実装をサポートするクラス。
 * @author Kiyohito Itoh
 */
public abstract class CheckboxTagSupport extends CheckedInputTagSupport {
    
    /** 変更パラメータのプレフィックス */
    public static final String CHECKBOX_OFF_PARAM_PREFIX = FW_PREFIX + "cbx_off_param_";
    
    @Override
    protected String getType() {
        return "checkbox";
    }
    
    /**
     * {@inheritDoc}
     * <br>
     * value属性が設定されていない場合は、デフォルトの値を設定する。
     */
    protected String getValue() {
        if (getAttributes().get(HtmlAttribute.VALUE) == null) {
            getAttributes().put(HtmlAttribute.VALUE, TagUtil.getCustomTagConfig().getCheckboxOnValue());
        }
        return super.getValue();
    }

    /**
     * {@inheritDoc}
     * <br>
     * チェックなしの場合は、チェックなしの場合に使用するラベルが指定された場合のみ出力する。
     */
    protected void createOutputTag(boolean checked) throws JspException {
        if (checked) {
            super.createOutputTag(checked);
        } else {
            String offLabel = getOffLabel();
            if (StringUtil.hasValue(offLabel)) {
                TagUtil.print(pageContext, TagUtil.escapeHtml(offLabel, true));
            }
        }
    }
    
    /**
     * {@inheritDoc}
     * <br>
     * チェックなしに対する値をhiddenパラメータに含める。<br>
     * チェックなしに対する値が設定されていない場合は、デフォルトの値を使用する。
     */
    protected void createInputTag(boolean checked) throws JspException {
        
        super.createInputTag(checked);
        
        if (getUseOffValue()) {
            String name = getAttributes().get(HtmlAttribute.NAME);
            TagUtil.getFormContext(pageContext)
                   .addHiddenTagInfo(CHECKBOX_OFF_PARAM_PREFIX + name, getOffValue());
        }
    }
    
    /**
     * チェックなしの値設定を使用するか否かを取得する。<br>
     * デフォルト実装では、常にtrueを返す。
     * @return チェックなしの値設定を使用するか否か
     */
    protected boolean getUseOffValue() {
        return true;
    }
    
    /**
     * チェックなしの場合に使用するラベルを取得する。
     * @return チェックなしの場合に使用するラベル
     */
    protected abstract String getOffLabel();
    
    /**
     * チェックなしの場合に使用する値を取得する。<br>
     * デフォルト実装では、デフォルトの値({@link CustomTagConfig#getCheckboxOffValue()})を返す。
     * @return チェックなしの場合に使用する値
     */
    protected String getOffValue() {
        return TagUtil.getCustomTagConfig().getCheckboxOffValue();
    }
}
