package org.seasar.kvasir.webapp.processor;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface RequestProcessor
{
    void init(ServletConfig config);


    void destroy();


    void doProcess(HttpServletRequest request, HttpServletResponse response,
        RequestProcessorChain chain)
        throws ServletException, IOException;
}
