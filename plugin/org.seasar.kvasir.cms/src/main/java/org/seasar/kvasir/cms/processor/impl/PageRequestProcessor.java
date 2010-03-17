package org.seasar.kvasir.cms.processor.impl;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.cms.processor.PageProcessorLifecycleListener;
import org.seasar.kvasir.webapp.processor.RequestProcessor;
import org.seasar.kvasir.webapp.processor.RequestProcessorChain;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageRequestProcessor
    implements RequestProcessor
{
    private CmsPlugin plugin_;

    private PageProcessorLifecycleListener[] lifecycleListeners_;

    private PageProcessorChainFactory processorChainFactory_;

    private KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    /*
     * RequestProcessor
     */

    public void init(ServletConfig config)
    {
        prepareForPageProcessors(config);

        lifecycleListeners_ = plugin_.getExtensionComponents(
            PageProcessorLifecycleListener.class, true);
        for (int i = 0; i < lifecycleListeners_.length; i++) {
            try {
                lifecycleListeners_[i].notifyPageProcessorsStarted();
            } catch (Throwable t) {
                log_
                    .error(
                        "PageProcessorLifecycleListener has thrown exception when started: pageProcessorLifecycleListener="
                            + lifecycleListeners_[i], t);
            }
        }
    }


    void prepareForPageProcessors(ServletConfig config)
    {
        processorChainFactory_ = new PageProcessorChainFactory(plugin_
            .getExtensionElements(PageProcessorElement.class, false),
            PageProcessor.class, config, plugin_.getPageProcessorPhases(),
            plugin_.getPageProcessorDefaultPhase());
    }


    public void destroy()
    {
        processorChainFactory_.destroy();
        processorChainFactory_ = null;

        lifecycleListeners_ = null;

        plugin_ = null;
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, RequestProcessorChain processorChain)
        throws ServletException, IOException
    {
        PageRequest pageRequest = (PageRequest)request
            .getAttribute(CmsPlugin.ATTR_PAGEREQUEST);

        PageProcessorChain chain = processorChainFactory_.newChain(pageRequest
            .getMy());
        chain.addProcessor(new PageProcessorChainAdapter(processorChain));
        chain.doProcess(request, response, pageRequest);
    }


    /*
     * for framework
     */

    public void setPlugin(CmsPlugin plugin)
    {
        plugin_ = plugin;
    }
}
