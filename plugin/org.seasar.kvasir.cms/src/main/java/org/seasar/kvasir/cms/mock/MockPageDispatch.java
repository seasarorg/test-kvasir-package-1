package org.seasar.kvasir.cms.mock;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.webapp.Dispatcher;


public class MockPageDispatch
    implements PageDispatch
{
    private Dispatcher dispatcher_;

    private String gardId_;

    private String[] gardIds_;

    private Page gardRootPage_;

    private Page[] gardRootPages_;

    private String localPathname_;

    private Page nearestPage_;

    private Page page_;

    private String pathname_;

    private Plugin<?> plugin_;


    public Dispatcher getDispatcher()
    {
        return dispatcher_;
    }


    public void setDispatcher(Dispatcher dispatcher)
    {
        dispatcher_ = dispatcher;
    }


    public String getGardId()
    {
        return gardId_;
    }


    public void setGardId(String gardId)
    {
        gardId_ = gardId;
    }


    public String[] getGardIds()
    {
        return gardIds_;
    }


    public void setGardIds(String[] gardIds)
    {
        gardIds_ = gardIds;
    }


    public Page getGardRootPage()
    {
        return gardRootPage_;
    }


    public void setGardRootPage(Page gardRootPage)
    {
        gardRootPage_ = gardRootPage;
    }


    public Page[] getGardRootPages()
    {
        return gardRootPages_;
    }


    public void setGardRootPages(Page[] gardRootPages)
    {
        gardRootPages_ = gardRootPages;
    }


    public String getLocalPathname()
    {
        return localPathname_;
    }


    public void setLocalPathname(String localPathname)
    {
        localPathname_ = localPathname;
    }


    public Page getNearestPage()
    {
        return nearestPage_;
    }


    public void setNearestPage(Page nearestPage)
    {
        nearestPage_ = nearestPage;
    }


    public Page getPage()
    {
        return page_;
    }


    public void setPage(Page page)
    {
        page_ = page;
        pathname_ = page.getPathname();
    }


    public String getPathname()
    {
        return pathname_;
    }


    public void setPathname(String pathname)
    {
        pathname_ = pathname;
    }


    public Plugin<?> getPlugin()
    {
        return plugin_;
    }


    public void setPlugin(Plugin<?> plugin)
    {
        plugin_ = plugin;
    }
}
