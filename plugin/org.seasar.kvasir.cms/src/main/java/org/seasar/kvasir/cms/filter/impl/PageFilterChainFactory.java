package org.seasar.kvasir.cms.filter.impl;

import javax.servlet.FilterConfig;

import org.seasar.kvasir.cms.AbstractGardSpecificChainFactory;
import org.seasar.kvasir.cms.extension.PageFilterElement;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.webapp.filter.impl.FilterConfigImpl;


public class PageFilterChainFactory
    extends
    AbstractGardSpecificChainFactory<FilterConfig, PageFilterElement, PageFilter, PageFilterChain>
{
    public PageFilterChainFactory(PageFilterElement[] elements,
        Class<PageFilter> filterClass, FilterConfig config, String[] phases,
        String defaultPhase)
    {
        super(elements, filterClass, config, phases, defaultPhase);
    }


    @Override
    protected PageFilter getProcessor(PageFilterElement element)
    {
        return element.getPageFilter();
    }


    @Override
    protected void init(PageFilter filter, FilterConfig config,
        PageFilterElement element)
    {
        filter.init(new FilterConfigImpl(element.getFullId(), config
            .getServletContext(), element.getPropertyHandler()));
    }


    @Override
    protected void destroy(PageFilter filter)
    {
        filter.destroy();
    }


    @Override
    protected PageFilterChain newChain()
    {
        return new PageFilterChainImpl();
    }


    @Override
    protected PageFilter newGardRootChangeProcessor(Page gardRootPage)
    {
        return new GardRootChangePageFilter(gardRootPage);
    }
}
