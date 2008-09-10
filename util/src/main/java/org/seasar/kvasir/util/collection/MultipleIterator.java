package org.seasar.kvasir.util.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class MultipleIterator
    implements Iterator
{
    private Iterator[]      itrs_;
    private int             idx_ = 0;


    public MultipleIterator(Iterator[] itrs)
    {
        List list = new ArrayList(itrs.length);
        for (int i = 0; i < itrs.length; i++) {
            if (itrs[i].hasNext()) {
                list.add(itrs[i]);
            }
        }
        itrs_ = (Iterator[])list.toArray(new Iterator[0]);
    }


    public boolean hasNext()
    {
        if (idx_ >= itrs_.length) {
            return false;
        } else if (itrs_[idx_].hasNext()) {
            return true;
        } else if (idx_ == itrs_.length - 1) {
            return false;
        } else {
            return true;
        }
    }


    public Object next()
    {
        if (idx_ >= itrs_.length) {
            return null;
        }
        if (!itrs_[idx_].hasNext()) {
            idx_++;
            if (idx_ == itrs_.length) {
                return null;
            }
        }
        return itrs_[idx_].next();
    }


    public void remove()
    {
        itrs_[idx_].remove();
    }
}
