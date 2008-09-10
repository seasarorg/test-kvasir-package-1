package org.seasar.kvasir.webapp.extension;

import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.util.collection.PropertyHandler;

import net.skirnir.xom.annotation.Child;


/**
 * @author YOKOTA Takehiko
 */
public interface WebappElement
    extends ExtensionElement
{
    InitParam[] getInitParams();


    @Child
    void addInitParam(InitParam initParam);


    void setInitParams(InitParam[] initParams);


    PropertyHandler getPropertyHandler();
}
