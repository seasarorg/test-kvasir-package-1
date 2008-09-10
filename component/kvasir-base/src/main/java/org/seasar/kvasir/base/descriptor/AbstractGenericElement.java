package org.seasar.kvasir.base.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;

import net.skirnir.xom.annotation.Child;


/**
 * @author YOKOTA Takehiko
 */
abstract public class AbstractGenericElement extends AbstractElement
{
    private PropertyHandler prop_;

    private List<Property> propList_ = new ArrayList<Property>();


    public synchronized PropertyHandler getPropertyHandler()
    {
        if (prop_ == null) {
            PropertyHandler prop = new MapProperties(
                new LinkedHashMap<String, String>());
            int size = propList_.size();
            for (int i = 0; i < size; i++) {
                Property metaData = propList_.get(i);
                String name = metaData.getName();
                String value = metaData.getValue();
                if ((name != null) && (value != null)) {
                    prop.setProperty(name, value);
                }
            }
            prop_ = new ElementProperties(getId(), getParent().getParent()
                .getPlugin(), prop);
        }
        return prop_;
    }


    public Property[] getProperties()
    {
        return propList_.toArray(new Property[0]);
    }


    @Child
    public void addProperty(Property prop)
    {
        propList_.add(prop);
    }


    public void setProperties(Property[] props)
    {
        propList_.clear();
        propList_.addAll(Arrays.asList(props));
    }
}
