package org.seasar.kvasir.system.container;

import org.seasar.cms.pluggable.util.PluggableUtils;
import org.seasar.framework.container.ExternalContext;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentContainerFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class S2ComponentContainerFactory extends ComponentContainerFactory
{
    protected S2ComponentContainer rootContainer_;

    static {
        try {
            Class.forName(
                "org.seasar.framework.container.factory.S2ContainerFactory",
                true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    protected void prepare()
    {
        super.prepare();

        S2Container s2container = PluggableUtils.newContainer();
        ExternalContext externalContext = s2container.getExternalContext();
        Object application = getApplication();
        if (externalContext != null && application != null) {
            externalContext.setApplication(application);
        }
        SingletonS2ContainerFactory.setContainer(s2container);
        rootContainer_ = new S2ComponentContainer(s2container);
    }


    @Override
    public void freeze()
    {
        super.freeze();
        getRootS2Container().init();
    }


    @Override
    protected void stop()
    {
        super.stop();
        SingletonS2ContainerFactory.destroy();
        rootContainer_ = null;
    }


    @Override
    public ComponentContainer getRootContainer()
    {
        return rootContainer_;
    }


    @Override
    public ComponentContainer createContainer(String configPath,
        ClassLoader cl, ComponentContainer[] requirements)
    {
        S2Container s2container;
        if (configPath == null) {
            if (requirements.length == 1) {
                return requirements[0];
            } else {
                s2container = PluggableUtils.newContainer();
                getRootS2Container().include(s2container);
            }
        } else {
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                if (cl != null) {
                    Thread.currentThread().setContextClassLoader(cl);
                }
                s2container = S2ContainerFactory.include(getRootS2Container(),
                    toURLString(configPath, cl));
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        }

        addRequirementsToLeaves(s2container, toS2Containers(requirements));

        return new S2ComponentContainer(s2container);
    }


    String toURLString(String resourcePath, ClassLoader cl)
    {
        if (resourcePath == null) {
            return null;
        }
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = getClass().getClassLoader();
            }
        }
        return cl.getResource(resourcePath).toExternalForm();
    }


    S2Container[] toS2Containers(ComponentContainer[] containers)
    {
        if (containers == null) {
            return null;
        }
        S2Container[] s2containers = new S2Container[containers.length];
        for (int i = 0; i < containers.length; i++) {
            if (!(containers[i] instanceof S2ComponentContainer)) {
                String message;
                if (containers[i] == null) {
                    message = "ComponentContainer is null";
                } else {
                    message = "ComponentContainer"
                        + " must be an instance of S2ComponentContainer:"
                        + " className=" + containers[i].getClass().getName();
                }
                throw new IllegalArgumentException(message);
            }
            s2containers[i] = ((S2ComponentContainer)containers[i])
                .getS2Container();
        }
        return s2containers;
    }


    protected S2Container getRootS2Container()
    {
        return SingletonS2ContainerFactory.getContainer();
    }


    void addRequirementsToLeaves(S2Container s2container,
        S2Container[] s2requirements)
    {
        int size = s2container.getChildSize();
        if (size == 0) {
            addRequirements(s2container, s2requirements);
        } else {
            for (int i = 0; i < size; i++) {
                addRequirementsToLeaves(s2container.getChild(i), s2requirements);
            }
        }
    }


    void addRequirements(S2Container s2container, S2Container[] s2requirements)
    {
        for (int i = 0; i < s2requirements.length; i++) {
            s2container.include(s2requirements[i]);
        }
    }
}
