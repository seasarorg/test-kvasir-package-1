package org.seasar.kvasir.cms.handler.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.handler.PageExceptionHandler;
import org.seasar.kvasir.cms.handler.PageExceptionHandlerChain;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.util.NormalizedRuntimeException;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerConfig;


/**
 * 例外が発生した場合に規定のテンプレートページを表示するような
 * PageExceptionHandler実装です。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class StandardPageExceptionHandler
    implements PageExceptionHandler
{
    public static final String SUBPATHNAME_TEMPLATES_EXCEPTIONS = Page.PATHNAME_TEMPLATES
        .substring(1)
        + "/exceptions";

    private Kvasir kvasir_;

    private AuthPlugin authPlugin_;

    private static final KvasirLog log_ = KvasirLogFactory
        .getLog(StandardPageExceptionHandler.class);


    public void setKvasir(Kvasir kvasir)
    {
        kvasir_ = kvasir;
    }


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    /*
     * PageExceptionHandler
     */

    public void init(ExceptionHandlerConfig config)
    {
    }


    public void destroy()
    {
    }


    public void doHandle(HttpServletRequest request,
        HttpServletResponse response, Exception ex, PageRequest pageRequest,
        PageExceptionHandlerChain chain)
    {
        if (response.isCommitted()) {
            // 既に何かが出力されてしまっているため、エラー画面へのフォワードはしないようにする。
            log_.error("Exception has occured", ex);
            return;
        }

        Page errorPage = null;
        Page[] gardRootPages = pageRequest.getMy().getGardRootPages();
        for (int i = 1; i < gardRootPages.length; i++) {
            if ((errorPage = findErrorPage(ex.getClass(), gardRootPages[i])) != null) {
                break;
            }
        }
        if (errorPage == null) {
            errorPage = findErrorPage(ex.getClass(), pageRequest.getRootPage());
        }

        if (errorPage != null) {
            if (log_.isDebugEnabled()) {
                log_.debug("Exception has occured", ex);
            }
            if (kvasir_.isUnderDevelopment()
                || authPlugin_.getCurrentActor().isAdministrator()) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                new NormalizedRuntimeException(ex).printStackTrace(pw);
                pw.flush();
                request.setAttribute("stackTrace", sw.toString());
            }
            RequestDispatcher rd = request.getRequestDispatcher(errorPage
                .getPathname());
            try {
                rd.forward(request, response);
            } catch (Throwable t) {
                log_.error("Exception has occured", ex);
                log_.error(
                    "...and tried to handle this exception, but couldn't forward to: "
                        + errorPage.getPathname(), t);
            }
            return;
        } else {
            log_.info("Exception has occured", ex);
        }

        chain.doHandle(request, response, ex, pageRequest);
    }


    Page findErrorPage(Class<?> throwableClass, Page gardRootPage)
    {
        for (Class<?> clazz = throwableClass; Exception.class
            .isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
            String className = clazz.getName();
            int dot = className.lastIndexOf('.');
            if (dot >= 0) {
                className = className.substring(dot + 1);
            }
            Page errorPage = gardRootPage
                .getChild(SUBPATHNAME_TEMPLATES_EXCEPTIONS + "/" + className);
            if (errorPage != null) {
                return errorPage;
            }
        }
        return null;
    }
}
