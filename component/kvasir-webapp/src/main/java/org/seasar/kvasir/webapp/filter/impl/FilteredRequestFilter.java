package org.seasar.kvasir.webapp.filter.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.filter.RequestFilter;
import org.seasar.kvasir.webapp.filter.RequestFilterChain;


public class FilteredRequestFilter
    implements RequestFilter
{
    private RequestFilter filter_;

    private Set<Dispatcher> dispatcherSet_ = new HashSet<Dispatcher>();


    public FilteredRequestFilter(RequestFilter filter, Dispatcher[] dispatchers)
    {
        filter_ = filter;
        dispatcherSet_.addAll(Arrays.asList(dispatchers));
    }


    public String toString()
    {
        return "FilteredRequestFilter(" + filter_ + ")";
    }


    public void destroy()
    {
        filter_.destroy();
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        RequestFilterChain chain)
        throws ServletException, IOException
    {
        if (dispatcherSet_.contains(dispatcher)) {
            filter_.doFilter(request, response, dispatcher, chain);
        } else {
            chain.doFilter(request, response, dispatcher);
        }
    }


    public void init(FilterConfig config)
    {
        filter_.init(config);
    }
}
