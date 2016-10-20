package nablarch.common.exclusivecontrol;

/**
 * 排他制御のテスト用の主キークラス。
 *
 * 内容は、{@link ExUserMstPk2}と全く同じ。
 *
 * @author hisaaki sioiri
 */
public class ExUserMstPk3 extends ExclusiveControlContext {
    
    public enum PK { USER_ID }

    public ExUserMstPk3(String userId) {
        setTableName("EXCLUSIVE_USER_MST3");
        setVersionColumnName("VERSION");
        setPrimaryKeyColumnNames(ExUserMstPk3.PK.values());
        appendCondition(ExUserMstPk3.PK.USER_ID, userId);
    }
}
