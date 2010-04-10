package org.seasar.kvasir.webapp.servlet;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.webapp.Globals;
import org.seasar.kvasir.webapp.chain.impl.RequestProcessorChainImpl;
import org.seasar.kvasir.webapp.extension.RequestProcessorElement;
import org.seasar.kvasir.webapp.impl.PhasedElementComparator;
import org.seasar.kvasir.webapp.processor.RequestProcessor;
import org.seasar.kvasir.webapp.processor.RequestProcessorChain;


/**
 * Kvasir/SoraへのHTTP経由のリクエストを受け付けるサーブレットです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirServlet extends HttpServlet
{
    private static final long serialVersionUID = 6491022652135462692L;

    private ServletConfig config_;


    @Override
    public void init(ServletConfig config)
        throws ServletException
    {
        config_ = config;
    }


    RequestProcessor[] getRequestProcessors()
    {
        RequestProcessor[] processors = (RequestProcessor[])Asgard.getKvasir()
            .getAttribute(Globals.ATTR_REQUESTPROCESSORS);
        if (processors == null) {
            processors = prepareForRequestProcessors();
        }
        return processors;
    }


    RequestProcessor[] prepareForRequestProcessors()
    {
        Kvasir kvasir = Asgard.getKvasir();

        PluginAlfr alfr = kvasir.getPluginAlfr();
        Plugin<?> plugin = alfr.getPlugin(Globals.PLUGINID);

        RequestProcessorElement[] elements = alfr.getExtensionElements(
            RequestProcessorElement.class, Globals.PLUGINID, false);

        Arrays.sort(elements, new PhasedElementComparator(PropertyUtils
            .toLines(plugin.getProperty(Globals.PROP_REQUESTPROCESSOR_PHASES)),
            plugin.getProperty(Globals.PROP_REQUESTPROCESSOR_PHASE_DEFAULT)));

        RequestProcessor[] processors = new RequestProcessor[elements.length];
        for (int i = 0; i < processors.length; i++) {
            processors[i] = elements[i].getRequestProcessor();
            processors[i].init(new ServletConfigImpl(elements[i].getFullId(),
                config_.getServletContext(), elements[i].getPropertyHandler()));
        }

        kvasir.setAttribute(Globals.ATTR_REQUESTPROCESSORS, processors);

        return processors;
    }


    @Override
    public void destroy()
    {
        // ServletのライフサイクルはKvasirのライフサイクルより長いため、
        // RequestProcessorの破棄はBaseWebappPlugin#stop()で行なう。

        config_ = null;
    }


    @Override
    protected void service(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException
    {
        doProcess(request, response);
    }


    protected void doProcess(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException
    {
        RequestProcessorChain chain = new RequestProcessorChainImpl(
            getRequestProcessors());
        chain.doProcess(request, response);
    }
}
