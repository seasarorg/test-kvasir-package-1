package org.seasar.kvasir.cms.zpt.impl;

import javax.servlet.http.HttpServletRequest;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.zpt.tales.TalesExpressionEvaluator;
import net.skirnir.freyja.zpt.tales.TalesUtils;
import net.skirnir.freyja.zpt.tales.TypePrefixHandler;
import net.skirnir.freyja.zpt.webapp.ServletTalesExpressionEvaluator;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;


/**
 * Kvasir/SoraのPageオブジェクトを取得するためのタイプです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class CmspageTypePrefixHandler
    implements TypePrefixHandler
{
    private ServletTalesExpressionEvaluator evaluator_;


    /*
     * TypePrefixHandler
     */

    public void setTalesExpressionEvaluator(TalesExpressionEvaluator evaluator)
    {
        evaluator_ = (ServletTalesExpressionEvaluator)evaluator;
    }


    public Object handle(TemplateContext context, VariableResolver varResolver,
        String expr)
    {
        PluginAlfr pluginAlfr = Asgard.getKvasir().getPluginAlfr();
        HttpServletRequest request = evaluator_.getRequest(varResolver);

        String heimPart = null;
        String pathname = expr;
        int colon = expr.indexOf(':');
        if (colon >= 0) {
            heimPart = expr.substring(0, colon);
            pathname = expr.substring(colon + 1);
        }

        heimPart = TalesUtils.resolveAsString(context, heimPart, varResolver,
            evaluator_);
        pathname = toAbsolutePathname(context, varResolver, TalesUtils
            .resolveAsString(context, pathname, varResolver, evaluator_));

        int heimId;
        if (heimPart != null) {
            heimId = Integer.parseInt(heimPart);
        } else {
            heimId = pluginAlfr.getPlugin(CmsPlugin.class).determineHeimId(
                request);
        }

        return pluginAlfr.getPlugin(PagePlugin.class).getPageAlfr().getPage(
            heimId, pathname);
    }


    String toAbsolutePathname(TemplateContext context,
        VariableResolver varResolver, String pathname)
    {
        if (pathname == null) {
            return null;
        }

        String lordPathname = null;
        if (pathname.startsWith(KvasirVariableResolver.VARNAME_THATLORD)) {
            lordPathname = ((Page)context.getVariableResolver().getVariable(
                context, KvasirVariableResolver.VARNAME_THATLORD))
                .getPathname();
        } else if (pathname.startsWith(KvasirVariableResolver.VARNAME_MYLORD)) {
            lordPathname = ((Page)context.getVariableResolver().getVariable(
                context, KvasirVariableResolver.VARNAME_MYLORD)).getPathname();
        }

        String basePathname = ((PageDispatch)context.getVariableResolver()
            .getVariable(context, KvasirVariableResolver.VARNAME_THAT))
            .getPathname();

        return PageUtils.getAbsolutePathname(pathname, lordPathname,
            basePathname);
    }
}
