package nablarch.common.web.tag;

import nablarch.common.permission.Permission;
import nablarch.common.permission.PermissionUtil;

/**
 * 認可情報に基づき、サブミットを行うタグの表示制御を行う必要があるか否かを判定するクラス。
 * 
 * @author Tomokazu Kagawa
 */
public class PermissionDisplayControlChecker implements DisplayControlChecker {

    /**
     * {@inheritDoc}
     */
    public boolean needsDisplayControl(String requestId) {

        Permission permission = PermissionUtil.getPermission();
        if (permission != null) {
            return !permission.permit(requestId);
        }
        return false;
    }
}
