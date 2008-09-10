package org.seasar.kvasir.webapp.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.Chain;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface ExceptionHandlerChain
    extends Chain<ExceptionHandler>
{
    void doHandle(HttpServletRequest request, HttpServletResponse response,
        Exception ex);
}
