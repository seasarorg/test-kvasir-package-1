package org.seasar.kvasir.cms.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.seasar.kvasir.cms.RequestSnapshot;


public class RequestSnapshotImpl
    implements RequestSnapshot
{
    private ServletContext context_;

    private HttpServletRequest request_;


    public RequestSnapshotImpl(ServletContext context,
        HttpServletRequest request)
    {
        context_ = context;
        request_ = request;
    }


    public ServletContext getServletConext()
    {
        return context_;
    }


    public HttpServletRequest getHttpServletRequest()
    {
        return request_;
    }
}
