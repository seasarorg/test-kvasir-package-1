package org.seasar.kvasir.cms.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.chain.Chain;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.webapp.Dispatcher;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageFilterChain
    extends Chain<PageFilter>
{
    void doFilter(HttpServletRequest request, HttpServletResponse response,
        Dispatcher dispatcher, PageRequest pageRequest)
        throws ServletException, IOException;
}
