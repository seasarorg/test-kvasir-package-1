package org.seasar.kvasir.cms.ymir.impl;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cms.ymir.extension.zpt.TemplatePathResolver;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;


public class KvasirTemplatePathResolver
    implements TemplatePathResolver
{
    public String getLocalPath(String path, HttpServletRequest request)
    {
        String gardRootPathname = getGardRootPathname(request);
        if (path.startsWith(gardRootPathname)) {
            return path.substring(gardRootPathname.length());
        } else {
            // gard外のパス。
            return null;
        }
    }


    public String resolve(String path, HttpServletRequest request)
    {
        if (path == null) {
            return null;
        }
        return getGardRootPathname(request) + path;
    }


    String getGardRootPathname(HttpServletRequest request)
    {
        return ((PageRequest)request.getAttribute(CmsPlugin.ATTR_PAGEREQUEST))
            .getMy().getGardRootPage().getPathname();
    }
}
