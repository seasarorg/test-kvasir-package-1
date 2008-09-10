package org.seasar.kvasir.cms.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.Chain;
import org.seasar.kvasir.cms.PageRequest;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageExceptionHandlerChain
    extends Chain<PageExceptionHandler>
{
    public void doHandle(HttpServletRequest request,
        HttpServletResponse response, Exception ex, PageRequest pageRequest);
}
