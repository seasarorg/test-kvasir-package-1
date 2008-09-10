package org.seasar.kvasir.cms.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerConfig;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageExceptionHandler
{
    void init(ExceptionHandlerConfig config);


    void destroy();


    void doHandle(HttpServletRequest request, HttpServletResponse response,
        Exception ex, PageRequest pageRequest, PageExceptionHandlerChain chain);
}
