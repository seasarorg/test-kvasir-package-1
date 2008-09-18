package org.seasar.kvasir.page.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.seasar.kvasir.base.log.KvasirLog;


public class DebugQueryRunner extends QueryRunner
{
    private KvasirLog log_;


    public DebugQueryRunner(KvasirLog log)
    {
        log_ = log;
    }


    @Override
    public int[] batch(Connection conn, String sql, Object[][] params)
        throws SQLException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN batch(").append(sql).append("), {");
        String delim = "";
        for (Object[] param : params) {
            sb.append(delim).append(Arrays.asList(param));
            delim = ", ";
        }
        sb.append("}");
        log_.debug(sb.toString());
        try {
            return super.batch(conn, sql, params);
        } finally {
            log_.debug("END batch");
        }
    }


    @Override
    public int[] batch(String sql, Object[][] params)
        throws SQLException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN batch(").append(sql).append("), {");
        String delim = "";
        for (Object[] param : params) {
            sb.append(delim).append(Arrays.asList(param));
            delim = ", ";
        }
        sb.append("}");
        log_.debug(sb.toString());
        try {
            return super.batch(sql, params);
        } finally {
            log_.debug("END batch");
        }
    }


    @Override
    public Object query(Connection conn, String sql, Object param,
        ResultSetHandler rsh)
        throws SQLException
    {
        log_.debug("BEGIN query(" + sql + "), " + param);
        try {
            return super.query(conn, sql, param, rsh);
        } finally {
            log_.debug("END query");
        }
    }


    @Override
    public Object query(Connection conn, String sql, Object[] params,
        ResultSetHandler rsh)
        throws SQLException
    {
        log_.debug("BEGIN query(" + sql + "), " + Arrays.asList(params));
        try {
            return super.query(conn, sql, params, rsh);
        } finally {
            log_.debug("END query");
        }
    }


    @Override
    public Object query(Connection conn, String sql, ResultSetHandler rsh)
        throws SQLException
    {
        log_.debug("BEGIN query(" + sql + ")");
        try {
            return super.query(conn, sql, rsh);
        } finally {
            log_.debug("END query");
        }
    }


    @Override
    public Object query(String sql, Object param, ResultSetHandler rsh)
        throws SQLException
    {
        log_.debug("BEGIN query(" + sql + "), " + param);
        try {
            return super.query(sql, param, rsh);
        } finally {
            log_.debug("END query");
        }
    }


    @Override
    public Object query(String sql, Object[] params, ResultSetHandler rsh)
        throws SQLException
    {
        log_.debug("BEGIN query(" + sql + "), " + Arrays.asList(params));
        try {
            return super.query(sql, params, rsh);
        } finally {
            log_.debug("END query");
        }
    }


    @Override
    public Object query(String sql, ResultSetHandler rsh)
        throws SQLException
    {
        log_.debug("BEGIN query(" + sql + ")");
        try {
            return super.query(sql, rsh);
        } finally {
            log_.debug("END query");
        }
    }


    @Override
    public int update(Connection conn, String sql)
        throws SQLException
    {
        log_.debug("BEGIN update(" + sql + ")");
        try {
            return super.update(conn, sql);
        } finally {
            log_.debug("END update");
        }
    }


    @Override
    public int update(Connection conn, String sql, Object param)
        throws SQLException
    {
        log_.debug("BEGIN update(" + sql + "), " + param);
        try {
            return super.update(conn, sql, param);
        } finally {
            log_.debug("END update");
        }
    }


    @Override
    public int update(Connection conn, String sql, Object[] params)
        throws SQLException
    {
        log_.debug("BEGIN update(" + sql + "), " + Arrays.asList(params));
        try {
            return super.update(conn, sql, params);
        } finally {
            log_.debug("END update");
        }
    }


    @Override
    public int update(String sql)
        throws SQLException
    {
        log_.debug("BEGIN update(" + sql + ")");
        try {
            return super.update(sql);
        } finally {
            log_.debug("END update");
        }
    }


    @Override
    public int update(String sql, Object param)
        throws SQLException
    {
        log_.debug("BEGIN update(" + sql + "), " + param);
        try {
            return super.update(sql, param);
        } finally {
            log_.debug("END update");
        }
    }


    @Override
    public int update(String sql, Object[] params)
        throws SQLException
    {
        log_.debug("BEGIN update(" + sql + "), " + Arrays.asList(params));
        try {
            return super.update(sql, params);
        } finally {
            log_.debug("END update");
        }
    }
}
