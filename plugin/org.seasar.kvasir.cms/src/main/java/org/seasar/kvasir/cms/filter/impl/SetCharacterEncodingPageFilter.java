package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.webapp.Dispatcher;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class SetCharacterEncodingPageFilter
    implements PageFilter
{
    public static final String PARAM_REQUESTENCODING = "requestEncoding";

    private String requestEncoding_;


    public void init(FilterConfig config)
    {
        requestEncoding_ = config.getInitParameter(PARAM_REQUESTENCODING);
    }


    public void destroy()
    {
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        if (requestEncoding_ != null && request.getCharacterEncoding() == null) {
            try {
                request.setCharacterEncoding(requestEncoding_);
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalArgumentException("Parameter '"
                    + PARAM_REQUESTENCODING + "' is invalid: "
                    + requestEncoding_, ex);
            }
        }

        chain.doFilter(request, response, dispatcher, pageRequest);
    }
}
