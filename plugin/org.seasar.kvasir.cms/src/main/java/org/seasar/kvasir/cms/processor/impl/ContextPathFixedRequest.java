package org.seasar.kvasir.cms.processor.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ContextPathFixedRequest extends HttpServletRequestWrapper
{
    private String contextPath_;


    public ContextPathFixedRequest(HttpServletRequest request,
        String contextPath)
    {
        super(request);
        contextPath_ = contextPath;
    }


    /*
     * HttpServletRequest
     */

    public String getContextPath()
    {
        return contextPath_;
    }
}
