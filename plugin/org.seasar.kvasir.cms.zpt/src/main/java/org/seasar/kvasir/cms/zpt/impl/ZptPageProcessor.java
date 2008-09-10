package org.seasar.kvasir.cms.zpt.impl;

import static net.skirnir.freyja.TemplateContext.PROP_CONTENT_TYPE;
import static org.seasar.kvasir.cms.CmsPlugin.ATTR_RESPONSECONTENTTYPE;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.annotation.ForPreparingMode;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentContainerFactory;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.util.ArrayUtils;
import org.seasar.kvasir.cms.GardIdProvider;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.cms.filter.PageFilterLifecycleListener;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.cms.processor.PageProcessorLifecycleListener;
import org.seasar.kvasir.cms.zpt.Globals;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.ability.template.TemplateAbility;
import org.seasar.kvasir.webapp.servlet.ServletConfigImpl;
import org.seasar.kvasir.webapp.servlet.ThreadLocalServletContext;
import org.seasar.kvasir.webapp.util.ServletUtils;

import net.skirnir.freyja.Element;
import net.skirnir.freyja.ExpressionEvaluator;
import net.skirnir.freyja.FreyjaRuntimeException;
import net.skirnir.freyja.TagEvaluator;
import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.freyja.TemplateSet;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.webapp.ServletSpecified;
import net.skirnir.freyja.webapp.VariableResolverFactory;
import net.skirnir.freyja.zpt.MetalTagEvaluator;
import net.skirnir.freyja.zpt.tales.PathResolver;
import net.skirnir.freyja.zpt.tales.TalesExpressionEvaluator;
import net.skirnir.freyja.zpt.webapp.ServletTalesExpressionEvaluator;


