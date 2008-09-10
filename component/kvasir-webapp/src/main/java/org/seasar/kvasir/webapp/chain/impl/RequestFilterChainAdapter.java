package org.seasar.kvasir.webapp.chain.impl;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.filter.RequestFilter;
import org.seasar.kvasir.webapp.filter.RequestFilterChain;


public class RequestFilterChainAdapter
    implements RequestFilter
{
    private FilterChain chain_;


    public RequestFilterChainAdapter(FilterChain chain)
    {
        chain_ = chain;
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        RequestFilterChain filterChain)
        throws IOException, ServletException
    {
        chain_.doFilter(request, response);
        filterChain.doFilter(request, response, dispatcher);
    }


    public void init(FilterConfig config)
    {
    }


    public void destroy()
    {
    }
}
