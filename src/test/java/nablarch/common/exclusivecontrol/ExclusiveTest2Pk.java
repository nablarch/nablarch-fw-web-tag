package nablarch.common.exclusivecontrol;

public class ExclusiveTest2Pk extends ExclusiveControlContext {

    private enum PK {
        ID
    }
    public ExclusiveTest2Pk(String id) {
        setTableName("EXCLUSIVE_TEST_2");
        setVersionColumnName("VERSION");
        setPrimaryKeyColumnNames(ExclusiveTest2Pk.PK.values());
        appendCondition(ExclusiveTest2Pk.PK.ID, id);
    }
}
