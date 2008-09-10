package org.seasar.kvasir.page.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.cms.database.SQLRuntimeException;
import org.seasar.kvasir.page.dao.MemberDao;
import org.seasar.kvasir.page.dao.MemberDto;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class GenericMemberDao extends BeantableDaoBase<MemberDto>
    implements MemberDao
{
    @Override
    protected Class<MemberDto> getDtoClass()
    {
        return MemberDto.class;
    }


    public void addUserByGroupId(Number groupId, Number userId)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            ResultSetHandler h = new ScalarHandler();
            Object[] params = new Object[] { groupId, userId };
            if (((Number)run.query(con, getQuery("addUserByGroupId.exists"),
                params, h)).intValue() == 0) {
                // 存在しないので追加する。
                run.update(con, getQuery("addUserByGroupId.insert"), params);
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }
}
