package nablarch.common.web.tag;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="checkbox")を出力するクラス。<br>
 * n:checkboxesタグで表示できないレイアウト時に使用する。
 * @author Kiyohito Itoh
 */
public class CheckboxTag extends CheckboxTagSupport {

    /** チェックなしの値設定を使用するか否か */
    private boolean useOffValue = true;
    
    /** チェックなしの場合に使用するラベル */
    private String offLabel;
    
    /** チェックなしの場合に使用する値 */
    private String offValue;
    
    /**
     * チェックなしの場合に使用するラベルを設定する。
     * @param offLabel チェックなしの場合に使用するラベル
     */
    public void setOffLabel(String offLabel) {
        this.offLabel = offLabel;
    }
    
    /**
     * チェックなしの場合に使用する値を設定する。
     * @param offValue チェックなしの場合に使用する値
     */
    public void setOffValue(String offValue) {
        this.offValue = offValue;
    }

    /**
     * チェックなしの値設定を使用するか否かを設定する。<br>
     * デフォルトはtrue。
     * @param useOffValue チェックなしの値設定を使用するか否か
     */
    public void setUseOffValue(boolean useOffValue) {
        this.useOffValue = useOffValue;
    }

    @Override
    protected String getOffLabel() {
        return offLabel;
    }

    /**
     * {@inheritDoc}<br>
     * offValueが指定されていない場合は、デフォルト値を返す。
     */
    protected String getOffValue() {
        return offValue != null ? offValue : super.getOffValue();
    }
    
    @Override
    protected boolean getUseOffValue() {
        return useOffValue;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "checkbox";
    }
}
