package org.seasar.kvasir.util.pathlock;

/**
 * @author YOKOTA Takehiko
 */
public class Lock
{
    private PathLock    pathLock_;
    private String[]    paths_;


    Lock(PathLock pathLock, String[] paths)
    {
        pathLock_ = pathLock;
        paths_ = paths;
    }


    public void unlock()
    {
        pathLock_.unlock(paths_);
    }
}
