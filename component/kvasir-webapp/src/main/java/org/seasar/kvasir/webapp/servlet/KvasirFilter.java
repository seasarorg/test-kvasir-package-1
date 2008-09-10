package org.seasar.kvasir.webapp.servlet;

import static org.seasar.kvasir.webapp.Globals.ATTR_EXCEPTION;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.ServiceUnavailableRuntimeException;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.Globals;
import org.seasar.kvasir.webapp.chain.impl.ExceptionHandlerChainImpl;
import org.seasar.kvasir.webapp.chain.impl.RequestFilterChainAdapter;
import org.seasar.kvasir.webapp.chain.impl.RequestFilterChainImpl;
import org.seasar.kvasir.webapp.extension.ExceptionHandlerElement;
import org.seasar.kvasir.webapp.extension.RequestFilterElement;
import org.seasar.kvasir.webapp.filter.RequestFilter;
import org.seasar.kvasir.webapp.filter.RequestFilterChain;
import org.seasar.kvasir.webapp.filter.impl.FilterConfigImpl;
import org.seasar.kvasir.webapp.handler.ExceptionHandler;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerChain;
import org.seasar.kvasir.webapp.handler.impl.ExceptionHandlerConfigImpl;
import org.seasar.kvasir.webapp.impl.PhasedElementComparator;
import org.seasar.kvasir.webapp.util.ServletUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirFilter
    implements Filter
{
    private static final String EXCEPTIONHANDLER_DEFAULTPHASE = "execute";

    private static final String[] EXCEPTIONHANDLER_PHASES = new String[] { EXCEPTIONHANDLER_DEFAULTPHASE };

    private FilterConfig config_;

    private Dispatcher dispatcher_;

    private final KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public void init(FilterConfig config)
        throws ServletException
    {
        config_ = config;

        String dispatcher = config.getInitParameter("dispatcher");
        if (dispatcher == null) {
            throw new ServletException(
                "Init-param 'dispatcher' must be specified");
        }
        dispatcher_ = Dispatcher.valueOf(dispatcher.toUpperCase());
    }


    RequestFilter[] getRequestFilters()
    {
        RequestFilter[] filters = (RequestFilter[])Asgard.getKvasir()
            .getAttribute(Globals.ATTR_REQUESTFILTERS);
        if (filters == null) {
            filters = prepareForRequestFilters();
        }
        return filters;
    }


    RequestFilter[] prepareForRequestFilters()
    {
        Kvasir kvasir = Asgard.getKvasir();

        PluginAlfr alfr = kvasir.getPluginAlfr();
        Plugin<?> plugin = alfr.getPlugin(Globals.PLUGINID);

        RequestFilterElement[] elements = alfr.getExtensionElements(
            RequestFilterElement.class, Globals.PLUGINID, false);

        Arrays.sort(elements, new PhasedElementComparator(PropertyUtils
            .toLines(plugin.getProperty(Globals.PROP_REQUESTFILTER_PHASES)),
            plugin.getProperty(Globals.PROP_REQUESTFILTER_PHASE_DEFAULT)));

        RequestFilter[] filters = new RequestFilter[elements.length];
        for (int i = 0; i < filters.length; i++) {
            filters[i] = elements[i].getRequestFilter();
            filters[i].init(new FilterConfigImpl(elements[i].getFullId(),
                config_.getServletContext(), elements[i].getPropertyHandler()));
        }

        kvasir.setAttribute(Globals.ATTR_REQUESTFILTERS, filters);

        return filters;
    }


    ExceptionHandler[] getExceptionHandlers()
    {
        ExceptionHandler[] handlers = (ExceptionHandler[])Asgard.getKvasir()
            .getAttribute(Globals.ATTR_EXCEPTIONHANDLERS);
        if (handlers == null) {
            handlers = prepareForExceptionHandlers();
        }

        return handlers;
    }


    ExceptionHandler[] prepareForExceptionHandlers()
    {
        Kvasir kvasir = Asgard.getKvasir();
        ExceptionHandlerElement[] elements = kvasir.getPluginAlfr()
            .getExtensionElements(ExceptionHandlerElement.class,
                Globals.PLUGINID, false);

        Arrays.sort(elements, new PhasedElementComparator(
            EXCEPTIONHANDLER_PHASES, EXCEPTIONHANDLER_DEFAULTPHASE));

        ExceptionHandler[] handlers = new ExceptionHandler[elements.length];
        for (int i = 0; i < elements.length; i++) {
            handlers[i] = elements[i].getExceptionHandler();
            handlers[i].init(new ExceptionHandlerConfigImpl(elements[i]
                .getFullId(), elements[i].getPropertyHandler()));
        }

        kvasir.setAttribute(Globals.ATTR_EXCEPTIONHANDLERS, handlers);

        return handlers;
    }


    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain filterChain)
        throws IOException, ServletException
    {
        Kvasir kvasir = Asgard.getKvasir();
        Thread thread = Thread.currentThread();
        ClassLoader cl = thread.getContextClassLoader();
        try {
            if (dispatcher_ != Dispatcher.REQUEST) {
                // forwardなどがあるとcontextClassLoaderがWebappClassLoaderに
                // 戻されてしまうっぽいのでコンテキストクラスローダを設定するようにしている。
                // なおdispatcherがREQUESTの時はKvasir#beginSession()中で
                // コンテキストクラスローダがセットされる。
                thread.setContextClassLoader(kvasir.getCurrentClassLoader());

                if (request.getAttribute(ATTR_EXCEPTION) == null) {
                    processChain(request, response, filterChain);
                } else {
                    // エラー発生後の遷移の場合は無限ループを避けるためにKvasirのフィルタを実行しない。
                    filterChain.doFilter(request, response);
                }
                return;
            }

            ComponentContainer container = kvasir.getRootComponentContainer();
            try {
                if (!kvasir.beginSession()) {
                    throw new ServiceUnavailableRuntimeException();
                }
                try {
                    container.setRequest(request);
                    container.setResponse(response);

                    processChain(request, response, filterChain);
                } finally {
                    container.setRequest(null);
                    container.setResponse(null);
                    long elapsedTime = kvasir.endSession();
                    if (log_.isDebugEnabled()) {
                        log_.debug("PATH: "
                            + ServletUtils.getPath((HttpServletRequest)request)
                            + ", ELAPSED TIME(ms): " + elapsedTime);
                    }
                }
            } catch (Exception ex) {
                if (isIgnorable(ex)) {
                    if (log_.isDebugEnabled()) {
                        log_.debug("[IGNORE] Ignorable exception", ex);
                    }
                } else {
                    if (log_.isDebugEnabled()) {
                        log_.debug("Execption occured", ex);
                    }
                    request.setAttribute(ATTR_EXCEPTION, ex);
                    boolean exceptionWasPassedThrough = false;
                    try {
                        exceptionWasPassedThrough = processExceptionHandlerChain(
                            (HttpServletRequest)request,
                            (HttpServletResponse)response, ex);
                    } catch (Throwable t) {
                        log_.error("Can't process exceptionHandler", t);
                    }
                    if (exceptionWasPassedThrough && !response.isCommitted()) {
                        // デフォルトの処理を行なう。
                        if (ex instanceof IOException) {
                            throw (IOException)ex;
                        } else if (ex instanceof ServletException) {
                            throw (ServletException)ex;
                        } else if (ex instanceof RuntimeException) {
                            throw (RuntimeException)ex;
                        } else {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        } finally {
            thread.setContextClassLoader(cl);
        }
    }


    boolean isIgnorable(Exception ex)
    {
        Throwable t = ex;
        do {
            if (t instanceof SocketException) {
                // Broken pipeとみなして無視する。
                return true;
            }
        } while ((t = t.getCause()) != null);
        return false;
    }


    boolean processChain(ServletRequest request, ServletResponse response,
        FilterChain filterChain)
        throws IOException, ServletException
    {
        RequestFilterChain chain = new RequestFilterChainImpl(
            getRequestFilters());
        chain.addProcessor(new RequestFilterChainAdapter(filterChain));
        chain.doFilter((HttpServletRequest)request,
            (HttpServletResponse)response, dispatcher_);
        return chain.isAllProcessorsProcessed();
    }


    boolean processExceptionHandlerChain(HttpServletRequest request,
        HttpServletResponse response, Exception ex)
    {
        ExceptionHandlerChain chain = new ExceptionHandlerChainImpl(
            getExceptionHandlers());
        chain.doHandle(request, response, ex);
        return chain.isAllProcessorsProcessed();
    }


    public void destroy()
    {
        // FilterのライフサイクルはKvasirのライフサイクルより長いため、
        // RequestFilterとExceptionHandlerの破棄はBaseWebappPlugin#stop()で行なう。

        config_ = null;
        dispatcher_ = null;
    }
}
