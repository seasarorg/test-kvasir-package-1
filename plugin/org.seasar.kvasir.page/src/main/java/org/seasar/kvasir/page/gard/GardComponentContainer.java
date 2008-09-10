package org.seasar.kvasir.page.gard;

import org.seasar.kvasir.page.Page;


/**
 * @author YOKOTA Takehiko
 */
public interface GardComponentContainer<T>
{
    GardComponentContainer<T> register(String gardId, T component);


    GardComponentContainer<T> registerDefault(T component);


    T get(String gardId);


    T getDefault();


    T[] getAll(T[] array);


    T find(String[] gardIds);


    T find(Page page);
}
