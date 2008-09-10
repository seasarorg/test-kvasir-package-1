package org.seasar.kvasir.system.container;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentNotFoundRuntimeException;
import org.seasar.kvasir.base.container.TooManyRegistrationRuntimeException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class S2ComponentContainer
    implements ComponentContainer
{
    private S2Container container_;


    public S2ComponentContainer(S2Container container)
    {
        container_ = container;
    }


    /*
     * public scope methods
     */
    @Override
    public String toString()
    {
        return container_.getPath();
    }


    public S2Container getS2Container()
    {
        return container_;
    }


    /*
     * ComponentContainer
     */

    public Object getComponent(Object key)
    {
        try {
            return container_.getComponent(key);
        } catch (org.seasar.framework.container.ComponentNotFoundRuntimeException ex) {
            throw new ComponentNotFoundRuntimeException(ex);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> key)
    {
        try {
            return (T)container_.getComponent(key);
        } catch (org.seasar.framework.container.ComponentNotFoundRuntimeException ex) {
            throw new ComponentNotFoundRuntimeException(ex);
        }
    }


    public Class<?> getComponentClass(Object key)
    {
        try {
            return container_.getComponentDef(key).getComponentClass();
        } catch (org.seasar.framework.container.ComponentNotFoundRuntimeException ex) {
            throw new ComponentNotFoundRuntimeException(ex);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> Class<T> getComponentClass(Class<T> key)
    {
        try {
            return (Class<T>)container_.getComponentDef(key)
                .getComponentClass();
        } catch (org.seasar.framework.container.ComponentNotFoundRuntimeException ex) {
            throw new ComponentNotFoundRuntimeException(ex);
        }
    }


    public boolean hasComponent(Object key)
    {
        return container_.hasComponentDef(key);
    }


    public Object getLocalComponent(Object key)
    {
        ComponentDef[] componentDefs = container_.findLocalComponentDefs(key);
        if (componentDefs.length == 1) {
            return componentDefs[0].getComponent();
        } else if (componentDefs.length > 1) {
            throw new TooManyRegistrationRuntimeException(
                "Local components corresponding to the key '" + key
                    + "' are found in: container path=" + container_.getPath());
        } else {
            throw new ComponentNotFoundRuntimeException(
                "Local component corresponding to the key '" + key
                    + "' is not found in: container path="
                    + container_.getPath());
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T getLocalComponent(Class<T> key)
    {
        ComponentDef[] componentDefs = container_.findLocalComponentDefs(key);
        if (componentDefs.length == 1) {
            return (T)componentDefs[0].getComponent();
        } else if (componentDefs.length > 1) {
            throw new TooManyRegistrationRuntimeException(
                "Local components corresponding to the key '" + key
                    + "' are found in: container path=" + container_.getPath());
        } else {
            throw new ComponentNotFoundRuntimeException(
                "Local component corresponding to the key '" + key
                    + "' is not found in: container path="
                    + container_.getPath());
        }
    }


    public boolean hasLocalComponent(Object key)
    {
        return (container_.findLocalComponentDefs(key).length > 0);
    }


    /*
     public void register(Object obj)
     {
     register(obj, null);
     }


     public void register(Object obj, String name)
     {
     ComponentDef cd = new ComponentDefImpl(obj.getClass(), name);
     cd.setInstanceMode(ContainerConstants.INSTANCE_OUTER);
     container_.register(cd);
     container_.injectDependency(this, Plugin.COMPONENT_PLUGIN);

     container_.register(obj, name);
     }


     public void injectDependency(Object obj, String name)
     {
     container_.injectDependency(obj, name);

     }


     public void injectDependency(Object obj, Class clazz)
     {
     container_.injectDependency(obj, clazz);
     }

     */
    public Object getRequest()
    {
        return container_.getRoot().getExternalContext().getRequest();
    }


    public void setRequest(Object request)
    {
        container_.getRoot().getExternalContext().setRequest(request);
    }


    public Object getResponse()
    {
        return container_.getRoot().getExternalContext().getResponse();
    }


    public void setResponse(Object response)
    {
        container_.getRoot().getExternalContext().setResponse(response);
    }


    public Object getApplication()
    {
        return container_.getRoot().getExternalContext().getApplication();
    }


    public void setApplication(Object application)
    {
        container_.getRoot().getExternalContext().setApplication(application);
    }


    public void init()
    {
        container_.init();
    }


    public void destroy()
    {
        try {
            container_.destroy();
        } catch (Throwable t) {
            ;
        }
    }


    public Object getRawContainer()
    {
        return container_;
    }

    /*
     * private scope methods
     */

    //    private String correctInstanceMode(String instanceMode)
    //    {
    //        if (INSTANCEMODE_SINGLETON.equals(instanceMode)) {
    //            return InstanceDef.SINGLETON_NAME;
    //        } else if (INSTANCEMODE_PROTOTYPE.equals(instanceMode)) {
    //                return InstanceDef.PROTOTYPE_NAME;
    //        } else if (INSTANCEMODE_REQUEST.equals(instanceMode)) {
    //            return InstanceDef.REQUEST_NAME;
    //        } else if (INSTANCEMODE_SESSION.equals(instanceMode)) {
    //            return InstanceDef.SESSION_NAME;
    //        } else {
    //            throw new IllegalArgumentException(
    //                "Unknown instanceMode: " + instanceMode);
    //        }
    //    }
}
