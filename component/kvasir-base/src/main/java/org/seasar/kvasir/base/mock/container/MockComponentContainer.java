package org.seasar.kvasir.base.mock.container;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentNotFoundRuntimeException;
import org.seasar.kvasir.base.mock.MockKvasir;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class MockComponentContainer
    implements ComponentContainer
{
    private Map<Object, Object> map_;


    public MockComponentContainer()
    {
        this(new HashMap<Object, Object>());
    }


    public MockComponentContainer(Map<Object, Object> map)
    {
        map_ = map;

        if (!map_.containsKey(Globals.COMPONENT_KVASIR)) {
            Kvasir kvasir = new MockKvasir();
            map_.put(Globals.COMPONENT_KVASIR, kvasir);
            map_.put(Kvasir.class, kvasir);
        }
    }


    /*
     * ComponentContainer
     */

    public Object getComponent(Object key)
    {
        Object component = map_.get(key);
        if (component != null) {
            return component;
        } else {
            throw new ComponentNotFoundRuntimeException(
                "Component not found: key=" + key);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> key)
    {
        Object component = map_.get(key);
        if (component != null) {
            return (T)component;
        } else {
            throw new ComponentNotFoundRuntimeException(
                "Component not found: key=" + key);
        }
    }


    public Class<?> getComponentClass(Object key)
    {
        Object component = map_.get(key);
        if (component != null) {
            return component.getClass();
        } else {
            throw new ComponentNotFoundRuntimeException(
                "Component not found: key=" + key);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> Class<T> getComponentClass(Class<T> key)
    {
        Object component = map_.get(key);
        if (component != null) {
            return (Class<T>)component.getClass();
        } else {
            throw new ComponentNotFoundRuntimeException(
                "Component not found: key=" + key);
        }
    }


    public boolean hasComponent(Object key)
    {
        return map_.containsKey(key);
    }


    public Object getLocalComponent(Object key)
    {
        return getComponent(key);
    }


    public <T> T getLocalComponent(Class<T> key)
    {
        return getComponent(key);
    }


    public boolean hasLocalComponent(Object key)
    {
        return hasComponent(key);
    }


    public Object getRequest()
    {
        return null;
    }


    public void setRequest(Object request)
    {
    }


    public Object getResponse()
    {
        return null;
    }


    public void setResponse(Object response)
    {
    }


    public Object getApplication()
    {
        return null;
    }


    public void setApplication(Object servletContext)
    {
    }


    public void init()
    {
    }


    public void destroy()
    {
    }


    public Object getRawContainer()
    {
        return null;
    }
}
