package org.seasar.kvasir.cms.java;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;


/**
 * <p><b>同期化：</b>
 * このクラスのサブクラスはスレッドセーフである必要はありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class Base
{
    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected PageRequest pageRequest;

    protected PageDispatch my;

    protected PageDispatch that;

    private boolean finish_;


    public final boolean process(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest)
        throws Exception
    {
        this.request = request;
        this.response = response;
        this.pageRequest = pageRequest;
        this.my = pageRequest.getMy();
        this.that = pageRequest.getThat();

        finish_ = false;
        execute();
        return finish_;
    }


    public void execute()
        throws Exception
    {
    }


    public final void finish()
    {
        finish_ = true;
    }
}
