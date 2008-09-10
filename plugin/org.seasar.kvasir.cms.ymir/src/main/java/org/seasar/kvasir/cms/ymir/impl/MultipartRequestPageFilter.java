package org.seasar.kvasir.cms.ymir.impl;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.seasar.cms.ymir.MultipartServletRequest;
import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.webapp.Dispatcher;


/**
 * マルチパートのリクエストを解釈するためのフィルタです。
 * <p>リクエストに対して文字エンコーディングをセットする場合は、
 * このフィルタよりも前のフィルタにてセットしておく必要があります。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class MultipartRequestPageFilter
    implements PageFilter
{
    public void init(FilterConfig config)
    {
    }


    public void destroy()
    {
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        boolean requestChanged = false;
        ComponentContainer container = null;
        Object oldRequest = null;

        if (ServletFileUpload.isMultipartContent(new ServletRequestContext(
            request))) {
            request = new MultipartServletRequest(request);
            container = Asgard.getKvasir().getComponentContainer();
            oldRequest = container.getRequest();
            requestChanged = true;
            container.setRequest(request);
        }
        try {
            chain.doFilter(request, response, dispatcher, pageRequest);
        } finally {
            if (requestChanged) {
                container.setRequest(oldRequest);
            }
        }
    }
}
