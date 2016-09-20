package nablarch.common.web.tag;

/**
 * 複数キーのデータに対して、入力データ復元とHTMLエスケープを行うinputタグ(type="checkbox")を出力するクラス。。
 *
 * @author Koichi Asano 
 *
 */
public class CompositeKeyCheckboxTag extends CompositeKeyCheckedInputTagSupport {

    @Override
    protected String getTagName() {
        return "compositeKeyCheckbox";
    }
    
    @Override
    protected String getType() {
        return "checkbox";
    }
}
