package org.seasar.kvasir.cms.filter.impl;

import static org.seasar.kvasir.cms.CmsPlugin.ATTR_CONTEXT_PATH;
import static org.seasar.kvasir.cms.CmsPlugin.ATTR_EXCEPTION;
import static org.seasar.kvasir.cms.CmsPlugin.ATTR_PAGEREQUEST;

import java.io.IOException;
import java.net.SocketException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.extension.PageExceptionHandlerElement;
import org.seasar.kvasir.cms.extension.PageFilterElement;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.cms.filter.PageFilterLifecycleListener;
import org.seasar.kvasir.cms.handler.PageExceptionHandler;
import org.seasar.kvasir.cms.handler.PageExceptionHandlerChain;
import org.seasar.kvasir.cms.handler.impl.PageExceptionHandlerChainFactory;
import org.seasar.kvasir.cms.impl.PageDispatchImpl;
import org.seasar.kvasir.cms.impl.PageRequestImpl;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.filter.RequestFilter;
import org.seasar.kvasir.webapp.filter.RequestFilterChain;
import org.seasar.kvasir.webapp.util.LocaleUtils;
import org.seasar.kvasir.webapp.util.ServletUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageRequestFilter
    implements RequestFilter
{
    private static final String EXCEPTIONHANDLER_DEFAULTPHASE = "execute";

    private static final String[] EXCEPTIONHANDLER_PHASES = new String[] { EXCEPTIONHANDLER_DEFAULTPHASE };

    private CmsPlugin plugin_;

    private PagePlugin pagePlugin_;

    private PageAlfr pageAlfr_;

    private PageFilterLifecycleListener[] lifecycleListeners_;

    private PageFilterChainFactory filterChainFactory_;

    private PageExceptionHandlerChainFactory handlerChainFactory_;

    private KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public void init(FilterConfig config)
    {
        pageAlfr_ = pagePlugin_.getPageAlfr();

        prepareForPageFilters(config);
        prepareForPageExceptionHandlers();

        lifecycleListeners_ = plugin_.getExtensionComponents(
            PageFilterLifecycleListener.class, true);
        for (int i = 0; i < lifecycleListeners_.length; i++) {
            try {
                lifecycleListeners_[i].notifyPageFiltersStarted();
            } catch (Throwable t) {
                log_
                    .error(
                        "PageFilterLifecycleListener has thrown exception when started: pageFilterLifecycleListener="
                            + lifecycleListeners_[i], t);
            }
        }
    }


    void prepareForPageFilters(FilterConfig config)
    {
        filterChainFactory_ = new PageFilterChainFactory(plugin_
            .getExtensionElements(PageFilterElement.class, false),
            PageFilter.class, config, plugin_.getPageFilterPhases(), plugin_
                .getPageFilterDefaultPhase());
    }


    void prepareForPageExceptionHandlers()
    {
        handlerChainFactory_ = new PageExceptionHandlerChainFactory(plugin_
            .getExtensionElements(PageExceptionHandlerElement.class, false),
            PageExceptionHandler.class, null, EXCEPTIONHANDLER_PHASES,
            EXCEPTIONHANDLER_DEFAULTPHASE);
    }


    public void destroy()
    {
        filterChainFactory_.destroy();
        filterChainFactory_ = null;

        handlerChainFactory_.destroy();
        handlerChainFactory_ = null;

        lifecycleListeners_ = null;

        plugin_ = null;
        pagePlugin_ = null;
        pageAlfr_ = null;
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        RequestFilterChain filterChain)
        throws ServletException, IOException
    {
        int heimId = plugin_.determineHeimId(request);
        String path = ServletUtils.getNativePath(request);
        if (log_.isDebugEnabled()) {
            log_.debug("REQUEST: heim=" + heimId + ", path=" + path
                + ", method=" + request.getMethod());
        }
        boolean endsWithSlash = path.endsWith("/");
        if (endsWithSlash) {
            path = path.substring(0, path.length() - 1);
        }

        // PageRequestを準備する。
        PageDispatch my = new PageDispatchImpl(pageAlfr_, pagePlugin_,
            dispatcher, heimId, path);
        if (dispatcher == Dispatcher.REQUEST) {
            String contextPath = ServletUtils.getRequestContextPath(request);

            // ノードページへのリクエストの場合、パスが「/」で終わっていない場合は「/」がつくようにする。
            // ただしメソッドがGETの時だけ。POSTの時などにリダイレクトされると困るので。
            if (my.getPage() != null && !endsWithSlash && my.getPage().isNode()
                && request.getMethod().equalsIgnoreCase("GET")) {
                StringBuffer sb = new StringBuffer();
                sb.append(contextPath).append(path).append("/");
                String queryString = request.getQueryString();
                if (queryString != null) {
                    sb.append("?").append(queryString);
                }
                String encodedURL = response.encodeRedirectURL(sb.toString());
                response.sendRedirect(encodedURL);
                return;
            }

            PageRequest pageRequest = new PageRequestImpl(contextPath,
                LocaleUtils.findLocale(request), my, my, pageAlfr_
                    .getRootPage(heimId));
            request.setAttribute(ATTR_PAGEREQUEST, pageRequest);
            request.setAttribute(ATTR_CONTEXT_PATH, contextPath);

            try {
                processChain(request, response, dispatcher, pageRequest,
                    filterChain);
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
                            request, response, ex, pageRequest);
                    } catch (Throwable t) {
                        log_.error("Can't process pageExceptionHandler", t);
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
        } else {
            PageRequest pageRequest = (PageRequest)request
                .getAttribute(CmsPlugin.ATTR_PAGEREQUEST);
            PageDispatch preMy = pageRequest.setMy(my);
            try {
                if (request.getAttribute(ATTR_EXCEPTION) == null) {
                    processChain(request, response, dispatcher, pageRequest,
                        filterChain);
                } else {
                    // エラー発生後の遷移の場合は無限ループを避けるためにPageFilterを実行しない。
                    filterChain.doFilter(request, response, dispatcher);
                }
            } finally {
                pageRequest.setMy(preMy);
            }
        }
    }


    boolean isIgnorable(Exception ex)
    {
        Throwable t = ex;
        do {
            if (t.getClass() == SocketException.class) {
                // Broken pipeとみなして無視する。
                return true;
            }
        } while ((t = t.getCause()) != null);
        return false;
    }


    boolean processChain(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, RequestFilterChain filterChain)
        throws ServletException, IOException
    {
        PageFilterChain chain = filterChainFactory_.newChain(pageRequest
            .getMy());
        chain.addProcessor(new PageFilterChainAdapter(filterChain));
        chain.doFilter(request, response, dispatcher, pageRequest);
        return chain.isAllProcessorsProcessed();
    }


    boolean processExceptionHandlerChain(HttpServletRequest request,
        HttpServletResponse response, Exception ex, PageRequest pageRequest)
    {
        PageExceptionHandlerChain chain = handlerChainFactory_
            .newChain(pageRequest.getMy());
        chain.doHandle(request, response, ex, pageRequest);
        return chain.isAllProcessorsProcessed();
    }


    /*
     * for framework
     */

    public void setPlugin(CmsPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }
}
