package org.seasar.kvasir.cms.util;

import javax.servlet.http.HttpServletRequest;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.impl.PageDispatchImpl;
import org.seasar.kvasir.cms.impl.PageRequestImpl;
import org.seasar.kvasir.page.Page;
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
        return newPageRequest(request, my, my, path);
    }


    public static PageRequest newPageRequest(HttpServletRequest request,
        PageDispatch my, PageDispatch that, String path)
    {
        PluginAlfr pluginAlfr = Asgard.getKvasir().getPluginAlfr();
        return new PageRequestImpl((String)request
            .getAttribute(CmsPlugin.ATTR_CONTEXT_PATH), LocaleUtils
            .findLocale(request), my, that, pluginAlfr.getPlugin(
            PagePlugin.class).getPageAlfr().getRootPage(
            pluginAlfr.getPlugin(CmsPlugin.class).determineHeimId(request)),
            path);
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
     * <p>PageRequestが存在してかつmyがPageを持つ場合は
     * myが持つPageが所属するHeimのIDが返されます。
     * このためmyが差し替えられている場合は
     * リクエストされた直接のHeimのIDは返されないことがあるので注意して下さい。
     * リクエストされた直接のHeimのIDを取得したい場合は{@link #getOriginalHeimId()}
     * を使って下さい。 
     * </p>
     * 
     * @return 現在処理中のリクエストにマッピングされているHeimのID。
     * @see #getOriginalHeimId()
     */
    public static int getHeimId()
    {
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        if (request != null) {
            PageRequest pageRequest = getPageRequest(request);
            if (pageRequest != null) {
                Page page = pageRequest.getMy().getPage();
                if (page != null) {
                    return page.getHeimId();
                } else {
                    return pageRequest.getRootPage().getHeimId();
                }
            } else {
                return Asgard.getKvasir().getPluginAlfr().getPlugin(
                    CmsPlugin.class).determineHeimId(request);
            }
        }
        return PathId.HEIM_MIDGARD;
    }


    /**
     * 現在処理中のリクエストにマッピングされているHeimのIDを返します。
     * <p>このメソッドは{@link #getHeimId()}とは異なり、
     * 元々のHeimのIDを返します。
     * </p>
     * 
     * @return 現在処理中のリクエストにマッピングされているHeimのID。
     * @see #getHeimId()
     */
    public static int getOriginalHeimId()
    {
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        if (request != null) {
            return Asgard.getKvasir().getPluginAlfr()
                .getPlugin(CmsPlugin.class).determineHeimId(request);
        }
        return PathId.HEIM_MIDGARD;
    }
}
