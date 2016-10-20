package nablarch.common.exclusivecontrol;

public class ExclusiveTest1Pk extends ExclusiveControlContext {

    private enum PK {
        ID
    }
    public ExclusiveTest1Pk(String id) {
        setTableName("EXCLUSIVE_TEST_1");
        setVersionColumnName("VERSION");
        setPrimaryKeyColumnNames(ExclusiveTest1Pk.PK.values());
        appendCondition(ExclusiveTest1Pk.PK.ID, id);
    }
}
