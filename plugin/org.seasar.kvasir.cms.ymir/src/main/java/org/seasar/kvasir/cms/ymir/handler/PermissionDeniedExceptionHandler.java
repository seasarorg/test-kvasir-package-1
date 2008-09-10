package org.seasar.kvasir.cms.ymir.handler;

import org.seasar.cms.ymir.handler.ExceptionHandler;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.page.SecurityRuntimeException;


public class PermissionDeniedExceptionHandler
    implements ExceptionHandler
{
    private PageRequest pageRequest_;


    public void setPageRequest(PageRequest pageRequest)
    {
        pageRequest_ = pageRequest;
    }


    public String handle(Throwable t)
    {
        // TODO USERをセットする？
        throw new SecurityRuntimeException(t).setPage(pageRequest_.getMy()
            .getPage());
    }
}
