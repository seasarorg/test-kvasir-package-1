package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.filter.RequestFilterChain;


public class PageFilterChainAdapter
    implements PageFilter
{
    private RequestFilterChain chain_;


    public PageFilterChainAdapter(RequestFilterChain chain)
    {
        chain_ = chain;
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        chain_.doFilter(request, response, dispatcher);
        chain.doFilter(request, response, dispatcher, pageRequest);
    }


    public void init(FilterConfig config)
    {
    }


    public void destroy()
    {
    }
}
