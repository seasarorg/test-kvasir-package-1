package org.seasar.kvasir.page.ability.table.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.cms.beantable.Beantable;
import org.seasar.cms.database.identity.ColumnMetaData;
import org.seasar.dao.annotation.tiger.Id;
import org.seasar.framework.util.ArrayUtil;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.dao.DaoPlugin;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.page.ability.table.TableAbilityPlugin;
import org.seasar.kvasir.page.ability.table.annotation.PageId;
import org.seasar.kvasir.page.ability.table.annotation.PageIdRef;
import org.seasar.kvasir.page.ability.table.extension.TableElement;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class TableAbilityPluginImpl extends AbstractPlugin<EmptySettings>
    implements TableAbilityPlugin
{
    private Map<String, String[]> tableNamesMap_;

    private Map<String, String> pageIdColumnNameMap_;

    private Map<String, String[]> pageIdRefColumnNamesMap_;

    private Map<String, String[]> idColumnNamesMap_;

    private DaoPlugin daoPlugin_;


    /*
     * constructors
     */

    public TableAbilityPluginImpl()
    {
    }


    /*
     * TableAbilityPlugin
     */

    public String[] getTableNames(String gardId)
    {
        String[] tableNames = tableNamesMap_.get(gardId);
        if (tableNames == null) {
            tableNames = new String[0];
        }
        return tableNames;
    }


    public String getPageIdColumnName(String tableName)
    {
        return pageIdColumnNameMap_.get(tableName.toUpperCase());
    }


    public String[] getPageIdRefColumnNames(String tableName)
    {
        return pageIdRefColumnNamesMap_.get(tableName.toUpperCase());
    }


    public String[] getIdColumnNames(String tableName)
    {
        return idColumnNamesMap_.get(tableName.toUpperCase());
    }


    /*
     * AbstractPlugin
     */

    protected boolean doStart()
    {
        TableElement[] elements = getExtensionElements(TableElement.class);
        tableNamesMap_ = new HashMap<String, String[]>();
        pageIdColumnNameMap_ = new HashMap<String, String>();
        pageIdRefColumnNamesMap_ = new HashMap<String, String[]>();
        idColumnNamesMap_ = new HashMap<String, String[]>();

        for (int i = 0; i < elements.length; i++) {
            TableElement element = elements[i];
            String tableName = element.getTableName();
            String uTableName = tableName.toUpperCase();
            String gardId = element.getGardId();

            String[] tableNames = tableNamesMap_.get(gardId);
            if (tableNames == null) {
                tableNames = new String[] { uTableName };
            } else {
                tableNames = (String[])ArrayUtil.add(tableNames, uTableName);
            }
            tableNamesMap_.put(gardId, tableNames);

            String pageIdColumnName = getPageIdColumnName(element);
            if (pageIdColumnName == null) {
                log_
                    .error("Attribute 'page-id-column-name' is not specified: element="
                        + pageIdColumnName);
                return false;
            }
            pageIdColumnNameMap_.put(uTableName, pageIdColumnName);

            pageIdRefColumnNamesMap_.put(uTableName,
                getPageIdRefColumnNames(element));
            idColumnNamesMap_.put(uTableName, getIdColumnNames(element));
        }

        return true;
    }


    String[] getIdColumnNames(TableElement element)
    {
        String[] names = element.getIdColumnNames();
        if (names.length == 0) {
            List<String> nameList = new ArrayList<String>();
            Beantable<?> beanTable = daoPlugin_.getBeantable(element
                .getTableName());
            if (beanTable != null) {
                ColumnMetaData[] columns = beanTable.getTableMetaData()
                    .getColumns();
                for (int i = 0; i < columns.length; i++) {
                    Method method = columns[i].getPropertyDescriptor()
                        .getReadMethod();
                    if (method.isAnnotationPresent(Id.class)) {
                        nameList.add(columns[i].getName());
                    }
                }
            }
            names = nameList.toArray(new String[0]);
        }
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].toUpperCase();
        }
        return names;
    }


    String[] getPageIdRefColumnNames(TableElement element)
    {
        String[] names = element.getPageIdRefColumnNames();
        if (names.length == 0) {
            List<String> nameList = new ArrayList<String>();
            Beantable<?> beanTable = daoPlugin_.getBeantable(element
                .getTableName());
            if (beanTable != null) {
                ColumnMetaData[] columns = beanTable.getTableMetaData()
                    .getColumns();
                for (int i = 0; i < columns.length; i++) {
                    Method method = columns[i].getPropertyDescriptor()
                        .getReadMethod();
                    if (method.isAnnotationPresent(PageIdRef.class)) {
                        nameList.add(columns[i].getName());
                    }
                }
            }
            names = nameList.toArray(new String[0]);
        }
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].toUpperCase();
        }
        return names;
    }


    String getPageIdColumnName(TableElement element)
    {
        String name = element.getPageIdColumnName();
        if (name == null) {
            Beantable<?> beanTable = daoPlugin_.getBeantable(element
                .getTableName());
            if (beanTable != null) {
                ColumnMetaData[] columns = beanTable.getTableMetaData()
                    .getColumns();
                for (int i = 0; i < columns.length; i++) {
                    Method method = columns[i].getPropertyDescriptor()
                        .getReadMethod();
                    if (method.isAnnotationPresent(PageId.class)) {
                        name = columns[i].getName();
                        break;
                    }
                }
            }
        }
        return (name != null ? name.toUpperCase() : null);
    }


    protected void doStop()
    {
        daoPlugin_ = null;

        tableNamesMap_ = null;
        pageIdColumnNameMap_ = null;
        pageIdRefColumnNamesMap_ = null;
        idColumnNamesMap_ = null;
    }


    /*
     * for framework
     */

    public void setDaoPlugin(DaoPlugin daoPlugin)
    {
        daoPlugin_ = daoPlugin;
    }
}
