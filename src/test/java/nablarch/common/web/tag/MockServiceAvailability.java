package nablarch.common.web.tag;

import nablarch.common.availability.BasicServiceAvailability;
import nablarch.core.db.transaction.SimpleDbTransactionManager;

import java.util.HashMap;
import java.util.Map;


public class MockServiceAvailability extends BasicServiceAvailability {

    /** データロードに使用するSimpleDbTransactionManagerのインスタンス。 */
//    private SimpleDbTransactionManager dbManager;

    /** リクエストテーブル検索クエリ。 */
//    private String query;

    /** リクエストIDを管理するリクエストテーブル名称。 */
//    private String tableName;

    /** リクエストテーブルに格納されているリクエストIDを保持する項目名称。 */
    private String requestTableRequestIdColumnName;

    /** リクエストテーブルに格納されているサービス提供可否状態を保持する項目名称。 */
    private String requestTableServiceAvailableColumnName;

    /** リクエストテーブルに格納されているサービス提供可否状態項目の状態：提供可を表す文字列。 */
    private String requestTableServiceAvailableOkStatus = "1";

    /**
     * データベースへの検索に使用するSimpleDbTransactionManagerインスタンスを設定する。
     *
     * @param dbManager SimpleDbTransactionManagerのインスタンス
     */
    public void setDbManager(SimpleDbTransactionManager dbManager) {
//        this.dbManager = dbManager;
    }

    /**
     * リクエストに紐付くリクエストテーブル名称を設定する。
     *
     * @param tableName リクエストテーブル名称
     */
    public void setTableName(String tableName) {
//        this.tableName = tableName;
    }

    /**
     * リクエストテーブルのリクエストID項目名称を設定する。
     *
     * @param requestTableRequestIdColumnName リクエストID項目名称
     */
    public void setRequestTableRequestIdColumnName(
            String requestTableRequestIdColumnName) {
        this.requestTableRequestIdColumnName = requestTableRequestIdColumnName;
    }

    /**
     * リクエストテーブルのサービス提供可否状態項目名称を設定する。
     *
     * @param requestTableServiceAvailableColumnName サービス提供可否状態項目名称
     */
    public void setRequestTableServiceAvailableColumnName(
            String requestTableServiceAvailableColumnName) {
        this.requestTableServiceAvailableColumnName = requestTableServiceAvailableColumnName;
    }

    /**
     * リクエストテーブルのサービス提供可否状態項目の状態：提供可を表す文字列を設定する。
     *
     * @param requestTableServiceAvailableOkStatus サービス提供可否状態項目の状態：提供可を表す文字列
     */
    public void setRequestTableServiceAvailableOkStatus(
            String requestTableServiceAvailableOkStatus) {
        this.requestTableServiceAvailableOkStatus = requestTableServiceAvailableOkStatus;
    }

    /**
     * パラメータのリクエストIDのサービス提供可否状態を判定し、結果を返却する。<br>
     *
     * @param requestId リクエストID
     * @return サービス提供可否状態を表すboolean （提供可の場合、TRUE）
     */
    @Override
    public boolean isAvailable(final String requestId) {
//        SqlResultSet resultSet = new SimpleDbTransactionExecutor<SqlResultSet>(dbManager) {
//            @Override
//            public SqlResultSet execute(AppDbConnection connection) {
//                SqlPStatement prepared = connection.prepareStatement(query);
//                int parameterIndex = 1;
//                prepared.setString(parameterIndex++, requestId);
//                prepared.setString(parameterIndex,
//                        requestTableServiceAvailableOkStatus);
//                return prepared.retrieve();
//            }
//        }
//        .doTransaction();
    	String request  = null;
    	request = requests.get(requestId);
    	if (!(request == null) && request.equals(requestTableServiceAvailableOkStatus)){
    		return true;
    	}else{
    		return false;
    	}
    }

    private Map<String, String> requests = new HashMap<String, String>();
    public void setRequest(String[][] requests){
        for (String[] params: requests) {
            String appliedRequestId = params[0];
            String appliedServiceAvailable = params[1];
            this.requests.put(appliedRequestId, appliedServiceAvailable);
        }
    }
    public void dropRequest(){
    	requests = new HashMap<String, String>();
    }


    /** SQL文を初期化する。 */
    public void initialize() {
//        query = buildQuery();
    }

    /**
     * リクエストテーブル検索クエリを生成する。
     *
     * @return リクエストテーブル検索クエリ
     */
//    protected String buildQuery() {
//        return "SELECT " + requestTableRequestIdColumnName
//                + " FROM " + tableName + " "
//                + " WHERE " + requestTableRequestIdColumnName + " = ?"
//                + " AND " + requestTableServiceAvailableColumnName + " = ?";
//    }

}
