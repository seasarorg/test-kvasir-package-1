package org.seasar.kvasir.webapp.chain.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.impl.AbstractChain;
import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.filter.RequestFilter;
import org.seasar.kvasir.webapp.filter.RequestFilterChain;


public class RequestFilterChainImpl extends AbstractChain<RequestFilter>
    implements RequestFilterChain
{
    public RequestFilterChainImpl(RequestFilter[] requestFilters)
    {
        addProcessors(requestFilters);
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher)
        throws IOException, ServletException
    {
        if (isProcessorEmpty()) {
            setAllProcessorsProcessed(true);
        } else {
            pollProcessor().doFilter(request, response, dispatcher, this);
        }
    }
}
