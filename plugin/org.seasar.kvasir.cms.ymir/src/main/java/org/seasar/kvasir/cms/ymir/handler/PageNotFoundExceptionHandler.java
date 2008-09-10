package org.seasar.kvasir.cms.ymir.handler;

import org.seasar.cms.ymir.handler.ExceptionHandler;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.page.PageNotFoundRuntimeException;


public class PageNotFoundExceptionHandler
    implements ExceptionHandler
{
    private PageRequest pageRequest_;


    public void setPageRequest(PageRequest pageRequest)
    {
        pageRequest_ = pageRequest;
    }


    public String handle(Throwable t)
    {
        throw new PageNotFoundRuntimeException(t).setPathname(pageRequest_
            .getMy().getPathname());
    }
}
