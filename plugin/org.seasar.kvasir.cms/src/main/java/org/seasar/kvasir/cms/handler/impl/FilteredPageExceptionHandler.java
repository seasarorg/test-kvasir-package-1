package org.seasar.kvasir.cms.handler.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.GardIdProvider;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.handler.PageExceptionHandler;
import org.seasar.kvasir.cms.handler.PageExceptionHandlerChain;
import org.seasar.kvasir.cms.impl.AbstractPageMatcher;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerConfig;


public class FilteredPageExceptionHandler extends AbstractPageMatcher
    implements PageExceptionHandler
{
    private PageExceptionHandler handler_;

    private Class<?> type_;


    public FilteredPageExceptionHandler(PageExceptionHandler handler,
        String what, String how, String except, boolean not, boolean regex,
        GardIdProvider gardIdProvider, Class<?> type)
    {
        super(what, how, except, not, regex, gardIdProvider);
        handler_ = handler;
        type_ = type;
    }


    public String toString()
    {
        return "FilteredPageExceptionHandler(" + handler_ + ")";
    }


    public void destroy()
    {
        handler_.destroy();
    }


    public void doHandle(HttpServletRequest request,
        HttpServletResponse response, Exception ex, PageRequest pageRequest,
        PageExceptionHandlerChain chain)
    {
        if (isMatched(pageRequest) && isMatched(ex)) {
            handler_.doHandle(request, response, ex, pageRequest, chain);
        } else {
            chain.doHandle(request, response, ex, pageRequest);
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
