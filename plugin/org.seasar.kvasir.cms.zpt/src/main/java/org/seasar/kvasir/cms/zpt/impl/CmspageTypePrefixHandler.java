package org.seasar.kvasir.cms.zpt.impl;

import javax.servlet.http.HttpServletRequest;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.zpt.tales.TalesExpressionEvaluator;
import net.skirnir.freyja.zpt.tales.TypePrefixHandler;
import net.skirnir.freyja.zpt.webapp.ServletTalesExpressionEvaluator;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.page.PagePlugin;


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

        Integer heimId = null;
        String pathname = expr;
        int colon = expr.indexOf(':');
        if (colon >= 0) {
            try {
                heimId = Integer.valueOf(expr.substring(0, colon));
                pathname = expr.substring(colon + 1);
            } catch (Throwable ignore) {
            }
        }
        if (heimId == null) {
            heimId = pluginAlfr.getPlugin(CmsPlugin.class).determineHeimId(
                request);
        }

        return pluginAlfr.getPlugin(PagePlugin.class).getPageAlfr().getPage(
            heimId, pathname);
    }
}
