package org.seasar.kvasir.page.gard.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.gard.GardComponentContainer;


/**
 * <p>
 * <b>同期化：</b>
 * このクラスはfreeze()の呼出し後はスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class GardComponentContainerImpl<T>
    implements GardComponentContainer<T>
{
    private Map<String, T> componentMap_ = new LinkedHashMap<String, T>();

    private boolean        freezed_      = false;


    public void freeze()
    {
        freezed_ = true;
    }


    /*
     * GardComponentContainer
     */

    public GardComponentContainer<T> register(String gardId, T component)
    {
        if (freezed_) {
            throw new IllegalStateException();
        }

        componentMap_.put(gardId, component);
        return this;
    }


    public GardComponentContainer<T> registerDefault(T component)
    {
        return register(null, component);
    }


    public T get(String gardId)
    {
        return componentMap_.get(gardId);
    }


    public T getDefault()
    {
        return get(null);
    }


    // TODO 引数にarrayを渡さずにこれを実現するにはどうすればいいの？
    public T[] getAll(T[] array)
    {
        return (T[])componentMap_.values().toArray(array);
    }


    public T find(String[] gardIds)
    {
        for (int i = 0; i < gardIds.length; i++) {
            T component = get(gardIds[i]);
            if (component != null) {
                return component;
            }
        }
        return getDefault();
    }


    public T find(Page page)
    {
        return find(page.getGardIds());
    }
}
