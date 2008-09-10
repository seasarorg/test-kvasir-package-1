package org.seasar.kvasir.page.impl;

import org.seasar.kvasir.page.Lock;


/**
 * @author YOKOTA Takehiko
 */
class LockImpl
    implements Lock
{
    private org.seasar.kvasir.util.pathlock.Lock    lock_;


    public LockImpl(org.seasar.kvasir.util.pathlock.Lock lock)
    {
        lock_ = lock;
    }


    public void unlock()
    {
        lock_.unlock();
    }


    org.seasar.kvasir.util.pathlock.Lock getLock()
    {
        return lock_;
    }
}
