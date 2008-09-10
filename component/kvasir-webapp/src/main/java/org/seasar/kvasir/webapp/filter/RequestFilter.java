package org.seasar.kvasir.webapp.filter;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.webapp.Dispatcher;


public interface RequestFilter
{
    void init(FilterConfig config);


    void destroy();


    void doFilter(HttpServletRequest request, HttpServletResponse response,
        Dispatcher dispatcher, RequestFilterChain filterChain)
        throws IOException, ServletException;
}
