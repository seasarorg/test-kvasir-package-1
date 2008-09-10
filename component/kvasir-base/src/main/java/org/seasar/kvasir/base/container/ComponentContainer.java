package org.seasar.kvasir.base.container;

/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface ComponentContainer
{
    String INSTANCEMODE_SINGLETON = "singleton";

    String INSTANCEMODE_PROTOTYPE = "prototype";

    String INSTANCEMODE_REQUEST = "request";

    String INSTANCEMODE_SESSION = "session";


    Object getComponent(Object key);


    <T> T getComponent(Class<T> key);


    Class<?> getComponentClass(Object key);


    <T> Class<T> getComponentClass(Class<T> key);


    boolean hasComponent(Object key);


    Object getLocalComponent(Object key);


    <T> T getLocalComponent(Class<T> key);


    boolean hasLocalComponent(Object key);


    Object getRequest();


    void setRequest(Object request);


    Object getResponse();


    void setResponse(Object response);


    Object getApplication();


    void setApplication(Object application);


    Object getRawContainer();


    void init();


    void destroy();
}
