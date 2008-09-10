package org.seasar.kvasir.cms.impl;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageDispatchImpl
    implements PageDispatch
{
    private PagePlugin pagePlugin_;

    private String pathname_;

    private Page page_;

    private Plugin<?> plugin_;

    private Page gardRootPage_;

    private String gardId_;

    private String localPathname_;

    private Page nearestPage_;

    private Page[] gardRootPages_;

    private String[] gardIds_;


    /*
     * constructors
     */

    public PageDispatchImpl(PageAlfr pageAlfr, PagePlugin pagePlugin,
        int heimId, String pathname)
    {
        Page page = pageAlfr.getPage(heimId, pathname);
        Page nearestPage;
        if (page != null) {
            nearestPage = page;
        } else {
            nearestPage = pageAlfr.findNearestPage(heimId, pathname);
        }

        pagePlugin_ = pagePlugin;
        pathname_ = pathname;
        page_ = page;
        nearestPage_ = nearestPage;
        gardRootPages_ = nearestPage.getGardRoots();
        gardIds_ = nearestPage.getGardIds();
    }


    /*
     * PageDispatch
     */

    public String getPathname()
    {
        return pathname_;
    }


    public Page getPage()
    {
        return page_;
    }


    public Page getNearestPage()
    {
        return nearestPage_;
    }


    public Page[] getGardRootPages()
    {
        return gardRootPages_;
    }


    public String getGardId()
    {
        return gardId_;
    }


    public Page getGardRootPage()
    {
        return gardRootPage_;
    }


    public String getLocalPathname()
    {
        return localPathname_;
    }


    public Plugin<?> getPlugin()
    {
        return plugin_;
    }


    /*
     * for framework
     */

    public void setGardRootPage(Page gardRootPage)
    {
        gardRootPage_ = gardRootPage;
        localPathname_ = pathname_.substring(gardRootPage
            .getPathname().length());
        gardId_ = gardRootPage.getGardId();
        plugin_ = pagePlugin_.getPlugin(gardId_);
    }


    public String[] getGardIds()
    {
        return gardIds_;
    }
}
