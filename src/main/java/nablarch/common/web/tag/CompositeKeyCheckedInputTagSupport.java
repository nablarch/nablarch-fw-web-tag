package nablarch.common.web.tag;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import nablarch.common.web.compositekey.CompositeKey;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;


/**
 * 複数キーのデータに対してradio、checkboxといった選択型の入力を実現する特殊なタグの作成を助けるクラス。
 *
 * @author Koichi Asano 
 *
 */
public abstract class CompositeKeyCheckedInputTagSupport extends CheckedInputTagSupport {

    /**
     * CompositeKey ではない場合に、リクエストスコープまたはパラメータから復元した「選択済み項目」
     * を保存しておく際に使用するキーのプレフィクス。 
     */
    private static final String REQUEST_VALUE_CACHE_KEY_PREFIX = ExecutionContext.FW_PREFIX + "request_value_";

    /** リクエストパラメータのプレフィクス */
    private String namePrefix;

    /** パラメータのキー名 */
    private List<String> keyNames = null;
    
    /** 値のオブジェクト(MapまたはJavaBeans形式のクラス) */
    private Object valueObject;
    
    /**
     * リクエストパラメータのプレフィクスを設定する。 
     * @param namePrefix リクエストパラメータのプレフィクス
     */
    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    /**
     * パラメータのキー名を設定する。 
     * @param keyNames パラメータのキー名
     */
    public void setKeyNames(String keyNames) {
        this.keyNames = TagUtil.getCommaSeparatedValueAsList(keyNames);
    }

    /**
     * 値のオブジェクト(MapまたはJavaBeans形式のクラス)を設定する。 
     * @param valueObject 値のオブジェクト(MapまたはJavaBeans形式のクラス)
     */
    public void setValueObject(Object valueObject) {
        this.valueObject = valueObject;
    }
    
    @Override
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();
        
        validateNameAndNamePrefix();

        // nablarch_hidden にパラメータを保存
        String name = getAttributes().get(HtmlAttribute.NAME);
        String valueList = TagUtil.storeKeyValueSetToHidden(pageContext, name, valueObject, keyNames, namePrefix);

        // 値を設定
        setValue(valueList);
        
        return super.doStartTag();
    }

    /**
     * name 属性が namePrefix 属性と keyNames 属性の組み合わせと重複していないかチェックする。
     * 
     * @throws IllegalArgumentException name 属性が namePrefix 属性と keyNames 属性の組み合わせと重複していた場合。
     */
    private void validateNameAndNamePrefix() throws IllegalArgumentException {
        String name = getAttributes().get(HtmlAttribute.NAME);
        for (String keyName : keyNames) {
            if (name.equals(namePrefix + "." + keyName)) {
                throw new IllegalArgumentException(
                        "name and namePrefix and keyNames attribute was wrong." 
                        + " name attribute must be different from name Prefix and key combination." 
                        + " name attribute = [" + name + "],"
                        + " namePrefix attribute = [" + namePrefix + "],"
                        + " keyNames attribute = " + keyNames + ""
                        );
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean isChecked() {        
        List<String> values = new ArrayList<String>();
        
        // このタグのvalueObjectからマルチキーを生成
        for (String key : keyNames) {
            Object obj = TagUtil.getPropertyFromObject(valueObject, key);
            if (obj == null) {
                // 1つでもキーがなければ false 
                return false;
            }
            values.add(obj.toString());
        }
        String valueStr = StringUtil.join(",", values);
        
        Object value = TagUtil.getValue(pageContext, (String) getAttributes()
                .get(HtmlAttribute.NAME), true);

        if (value != null && value instanceof CompositeKey) {
            return valueStr.equals(value.toString());
        }

        // CompositeKey じゃない場合、頑張ってキーを作成
        List<String> requestValues = (List<String>) pageContext.getAttribute(REQUEST_VALUE_CACHE_KEY_PREFIX + getAttributes()
                .get(HtmlAttribute.NAME), PageContext.REQUEST_SCOPE);
        
        if (requestValues == null) {
            requestValues = TagUtil.createCompositeKeyValueList(pageContext,
                        namePrefix, keyNames);
            pageContext.setAttribute(CompositeKeyCheckedInputTagSupport.REQUEST_VALUE_CACHE_KEY_PREFIX + getAttributes()
                        .get(HtmlAttribute.NAME), requestValues, PageContext.REQUEST_SCOPE);

        }
        
        return requestValues.contains(valueStr);
    }
}
