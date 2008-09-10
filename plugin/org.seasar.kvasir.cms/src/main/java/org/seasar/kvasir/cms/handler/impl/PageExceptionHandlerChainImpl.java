package org.seasar.kvasir.cms.handler.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.impl.AbstractChain;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.handler.PageExceptionHandler;
import org.seasar.kvasir.cms.handler.PageExceptionHandlerChain;


public class PageExceptionHandlerChainImpl extends
    AbstractChain<PageExceptionHandler>
    implements PageExceptionHandlerChain
{
    public PageExceptionHandlerChainImpl()
    {
    }


    public PageExceptionHandlerChainImpl(PageExceptionHandler[] handlers)
    {
        addProcessors(handlers);
    }


    public void doHandle(HttpServletRequest request,
        HttpServletResponse response, Exception ex, PageRequest pageRequest)
    {
        if (isProcessorEmpty()) {
            setAllProcessorsProcessed(true);
        } else {
            pollProcessor().doHandle(request, response, ex, pageRequest, this);
        }
    }
}
