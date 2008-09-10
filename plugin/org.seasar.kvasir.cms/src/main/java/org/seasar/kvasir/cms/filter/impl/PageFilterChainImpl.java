package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.impl.AbstractChain;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.webapp.Dispatcher;


public class PageFilterChainImpl extends AbstractChain<PageFilter>
    implements PageFilterChain
{
    public PageFilterChainImpl()
    {
    }


    public PageFilterChainImpl(PageFilter[] filters)
    {
        addProcessors(filters);
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest)
        throws ServletException, IOException
    {
        if (isProcessorEmpty()) {
            setAllProcessorsProcessed(true);
        } else {
            pollProcessor().doFilter(request, response, dispatcher,
                pageRequest, this);
        }
    }
}
