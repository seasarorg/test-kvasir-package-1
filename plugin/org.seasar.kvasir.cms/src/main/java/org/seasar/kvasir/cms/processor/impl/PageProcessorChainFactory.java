package org.seasar.kvasir.cms.processor.impl;

import javax.servlet.ServletConfig;

import org.seasar.kvasir.cms.AbstractGardSpecificChainFactory;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.webapp.servlet.ServletConfigImpl;


public class PageProcessorChainFactory
    extends
    AbstractGardSpecificChainFactory<ServletConfig, PageProcessorElement, PageProcessor, PageProcessorChain>
{

    public PageProcessorChainFactory(PageProcessorElement[] elements,
        Class<PageProcessor> processorClass, ServletConfig config,
        String[] phases, String defaultPhase)
    {
        super(elements, processorClass, config, phases, defaultPhase);
    }


    @Override
    protected PageProcessor getProcessor(PageProcessorElement element)
    {
        return element.getPageProcessor();
    }


    @Override
    protected void init(PageProcessor processor, ServletConfig config,
        PageProcessorElement element)
    {
        processor.init(new ServletConfigImpl(element.getFullId(), config
            .getServletContext(), element.getPropertyHandler()));
    }


    @Override
    protected void destroy(PageProcessor processor)
    {
        processor.destroy();
    }


    @Override
    protected PageProcessorChain newChain()
    {
        return new PageProcessorChainImpl();
    }


    @Override
    protected PageProcessor newGardRootChangeProcessor(Page gardRootPage)
    {
        return new GardRootChangePageProcessor(gardRootPage);
    }
}
