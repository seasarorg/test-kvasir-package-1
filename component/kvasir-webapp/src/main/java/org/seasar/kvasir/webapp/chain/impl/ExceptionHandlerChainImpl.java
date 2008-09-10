package org.seasar.kvasir.webapp.chain.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.impl.AbstractChain;
import org.seasar.kvasir.webapp.handler.ExceptionHandler;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerChain;


public class ExceptionHandlerChainImpl extends AbstractChain<ExceptionHandler>
    implements ExceptionHandlerChain
{
    public ExceptionHandlerChainImpl(ExceptionHandler[] handlers)
    {
        addProcessors(handlers);
    }


    public void doHandle(HttpServletRequest request,
        HttpServletResponse response, Exception ex)
    {
        if (isProcessorEmpty()) {
            setAllProcessorsProcessed(true);
        } else {
            pollProcessor().doHandle(request, response, ex, this);
        }
    }
}
