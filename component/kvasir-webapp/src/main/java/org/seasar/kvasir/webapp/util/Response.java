package org.seasar.kvasir.webapp.util;

import org.seasar.kvasir.util.io.Content;


public class Response extends Content
{
    private int status_;


    public int getStatus()
    {
        return status_;
    }


    public void setStatus(int status)
    {
        status_ = status;
    }
}
