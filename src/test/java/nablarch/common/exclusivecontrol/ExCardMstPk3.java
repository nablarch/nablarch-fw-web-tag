package nablarch.common.exclusivecontrol;

/**
 * 排他制御のテスト用の主キークラス。
 * @author Kiyohito Itoh
 */
public class ExCardMstPk3 extends ExclusiveControlContext {

    public enum PK { CARD_ID };
    
    public ExCardMstPk3(String cardId) {
        setTableName("EXCLUSIVE_CARD_MST3");
        setVersionColumnName("VERSION");
        setPrimaryKeyColumnNames(PK.values());
        appendCondition(PK.CARD_ID, cardId);
    }
}
