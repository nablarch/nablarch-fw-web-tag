package nablarch.common.exclusivecontrol;

/**
 * テスト用のカスタマイズ。
 * @author Kiyohito Itoh
 */
public class CustomExclusiveControlManager extends BasicExclusiveControlManager {
    
    @Override
    protected String getUpdateSqlTemplate() {
        return "UPDATE $TABLE_NAME$ SET $VERSION$ = ($VERSION$ + 10) WHERE $PRIMARY_KEYS_CONDITION$";
    }
}
