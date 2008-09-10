package org.seasar.kvasir.base.dao.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Required;


/**
 * @author YOKOTA Takehiko
 */
@Bean("database-system")
public class DatabaseSystemElement extends AbstractElement
{
    private String name_;

    private String driverClass_;

    private String validationQuery_;


    public String getDriverClass()
    {
        return driverClass_;
    }


    public String getValidationQuery()
    {
        return validationQuery_;
    }


    public String getName()
    {
        return name_;
    }


    /*
     * for framework
     */

    @Attribute
    @Required
    public void setDriverClass(String driverClass)
    {
        driverClass_ = driverClass;
    }


    @Attribute
    public void setValidationQuery(String validationQuery)
    {
        validationQuery_ = validationQuery;
    }


    @Attribute
    public void setName(String name)
    {
        name_ = name;
    }
}
