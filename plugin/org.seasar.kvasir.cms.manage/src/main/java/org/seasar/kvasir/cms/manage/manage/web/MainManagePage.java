package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.cms.ymir.Path;
import org.seasar.kvasir.page.Page;


public class MainManagePage extends PageBase
{
    /*
     * public scope methods
     */

    public Object do_execute()
    {
        Page page = getPage();
        if (page == null) {
            return "/error/page-not-found.html";
        }

        if (page.isNode()) {
            return new Path("/list-page.do" + getPathname());
        } else {
            return new Path("/edit-page.do" + getPathname());
        }
    }
}
