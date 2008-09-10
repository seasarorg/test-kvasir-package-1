package org.seasar.kvasir.page.dao;

import java.util.List;

import org.seasar.kvasir.page.ability.Permission;
import org.seasar.kvasir.page.ability.Privilege;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PermissionDao
{
    boolean permits(Integer pageId, Integer userId, Privilege priv);


    List<Permission> getPermissionListByPageId(Integer pageId);


    void setPermissionsByPageId(Integer pageId, Permission[] perms);


    void clearPermissionsByPageId(Integer pageId);


    void grantPrivilegeByPageId(Integer pageId, Integer roleId, Privilege priv);


    void revokePrivilegeByPageIdAndRoleIdAndPrivType(Integer pageId,
        Integer roleId, Integer privType);


    void revokePrivilegesByPageIdAndRoleId(Integer pageId, Integer roleId);


    void revokePrivilegesByRoleId(Integer roleId);


    Number getPrivilegeLevelByPageIdAndRoleIdAndPrivType(Integer pageId,
        Integer roleId, Integer privType);
}
