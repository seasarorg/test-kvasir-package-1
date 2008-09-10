package org.seasar.kvasir.webapp.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface ExceptionHandler
{
    void init(ExceptionHandlerConfig config);


    void destroy();


    void doHandle(HttpServletRequest request, HttpServletResponse response,
        Exception ex, ExceptionHandlerChain chain);
}
