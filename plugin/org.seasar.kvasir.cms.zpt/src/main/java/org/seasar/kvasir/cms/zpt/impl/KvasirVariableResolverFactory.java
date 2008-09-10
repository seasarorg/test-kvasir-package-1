package org.seasar.kvasir.cms.zpt.impl;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;

import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.webapp.ServletVariableResolver;
import net.skirnir.freyja.webapp.VariableResolverFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirVariableResolverFactory
    implements VariableResolverFactory
{
    private Kvasir kvasir_;

    private PagePlugin pagePlugin_;

    private PageAlfr pageAlfr_;


    /*
     * public scope methods
     */

    public KvasirVariableResolverFactory setKvasir(Kvasir kvasir)
    {
        kvasir_ = kvasir;
        return this;
    }


    public KvasirVariableResolverFactory setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
        return this;
    }


    public KvasirVariableResolverFactory setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
        return this;
    }


    /*
     * VariableResolverFactory
     */

    public VariableResolver newResolver(HttpServletRequest request,
        HttpServletResponse response, ServletContext sc, Locale locale,
        VariableResolver parent)
    {
        VariableResolver resolver = parent;
        if (request != null && response != null && sc != null) {
            resolver = new ServletVariableResolver(request, response, sc,
                locale, true, parent);
        }
        return new KvasirVariableResolver(kvasir_, pagePlugin_, pageAlfr_,
            resolver);
    }
}
