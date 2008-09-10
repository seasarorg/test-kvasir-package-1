package org.seasar.kvasir.webapp.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.Chain;
import org.seasar.kvasir.webapp.Dispatcher;


public interface RequestFilterChain
    extends Chain<RequestFilter>
{
    void doFilter(HttpServletRequest request, HttpServletResponse response,
        Dispatcher dispatcher)
        throws IOException, ServletException;
}
