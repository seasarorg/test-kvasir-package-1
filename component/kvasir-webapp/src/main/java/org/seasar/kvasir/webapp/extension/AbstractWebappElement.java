package org.seasar.kvasir.webapp.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.ElementProperties;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;

import net.skirnir.xom.annotation.Child;


/**
 * @author YOKOTA Takehiko
 */
abstract public class AbstractWebappElement extends AbstractElement
    implements WebappElement
{
    private PropertyHandler prop_;

    private List<InitParam> initParamList_ = new ArrayList<InitParam>();


    public InitParam[] getInitParams()
    {
        return initParamList_.toArray(new InitParam[0]);
    }


    @Child
    public void addInitParam(InitParam initParam)
    {
        initParamList_.add(initParam);
    }


    public void setInitParams(InitParam[] initParams)
    {
        initParamList_.clear();
        initParamList_.addAll(Arrays.asList(initParams));
    }


    public synchronized PropertyHandler getPropertyHandler()
    {
        if (prop_ == null) {
            PropertyHandler prop = new MapProperties(
                new LinkedHashMap<String, String>());
            int size = initParamList_.size();
            for (int i = 0; i < size; i++) {
                InitParam initParam = initParamList_.get(i);
                String name = initParam.getParamName();
                String value = initParam.getParamValue();
                if ((name != null) && (value != null)) {
                    prop.setProperty(name, value);
                }
            }
            prop_ = new ElementProperties(getId(), getParent().getParent()
                .getPlugin(), prop);
        }
        return prop_;
    }
}
