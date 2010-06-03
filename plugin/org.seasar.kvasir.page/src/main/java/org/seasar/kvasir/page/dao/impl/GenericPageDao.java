package org.seasar.kvasir.page.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.seasar.cms.beantable.Beantable;
import org.seasar.cms.beantable.Formula;
import org.seasar.cms.beantable.Pair;
import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.cms.database.SQLRuntimeException;
import org.seasar.kvasir.base.dao.QueryHandler;
import org.seasar.kvasir.base.dao.SQLUtils;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.util.collection.LRUMap;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageNotFoundRuntimeException;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.condition.PageConditionParser;
import org.seasar.kvasir.page.condition.PageConditionWithHeimId;
import org.seasar.kvasir.page.condition.ParsedPageCondition;
import org.seasar.kvasir.page.dao.PageDao;
import org.seasar.kvasir.page.dao.PageDto;
import org.seasar.kvasir.page.dao.TableConstants;
import org.seasar.kvasir.page.impl.CachedPair;
import org.seasar.kvasir.page.impl.PageConditionKey;
import org.seasar.kvasir.util.PropertyUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class GenericPageDao extends BeantableDaoBase<PageDto>
    implements PageDao, QueryHandler
{
    private static final String COLUMN_PATHNAME = TableConstants.TABLE_PAGE
        + "." + TableConstants.COL_PAGE_PARENTPATHNAME + " || '/' || "
        + TableConstants.TABLE_PAGE + "." + TableConstants.COL_PAGE_NAME + " "
        + TableConstants.ALIAS_PAGE_PATHNAME;

    private static final String[] PAGELIST_COLUMNS_ID = new String[] {
        TableConstants.TABLE_PAGE + "." + TableConstants.COL_PAGE_ID,
        COLUMN_PATHNAME };

    private static final String[] PAGELIST_COLUMNS_OBJECT;

    private static final String[] PAGELIST_COLUMNS_NAME = new String[] {
        TableConstants.TABLE_PAGE + "." + TableConstants.COL_PAGE_NAME,
        COLUMN_PATHNAME };

    private static final String[] PAGELIST_COLUMNS_COUNT = new String[] { "COUNT(*)" };

    private static final String[] PAGELIST_COLUMNS_PATHNAME = new String[] { COLUMN_PATHNAME };

    protected static final Integer ID_ROOT = new Integer(Page.ID_ROOT);

    private boolean beantableInjected_;

    private boolean pagePluginInjected_;

    protected PagePlugin pagePlugin_;

    protected PageConditionParser parser_;

    private final KvasirLog log_ = KvasirLogFactory
        .getLog(GenericPageDao.class);

    // FIXME サイズを設定で変更できるように。
    private Map<PageConditionKey, CachedPair> cachedPairMap = Collections
        .synchronizedMap(new LRUMap<PageConditionKey, CachedPair>(500));

    static {
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(TableConstants.COLS_PAGE));
        list.add(COLUMN_PATHNAME);
        PAGELIST_COLUMNS_OBJECT = list.toArray(new String[0]);
    }


    @Override
    protected Class<PageDto> getDtoClass()
    {
        return PageDto.class;
    }


    @Override
    public void setBeantable(Beantable<PageDto> beantable)
    {
        super.setBeantable(beantable);
        beantableInjected_ = true;
        if (pagePluginInjected_) {
            preparePageConditionParser();
        }
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
        pagePluginInjected_ = true;
        if (beantableInjected_) {
            preparePageConditionParser();
        }
    }


    void preparePageConditionParser()
    {
        parser_ = new PageConditionParser(beantable_.getIdentity(), pagePlugin_);
    }


    public PageDto getObjectById(Number id)
    {
        Connection con = null;
        try {
            con = getConnection();
            QueryRunner run = newQueryRunner();
            return (PageDto)run.query(con, getQuery("getObjectById"), id,
                beantableHandler_);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    private QueryRunner newQueryRunner()
    {
        if (log_.isDebugEnabled()) {
            return new DebugQueryRunner(log_);
        }
        return new QueryRunner();
    }


    public PageDto getObjectByPathname(Number heimId, String pathname)
    {
        String parentPathname;
        String name;
        int slash = pathname.lastIndexOf('/');
        if (slash < 0) {
            parentPathname = null;
            name = pathname;
        } else {
            parentPathname = pathname.substring(0, slash);
            name = pathname.substring(slash + 1);
        }

        return getObjectByParentPathnameAndName(heimId, parentPathname, name);
    }


    @SuppressWarnings("unchecked")
    public List<PageDto> getObjectListByIds(Number[] ids)
    {
        if (ids.length == 0) {
            return new ArrayList<PageDto>();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('?');
        }

        String template = constructPair("getObjectListByIds",
            new String[] { sb.toString() }, null, ids).getTemplate();

        Connection con = null;
        try {
            con = getConnection();
            QueryRunner run = newQueryRunner();
            return (List<PageDto>)run.query(con, template, ids,
                beantableListHandler_);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public List<Object> getObjectList(PageCondition cond)
    {
        return getPageList(cond, false, PAGELIST_COLUMNS_OBJECT,
            beantableListHandler_);
    }


    public List<Number> getIdList(PageCondition cond)
    {
        List<Object> pageList = getPageList(cond, false, PAGELIST_COLUMNS_ID,
            scalarListHandler_);
        List<Number> idList = new ArrayList<Number>();
        for (Iterator<Object> itr = pageList.iterator(); itr.hasNext();) {
            idList.add((Number)itr.next());
        }
        return idList;
    }


    public List<String> getNameListByParentPathname(Number heimId,
        String parentPathname, PageCondition cond)
    {
        PageCondition pc = preparePageConditionToGetPageListByParentPathname(
            heimId, parentPathname, cond);

        String[] columns = (!cond.isRecursive()) ? PAGELIST_COLUMNS_NAME
            : PAGELIST_COLUMNS_PATHNAME;

        List<Object> pageList = getPageList(pc, false, columns,
            scalarListHandler_);
        List<String> nameList = new ArrayList<String>();
        if (cond.isRecursive()) {
            // 返ってきているのは絶対パス名なので、
            // 相対パス名に変換する。
            int pos = parentPathname.length() + 1;
            for (Iterator<Object> itr = pageList.iterator(); itr.hasNext();) {
                nameList.add(((String)itr.next()).substring(pos));
            }
        } else {
            for (Iterator<Object> itr = pageList.iterator(); itr.hasNext();) {
                nameList.add((String)itr.next());
            }
        }
        return nameList;
    }


    public List<Number> getIdListByParentPathname(Number heimId,
        String parentPathname, PageCondition cond)
    {
        PageCondition pc = preparePageConditionToGetPageListByParentPathname(
            heimId, parentPathname, cond);

        List<Object> pageList = getPageList(pc, false, PAGELIST_COLUMNS_ID,
            scalarListHandler_);
        List<Number> idList = new ArrayList<Number>();
        for (Iterator<Object> itr = pageList.iterator(); itr.hasNext();) {
            idList.add((Number)itr.next());
        }
        return idList;
    }


    public List<Object> getObjectListByParentPathname(Number heimId,
        String parentPathname, PageCondition cond)
    {
        PageCondition pc = preparePageConditionToGetPageListByParentPathname(
            heimId, parentPathname, cond);

        return getPageList(pc, false, PAGELIST_COLUMNS_OBJECT,
            beantableListHandler_);
    }


    public Number getCountByParentPathname(Number heimId,
        String parentPathname, PageCondition cond)
    {
        PageCondition pc = preparePageConditionToGetPageListByParentPathname(
            heimId, parentPathname, cond);

        List<Object> list = getPageList(pc, true, PAGELIST_COLUMNS_COUNT,
            scalarListHandler_);
        // SELECT COUNT(*), MAX( ) ...
        // というSQLを発行しているが、これがH2ではレコードを返さないことがある。
        // ex.
        // SELECT COUNT(*),MAX(permission.level)
        // FROM page,property AS property0,property AS property1,
        // member,casto,permission
        // WHERE page.id=property0.pageid AND property0.name='a'
        // AND page.id=property1.pageid AND property1.name='subType'
        // AND ((property0.value='A' AND property1.value='sub')
        // AND (page.parentPathname='/path/to/dir'))
        // AND page.type='page'
        // AND ('2010-06-02 19:41:43' BETWEEN page.revealDate AND page.concealDate)
        // AND page.heimid=10 AND page.id=permission.pageid
        // AND permission.type=0 AND permission.level>=2
        // AND permission.roleid=casto.roleid
        // AND casto.groupid=member.groupid
        // AND (permission.roleid=8 OR casto.groupid=5 OR permission.roleid=9
        // AND page.owneruserid=3 OR casto.userid=3 OR member.userid=3)
        // GROUP BY permission.type HAVING MAX(permission.level)<>127
        return list.isEmpty() ? Integer.valueOf(0) : (Number)list.get(0);
    }


    public Number getCount(PageCondition cond)
    {
        List<Object> list = getPageList(cond, true, PAGELIST_COLUMNS_COUNT,
            scalarListHandler_);
        return list.isEmpty() ? Integer.valueOf(0) : (Number)list.get(0);
    }


    public boolean childNameExists(Number heimId, String pathname, String name)
    {
        Connection con = null;
        try {
            con = getConnection();
            QueryRunner run = newQueryRunner();
            Number count = (Number)run.query(con, getQuery("childNameExists"),
                new Object[] { heimId, pathname, name }, scalarHandler_);
            return (count.intValue() > 0);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    public void insert(PageDto dto)
    {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();

            QueryRunner run = newQueryRunner();
            if (dto.getOrderNumber() == null) {
                Number value = (Number)run.query(con,
                    getQuery("insert.getMaxOrderNumber"), new Object[] {
                        dto.getHeimId(), dto.getRawParentPathname() },
                    scalarHandler_);
                int orderNumber = (value != null) ? (value.intValue() + 1) : 1;
                dto.setOrderNumber(new Integer(orderNumber));
            }
            beantable_.insertColumn(dto);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con, pst, null);
        }
    }


    public int updateById(Number id, PageDto dto)
    {
        Formula where = new Formula(getQuery("updateById.where"));
        where.setObject(1, id);

        try {
            return beantable_.updateColumns(dto, where);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }


    public int deleteById(Number id)
    {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(getQuery("deleteById"));
            pst.setObject(1, id);
            return pst.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con, pst, null);
        }
    }


    public void changeLordId(Number heimId, String pathname, Number oldLordId,
        Number newLordId)
    {
        String parentPathname = PageUtils.getParentPathname(pathname);
        String name = PageUtils.getName(pathname);

        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(getQuery("changeLordId"));
            int idx = 1;
            pst.setObject(idx++, newLordId);
            pst.setObject(idx++, heimId);
            pst.setObject(idx++, parentPathname);
            pst.setObject(idx++, name);
            pst.setObject(idx++, oldLordId);
            pst.setObject(idx++, pathname);
            pst.setObject(idx++, SQLUtils.escape(pathname, "_%", '|') + "/%");
            pst.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con, pst, null);
        }
    }


    public void moveTo(Number heimId, Number fromId, String fromParentPathname,
        String fromName, Number toParentId, String toParentPathname,
        String toName)
    {
        String fromPathname = fromParentPathname + "/" + fromName;
        String toPathname = toParentPathname + "/" + toName;

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = getConnection();

            pst = con.prepareStatement(getQuery("moveTo.lock"));
            int idx = 1;
            pst.setInt(idx++, fromId.intValue());
            pst.setInt(idx++, toParentId.intValue());
            pst.setObject(idx++, heimId);
            pst.setObject(idx++, fromPathname);
            pst.setObject(idx++, SQLUtils.escape(fromPathname, "_%", '|')
                + "/%");
            rs = pst.executeQuery();
            checkIfPagesExist(new Number[] { fromId, toParentId }, rs);
            rs.close();
            rs = null;
            pst.close();
            pst = null;

            pst = con.prepareStatement(getQuery("moveTo.updateTarget"));
            idx = 1;
            pst.setObject(idx++, toParentPathname);
            pst.setObject(idx++, toName);
            pst.setObject(idx++, heimId);
            pst.setObject(idx++, fromParentPathname);
            pst.setObject(idx++, fromName);
            pst.executeUpdate();
            pst.close();
            pst = null;

            pst = con.prepareStatement(getQuery("moveTo.updateDescendants"));
            fromPathname = fromParentPathname + "/" + fromName;
            toPathname = toParentPathname + "/" + toName;
            idx = 1;
            pst.setObject(idx++, toPathname);
            pst.setInt(idx++, fromPathname.length() + 1);
            pst.setObject(idx++, heimId);
            pst.setObject(idx++, fromPathname);
            pst.setObject(idx++, SQLUtils.escape(fromPathname, "_%", '|')
                + "/%");
            pst.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con, pst, rs);
        }
    }


    void checkIfPagesExist(Number[] ids, ResultSet rs)
        throws SQLException
    {
        Set<Number> idSet = new HashSet<Number>(Arrays.asList(ids));
        while (rs.next()) {
            idSet.remove(rs.getInt(1));
            if (idSet.isEmpty()) {
                return;
            }
        }
        PageNotFoundRuntimeException ex = new PageNotFoundRuntimeException();
        for (Iterator<Number> itr = idSet.iterator(); itr.hasNext();) {
            ex.addId(itr.next().intValue());
        }
        throw ex;
    }


    public int releasePages(Number ownerUserId)
    {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(getQuery("releasePages"));
            int idx = 1;
            pst.setObject(idx++, ownerUserId);
            return pst.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con, pst, null);
        }
    }


    /*
     * protected scope methods
     */

    protected PageDto getObjectByParentPathnameAndName(Number heimId,
        String parentPathname, String name)
    {
        if (parentPathname == null) {
            parentPathname = PageDto.PARENT_DUMMY;
        }

        Connection con = null;
        try {
            con = getConnection();
            QueryRunner run = newQueryRunner();
            return (PageDto)run.query(con,
                getQuery("getObjectByParentPathnameAndName"), new Object[] {
                    heimId, parentPathname, name }, beantableHandler_);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    protected PageCondition preparePageConditionToGetPageListByParentPathname(
        Number heimId, String parentPathname, PageCondition cond)
    {
        org.seasar.kvasir.page.condition.Formula o;
        if (!cond.isRecursive()) {
            o = new org.seasar.kvasir.page.condition.Formula(
                PageCondition.FIELD_PARENTPATHNAME + "=?").setString(1,
                parentPathname).freeze();
        } else {
            String escapedParentPathname = SQLUtils.escape(parentPathname,
                "_%", '|');
            o = new org.seasar.kvasir.page.condition.Formula("("
                + PageCondition.FIELD_PARENTPATHNAME + "=? OR "
                + PageCondition.FIELD_PARENTPATHNAME + " LIKE ? ESCAPE '|')")
                .setString(1, escapedParentPathname).setString(2,
                    escapedParentPathname + "/%").freeze();
        }

        return new PageConditionWithHeimId(((PageCondition)cond.clone())
            .addOption(o).freeze(), heimId.intValue());
    }


    @SuppressWarnings("unchecked")
    protected List<Object> getPageList(PageCondition cond,
        boolean suppressOrders, String[] columns, ResultSetHandler h)
    {
        Pair pair = getPair(cond, suppressOrders, columns);

        Connection con = null;
        try {
            con = getConnection();
            QueryRunner run = newQueryRunner();

            return (List<Object>)run.query(con, pair.getTemplate(), pair
                .getParameters(), h);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }


    Pair getPair(PageCondition cond, boolean suppressOrders, String[] columns)
    {
        PageConditionKey key = new PageConditionKey(cond, suppressOrders,
            columns);
        CachedPair cachedPair = cachedPairMap.get(key);
        if (cachedPair == null) {
            cachedPair = newCachedPair(cond, suppressOrders, columns);
            cachedPairMap.put(key, cachedPair);
        }
        if (cond.isIncludeConcealed()) {
            // 可変パラメータがないのでそのまま返してよい。
            return cachedPair.getPair(null);
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(PageConditionParser.PARAMETER_CURRENTDATE, new Timestamp(
                System.currentTimeMillis()));
            return cachedPair.getPair(map);
        }
    }


    CachedPair newCachedPair(PageCondition cond, boolean suppressOrders,
        String[] columns)
    {
        int offset = cond.getOffset();
        int length = cond.getLength();
        String key;
        if (offset == PageCondition.OFFSET_FIRST) {
            if (length == PageCondition.LENGTH_ALL) {
                key = "getPageList.first.all";
            } else {
                key = "getPageList.first.length";
            }
        } else {
            if (length == PageCondition.LENGTH_ALL) {
                // FIXME H2では正しく動かない。
                key = "getPageList.offset.all";
            } else {
                key = "getPageList.offset.length";
            }
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("offset", new Integer(offset));
        paramMap.put("length", new Integer(length));

        org.seasar.kvasir.page.condition.Order[] orders;
        if (suppressOrders) {
            orders = null;
        } else {
            orders = cond.getOrders();
        }

        ParsedPageCondition parsed = parser_.parse(this, columns, cond
            .getType(), cond.isIncludeConcealed(), cond.isOnlyListed(), cond
            .getCurrentDate(), cond.getHeimId(), cond.getUser(), cond
            .getPrivilege(), cond.getOption(), orders);

        StringBuilder sb = new StringBuilder();
        columns = parsed.getColumns();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(columns[i]);
        }
        String[] blocks = new String[] { sb.toString(),
            PropertyUtils.join(parsed.getTables()), parsed.getBase() };

        return new CachedPair(constructPair(key, blocks, paramMap, parsed
            .getParameters()));
    }


    @SuppressWarnings("unchecked")
    public <R> R runWithLocking(Number[] ids, Processable<R> processable)
        throws ProcessableRuntimeException, PageNotFoundRuntimeException
    {
        if (ids.length == 0) {
            throw new IllegalArgumentException("Id array is empty");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('?');
        }

        String template = constructPair("runWithLocking",
            new String[] { sb.toString() }, null, ids).getTemplate();

        Connection con = null;
        try {
            con = getConnection();
            QueryRunner run = newQueryRunner();
            List<Number> lockedIds = (List<Number>)run.query(con, template,
                ids, scalarListHandler_);

            if (lockedIds.size() < ids.length) {
                // 数が足りない＝ページが削除されてしまった、なのでExceptionをスローする。
                Set<Number> idSet = new HashSet<Number>();
                for (Iterator<Number> itr = lockedIds.iterator(); itr.hasNext();) {
                    idSet.add(itr.next());
                }
                sb = new StringBuilder("The following pages are not found: ");
                String delim = "";
                for (int i = 0; i < ids.length; i++) {
                    if (!idSet.contains(ids[i])) {
                        sb.append(delim).append(ids[i]);
                        delim = ",";
                    }
                }
                throw new PageNotFoundRuntimeException(sb.toString());
            }

            return processable.process();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }
}
