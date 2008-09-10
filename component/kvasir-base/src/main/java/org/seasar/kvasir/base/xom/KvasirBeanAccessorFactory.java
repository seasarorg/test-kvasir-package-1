package org.seasar.kvasir.base.xom;

import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.BeanAccessorFactory;


public class KvasirBeanAccessorFactory
    implements BeanAccessorFactory
{
    public BeanAccessor newInstance()
    {
        return new KvasirBeanAccessor();
    }
}
