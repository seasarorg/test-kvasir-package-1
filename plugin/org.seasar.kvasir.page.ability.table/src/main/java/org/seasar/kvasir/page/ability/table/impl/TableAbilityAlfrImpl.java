package org.seasar.kvasir.page.ability.table.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.seasar.cms.database.SQLRuntimeException;
import org.seasar.cms.database.identity.Identity;
import org.seasar.framework.container.annotation.tiger.Aspect;
import org.seasar.kvasir.base.dao.DaoPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.AbstractPageAbilityAlfr;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.AttributeFilter;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.table.TableAbility;
import org.seasar.kvasir.page.ability.table.TableAbilityAlfr;
import org.seasar.kvasir.page.ability.table.TableAbilityPlugin;
import org.seasar.kvasir.page.extension.PageAbilityAlfrElement;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.InputStreamFactory;
import org.seasar.kvasir.util.io.impl.ByteArrayInputStreamFactory;
import org.seasar.kvasir.util.io.impl.FileInputStreamFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class TableAbilityAlfrImpl extends AbstractPageAbilityAlfr
    implements TableAbilityAlfr
{
    private static final int COUNT_THRESHOLD = 500;

    private PageAbilityAlfrElement element_;

    private TableAbilityPlugin plugin_;

    private DataSource ds_;

    private DaoPlugin daoPlugin_;

    private PageAlfr pageAlfr_;


    public TableAbilityAlfrImpl()
    {
    }


    /*
     * TableAbilityAlfr
     */

    public String[] getTableNames(Page page)
    {
        if (page == null) {
            return new String[0];
        }
        return getTableNames0(page);
    }


    String[] getTableNames0(Page page)
    {
        List<String> list = new ArrayList<String>();
        String[] gardIds = page.getGardIds();
        for (int i = 0; i < gardIds.length; i++) {
            list.addAll(Arrays.asList(plugin_.getTableNames(gardIds[i])));
        }
        list.addAll(Arrays.asList(plugin_.getTableNames(null)));
        return (String[])list.toArray(new String[0]);
    }


    /*
     * AbstractPageAbilityAlfr
     */

    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
        element_ = null;
        plugin_ = null;
        ds_ = null;
        daoPlugin_ = null;
    }


    /*
     * PageAbilityAlfr
     */

    public PageAbility getAbility(Page page)
    {
        return new TableAbilityImpl(this, page);
    }


    public Class<? extends PageAbility> getAbilityInterfaceClass()
    {
        return TableAbility.class;
    }


    public String getShortId()
    {
        return SHORTID;
    }


    public void create(Page page)
    {
        // 特に何もしない。
    }


    @Aspect("pageLockingInterceptor")
    public void delete(Page page)
    {
        if (page == null) {
            return;
        }

        int pageId = page.getId();
        String[] tableNames = getTableNames0(page);
        for (int i = 0; i < tableNames.length; i++) {
            deleteRecords(tableNames[i], pageId);
        }
    }


    @Aspect("j2ee.requiredTx")
    void deleteRecords(String tableName, int pageId)
    {
        String column = plugin_.getPageIdColumnName(tableName);
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = ds_.getConnection();
            pst = con.prepareStatement("DELETE FROM " + tableName + " WHERE "
                + column + "=?");
            pst.setInt(1, pageId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con, pst, null);
        }
    }


    public String[] getVariants(Page page)
    {
        return new String[] { Page.VARIANT_DEFAULT };
    }


    public Attribute getAttribute(Page page, String name, String variant)
    {
        if (page == null) {
            return null;
        }

        Attribute attr = null;
        if (isTableBound(name, page) && Page.VARIANT_DEFAULT.equals(variant)) {
            attr = new Attribute();
            attr
                .setStream(SUBNAME_DEFAULT, getRecordsAsInputStream(name, page));
        }
        return attr;
    }


    @Aspect("j2ee.requiredTx")
    InputStreamFactory getRecordsAsInputStream(String tableName, Page page)
    {
        int pageId = page.getId();
        Identity identity = daoPlugin_.getIdentity();
        String[] columns;
        try {
            columns = identity.getColumns(tableName);
        } catch (SQLException ex) {
            throw new SQLRuntimeException("Can't colum names of table '"
                + tableName + "'", ex);
        }
        for (int i = 0; i < columns.length; i++) {
            columns[i] = columns[i].toUpperCase();
        }

        boolean[] pageIdRefs = new boolean[columns.length];
        Set<String> pageIdRefColumnSet = new HashSet<String>(Arrays
            .asList(plugin_.getPageIdRefColumnNames(tableName)));
        for (int i = 0; i < columns.length; i++) {
            pageIdRefs[i] = pageIdRefColumnSet.contains(columns[i]);
        }

        String pageIdColumn = plugin_.getPageIdColumnName(tableName);
        String selectCountSQL = "SELECT COUNT(*) FROM " + tableName + " WHERE "
            + pageIdColumn + "=?";

        StringBuffer sb = new StringBuffer("SELECT ");
        String delim = "";
        for (int i = 0; i < columns.length; i++) {
            sb.append(delim);
            delim = ",";
            sb.append(columns[i]);
        }
        sb.append(" FROM ").append(tableName).append(" WHERE ").append(
            pageIdColumn).append("=? ORDER BY ").append(pageIdColumn);
        String selectRecordsSQL = sb.toString();

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        OutputStream os = null;
        try {
            con = ds_.getConnection();
            pst = con.prepareStatement(selectCountSQL);
            pst.setInt(1, pageId);
            rs = pst.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            pst.close();
            pst = null;

            File tmpFile = null;
            if (count < COUNT_THRESHOLD) {
                os = new ByteArrayOutputStream();
            } else {
                tmpFile = File.createTempFile("kvasir", null);
                tmpFile.deleteOnExit();
                os = new FileOutputStream(tmpFile);
            }
            Writer writer = new BufferedWriter(new OutputStreamWriter(os,
                "UTF-8"));

            pst = con.prepareStatement(selectRecordsSQL);
            pst.setInt(1, pageId);
            rs = pst.executeQuery();

            CSVWriter csvWriter = new CSVWriter(writer);
            csvWriter.writeNext(columns);

            String[] record = new String[columns.length];
            while (rs.next()) {
                for (int i = 0; i < record.length; i++) {
                    if (pageIdRefs[i]) {
                        String referredPagePathname;
                        Page referredPage = pageAlfr_.getPage(rs.getInt(i + 1));
                        if (referredPage != null) {
                            referredPagePathname = referredPage.getPathname();
                        } else {
                            referredPagePathname = Page.PATHNAME_ROOT;
                        }
                        record[i] = PageUtils.encodePathname(page,
                            referredPagePathname);
                    } else {
                        record[i] = rs.getString(i + 1);
                    }
                }
                csvWriter.writeNext(record);
            }
            csvWriter.close();
            if (tmpFile != null) {
                return new FileInputStreamFactory(tmpFile);
            } else {
                return new ByteArrayInputStreamFactory(
                    ((ByteArrayOutputStream)os).toByteArray());
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con, pst, rs);
            IOUtils.closeQuietly(os);
        }
    }


    boolean isTableBound(String tableName, Page page)
    {
        String[] gardIds = page.getGardIds();
        for (int i = 0; i < gardIds.length; i++) {
            String[] tableNames = plugin_.getTableNames(gardIds[i]);
            for (int j = 0; j < tableNames.length; j++) {
                if (tableNames[j].equalsIgnoreCase(tableName)) {
                    return true;
                }
            }
        }
        String[] tableNames = plugin_.getTableNames(null);
        for (int i = 0; i < tableNames.length; i++) {
            if (tableNames[i].equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }


    @Aspect("pageLockingInterceptor")
    public void setAttribute(Page page, String name, String variant,
        Attribute attr)
    {
        if (page == null) {
            return;
        }

        if (isTableBound(name, page) && Page.VARIANT_DEFAULT.equals(variant)) {
            InputStreamFactory isf = attr.getStream(SUBNAME_DEFAULT);
            setRecords(name, page, isf);
        }
    }


    @Aspect("j2ee.requiredTx")
    void setRecords(String tableName, Page page, InputStreamFactory isf)
    {
        int pageId = page.getId();
        Identity identity = daoPlugin_.getIdentity();
        String[] columns;
        try {
            columns = identity.getColumns(tableName);
        } catch (SQLException ex) {
            throw new SQLRuntimeException("Can't colum names of table '"
                + tableName + "'", ex);
        }
        Set<String> columnSet = new HashSet<String>();
        for (int i = 0; i < columns.length; i++) {
            columnSet.add(columns[i].toUpperCase());
        }

        String pageIdColumn = plugin_.getPageIdColumnName(tableName);
        Set<String> pageIdRefColumnSet = new HashSet<String>(Arrays
            .asList(plugin_.getPageIdRefColumnNames(tableName)));
        Set<String> idColumnSet = new HashSet<String>(Arrays.asList(plugin_
            .getIdColumnNames(tableName)));

        Connection con = null;
        PreparedStatement pst = null;
        InputStream is = null;
        try {
            con = ds_.getConnection();
            pst = con.prepareStatement("DELETE FROM " + tableName + " WHERE "
                + plugin_.getPageIdColumnName(tableName) + "=?");
            pst.setInt(1, pageId);
            pst.executeUpdate();
            pst.close();
            pst = null;

            is = isf.getInputStream();
            Reader reader = new BufferedReader(new InputStreamReader(is,
                "UTF-8"));
            CSVReader csvReader = new CSVReader(reader);
            columns = csvReader.readNext();
            if (columns == null) {
                return;
            }

            boolean[] isEnabled = new boolean[columns.length];
            boolean[] isPageIdRef = new boolean[columns.length];
            boolean[] isId = new boolean[columns.length];
            StringBuffer sb = new StringBuffer("INSERT INTO ")
                .append(tableName).append(" (");
            String delim = "";
            int enabledColumnCounts = 0;
            for (int i = 0; i < columns.length; i++) {
                isEnabled[i] = columnSet.contains(columns[i]);
                if (isEnabled[i]) {
                    isPageIdRef[i] = pageIdRefColumnSet.contains(columns[i]);
                    isId[i] = idColumnSet.contains(columns[i]);

                    if (isId[i]) {
                        continue;
                    }

                    sb.append(delim);
                    delim = ",";
                    sb.append(columns[i]);
                    enabledColumnCounts++;
                }
            }
            if (enabledColumnCounts == 0) {
                return;
            }
            sb.append(") VALUES (");
            delim = "";
            for (int i = 0; i < enabledColumnCounts; i++) {
                sb.append(delim);
                delim = ",";
                sb.append("?");
            }
            sb.append(")");
            pst = con.prepareStatement(sb.toString());

            Integer pageIdInteger = Integer.valueOf(pageId);
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                int idx = 1;
                for (int i = 0; i < columns.length; i++) {
                    if (isEnabled[i]) {
                        if (isId[i]) {
                            continue;
                        }

                        Object value;
                        if (columns[i].equals(pageIdColumn)) {
                            value = pageIdInteger;
                        } else if (isPageIdRef[i]) {
                            Page referredPage = PageUtils.decodeToPage(page,
                                record[i]);
                            if (referredPage != null) {
                                value = new Integer(referredPage.getId());
                            } else {
                                value = new Integer(Page.ID_ROOT);
                            }
                        } else {
                            // 数値カラムの場合は数値にしないとまずい？
                            value = record[i];
                        }
                        pst.setObject(idx++, value);
                    }
                }
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con, pst, null);
            IOUtils.closeQuietly(is);
        }
    }


    @Aspect("pageLockingInterceptor")
    public void removeAttribute(Page page, String name, String variant)
    {
        if (page == null) {
            return;
        }

        if (isTableBound(name, page) && Page.VARIANT_DEFAULT.equals(variant)) {
            deleteRecords(name, page.getId());
        }
    }


    @Aspect("pageLockingInterceptor")
    public void clearAttributes(Page page)
    {
        if (page == null) {
            return;
        }

        String[] tableNames = getTableNames0(page);
        int pageId = page.getId();
        for (int i = 0; i < tableNames.length; i++) {
            deleteRecords(tableNames[i], pageId);
        }
    }


    public boolean containsAttribute(Page page, String name, String variant)
    {
        if (page == null) {
            return false;
        }

        return (isTableBound(name, page) && Page.VARIANT_DEFAULT
            .equals(variant));
    }


    public Iterator<String> attributeNames(Page page, String variant,
        AttributeFilter filter)
    {
        if (page == null) {
            return new ArrayList<String>().iterator();
        }

        return new ArrayList<String>(Arrays.asList(getTableNames0(page)))
            .iterator();
    }


    /*
     * for framework
     */

    public void setElement(PageAbilityAlfrElement element)
    {
        element_ = element;
    }


    public void setPlugin(TableAbilityPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setDataSource(DataSource ds)
    {
        ds_ = ds;
    }


    public void setDaoPlugin(DaoPlugin daoPlugin)
    {
        daoPlugin_ = daoPlugin;
    }


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }
}
