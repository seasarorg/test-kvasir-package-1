package org.seasar.kvasir.system.container;

import org.seasar.cms.pluggable.SingletonPluggableContainerFactory;
import org.seasar.cms.pluggable.hotdeploy.DistributedHotdeployBehavior;
import org.seasar.cms.pluggable.util.PluggableUtils;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.impl.S2ContainerBehavior;
import org.seasar.framework.container.impl.S2ContainerBehavior.Provider;
import org.seasar.kvasir.base.container.ComponentContainer;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class PluggableS2ComponentContainerFactory extends
    S2ComponentContainerFactory
{
    @Override
    public void prepare()
    {
        SingletonPluggableContainerFactory.setApplication(getApplication());
        SingletonPluggableContainerFactory.prepareForContainer();
        rootContainer_ = new PluggableS2ComponentContainer(
            SingletonPluggableContainerFactory.getRootContainer());
    }


    @Override
    public void freeze()
    {
        SingletonPluggableContainerFactory.init();
    }


    @Override
    public void stop()
    {
        SingletonPluggableContainerFactory.destroy();
        rootContainer_ = null;
    }


    @Override
    public ComponentContainer createContainer(String configPath,
        ClassLoader cl, ComponentContainer[] requirements)
    {
        return new PluggableS2ComponentContainer(
            SingletonPluggableContainerFactory.integrate(toURLString(
                configPath, cl), cl, toS2Containers(requirements)));
    }


    protected S2Container getRootS2Container()
    {
        return SingletonPluggableContainerFactory.getRootContainer();
    }


    @Override
    public void beginSession()
    {
        DistributedHotdeployBehavior behavior = getHotdeployBehavior();
        if (behavior != null) {
            behavior.start();
        }
    }


    @Override
    public void endSession()
    {
        DistributedHotdeployBehavior behavior = getHotdeployBehavior();
        if (behavior != null) {
            behavior.stop();
        }
    }


    /**
     * 現在のスレッドに関連付けるコンテキストクラスローダとして使用すべきクラスローダを返します。
     * <p>Kvasirが開発モードでかつセッションが開始されている場合は、
     * このメソッドはHotdeployクラスローダとコンテキストクラスローダを組み合わせたクラスローダを返します。
     * そうでない場合は単にコンテキストクラスローダを返します。
     * </p>
     * 
     * @return クラスローダ。
     */
    @Override
    public ClassLoader getCurrentClassLoader()
    {
        ClassLoader classLoader = Thread.currentThread()
            .getContextClassLoader();
        DistributedHotdeployBehavior behavior = getHotdeployBehavior();
        if (behavior != null) {
            classLoader = PluggableUtils.adjustClassLoader(behavior,
                classLoader);
        }
        return classLoader;
    }


    DistributedHotdeployBehavior getHotdeployBehavior()
    {
        Provider provider = S2ContainerBehavior.getProvider();
        if (provider instanceof DistributedHotdeployBehavior) {
            return (DistributedHotdeployBehavior)provider;
        } else {
            return null;
        }
    }
}
