package org.seasar.kvasir.cms.manage.manage.web;

import java.util.Locale;
import java.util.Map;

import org.seasar.cms.ymir.Path;
import org.seasar.cms.ymir.Request;
import org.seasar.cms.ymir.extension.annotation.In;
import org.seasar.cms.ymir.extension.annotation.Out;
import org.seasar.cms.ymir.scope.impl.SessionScope;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.manage.ManagePlugin;
import org.seasar.kvasir.cms.manage.PageService;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;

import net.skirnir.freyja.render.Notes;


public class PageBase
{
    /*
     * for framework
     */

    private ManagePlugin plugin_;

    private PagePlugin pagePlugin_;

    private CmsPlugin cmsPlugin_;

    /*
     * for presentation tier
     */

    private String pathname_;

    private Page page_;

    private Notes notes_;

    private Notes transferredNotes_;

    /*
     * inner fields
     */

    private Request ymirRequest_;

    private PageRequest pageRequest_;

    private Locale locale_;

    private PageAlfr pageAlfr_;

    private PageService pageService_;

    private Page topPage_;

    private String topPathname_;

    private Integer currentHeimId_;

    protected final KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    /*
     * public scope methods
     */

    public Request getYmirRequest()
    {
        return ymirRequest_;
    }


    public PageRequest getPageRequest()
    {
        return pageRequest_;
    }


    public Locale getLocale()
    {
        return locale_;
    }


    public ManagePlugin getPlugin()
    {
        return plugin_;
    }


    public PagePlugin getPagePlugin()
    {
        return pagePlugin_;
    }


    public CmsPlugin getCmsPlugin()
    {
        return cmsPlugin_;
    }


    public PageAlfr getPageAlfr()
    {
        return pageAlfr_;
    }


    @In(name = ManagePlugin.ATTR_MANAGE_HEIMID, scopeClass = SessionScope.class)
    public void setCurrentHeimId(int currentHeimId)
    {
        currentHeimId_ = currentHeimId;
    }


    @Out(name = ManagePlugin.ATTR_MANAGE_HEIMID, scopeClass = SessionScope.class)
    public int getCurrentHeimId()
    {
        if (currentHeimId_ == null) {
            currentHeimId_ = getRequestedHeimId();
        }
        return currentHeimId_;
    }


    public int getRequestedHeimId()
    {
        return pageRequest_.getRootPage().getHeimId();
    }


    public String getPathname()
    {
        return pathname_;
    }


    public Page getPage()
    {
        if (page_ == null) {
            page_ = pageAlfr_.getPage(getCurrentHeimId(), pathname_);
        }
        return page_;
    }


    public PageService getPageService()
    {
        if (pageService_ == null) {
            pageService_ = getPlugin().getPageService(getPage().getType());
        }
        return pageService_;
    }


    public Page getTopPage()
    {
        return topPage_;
    }


    public String getTopPathname()
    {
        return topPathname_;
    }


    public String getResource(String name)
    {
        String value = plugin_.getProperty(name, locale_);
        if (value == null) {
            value = name;
        }
        return value;
    }


    public void setNotes(Notes notes)
    {
        notes_ = notes;
        transferredNotes_ = notes;
    }


    protected Path getRedirection(String path)
    {
        return getRedirection(path, null);
    }


    protected Path getRedirection(String path, Map<String, String[]> paramMap)
    {
        return new Path(path, paramMap);
    }


    public boolean isInMidgard()
    {
        return (getCurrentHeimId() == PathId.HEIM_MIDGARD);
    }


    /*
     * for framework
     */

    public void setPlugin(ManagePlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    public void setCmsPlugin(CmsPlugin cmsPlugin)
    {
        cmsPlugin_ = cmsPlugin;
    }


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    public void setYmirRequest(Request ymirRequest)
    {
        ymirRequest_ = ymirRequest;
        pathname_ = ymirRequest.getPathInfo();
        // 念のため。
        if (pathname_.endsWith("/")) {
            pathname_ = pathname_.substring(0, pathname_.length() - 1);
        }
    }


    public void setPageRequest(PageRequest pageRequest)
    {
        pageRequest_ = pageRequest;
        locale_ = pageRequest.getLocale();
        topPage_ = pageRequest.getMy().getGardRootPage();
        topPathname_ = topPage_.getPathname();
    }


    @Out(SessionScope.class)
    public Notes getTransferredNotes()
    {
        return transferredNotes_;
    }


    @In(SessionScope.class)
    public void setTransferredNotes(Notes transferredNotes)
    {
        notes_ = transferredNotes;
        transferredNotes_ = null;
    }


    /*
     * for presentation tier
     */

    public Notes getNotes()
    {
        return notes_;
    }
}
