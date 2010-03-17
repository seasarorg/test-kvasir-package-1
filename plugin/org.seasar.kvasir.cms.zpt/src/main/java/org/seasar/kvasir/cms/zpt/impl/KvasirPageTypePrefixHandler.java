package org.seasar.kvasir.cms.zpt.impl;

import javax.servlet.http.HttpServletRequest;

import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.util.html.HTMLUtils;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.zpt.webapp.PageTypePrefixHandler;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirPageTypePrefixHandler extends PageTypePrefixHandler
{
    /*
     * public scope methods
     */

    public KvasirPageTypePrefixHandler setPageAlfr(PageAlfr pageAlfr)
    {
        return this;
    }


    /*
     * TypePrefixHandler
     */

    @Override
    protected String filterPath(TemplateContext context,
        VariableResolver varResolver, String path)
    {
        if (path == null) {
            return null;
        }
        if (path.startsWith(KvasirVariableResolver.VARNAME_THATLORD)) {
            path = getAbsolutePath(
                (Page)context.getVariableResolver().getVariable(context,
                    KvasirVariableResolver.VARNAME_THATLORD),
                path
                    .substring(KvasirVariableResolver.VARNAME_THATLORD.length()));
        } else if (path.startsWith(KvasirVariableResolver.VARNAME_MYLORD)) {
            path = getAbsolutePath((Page)context.getVariableResolver()
                .getVariable(context, KvasirVariableResolver.VARNAME_MYLORD),
                path.substring(KvasirVariableResolver.VARNAME_MYLORD.length()));
        }
        return HTMLUtils.reencode(path, "UTF-8");
    }


    String getAbsolutePath(Page lord, String lordRelativePath)
    {
        if (lord != null) {
            if (lordRelativePath.startsWith("/")) {
                return lord.getPathname() + lordRelativePath;
            } else {
                // trimmedが「;a=b」などの場合。
                return lord.getPathname() + "/" + lordRelativePath;
            }
        } else {
            if (lordRelativePath.startsWith("/")) {
                return lordRelativePath;
            } else {
                // trimmedが「;a=b」などの場合。
                return "/" + lordRelativePath;
            }
        }
    }


    @Override
    protected String getContextPath(HttpServletRequest request)
    {
        return (String)request.getAttribute(CmsPlugin.ATTR_CONTEXT_PATH);
    }
}
