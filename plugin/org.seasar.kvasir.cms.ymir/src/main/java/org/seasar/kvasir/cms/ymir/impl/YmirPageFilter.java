package org.seasar.kvasir.cms.ymir.impl;

import static org.seasar.cms.ymir.MultipartServletRequest.ATTR_FORMFILEMAP;
import static org.seasar.kvasir.cms.CmsPlugin.ATTR_RESPONSECONTENTTYPE;
import static org.seasar.kvasir.cms.zpt.impl.ZptPageProcessor.ATTR_VARIABLERESOLVER;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cms.pluggable.ThreadContext;
import org.seasar.cms.ymir.Application;
import org.seasar.cms.ymir.ApplicationManager;
import org.seasar.cms.ymir.FormFile;
import org.seasar.cms.ymir.HttpServletResponseFilter;
import org.seasar.cms.ymir.Request;
import org.seasar.cms.ymir.Response;
import org.seasar.cms.ymir.Ymir;
import org.seasar.cms.ymir.YmirVariableResolver;
import org.seasar.cms.ymir.impl.HttpServletRequestAttributeContainer;
import org.seasar.cms.ymir.util.ServletUtils;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.RequestSnapshot;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.cms.processor.LocalPathResolver;
import org.seasar.kvasir.cms.processor.impl.VirtualHttpServletRequest;
import org.seasar.kvasir.cms.processor.impl.VirtualServletContext;
import org.seasar.kvasir.cms.ymir.ApplicationMapping;
import org.seasar.kvasir.cms.ymir.YmirApplication;
import org.seasar.kvasir.cms.ymir.YmirPlugin;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.webapp.Dispatcher;


