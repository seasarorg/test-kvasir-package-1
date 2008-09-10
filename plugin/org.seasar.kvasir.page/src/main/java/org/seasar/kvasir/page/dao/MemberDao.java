package org.seasar.kvasir.page.dao;

import org.seasar.dao.annotation.tiger.Sql;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface MemberDao
{
    @Sql("SELECT COUNT(*) FROM member WHERE groupid=? AND userid=?")
    boolean containsUserByGroupId(Number groupId, Number userId);


    void addUserByGroupId(Number groupId, Number userId);


    @Sql("DELETE FROM member WHERE groupid=? AND userid=?")
    void removeUserByGroupId(Number groupId, Number userId);


    @Sql("DELETE FROM member WHERE groupid=?")
    void clearUsersByGroupId(Number groupId);


    @Sql("DELETE FROM member WHERE userid=?")
    void clearGroupsByUserId(Number userId);


    @Sql("SELECT userid FROM member WHERE groupid=? ORDER BY userid")
    Number[] getUserIdsByGroupId(Number groupId);


    @Sql("SELECT groupid FROM member WHERE userid=? ORDER BY groupid")
    Number[] getGroupIdsByUserId(Number userId);
}
