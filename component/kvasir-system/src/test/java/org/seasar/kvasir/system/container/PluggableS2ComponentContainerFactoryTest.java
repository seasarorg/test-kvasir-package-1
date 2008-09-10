package org.seasar.kvasir.system.container;

public class PluggableS2ComponentContainerFactoryTest extends
    S2ComponentContainerFactoryTestCase
{
    @Override
    public S2ComponentContainerFactory newTarget()
    {
        return new PluggableS2ComponentContainerFactory();
    }
}
