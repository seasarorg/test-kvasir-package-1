package org.seasar.kvasir.cms.manage.manage.web;

import javax.servlet.http.HttpServletRequest;


public class LogoutPage extends PageBase
{
    private HttpServletRequest request_;


    public void setRequest(HttpServletRequest request)
    {
        request_ = request;
    }


    public String do_execute()
    {
        getCmsPlugin().logout(request_);
        return "redirect:!";
    }
}
