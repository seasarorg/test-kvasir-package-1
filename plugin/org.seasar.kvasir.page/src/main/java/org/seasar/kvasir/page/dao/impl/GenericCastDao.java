package org.seasar.kvasir.page.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.cms.database.SQLRuntimeException;
import org.seasar.kvasir.page.dao.CastDao;
import org.seasar.kvasir.page.dao.CastDto;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class GenericCastDao extends BeantableDaoBase<CastDto>
    implements CastDao
{
    @Override
    protected Class<CastDto> getDtoClass()
    {
        return CastDto.class;
    }


    public boolean isUserInRole(Integer roleId, Integer userId)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            ResultSetHandler h = new ScalarHandler();
            Object[] params = new Object[] { roleId, userId, userId };
          return (((Number)run
                .query(con, getQuery("isUserInRole"), params, h)).intValue() > 0);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public void giveRoleToUser(Integer roleId, Integer userId)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            ResultSetHandler h = new ScalarHandler();
            Object[] params = new Object[] { roleId, userId };
            if (((Number)run.query(con, getQuery("giveRoleToUser.exists"),
                params, h)).intValue() == 0) {
                // 存在しないので追加する。
                run.update(con, getQuery("giveRoleToUser.insert"), params);
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public void giveRoleToGroup(Integer roleId, Integer groupId)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            ResultSetHandler h = new ScalarHandler();
            Object[] params = new Object[] { roleId, groupId };
            if (((Number)run.query(con, getQuery("giveRoleToGroup.exists"),
                params, h)).intValue() == 0) {
                // 存在しないので追加する。
                run.update(con, getQuery("giveRoleToGroup.insert"), params);
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }
}
