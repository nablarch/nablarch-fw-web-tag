package nablarch.common.web.tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.jsp.JspException;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="hidden")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class PlainHiddenTag extends InputTagSupport {

    /**
     * {@inheritDoc}
     * <pre>
     * 入力画面と確認画面で同じ出力を行う。
     * 画面間の状態維持のために確認画面でhiddenタグを参照したいケースがあるので、
     * 確認画面でも入力画面を同じ出力を行う。
     * 
     * value属性に指定されたvalueを設定する。
     * type属性に"text"を設定する。
     * 属性はHTMLエスケープして出力する。
     * </pre>
     */
    public int doStartTag() throws JspException {

        checkChildElementsOfForm();

        getAttributes().put(HtmlAttribute.TYPE, "hidden");
        String name = getAttributes().get(HtmlAttribute.NAME);
        Collection<?> values = TagUtil.getMultipleValues(pageContext, name);
        String ls = TagUtil.getCustomTagConfig().getLineSeparator();

        StringBuilder sb = new StringBuilder();
        for (HtmlAttributes attrs : createHiddenAttributesList(getAttributes(), values)) {
            if (sb.length() != 0) {
                sb.append(ls);
            }
            sb.append(TagUtil.createTagWithoutBody("input", attrs));
        }
        TagUtil.print(pageContext, sb.toString());
        TagUtil.getFormContext(pageContext).addInputName(name);

        return SKIP_BODY;
    }

    /**
     * ベースとなるHTML属性に指定された値を追加してhidden用のHTML属性リストを作成する。
     * 
     * @param baseAttributes ベースとなるHTML属性
     * @param values value属性の値リスト
     * @return hidden用のHTML属性リスト
     */
    private List<HtmlAttributes> createHiddenAttributesList(HtmlAttributes baseAttributes, Collection<?> values) {

        List<HtmlAttributes> hiddenAttributes = new ArrayList<HtmlAttributes>();

        if (values.isEmpty()) {
            hiddenAttributes.add(createHiddenAttributes(baseAttributes, ""));
            return hiddenAttributes;
        }

        for (Object value : values) {
            hiddenAttributes.add(createHiddenAttributes(baseAttributes, value != null ? value : ""));
        }
        return hiddenAttributes;
    }

    /**
     * ベースとなるHTML属性に指定された値を追加してhidden用のHTML属性を作成する。
     * 
     * @param baseAttributes ベースとなるHTML属性
     * @param value value属性の値
     * @return hidden用のHTML属性
     */
    private HtmlAttributes createHiddenAttributes(HtmlAttributes baseAttributes, Object value) {
        HtmlAttributes htmlAttributes = new HtmlAttributes();
        htmlAttributes.putAll(baseAttributes);
        htmlAttributes.put(HtmlAttribute.VALUE, value);
        return htmlAttributes;
    }

    @Override
    protected String getTagName() {
        return "plainHidden";
    }
}
