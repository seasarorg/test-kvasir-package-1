package org.seasar.kvasir.webapp.handler.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.webapp.handler.ExceptionHandler;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerChain;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerConfig;


public class FilteredExceptionHandler
    implements ExceptionHandler
{
    private ExceptionHandler handler_;

    private Class<?> type_;


    public FilteredExceptionHandler(ExceptionHandler handler, Class<?> type)
    {
        handler_ = handler;
        type_ = type;
    }


    public String toString()
    {
        return "FilteredExceptionHandler(" + handler_ + ")";
    }


    public void destroy()
    {
        handler_.destroy();
    }


    public void doHandle(HttpServletRequest request,
        HttpServletResponse response, Exception ex, ExceptionHandlerChain chain)
    {
        if (isMatched(ex)) {
            handler_.doHandle(request, response, ex, chain);
        } else {
            chain.doHandle(request, response, ex);
        }
    }


    boolean isMatched(Exception ex)
    {
        return (type_ == null || type_.isAssignableFrom(ex.getClass()));
    }


    public void init(ExceptionHandlerConfig config)
    {
        handler_.init(config);
    }
}
