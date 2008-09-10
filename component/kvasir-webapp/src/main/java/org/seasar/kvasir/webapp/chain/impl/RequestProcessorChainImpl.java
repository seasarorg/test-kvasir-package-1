package org.seasar.kvasir.webapp.chain.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.impl.AbstractChain;
import org.seasar.kvasir.webapp.processor.RequestProcessor;
import org.seasar.kvasir.webapp.processor.RequestProcessorChain;


public class RequestProcessorChainImpl extends AbstractChain<RequestProcessor>
    implements RequestProcessorChain
{
    public RequestProcessorChainImpl(RequestProcessor[] requestProcessors)
    {
        addProcessors(requestProcessors);
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException
    {
        if (isProcessorEmpty()) {
            setAllProcessorsProcessed(true);
        } else {
            pollProcessor().doProcess(request, response, this);
        }
    }
}
