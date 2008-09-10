package org.seasar.kvasir.cms.processor.impl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
class VirtualRequestDispatcher
    implements RequestDispatcher
{
    private RequestDispatcher rd_;


    public VirtualRequestDispatcher(String path, RequestDispatcher rd)
    {
        rd_ = rd;
    }


    /*
     * RequestDispatcher
     */

    public void forward(ServletRequest request, ServletResponse response)
        throws ServletException, IOException
    {
        // アプリケーションでラップされている可能性があるので、オリジナルのRequestを使うのではなく
        // 与えられたRequestをVirtualRequestを無効化した上で使うようにしている。
        VirtualHttpServletRequest vrequest = stripRequest(request);
        boolean activated = vrequest.isActivated();
        vrequest.setActivated(false);
        try {
            //        request.removeAttribute(ServletPageProcessor.ATTR_CONTEXT_PATH);
            rd_.forward(request, response);
        } finally {
            vrequest.setActivated(activated);
        }
    }


    /*
     public void include(ServletRequest request, ServletResponse response)
     throws ServletException, IOException
     {
     VirtualHttpServletRequest vrequest = stripRequest(request);
     vrequest.set

     String servletPath = vrequest.setIncludeServletPath(path_);
     String pathInfo = vrequest.setIncludePathInfo(null);
     //  Object pageRequestInfo = request.getAttribute(
     //      CmsPlugin.ATTR_INCLUDE_PAGEREQUESTINFO);
     try {
     //      request.setAttribute(
     //          CmsPlugin.ATTR_INCLUDE_PAGEREQUESTINFO,
     //          (path_ != null) ? new PageRequest(pageAlfr_, path_) : null);

     rd_.include(request, response);
     } finally {
     vrequest.setIncludeServletPath(servletPath);
     vrequest.setIncludePathInfo(pathInfo);
     //      request.setAttribute(
     //          CmsPlugin.ATTR_INCLUDE_PAGEREQUESTINFO,
     //          pageRequestInfo);
     }
     }
     */
    public void include(ServletRequest request, ServletResponse response)
        throws ServletException, IOException
    {
        // アプリケーションでラップされている可能性があるので、オリジナルのRequestを使うのではなく
        // 与えられたRequestをVirtualRequestを無効化した上で使うようにしている。
        VirtualHttpServletRequest vrequest = stripRequest(request);
        boolean activated = vrequest.isActivated();
        vrequest.setActivated(false);
        try {
            rd_.include(request, response);
        } finally {
            vrequest.setActivated(activated);
        }
    }


    /*
     * private scope methods
     */

    private VirtualHttpServletRequest stripRequest(ServletRequest request)
    {
        VirtualHttpServletRequest vrequest = null;
        while (true) {
            if (request instanceof VirtualHttpServletRequest) {
                vrequest = (VirtualHttpServletRequest)request;
                break;
            } else if (request instanceof HttpServletRequestWrapper) {
                request = ((HttpServletRequestWrapper)request).getRequest();
            } else {
                throw new RuntimeException("May logic error");
            }
        }
        return vrequest;
    }
}
