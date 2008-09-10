package org.seasar.kvasir.cms.processor.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.impl.AbstractChain;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;


public class PageProcessorChainImpl extends AbstractChain<PageProcessor>
    implements PageProcessorChain
{
    public PageProcessorChainImpl()
    {
    }


    public PageProcessorChainImpl(PageProcessor[] processors)
    {
        addProcessors(processors);
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest)
        throws ServletException, IOException
    {
        if (isProcessorEmpty()) {
            setAllProcessorsProcessed(true);
        } else {
            pollProcessor().doProcess(request, response, pageRequest, this);
        }
    }
}
