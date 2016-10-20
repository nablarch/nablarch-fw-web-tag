package nablarch.common.exclusivecontrol;

/**
 * 排他制御のテスト用の主キークラス。
 * @author Habu Miki
 */
public class ExUserMstLowerPk extends ExclusiveControlContext {

    public enum PK { user_id, pk2, pk3 };
    
    public ExUserMstLowerPk(String userId, String pk2, String pk3) {
        setTableName("EXCLUSIVE_USER_MST");
        setVersionColumnName("VERSION");
        setPrimaryKeyColumnNames(PK.values());
        appendCondition(PK.user_id, userId);
        appendCondition(PK.pk2, pk2);
        appendCondition(PK.pk3, pk3);
    }
}
