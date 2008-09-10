package org.seasar.kvasir.cms.processor.impl;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.classloader.CachedClassLoader;
import org.seasar.kvasir.base.classloader.CompositeClassLoader;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.RequestSnapshot;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.cms.processor.LocalPathResolver;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ServletPageProcessor
    implements PageProcessor
{
    public static final String PARAM_SERVLET_NAME = "servlet-name";

    public static final String PARAM_SERVLET_COMPONENT = "servlet-component";

    public static final String PARAM_RESOLVER_COMPONENT = "local-path-resolver-component";

    /**
     * パスに対応するファイルをLocalPathResolver
     * を使って見つけられなかった場合でもこのPageProcessor
     * に処理をさせるかどうかを表すパラメータのキーです。
     */
    public static final String PARAM_PROCESS_VIRTUAL_PATH = "process-virtual-path";

    public static final String PARAM_URL_PATTERN = "url-pattern";

    public static final String COMPONENT_DEFAULTLOCALPATHRESOLVER = "defaultLocalPathResolver";

    private Kvasir kvasir_;

    private PagePlugin pagePlugin_;

    private CmsPlugin plugin_;

    private PageProcessorElement element_;

    private PageAlfr pageAlfr_;

    private ComponentContainer container_;

    private ServletConfig config_;

    private String servletComponentName_;

    private String servletName_;

    private String resolverComponentName_;

    private boolean processVirtualPath_;

    private String urlPattern_;

    private Map<Integer, Bag> servletMap_ = new HashMap<Integer, Bag>();

    private Map<String, ClassLoader> clMap_ = new HashMap<String, ClassLoader>();

    private KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    /*
     * PageProcessor
     */

    public void init(ServletConfig config)
    {
        Plugin<?> plugin = element_.getPlugin();
        pageAlfr_ = pagePlugin_.getPageAlfr();
        container_ = plugin.getComponentContainer();

        config_ = config;
        servletComponentName_ = config
            .getInitParameter(PARAM_SERVLET_COMPONENT);

        String servletName = config.getInitParameter(PARAM_SERVLET_NAME);
        if (servletName == null) {
            servletName = servletComponentName_;
        }
        servletName_ = servletName;

        resolverComponentName_ = config
            .getInitParameter(PARAM_RESOLVER_COMPONENT);
        if (resolverComponentName_ == null) {
            resolverComponentName_ = COMPONENT_DEFAULTLOCALPATHRESOLVER;
        }

        processVirtualPath_ = PropertyUtils.valueOf(config
            .getInitParameter(PARAM_PROCESS_VIRTUAL_PATH), false);

        urlPattern_ = PropertyUtils.valueOf(config
            .getInitParameter(PARAM_URL_PATTERN), "/*");
    }


    public void destroy()
    {
        kvasir_ = null;
        pagePlugin_ = null;
        element_ = null;

        pageAlfr_ = null;
        container_ = null;
        config_ = null;
        servletComponentName_ = null;
        servletName_ = null;
        resolverComponentName_ = null;
        processVirtualPath_ = false;
        urlPattern_ = null;

        Thread thread = Thread.currentThread();
        ClassLoader cl = thread.getContextClassLoader();
        for (Iterator<Bag> itr = servletMap_.values().iterator(); itr.hasNext();) {
            Bag pair = itr.next();
            try {
                thread.setContextClassLoader(pair.getClassLoader());
                pair.getServlet().destroy();
            } catch (Throwable t) {
                log_.error("Can't destroy servlet: " + pair.getServlet(), t);
            } finally {
                thread.setContextClassLoader(cl);
            }
        }
        servletMap_.clear();
        clMap_.clear();
    }


    @SuppressWarnings("deprecation")
    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        Bag bag = getBag(pageRequest);

        // processVirtualPathがtrueの場合か
        // 処理対象のファイルが存在する場合だけServletを呼び出す。
        if (shouldProcess(bag, pageRequest)) {
            HttpServlet servlet = bag.getServlet();
            HttpServletRequest vRequest = new VirtualHttpServletRequest(bag
                .getServletContext(), request, pageRequest.getMy()
                .getGardRootPage().getPathname(), pageRequest.getMy()
                .getLocalPathname(), urlPattern_);

            Thread thread = Thread.currentThread();
            ClassLoader cl = thread.getContextClassLoader();
            RequestSnapshot snapshot = plugin_.enter(servlet
                .getServletContext(), vRequest);
            try {
                thread.setContextClassLoader(bag.getClassLoader());

                notifyServing(servlet, vRequest, response);

                if (servlet instanceof javax.servlet.SingleThreadModel) {
                    synchronized (servlet) {
                        servlet.service(vRequest, response);
                    }
                } else {
                    servlet.service(vRequest, response);
                }
            } finally {
                try {
                    notifyServed(servlet, vRequest, response);
                } finally {
                    plugin_.leave(snapshot);
                    thread.setContextClassLoader(cl);
                }
            }

            return;
        }

        chain.doProcess(request, response, pageRequest);
    }


    protected void notifyServing(HttpServlet servlet,
        HttpServletRequest request, HttpServletResponse response)
    {
    }


    protected void notifyServed(HttpServlet servlet,
        HttpServletRequest request, HttpServletResponse response)
    {
    }


    protected boolean shouldProcess(Bag bag, PageRequest pageRequest)
    {
        boolean process = false;
        if (processVirtualPath_) {
            process = true;
        } else {
            String realPath = bag.getResolver().getRealPath(
                pageRequest.getMy().getLocalPathname());
            if (realPath != null && new File(realPath).exists()) {
                process = true;
            }
        }
        return process;
    }


    Bag getBag(PageRequest pageRequest)
        throws ServletException, IOException
    {
        Integer gardRootId = new Integer(pageRequest.getMy().getGardRootPage()
            .getId());
        synchronized (servletMap_) {
            Bag pair = (Bag)servletMap_.get(gardRootId);
            if (pair == null) {
                pair = createBag(pageRequest);
                servletMap_.put(gardRootId, pair);
            }
            return pair;
        }
    }


    @SuppressWarnings("unchecked")
    Bag createBag(PageRequest pageRequest)
        throws ServletException, IOException
    {
        PropertyHandler prop = new MapProperties(
            new LinkedHashMap<String, String>());
        for (Enumeration<String> enm = config_.getInitParameterNames(); enm
            .hasMoreElements();) {
            String name = enm.nextElement();
            prop.setProperty(name, config_.getInitParameter(name));
        }

        LocalPathResolver resolver = (LocalPathResolver)container_
            .getComponent(resolverComponentName_);
        ClassUtils.setProperty(resolver, element_);
        resolver.init(pageRequest.getMy().getGardRootPage(), config_);

        VirtualServletContext vContext = new VirtualServletContext(
            servletName_, prop, resolver, pageRequest.getMy().getGardRootPage()
                .getId(), kvasir_, pageAlfr_);
        ServletConfig vConfig = new VirtualServletConfig(servletName_, vContext);

        HttpServlet servlet = newServlet();

        ClassLoader webappCl = getWebappClassLoader(pageRequest, servlet);
        Thread thread = Thread.currentThread();
        ClassLoader cl = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(webappCl);
            notifyServletInitializing(servlet, vConfig);
            servlet.init(vConfig);
            notifyServletInitialized(servlet, vConfig);
        } finally {
            thread.setContextClassLoader(cl);
        }

        return new Bag(vContext, servlet, webappCl, resolver);
    }


    protected void notifyServletInitializing(HttpServlet servlet,
        ServletConfig config)
    {
    }


    protected void notifyServletInitialized(HttpServlet servlet,
        ServletConfig config)
    {
    }


    protected HttpServlet newServlet()
    {
        return (HttpServlet)container_.getComponent(servletComponentName_);
    }


    protected ClassLoader getWebappClassLoader(PageRequest pageRequest,
        HttpServlet servlet)
    {
        // Servlet動作時にコンテキストクラスローダとして使用するための
        // クラスローダを生成しておく。
        // Servletコンテナでは通常WebappClassLoaderがServletクラスの生成元
        // クラスローダかつコンテキストクラスローダであるが、Kvasir/Soraでは
        // Servletクラスの生成元クラスローダとコンテキストクラスローダは異なる
        // ため、ServletPageProcessorではServletの動作時にそれと同じ状態を作り
        // 出す。こうすることで、Servlet内でコンテキストクラスローダを使って
        // 何らかのクラスをロードするようなServletを正しく動作させることが
        // できる。
        // （ただし、Kvasir/SoraではServletコンテナの場合とは異なりServlet
        // クラスとそれ以外のクラスが同一ClassLoaderからロードされているとは
        // 限らないため、このクラスローダでは正しく動作しないことがある。
        // その場合は全てのJARを同じ
        // プラグインに含めておくしかない。）
        synchronized (clMap_) {
            ClassLoader webappCl = clMap_.get(servletComponentName_);
            if (webappCl == null) {
                //                webappCl = new CompositeClassLoader(new ClassLoader[]{
                //                    element_.getPlugin().getPlugin().getInnerClassLoader(),
                //                    cl },
                //                    true, true);
                //                webappCl = new CompositeClassLoader(new ClassLoader[]{
                //                    servlet.getClass().getClassLoader(),
                //                    element_.getPlugin().getPlugin().getInnerClassLoader() },
                //                    true, true);
                //                webappCl = element_.getPlugin().getPlugin()
                //                    .getInnerClassLoader();
                LinkedList<ClassLoader> list = new LinkedList<ClassLoader>();
                Object obj = servlet;
                Class<?> clazz = obj.getClass();
                while (clazz != null && !clazz.getName().startsWith("javax.")) {
                    list.addFirst(clazz.getClassLoader());
                    clazz = clazz.getSuperclass();
                }
                Plugin<?> belongingPlugin = element_.getPlugin();
                list.add(belongingPlugin.getInnerClassLoader());
                Plugin<?> gardPlugin = pageRequest.getMy().getPlugin();
                if (gardPlugin != null && gardPlugin != belongingPlugin) {
                    list.add(gardPlugin.getInnerClassLoader());
                }
                list.add(Thread.currentThread().getContextClassLoader());
                webappCl = new CachedClassLoader(new CompositeClassLoader(list
                    .toArray(new ClassLoader[0])));
                clMap_.put(servletComponentName_, webappCl);
            }
            return webappCl;
        }
    }


    /*
     * for framework
     */

    public void setKvasir(Kvasir kvasir)
    {
        kvasir_ = kvasir;
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    public void setPlugin(CmsPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setElement(PageProcessorElement element)
    {
        element_ = element;
    }


    /*
     * inner classes
     */

    public static class Bag
    {
        private VirtualServletContext servletContext_;

        private HttpServlet servlet_;

        private ClassLoader classLoader_;

        private LocalPathResolver resolver_;


        public Bag(VirtualServletContext servletContext, HttpServlet servlet,
            ClassLoader classLoader, LocalPathResolver resolver)
        {
            servletContext_ = servletContext;
            servlet_ = servlet;
            classLoader_ = classLoader;
            resolver_ = resolver;
        }


        public VirtualServletContext getServletContext()
        {
            return servletContext_;
        }


        public HttpServlet getServlet()
        {
            return servlet_;
        }


        public ClassLoader getClassLoader()
        {
            return classLoader_;
        }


        public LocalPathResolver getResolver()
        {
            return resolver_;
        }
    }
}
