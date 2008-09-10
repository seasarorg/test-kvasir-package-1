package org.seasar.kvasir.base.container.impl;

import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentNotFoundRuntimeException;


/**
 * 複数のComponentContainerから横断的にコンポーネントを検索するための
 * ComponentContainer実装です。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class CompositeComponentContainer
    implements ComponentContainer
{
    private ComponentContainer[] containers_;

    private ComponentContainer parent_;


    public CompositeComponentContainer(ComponentContainer container,
        ComponentContainer parent)
    {
        this(new ComponentContainer[] { container }, parent);
    }


    public CompositeComponentContainer(ComponentContainer[] containers,
        ComponentContainer parent)
    {
        containers_ = containers;
        parent_ = parent;
    }


    /*
     * ComponentContainer
     */

    public Object getComponent(Object key)
    {
        for (int i = 0; i < containers_.length; i++) {
            if (containers_[i].hasComponent(key)) {
                return containers_[i].getComponent(key);
            }
        }
        if ((parent_ != null) && parent_.hasComponent(key)) {
            return parent_.getComponent(key);
        }
        throw new ComponentNotFoundRuntimeException("Component not found: key="
            + key);
    }


    public <T> T getComponent(Class<T> key)
    {
        for (int i = 0; i < containers_.length; i++) {
            if (containers_[i].hasComponent(key)) {
                return containers_[i].getComponent(key);
            }
        }
        if ((parent_ != null) && parent_.hasComponent(key)) {
            return parent_.getComponent(key);
        }
        throw new ComponentNotFoundRuntimeException("Component not found: key="
            + key);
    }


    public Class<?> getComponentClass(Object key)
    {
        for (int i = 0; i < containers_.length; i++) {
            if (containers_[i].hasComponent(key)) {
                return containers_[i].getComponentClass(key);
            }
        }
        if ((parent_ != null) && parent_.hasComponent(key)) {
            return parent_.getComponentClass(key);
        }
        throw new ComponentNotFoundRuntimeException("Component not found: key="
            + key);
    }


    public <T> Class<T> getComponentClass(Class<T> key)
    {
        for (int i = 0; i < containers_.length; i++) {
            if (containers_[i].hasComponent(key)) {
                return containers_[i].getComponentClass(key);
            }
        }
        if ((parent_ != null) && parent_.hasComponent(key)) {
            return parent_.getComponentClass(key);
        }
        throw new ComponentNotFoundRuntimeException("Component not found: key="
            + key);
    }


    public boolean hasComponent(Object key)
    {
        for (int i = 0; i < containers_.length; i++) {
            if (containers_[i].hasComponent(key)) {
                return true;
            }
        }
        if (parent_ != null) {
            return parent_.hasComponent(key);
        }

        return false;
    }


    public Object getLocalComponent(Object key)
    {
        for (int i = 0; i < containers_.length; i++) {
            if (containers_[i].hasLocalComponent(key)) {
                return containers_[i].getLocalComponent(key);
            }
        }
        if ((parent_ != null) && parent_.hasLocalComponent(key)) {
            return parent_.getLocalComponent(key);
        }
        throw new ComponentNotFoundRuntimeException("Component not found: key="
            + key);
    }


    public <T> T getLocalComponent(Class<T> key)
    {
        for (int i = 0; i < containers_.length; i++) {
            if (containers_[i].hasLocalComponent(key)) {
                return containers_[i].getLocalComponent(key);
            }
        }
        if ((parent_ != null) && parent_.hasLocalComponent(key)) {
            return parent_.getLocalComponent(key);
        }
        throw new ComponentNotFoundRuntimeException("Component not found: key="
            + key);
    }


    public boolean hasLocalComponent(Object key)
    {
        for (int i = 0; i < containers_.length; i++) {
            if (containers_[i].hasLocalComponent(key)) {
                return true;
            }
        }
        if (parent_ != null) {
            return parent_.hasLocalComponent(key);
        }

        return false;
    }


    public Object getRequest()
    {
        if (parent_ != null) {
            return parent_.getRequest();
        } else {
            return null;
        }
    }


    public void setRequest(Object request)
    {
        if (parent_ != null) {
            parent_.setRequest(request);
        }
    }


    public Object getResponse()
    {
        if (parent_ != null) {
            return parent_.getResponse();
        } else {
            return null;
        }
    }


    public void setResponse(Object response)
    {
        if (parent_ != null) {
            parent_.setResponse(response);
        }
    }


    public Object getApplication()
    {
        if (parent_ != null) {
            return parent_.getApplication();
        } else {
            return null;
        }
    }


    public void setApplication(Object servletContext)
    {
        if (parent_ != null) {
            parent_.setApplication(servletContext);
        }
    }


    public void init()
    {
        for (int i = 0; i < containers_.length; i++) {
            containers_[i].init();
        }
        if (parent_ != null) {
            parent_.init();
        }
    }


    public void destroy()
    {
        for (int i = 0; i < containers_.length; i++) {
            containers_[i].destroy();
        }
        if (parent_ != null) {
            parent_.destroy();
        }
    }


    public Object getRawContainer()
    {
        throw new UnsupportedOperationException();
    }
}
