package org.seasar.kvasir.cms.zpt.impl;

import javax.servlet.http.HttpServletRequest;

import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.webapp.util.ServletUtils;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.zpt.tales.TalesExpressionEvaluator;
import net.skirnir.freyja.zpt.webapp.IncludeTypePrefixHandler;
import net.skirnir.freyja.zpt.webapp.ServletTalesExpressionEvaluator;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirIncludeTypePrefixHandler extends IncludeTypePrefixHandler
{
    private PageAlfr pageAlfr_;

    private ServletTalesExpressionEvaluator evaluator_;


    /*
     * public scope methods
     */

    public KvasirIncludeTypePrefixHandler setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
        return this;
    }


    /*
     * IncludeTypePrefixHandler
     */

    @Override
    public void setTalesExpressionEvaluator(TalesExpressionEvaluator evaluator)
    {
        evaluator_ = (ServletTalesExpressionEvaluator)evaluator;
        super.setTalesExpressionEvaluator(evaluator);
    }


    /*
     * TypePrefixHandler
     */

    @Override
    protected String filterPath(TemplateContext context,
        VariableResolver varResolver, String path)
    {
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

        HttpServletRequest request = evaluator_.getRequest(varResolver);
        String contextPath = ServletUtils.getRequestContextPath(request);
        String originalContextPath = (String)request
            .getAttribute(CmsPlugin.ATTR_CONTEXT_PATH);

        return path.substring(contextPath.length()
            - originalContextPath.length());
    }


    String getAbsolutePath(Page lord, String lordRelativePath)
    {
        if (lord != null) {
            return lord.getPathname() + lordRelativePath;
        } else {
            return lordRelativePath;
        }
    }
}
