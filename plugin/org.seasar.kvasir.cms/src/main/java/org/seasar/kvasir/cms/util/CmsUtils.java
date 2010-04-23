package org.seasar.kvasir.cms.util;

import javax.servlet.http.HttpServletRequest;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.impl.PageDispatchImpl;
import org.seasar.kvasir.cms.impl.PageRequestImpl;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.util.LocaleUtils;


public class CmsUtils
{
    private CmsUtils()
    {
    }


    public static PageDispatch newPageDispatch(HttpServletRequest request,
        Dispatcher dispatcher, String path)
    {
        PluginAlfr pluginAlfr = Asgard.getKvasir().getPluginAlfr();
        PagePlugin pagePlugin = pluginAlfr.getPlugin(PagePlugin.class);
        CmsPlugin cmsPlugin = pluginAlfr.getPlugin(CmsPlugin.class);
        return new PageDispatchImpl(pagePlugin.getPageAlfr(), pagePlugin,
            dispatcher, cmsPlugin.determineHeimId(request), path);
    }


    public static PageRequest newPageRequest(HttpServletRequest request,
        Dispatcher dispatcher, String path)
    {
        PageDispatch my = newPageDispatch(request, dispatcher, path);
        return newPageRequest(request, my, my);
    }


    public static PageRequest newPageRequest(HttpServletRequest request,
        PageDispatch my, PageDispatch that)
    {
        PluginAlfr pluginAlfr = Asgard.getKvasir().getPluginAlfr();
        return new PageRequestImpl((String)request
            .getAttribute(CmsPlugin.ATTR_CONTEXT_PATH), LocaleUtils
            .findLocale(request), my, that, pluginAlfr.getPlugin(
            PagePlugin.class).getPageAlfr().getRootPage(
            pluginAlfr.getPlugin(CmsPlugin.class).determineHeimId(request)));
    }


    public static PageRequest getPageRequest(HttpServletRequest request)
    {
        if (request != null) {
            return (PageRequest)request
                .getAttribute(CmsPlugin.ATTR_PAGEREQUEST);
        } else {
            return null;
        }
    }


    public static PageRequest getPageRequest()
    {
        return getPageRequest(ServletUtils.getHttpServletRequest());
    }


    public static String getOriginalContextPath(HttpServletRequest request)
    {
        if (request != null) {
            return (String)request.getAttribute(CmsPlugin.ATTR_CONTEXT_PATH);
        } else {
            return null;
        }
    }


    /**
     * 現在処理中のリクエストにマッピングされているHeimのIDを返します。
     * 
     * @return 現在処理中のリクエストにマッピングされているHeimのID。
     */
    public static int getHeimId()
    {
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        if (request != null) {
            PageRequest pageRequest = getPageRequest(request);
            if (pageRequest != null) {
                return pageRequest.getRootPage().getHeimId();
            } else {
                return Asgard.getKvasir().getPluginAlfr().getPlugin(
                    CmsPlugin.class).determineHeimId(request);
            }
        }
        return PathId.HEIM_MIDGARD;
    }
}
