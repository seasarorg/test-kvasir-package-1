package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.webapp.Dispatcher;


public class GardRootChangePageFilter
    implements PageFilter
{
    private PageAlfr pageAlfr_;

    private int gardRootId_;


    public GardRootChangePageFilter(Page gardRootPage)
    {
        pageAlfr_ = gardRootPage.getAlfr();
        gardRootId_ = gardRootPage.getId();
    }


    @Override
    public String toString()
    {
        return "GardRootChangePageFilter(gardRootId=" + gardRootId_ + ")";
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        pageRequest.getMy().setGardRootPage(pageAlfr_.getPage(gardRootId_));
        chain.doFilter(request, response, dispatcher, pageRequest);
    }


    public void init(FilterConfig config)
    {
    }


    public void destroy()
    {
    }
}
