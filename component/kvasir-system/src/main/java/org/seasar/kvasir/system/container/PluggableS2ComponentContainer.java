package org.seasar.kvasir.system.container;

import org.seasar.framework.container.S2Container;


public class PluggableS2ComponentContainer extends S2ComponentContainer
{
    public PluggableS2ComponentContainer(S2Container container)
    {
        super(container);
    }


    @Override
    public void destroy()
    {
        // s2-pluggable使用時はContainer同士が共通の子を持つことがあるため、
        // Containerをdestroyすると他のContainerが参照している子Containerを
        // destroyしてしまう危険がある。そのためPluggableS2ContainerFactory.destroy()
        // でContainerを一括destroyすることにし、ここでは何もしないようにしている。
    }
}
