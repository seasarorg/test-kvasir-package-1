package org.seasar.kvasir.webapp.chain.impl;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.filter.RequestFilterChain;


public class FilterChainAdapter
    implements Filter
{
    private RequestFilterChain chain_;

    private Dispatcher dispatcher_;


    public FilterChainAdapter(RequestFilterChain chain, Dispatcher dispatcher)
    {
        chain_ = chain;
        dispatcher_ = dispatcher;
    }


    public void init(FilterConfig config)
    {
    }


    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain)
        throws IOException, ServletException
    {
        chain_.doFilter((HttpServletRequest)request,
            (HttpServletResponse)response, dispatcher_);
        chain.doFilter(request, response);
    }


    public void destroy()
    {
    }
}
