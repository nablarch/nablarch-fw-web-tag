package nablarch.common.web.tag;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.SortedSet;

import nablarch.common.permission.Permission;
import nablarch.common.permission.PermissionUtil;

import org.junit.Test;

/**
 * {@link PermissionDisplayControlChecker}の単体テスト。
 *
 * @author takanori tani
 */
public class PermissionDisplayControlCheckerTest {

    /**
     * {@link PermissionDisplayControlChecker#needsDisplayControl(String)}のテスト。
     */
    @Test
    public void testNeedsDisplayControl() {
        PermissionDisplayControlChecker target = new PermissionDisplayControlChecker();
        // permissionが設定されていない場合。
        PermissionUtil.setPermission(null);
        assertFalse("permissionが設定されていない場合は判断できないので表示制御は不要。", target.needsDisplayControl("null"));

        // 権限がない場合(permit=false)
        MockPermission permitNg = new MockPermission();
        permitNg.permit = false;
        PermissionUtil.setPermission(permitNg);
        assertThat("呼び出し前はNull(事前チェック)", permitNg.paramId, nullValue());

        assertTrue("権限がない場合は表示制御が必要。", target.needsDisplayControl("permitNgRequest"));
        assertThat("呼び出したリクエストIDを使用している。", permitNg.paramId, is("permitNgRequest"));

        // 権限がある場合(permit=true)
        MockPermission permitOk = new MockPermission();
        permitOk.permit = true;
        PermissionUtil.setPermission(permitOk);
        assertThat("呼び出し前はNull(事前チェック)", permitOk.paramId, nullValue());

        assertFalse("権限があれば表示制御は不要。", target.needsDisplayControl("permitOkRequest"));
        assertThat("呼び出したリクエストIDを使用している。", permitOk.paramId, is("permitOkRequest"));
    }

    /**
     * テスト用のMockクラス。
     * パラメータのアサートをするために
     */
    private static class MockPermission implements Permission {
        private String paramId = null;
        private boolean permit = false;
        @Override
        public boolean permit(String requestId) {
            paramId = requestId;
            return permit;
        }
        @Override
        public SortedSet<String> getRequestIds() {
            return null;
        }
    }
}
