package org.seasar.kvasir.page.dao;

import org.seasar.dao.annotation.tiger.Sql;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface CastDao
{
    @Sql("SELECT userid FROM casto WHERE roleid=? AND userid<>0 ORDER BY userid")
    Number[] getUserIdsByRoleId(Integer roleId);


    @Sql("SELECT groupid FROM casto WHERE roleid=? AND groupid<>0 ORDER BY groupid")
    Number[] getGroupIdsByRoleId(Integer roleId);


    @Sql("SELECT roleid FROM casto WHERE userid=? ORDER BY roleid")
    Number[] getRoleIdsByUserId(Integer integer);


    @Sql("SELECT roleid FROM casto WHERE groupid=? ORDER BY roleid")
    Number[] getRoleIdsByGroupId(Integer integer);


    @Sql("SELECT COUNT(*) FROM casto WHERE roleid=? AND userid=?")
    boolean containsUserByRoleId(Integer roleId, Integer userId);


    @Sql("SELECT COUNT(*) FROM casto WHERE roleid=? AND groupid=?")
    boolean containsGroupByRoleId(Integer roleId, Integer groupId);


    boolean isUserInRole(Integer roleId, Integer userId);


    void giveRoleToUser(Integer roleId, Integer userId);


    void giveRoleToGroup(Integer roleId, Integer groupId);


    @Sql("DELETE FROM casto WHERE roleid=? AND userid=?")
    void deleteUserByRoleId(Integer roleId, Integer userId);


    @Sql("DELETE FROM casto WHERE roleid=? AND groupid=?")
    void deleteGroupByRoleId(Integer roleId, Integer groupId);


    @Sql("DELETE FROM casto WHERE roleid=? AND groupid=0")
    void deleteAllUsersByRoleId(Integer roleId);


    @Sql("DELETE FROM casto WHERE roleid=? AND userid=0")
    void deleteAllGroupsByRoleId(Integer roleId);


    @Sql("DELETE FROM casto WHERE roleid=?")
    void deleteByRoleId(Integer roleId);


    @Sql("DELETE FROM casto WHERE userid=?")
    void deleteByUserId(Integer userId);


    @Sql("DELETE FROM casto WHERE groupid=?")
    void deleteByGroupId(Integer groupId);
}
