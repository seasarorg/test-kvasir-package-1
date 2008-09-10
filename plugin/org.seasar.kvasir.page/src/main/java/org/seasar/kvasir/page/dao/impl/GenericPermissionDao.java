package org.seasar.kvasir.page.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.cms.database.SQLRuntimeException;
import org.seasar.kvasir.page.ability.Permission;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PrivilegeLevel;
import org.seasar.kvasir.page.dao.PermissionDao;
import org.seasar.kvasir.page.dao.PermissionDto;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class GenericPermissionDao extends BeantableDaoBase<PermissionDto>
    implements PermissionDao
{
    @Override
    protected Class<PermissionDto> getDtoClass()
    {
        return PermissionDto.class;
    }


    public boolean permits(Integer pageId, Integer userId, Privilege priv)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            ResultSetHandler h = new ScalarHandler();
            Integer type = new Integer(priv.getType().getValue());
            Object[] params = new Object[] { pageId, type, userId, userId,
                userId };
            Number level = (Number)run.query(con, getQuery("permits"), params,
                h);
            if (level.intValue() == PrivilegeLevel.NEVER.getValue()) {
                return false;
            } else {
                return (level.intValue() >= priv.getLevel().getValue());
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public List<Permission> getPermissionListByPageId(Integer pageId)
    {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(getQuery("getPermissionListByPageId"));
            int idx = 1;
            pst.setObject(idx++, pageId);
            rs = pst.executeQuery();
            List<Permission> list = new ArrayList<Permission>();
            while (rs.next()) {
                list.add(new Permission(rs.getInt(1), rs.getInt(2), rs
                    .getInt(3)));
            }

            return list;
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con, pst, rs);
        }
    }


    public void setPermissionsByPageId(Integer pageId, Permission[] perms)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            run.update(con, getQuery("setPermissionsByPageId.clear"), pageId);
            Object[] params = new Object[] { pageId, null, null, null };
            for (int i = 0; i < perms.length; i++) {
                params[1] = new Integer(perms[i].getRole().getId());
                Privilege priv = perms[i].getPrivilege();
                params[2] = new Integer(priv.getType().getValue());
                params[3] = new Integer(priv.getLevel().getValue());
                run.update(con, getQuery("setPermissionsByPageId.insert"),
                    params);
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public void clearPermissionsByPageId(Integer pageId)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            run.update(con, getQuery("clearPermissionsByPageId"), pageId);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public void grantPrivilegeByPageId(Integer pageId, Integer roleId,
        Privilege priv)
    {
        Connection con = null;
        try {
            con = getConnection();

            ResultSetHandler h = new ScalarHandler();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("pageid", pageId);
            paramMap.put("roleid", roleId);
            paramMap.put("type", new Integer(priv.getType().getValue()));
            paramMap.put("level", new Integer(priv.getLevel().getValue()));

            if (((Number)runQuery(con, "grantPrivilegeByPageId.exists",
                paramMap, h)).intValue() == 0) {
                // 存在しないのでタプルを挿入する。
                runUpdate(con, "grantPrivilegeByPageId.insert", paramMap);
            } else {
                // 存在するのでタプルを変更する。
                runUpdate(con, "grantPrivilegeByPageId.update", paramMap);
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public void revokePrivilegeByPageIdAndRoleIdAndPrivType(Integer pageId,
        Integer roleId, Integer privType)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            run.update(con,
                getQuery("revokePrivilegeByPageIdAndRoleIdAndPrivType"),
                new Object[] { pageId, roleId, privType });
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public void revokePrivilegesByPageIdAndRoleId(Integer pageId, Integer roleId)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            run.update(con, getQuery("revokePrivilegesByPageIdAndRoleId"),
                new Object[] { pageId, roleId });
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public void revokePrivilegesByRoleId(Integer roleId)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            run.update(con, getQuery("revokePrivilegesByRoleId"), roleId);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public Number getPrivilegeLevelByPageIdAndRoleIdAndPrivType(Integer pageId,
        Integer roleId, Integer privType)
    {
        Connection con = null;
        try {
            con = getConnection();

            QueryRunner run = new QueryRunner();
            ResultSetHandler h = new ScalarHandler();
            Number level = (Number)run.query(con,
                getQuery("getPrivilegeLevelByPageIdAndRoleIdAndPrivType"),
                new Object[] { pageId, roleId, privType }, h);
            if ((level == null)
                || (level.intValue() == PrivilegeLevel.NEVER.getValue())) {
                return new Integer(PrivilegeLevel.NONE.getValue());
            } else {
                return level;
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }
}
