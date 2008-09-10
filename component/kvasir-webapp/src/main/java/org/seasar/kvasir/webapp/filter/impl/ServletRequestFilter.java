package org.seasar.kvasir.webapp.filter.impl;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.filter.RequestFilter;
import org.seasar.kvasir.webapp.filter.RequestFilterChain;


public class ServletRequestFilter
    implements RequestFilter
{
    private Filter filter_;


    public void setFilter(Filter filter)
    {
        filter_ = filter;
    }


    public void destroy()
    {
        filter_.destroy();
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, final Dispatcher dispatcher,
        final RequestFilterChain filterChain)
        throws IOException, ServletException
    {
        filter_.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request,
                ServletResponse response)
                throws IOException, ServletException
            {
                filterChain.doFilter((HttpServletRequest)request,
                    (HttpServletResponse)response, dispatcher);
            }
        });
    }


    public void init(FilterConfig config)
    {
        try {
            filter_.init(config);
        } catch (ServletException ex) {
            throw new IORuntimeException(ex);
        }
    }
}
