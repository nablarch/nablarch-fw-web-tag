package nablarch.common.exclusivecontrol;

/**
 * 排他制御のテスト用の主キークラス。
 * @author Kiyohito Itoh
 */
public class ExUserMstPk2 extends ExclusiveControlContext {

    public enum PK { USER_ID };
    
    public ExUserMstPk2(String userId) {
        setTableName("EXCLUSIVE_USER_MST2");
        setVersionColumnName("VERSION");
        setPrimaryKeyColumnNames(PK.values());
        appendCondition(PK.USER_ID, userId);
    }
}