public class YmirPageFilter
    implements PageFilter
{
    private static final String NAME_YMIR = "ymir";

    private Kvasir kvasir_;

    private YmirPlugin plugin_;

    private PagePlugin pagePlugin_;

    private CmsPlugin cmsPlugin_;

    private PageAlfr pageAlfr_;

    private ApplicationManager applicationManager_;

    private Ymir ymir_;

    private FilterConfig config_;

    private Map<Integer, Bag> pairMap_ = new HashMap<Integer, Bag>();


    public void init(FilterConfig config)
    {
        config_ = config;
        ymir_ = plugin_.getYmir();
        pageAlfr_ = pagePlugin_.getPageAlfr();
    }


    @SuppressWarnings("unchecked")
    public void doFilter(HttpServletRequest httpRequest,
        HttpServletResponse httpResponse, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        Bag bag = getBag(pageRequest);

        Map<String, FormFile[]> fileParameterMap = (Map<String, FormFile[]>)httpRequest
            .getAttribute(ATTR_FORMFILEMAP);
        if (fileParameterMap != null) {
            httpRequest.removeAttribute(ATTR_FORMFILEMAP);
        } else {
            fileParameterMap = new HashMap<String, FormFile[]>();
        }

        VirtualServletContext vContext = bag.getServletContext();
        HttpServletRequest vHttpRequest = new VirtualHttpServletRequest(
            vContext, httpRequest, pageRequest.getMy().getGardRootPage()
                .getPathname(), pageRequest.getMy().getLocalPathname(), "/*");
        HttpServletRequestAttributeContainer attributeContainer = new HttpServletRequestAttributeContainer(
            vHttpRequest);
        ThreadContext threadContext = getThreadContext();

        Object oldVariableResolver = httpRequest
            .getAttribute(ATTR_VARIABLERESOLVER);

        Object backupped = null;
        Object responseContentType = null;
        if (dispatcher == Dispatcher.INCLUDE) {
            backupped = ymir_.backupForInclusion(attributeContainer);
            responseContentType = httpRequest
                .getAttribute(ATTR_RESPONSECONTENTTYPE);
        }
        boolean proceed = false;
        HttpServletResponseFilter responseFilter = null;
        RequestSnapshot snapshot = cmsPlugin_.enter(vContext, vHttpRequest);
        Application oldApplication = applicationManager_
            .getContextApplication();
        Object oldPageRequest = threadContext.findComponent(PageRequest.class);

        Application application = bag.getActualApplication(pageRequest.getMy()
            .getLocalPathname());
        applicationManager_.setContextApplication(application);
        threadContext.setComponent(PageRequest.class, pageRequest);

        Request request = ymir_.prepareForProcessing(ServletUtils
            .getContextPath(vHttpRequest), pageRequest.getMy()
            .getLocalPathname(), httpRequest.getMethod(), dispatcher.name(),
            httpRequest.getParameterMap(), fileParameterMap,
            attributeContainer, pageRequest.getLocale());
        try {
            try {
                Response response = ymir_.processRequest(request);

                String contentType = response.getContentType();
                if (contentType != null) {
                    httpRequest.setAttribute(ATTR_RESPONSECONTENTTYPE,
                        contentType);
                }

                httpRequest.setAttribute(ATTR_VARIABLERESOLVER,
                    new YmirVariableResolver(request, httpRequest, application
                        .getS2Container()));

                responseFilter = ymir_.processResponse(vContext, vHttpRequest,
                    httpResponse, request, response);
                if (responseFilter != null) {
                    proceed = true;
                }
            } catch (Throwable t) {
                ymir_.processResponse(vContext, vHttpRequest, httpResponse,
                    request, ymir_.processException(request, t));
            }
            if (proceed) {
                chain.doFilter(httpRequest,
                    (responseFilter != null ? responseFilter : httpResponse),
                    dispatcher, pageRequest);
                if (responseFilter != null) {
                    responseFilter.commit();
                }
            }
        } finally {
            threadContext.setComponent(PageRequest.class, oldPageRequest);
            applicationManager_.setContextApplication(oldApplication);
            cmsPlugin_.leave(snapshot);

            if (dispatcher == Dispatcher.INCLUDE) {
                httpRequest.setAttribute(ATTR_RESPONSECONTENTTYPE,
                    responseContentType);
                ymir_.restoreForInclusion(attributeContainer, backupped);
            }
            httpRequest
                .setAttribute(ATTR_VARIABLERESOLVER, oldVariableResolver);
        }
    }


    ThreadContext getThreadContext()
    {
        return kvasir_.getRootComponentContainer().getComponent(
            ThreadContext.class);
    }


    Bag getBag(PageRequest pageRequest)
        throws ServletException
    {
        Integer gardRootId = new Integer(pageRequest.getMy().getGardRootPage()
            .getId());
        synchronized (pairMap_) {
            Bag bag = pairMap_.get(gardRootId);
            if (bag == null) {
                bag = createBag(pageRequest);
                pairMap_.put(gardRootId, bag);
            }
            return bag;
        }
    }


    @SuppressWarnings("unchecked")
    Bag createBag(PageRequest pageRequest)
        throws ServletException
    {
        YmirApplication application = plugin_.getApplication(pageRequest
            .getMy().getGardId());
        if (application == null) {
            throw new ServletException(
                "YmirPageFilter has been received request,"
                    + " but corresponding <application> entry had not been registered: requested-gard-id="
                    + pageRequest.getMy().getGardId());
        }

        PropertyHandler prop = new MapProperties(
            new LinkedHashMap<String, String>());
        for (Enumeration<String> enm = config_.getInitParameterNames(); enm
            .hasMoreElements();) {
            String name = (String)enm.nextElement();
            prop.setProperty(name, config_.getInitParameter(name));
        }

        LocalPathResolver resolver = new YmirLocalPathResolver(application
            .getWebappRootResource());

        VirtualServletContext vContext = new VirtualServletContext(NAME_YMIR,
            prop, resolver, pageRequest.getMy().getGardRootPage().getId(),
            kvasir_, pageAlfr_);

        return new Bag(vContext, application, plugin_
            .getApplicationMappings(application.getId()), resolver);
    }


    public void destroy()
    {
        config_ = null;
        ymir_ = null;
        pageAlfr_ = null;
    }


    static class Bag
    {
        private VirtualServletContext servletContext_;

        private Application application_;

        private ApplicationMapping[] applicationMappings_;

        private LocalPathResolver resolver_;


        public Bag(VirtualServletContext servletContext,
            Application application, ApplicationMapping[] applicationMappings,
            LocalPathResolver resolver)
        {
            servletContext_ = servletContext;
            application_ = application;
            applicationMappings_ = applicationMappings;
            resolver_ = resolver;
        }


        public VirtualServletContext getServletContext()
        {
            return servletContext_;
        }


        public Application getActualApplication(String path)
        {
            for (int i = 0; i < applicationMappings_.length; i++) {
                if (applicationMappings_[i].isMatched(path)) {
                    return applicationMappings_[i].getForwardedApplication();
                }
            }
            return application_;
        }


        public LocalPathResolver getResolver()
        {
            return resolver_;
        }
    }


    public void setKvasir(Kvasir kvasir)
    {
        kvasir_ = kvasir;
    }


    public void setPlugin(YmirPlugin plugin)
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


    public void setApplicationManager(ApplicationManager applicationManager)
    {
        applicationManager_ = applicationManager;
    }
}
