package org.seasar.kvasir.base.webapp.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.webapp.extension.ExceptionHandlerElement;
import org.seasar.kvasir.webapp.handler.ExceptionHandler;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerChain;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerConfig;
import org.seasar.kvasir.webapp.util.ServletUtils;


public class TransitionExceptionHandler
    implements ExceptionHandler
{
    public static final String PROP_PATHNAME = "pathname";

    public static final String PROP_REDIRECT = "redirect";

    private ExceptionHandlerElement element_;

    private String pathname_;

    private boolean redirect_;

    private static final KvasirLog log_ = KvasirLogFactory
        .getLog(TransitionExceptionHandler.class);


    public void setElement(ExceptionHandlerElement element)
    {
        element_ = element;
    }


    public void init(ExceptionHandlerConfig config)
    {
        pathname_ = config.getInitParameter(PROP_PATHNAME);
        if (pathname_ == null) {
            throw new IllegalArgumentException(PROP_PATHNAME
                + " must be specified: plugin-id="
                + element_.getPlugin().getId() + ", component-id="
                + element_.getId());
        }
        redirect_ = PropertyUtils.valueOf(config
            .getInitParameter(PROP_REDIRECT), false);
    }


    public void doHandle(HttpServletRequest request,
        HttpServletResponse response, Exception ex, ExceptionHandlerChain chain)
    {
        if (response.isCommitted()) {
            // 既に何かが出力されてしまっているため、エラー画面へのフォワードはしないようにする。
            log_.error("Exception has occured", ex);
            return;
        }

        if (log_.isDebugEnabled()) {
            log_.debug("Exception has occured", ex);
        }

        try {
            if (redirect_) {
                response.sendRedirect(response.encodeRedirectURL(ServletUtils
                    .getURI(pathname_, request)));
            } else {
                request.getRequestDispatcher(pathname_).forward(request,
                    response);
            }
        } catch (Throwable t) {
            log_.error("Exception has occured", ex);
            log_.error(
                "...and tried to handle this exception, but couldn't forward to: "
                    + pathname_, t);
        }
    }


    public void destroy()
    {
    }
}
