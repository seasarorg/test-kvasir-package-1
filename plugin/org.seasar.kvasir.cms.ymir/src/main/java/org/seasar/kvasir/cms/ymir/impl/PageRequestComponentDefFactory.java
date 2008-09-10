package org.seasar.kvasir.cms.ymir.impl;

import org.seasar.cms.pluggable.ThreadContextComponentDef;
import org.seasar.cms.pluggable.ThreadContextComponentDefFactory;
import org.seasar.kvasir.cms.PageRequest;


public class PageRequestComponentDefFactory
    implements ThreadContextComponentDefFactory
{
    public Class<?> getComponentClass()
    {
        return PageRequest.class;
    }


    public String getComponentName()
    {
        return "pageRequest";
    }


    public ThreadContextComponentDef newInstance()
    {
        return new ThreadContextComponentDef(getComponentClass(),
            getComponentName());
    }
}