/**
 * ZPTをレンダリングするためのPageProcessorです。
 * <p>あるgardに属するテンプレート中でマクロを探索する時に
 * 祖先gardに属するテンプレートも探索対象とするために、
 * ルートgardが持つZptPageProcessorを各gardで共有することができます。
 * その場合は<code>plugin.xml</code>で<code>page-processor</code>
 * に指定するコンポーネント名として
 * <code>zptPageProcessor</code>を指定し、
 * <code>page-processor</code>に<code>init-param</code>タグで
 * 適切な<code>local-path-resolver-id</code>
 * プロパティを指定するようにして下さい。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ZptPageProcessor
    implements PageProcessor, PageProcessorLifecycleListener,
    PageFilterLifecycleListener
{
    public static final String PARAM_RESOLVER_ID = "local-path-resolver-id";

    public static final String ID_DEFAULTLOCALPATHRESOLVER = "defaultLocalPathResolver";

    private static final String ID_ZPTPAGEPROCESSOR = Globals.ID
        + ".zptPageProcessor";

    public static final String ATTR_VARIABLERESOLVER = ID_ZPTPAGEPROCESSOR
        + ".variableResolver";

    public static final String TEMPLATETYPE = "zpt";

    private Kvasir kvasir_;

    private PagePlugin pagePlugin_;

    private PageAlfr pageAlfr_;

    private List<PageProcessorElement> elementList_ = new ArrayList<PageProcessorElement>();

    private Map<String, ServletConfig> configMap_ = new HashMap<String, ServletConfig>();

    private TagEvaluator tagEvaluator_;

    private ExpressionEvaluator expEvaluator_;

    private VariableResolverFactory vrf_;

    private ServletContext servletContext_;

    private TemplateEvaluator evaluator_;

    private KvasirTemplateSet templateSet_;

    private TemplateSet cachedTemplateSet_;

    private boolean started_;

    private PathResolver[] pathResolvers_ = new PathResolver[0];


    public boolean start()
    {
        TemplateEvaluator defaultEvaluator = newEvaluator();

        if (tagEvaluator_ == null) {
            tagEvaluator_ = defaultEvaluator.getTagEvaluator();
        }

        if (expEvaluator_ == null) {
            expEvaluator_ = defaultEvaluator.getExpressionEvaluator();
        }

        evaluator_ = new TemplateEvaluator(tagEvaluator_, expEvaluator_);

        if (vrf_ == null) {
            vrf_ = new KvasirVariableResolverFactory().setKvasir(kvasir_)
                .setPagePlugin(pagePlugin_).setPageAlfr(pageAlfr_);
        }

        templateSet_ = new KvasirTemplateSet("UTF-8", evaluator_, pageAlfr_);
        cachedTemplateSet_ = new KvasirCachedTemplateSet(templateSet_);

        started_ = true;

        return true;
    }


    public TemplateEvaluator getEvaluator()
    {
        return evaluator_;
    }


    public TemplateEvaluator newEvaluator()
    {
        TalesExpressionEvaluator expEvaluator = new ServletTalesExpressionEvaluator()
            .addTypePrefix(ServletTalesExpressionEvaluator.TYPE_PAGE,
                new KvasirPageTypePrefixHandler().setPageAlfr(pageAlfr_))
            .addTypePrefix(ServletTalesExpressionEvaluator.TYPE_INCLUDE,
                new KvasirIncludeTypePrefixHandler().setPageAlfr(pageAlfr_))
            .addPathResolver(new KvasirPathResolver());
        for (int i = 0; i < pathResolvers_.length; i++) {
            expEvaluator.addPathResolver(pathResolvers_[i]);
        }
        return new TemplateEvaluator(new MetalTagEvaluator(), expEvaluator);
    }


    /**
     * ZptPageProcessorが持つTALESのEvaluatorにPathResolverを追加します。
     * <p>このメソッドはstart()が呼ばれた後に呼び出して下さい。</p>
     * 
     * @param pathResolver 追加するPathResolver。
     */
    @ForPreparingMode
    public void addPathResolver(PathResolver pathResolver)
    {
        if (!started_) {
            throw new IllegalStateException(
                "Call this method after start() invoked.");
        }

        if (expEvaluator_ instanceof TalesExpressionEvaluator) {
            ((TalesExpressionEvaluator)expEvaluator_)
                .addPathResolver(pathResolver);
        }
        pathResolvers_ = ArrayUtils.add(pathResolvers_, pathResolver);
    }


    /*
     * PageProcessor
     */

    public void init(ServletConfig config)
    {
        configMap_.put(config.getServletName(), config);
    }


    @ForPreparingMode
    protected void registerLocalPathResolver(ServletConfig config,
        PageProcessorElement element)
    {
        Plugin<?> plugin = element.getPlugin();
        String gardId = element.getGardId();
        GardIdProvider provider = element.getGardIdProvider();

        String resolverComponentName = getInitParameter(config,
            PARAM_RESOLVER_ID, ID_DEFAULTLOCALPATHRESOLVER);
        ComponentContainer container = plugin.getComponentContainer();

        if (ID_ZPTPAGEPROCESSOR.equals(element.getFullId())) {
            templateSet_.setBasePathResolver(container, resolverComponentName,
                element, config);
        } else if (gardId == null && provider != null) {
            templateSet_.addLocalPathResolver(provider, container,
                resolverComponentName, element, config);
        } else {
            templateSet_.addLocalPathResolver(gardId, container,
                resolverComponentName, element, config);
        }
    }


    @ForPreparingMode
    private String getInitParameter(ServletConfig config, String name,
        String defaultValue)
    {
        String value = config.getInitParameter(name);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }


    @ForPreparingMode
    public void destroy()
    {
        kvasir_ = null;
        pagePlugin_ = null;
        pageAlfr_ = null;
        elementList_.clear();
        configMap_.clear();
        tagEvaluator_ = null;
        expEvaluator_ = null;
        vrf_ = null;

        servletContext_ = null;
        evaluator_ = null;
        templateSet_ = null;
        cachedTemplateSet_ = null;

        pathResolvers_ = new PathResolver[0];
        started_ = false;
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        String path = ServletUtils.getPath(request);

        Element[] elems = cachedTemplateSet_.getElements(path);
        if (elems != null) {
            if (request.getAttribute(ATTR_RESPONSECONTENTTYPE) == null) {
                Page page = pageRequest.getMy().getPage();
                if (page != null) {
                    String responseContentType = page.getAbility(
                        TemplateAbility.class).getResponseContentType();
                    if (responseContentType.length() > 0) {
                        request.setAttribute(ATTR_RESPONSECONTENTTYPE,
                            responseContentType);
                    }
                }
            }
            TemplateContext context = evaluator_.newContext();
            buildTemplateContext(context, servletContext_, request, response,
                pageRequest.getLocale(), path);
            String evaluated;
            try {
                evaluated = evaluator_.evaluate(context, elems);
            } catch (FreyjaRuntimeException ex) {
                throw ex.setTemplateName(path);
            }
            response.setContentType(context.getProperty(PROP_CONTENT_TYPE));
            PrintWriter writer = response.getWriter();
            writer.print(evaluated);
            writer.flush();
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doProcess(request, response, pageRequest);
    }


    public String evaluate(HttpServletRequest request,
        HttpServletResponse response, ServletContext servletContext,
        Locale locale, VariableResolver resolver, String responseContentType,
        String templateName, Element[] elems)
        throws FreyjaRuntimeException
    {
        TemplateContext context = evaluator_.newContext();
        buildTemplateContext(context, servletContext, request, response,
            locale, templateName, resolver, responseContentType);

        try {
            return evaluator_.evaluate(context, elems);
        } catch (FreyjaRuntimeException ex) {
            throw ex.setTemplateName(templateName);
        }
    }


    public void buildTemplateContext(TemplateContext context,
        ServletContext servletContext, HttpServletRequest request,
        HttpServletResponse response, Locale locale, String templateName)
    {
        String responseContentType = null;
        Object obj = request.getAttribute(ATTR_RESPONSECONTENTTYPE);
        if (obj instanceof String) {
            responseContentType = (String)obj;
        }
        if (responseContentType == null
            || responseContentType.trim().length() == 0) {
            responseContentType = "text/html; charset=UTF-8";
        }

        VariableResolver resolver = null;
        obj = request.getAttribute(ATTR_VARIABLERESOLVER);
        if (obj instanceof VariableResolver) {
            resolver = (VariableResolver)obj;
        }

        buildTemplateContext(context, servletContext, request, response,
            locale, templateName, resolver, responseContentType);
    }


    void buildTemplateContext(TemplateContext context,
        ServletContext servletContext, HttpServletRequest request,
        HttpServletResponse response, Locale locale, String templateName,
        VariableResolver resolver, String responseContentType)
    {
        context.setProperty(PROP_CONTENT_TYPE, responseContentType);
        context.setVariableResolver(vrf_.newResolver(request, response,
            servletContext, locale, resolver));
        context.setTemplateName(templateName);
        context.setTemplateSet(cachedTemplateSet_);
    }


    /*
     * for framework
     */

    public void setTagEvaluator(TagEvaluator tagEvaluator)
    {
        tagEvaluator_ = tagEvaluator;
    }


    public void setExpressionEvaluator(ExpressionEvaluator expEvaluator)
    {
        expEvaluator_ = expEvaluator;
    }


    public VariableResolverFactory getVariableResolverFactory()
    {
        return vrf_;
    }


    public void setVariableResolverFactory(VariableResolverFactory vrf)
    {
        vrf_ = vrf;
    }


    public void setKvasir(Kvasir kvasir)
    {
        kvasir_ = kvasir;
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    public void setElement(PageProcessorElement element)
    {
        elementList_.add(element);
    }


    /*
     * PageProcessorLifecycleListener
     */

    public void notifyPageProcessorsStarted()
    {
        for (Iterator<PageProcessorElement> itr = elementList_.iterator(); itr
            .hasNext();) {
            PageProcessorElement element = itr.next();
            ServletConfig config = configMap_.get(element.getFullId());
            registerLocalPathResolver(config, element);
        }
    }


    /*
     * PageFilterLifecycleListener
     */

    public void notifyPageFiltersStarted()
    {
        servletContext_ = (ServletContext)ComponentContainerFactory
            .getApplication();
        // ZPTはサイト全体で1つのZPTページ空間を構成するので、VirtualServletContext
        // を使ってはいけない。（でないとパスの解釈が一意に決まらなくなってしまう。）
        if (servletContext_ instanceof ThreadLocalServletContext) {
            servletContext_ = ((ThreadLocalServletContext)servletContext_)
                .getOriginalServletContext();
        }
        if (expEvaluator_ instanceof ServletSpecified) {
            ((ServletSpecified)expEvaluator_).init(new ServletConfigImpl("zpt",
                servletContext_));
        }
    }
}
