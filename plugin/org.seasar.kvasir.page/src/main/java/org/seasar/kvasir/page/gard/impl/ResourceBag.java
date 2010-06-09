package org.seasar.kvasir.page.gard.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.gard.PageGardUtils;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.Resource;


class ResourceBag
    implements Comparable<ResourceBag>
{
    private Resource resource_;

    private MapProperties fieldProp_;

    private int orderNumber_;


    ResourceBag(Resource resource)
    {
        resource_ = resource;
        Resource contentDir = resource.getChildResource(".kv");
        fieldProp_ = PageGardUtils.loadProperties(contentDir
            .getChildResource(PageGardImporterImpl.FIELD_XPROPERTIES));
        orderNumber_ = PropertyUtils.valueOf(fieldProp_
            .getProperty(Page.FIELD_ORDERNUMBER), Integer.MAX_VALUE);
    }


    public Resource getResource()
    {
        return resource_;
    }


    public MapProperties getFieldProperties()
    {
        return fieldProp_;
    }


    public int getOrderNumber()
    {
        return orderNumber_;
    }


    public int compareTo(ResourceBag o)
    {
        if (orderNumber_ > o.orderNumber_) {
            return 1;
        } else if (orderNumber_ < o.orderNumber_) {
            return -1;
        } else {
            return 0;
        }
    }
}
