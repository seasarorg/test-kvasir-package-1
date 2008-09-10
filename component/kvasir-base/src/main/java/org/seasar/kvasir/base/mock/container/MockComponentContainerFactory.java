package org.seasar.kvasir.base.mock.container;

import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentContainerFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class MockComponentContainerFactory extends ComponentContainerFactory
{
    @Override
    public void freeze()
    {
    }


    public ComponentContainer createContainer(String configPath)
    {
        return createContainer(configPath, null, null);
    }


    @Override
    public ComponentContainer createContainer(String configPath, ClassLoader cl,
        ComponentContainer[] requirements)
    {
        return new MockComponentContainer();
    }


    @Override
    public void stop()
    {
    }


    @Override
    public ComponentContainer getRootContainer()
    {
        return null;
    }
}
