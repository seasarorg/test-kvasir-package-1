package org.seasar.kvasir.cms.processor;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageProcessor
{
    void init(ServletConfig config);


    void destroy();


    void doProcess(HttpServletRequest request, HttpServletResponse response,
        PageRequest pageRequest, PageProcessorChain chain)
        throws ServletException, IOException;
}
