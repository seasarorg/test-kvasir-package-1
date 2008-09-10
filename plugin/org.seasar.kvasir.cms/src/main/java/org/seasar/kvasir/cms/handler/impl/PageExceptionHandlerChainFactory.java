package org.seasar.kvasir.cms.handler.impl;

import org.seasar.kvasir.cms.AbstractGardSpecificChainFactory;
import org.seasar.kvasir.cms.extension.PageExceptionHandlerElement;
import org.seasar.kvasir.cms.handler.PageExceptionHandler;
import org.seasar.kvasir.cms.handler.PageExceptionHandlerChain;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerConfig;
import org.seasar.kvasir.webapp.handler.impl.ExceptionHandlerConfigImpl;


public class PageExceptionHandlerChainFactory
    extends
    AbstractGardSpecificChainFactory<Object, PageExceptionHandlerElement, PageExceptionHandler, PageExceptionHandlerChain>
{
    public PageExceptionHandlerChainFactory(
        PageExceptionHandlerElement[] elements,
        Class<PageExceptionHandler> handlerClass,
        ExceptionHandlerConfig config, String[] phases, String defaultPhase)
    {
        super(elements, handlerClass, config, phases, defaultPhase);
    }


    @Override
    protected PageExceptionHandler getProcessor(
        PageExceptionHandlerElement element)
    {
        return element.getPageExceptionHandler();
    }


    @Override
    protected void init(PageExceptionHandler handler, Object config,
        PageExceptionHandlerElement element)
    {
        handler.init(new ExceptionHandlerConfigImpl(element.getFullId(),
            element.getPropertyHandler()));
    }


    @Override
    protected void destroy(PageExceptionHandler handler)
    {
        handler.destroy();
    }


    @Override
    protected PageExceptionHandlerChain newChain()
    {
        return new PageExceptionHandlerChainImpl();
    }


    @Override
    protected PageExceptionHandler newGardRootChangeProcessor(Page gardRootPage)
    {
        return new GardRootChangePageExceptionHandler(gardRootPage);
    }
}
