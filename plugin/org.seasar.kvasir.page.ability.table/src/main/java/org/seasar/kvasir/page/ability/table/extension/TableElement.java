package org.seasar.kvasir.page.ability.table.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.base.util.ArrayUtils;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.NONE)
@Bean("table")
public class TableElement extends AbstractElement
{
    private String gardId_;

    private String tableName_;

    private String pageIdColumnName_;

    private String[] pageIdRefColumnNames_ = new String[0];

    private String[] idColumnNames_ = new String[0];


    public String getGardId()
    {
        return gardId_;
    }


    @Attribute
    public void setGardId(String gardId)
    {
        gardId_ = gardId;
    }


    public String getTableName()
    {
        return tableName_;
    }


    @Attribute
    @Required
    public void setTableName(String tableName)
    {
        tableName_ = tableName;
    }


    public String getPageIdColumnName()
    {
        return pageIdColumnName_;
    }


    @Attribute
    public void setPageIdColumnName(String pageIdColumnName)
    {
        pageIdColumnName_ = pageIdColumnName;
    }


    public String[] getIdColumnNames()
    {
        return idColumnNames_;
    }


    @Child
    public void addIdColumnName(String idColumnName)
    {
        idColumnNames_ = ArrayUtils.add(idColumnNames_, idColumnName);
    }


    public void setIdColumnNames(String[] idColumnNames)
    {
        idColumnNames_ = idColumnNames;
    }


    public String[] getPageIdRefColumnNames()
    {
        return pageIdRefColumnNames_;
    }


    @Child
    public void addPageIdRefColumnName(String pageIdRefColumnName)
    {
        pageIdRefColumnNames_ = ArrayUtils.add(pageIdRefColumnNames_,
            pageIdRefColumnName);
    }


    public void setPageIdRefColumnNames(String[] pageIdRefColumnNames)
    {
        pageIdRefColumnNames_ = pageIdRefColumnNames;
    }
}
