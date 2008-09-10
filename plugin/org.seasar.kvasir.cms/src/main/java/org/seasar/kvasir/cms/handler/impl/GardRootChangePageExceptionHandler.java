package org.seasar.kvasir.cms.handler.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.handler.PageExceptionHandler;
import org.seasar.kvasir.cms.handler.PageExceptionHandlerChain;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerConfig;


public class GardRootChangePageExceptionHandler
    implements PageExceptionHandler
{
    private PageAlfr pageAlfr_;

    private int gardRootId_;


    public GardRootChangePageExceptionHandler(Page gardRootPage)
    {
        pageAlfr_ = gardRootPage.getAlfr();
        gardRootId_ = gardRootPage.getId();
    }


    public void doHandle(HttpServletRequest request,
        HttpServletResponse response, Exception ex, PageRequest pageRequest,
        PageExceptionHandlerChain chain)
    {
        pageRequest.getMy().setGardRootPage(pageAlfr_.getPage(gardRootId_));
        chain.doHandle(request, response, ex, pageRequest);
    }


    public void init(ExceptionHandlerConfig config)
    {
    }


    public void destroy()
    {
    }
}
