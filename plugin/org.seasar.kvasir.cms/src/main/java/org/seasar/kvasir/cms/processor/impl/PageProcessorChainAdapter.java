package org.seasar.kvasir.cms.processor.impl;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.webapp.processor.RequestProcessorChain;


public class PageProcessorChainAdapter
    implements PageProcessor
{
    private RequestProcessorChain chain_;


    public PageProcessorChainAdapter(RequestProcessorChain chain)
    {
        chain_ = chain;
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        chain_.doProcess(request, response);
        chain.doProcess(request, response, pageRequest);
    }


    public void init(ServletConfig config)
    {
    }


    public void destroy()
    {
    }
}
