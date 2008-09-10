package org.seasar.kvasir.cms.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.cms.PageRequest;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageExceptionHandlerInvoker
{
    void init();


    void destroy();


    boolean invokeHandlers(HttpServletRequest request,
        HttpServletResponse response, Exception ex, PageRequest pageRequest,
        String pathname, Page page);
}
