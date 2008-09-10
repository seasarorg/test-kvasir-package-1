package org.seasar.kvasir.util.collection;

import java.util.Enumeration;
import java.util.Iterator;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class EnumerationIterator
    implements Iterator
{
    private Enumeration     enm_;


    public EnumerationIterator(Enumeration enm)
    {
        enm_ = enm;
    }


    public boolean hasNext()
    {
        return enm_.hasMoreElements();
    }


    public Object next()
    {
        return enm_.nextElement();
    }


    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
