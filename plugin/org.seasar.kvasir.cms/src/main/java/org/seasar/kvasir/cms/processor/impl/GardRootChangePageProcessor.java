package org.seasar.kvasir.cms.processor.impl;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;


public class GardRootChangePageProcessor
    implements PageProcessor
{
    private PageAlfr pageAlfr_;

    private int gardRootId_;


    public GardRootChangePageProcessor(Page gardRootPage)
    {
        pageAlfr_ = gardRootPage.getAlfr();
        gardRootId_ = gardRootPage.getId();
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        pageRequest.getMy().setGardRootPage(pageAlfr_.getPage(gardRootId_));
        chain.doProcess(request, response, pageRequest);
    }


    public void init(ServletConfig config)
    {
    }


    public void destroy()
    {
    }
}
