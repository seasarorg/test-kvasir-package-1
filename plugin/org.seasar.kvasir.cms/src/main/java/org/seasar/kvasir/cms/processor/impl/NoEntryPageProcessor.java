package org.seasar.kvasir.cms.processor.impl;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.page.PageNotFoundRuntimeException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class NoEntryPageProcessor
    implements PageProcessor
{
    /*
     * PageProcessor
     */

    public void init(ServletConfig config)
    {
    }


    public void destroy()
    {
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        if (pageRequest.getMy().getPage() == null) {
            throw new PageNotFoundRuntimeException().setPathname(pageRequest
                .getMy().getPathname());
        }

        chain.doProcess(request, response, pageRequest);
    }
}
