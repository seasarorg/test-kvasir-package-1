package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.kvasir.util.html.HTMLUtils;


public class ViewPagePage extends MainPanePage
{
    /*
     * public scope methods
     */

    public Object do_execute()
    {
        String url;
        String site = getCmsPlugin().getSite(getCurrentHeimId());
        if (site != null) {
            url = site + getPageRequest().getContextPath() + getPathname();
        } else {
            url = "!" + getPathname();
        }
        return getRedirection(url);
    }


    public String do_switch()
    {
        return "content:text/html:<html><body onload=\"document.getElementById('form').submit()\"><form id=\"form\" action=\""
            + getYmirRequest().getContextPath()
            + "/view-page.do"
            + HTMLUtils.filter(HTMLUtils.reencode(getPathname()))
            + "\" target=\"_top\"></form></body></html>";
    }
}
