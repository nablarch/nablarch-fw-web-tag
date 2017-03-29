package nablarch.common.web.tag;

import java.util.HashMap;
import java.util.Map;

import nablarch.common.availability.ServiceAvailability;


public class MockServiceAvailability implements ServiceAvailability {
    /** リクエストテーブルに格納されているサービス提供可否状態項目の状態：提供可を表す文字列。 */
    private String requestTableServiceAvailableOkStatus = "1";


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
        if (requests.get(requestId) != null && requests.get(requestId)
                                                       .equals(requestTableServiceAvailableOkStatus)) {
            return true;
        } else {
            return false;
        }
    }

    private Map<String, String> requests = new HashMap<String, String>();

    public void setRequest(String[][] requests) {
        for (String[] params : requests) {
            String appliedRequestId = params[0];
            String appliedServiceAvailable = params[1];
            this.requests.put(appliedRequestId, appliedServiceAvailable);
        }
    }

    public void dropRequest() {
        requests = new HashMap<String, String>();
    }
}
