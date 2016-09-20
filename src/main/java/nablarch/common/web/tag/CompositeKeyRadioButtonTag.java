package nablarch.common.web.tag;

/**
 * 複数キーのデータに対して、入力データ復元とHTMLエスケープを行うinputタグ(type="radio")を出力するクラス。。
 *
 * @author Koichi Asano 
 *
 */
public class CompositeKeyRadioButtonTag extends CompositeKeyCheckedInputTagSupport {

    @Override
    protected String getTagName() {
        return "compositeKeyRadioButton";
    }
    
    @Override
    protected String getType() {
        return "radio";
    }
}
