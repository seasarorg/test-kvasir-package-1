package org.seasar.kvasir.cms.processor.impl;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.cms.processor.LocalPathResolver;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.util.ClassUtils;


/**
 * 指定された<code>LocalPathResolver</code>で
 * gardRootPageからの相対パスを解決した結果であるFileをリクエストのあった
 * URIのボディとみなして処理を行なうPageProcessor
 * を記述するための基底クラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class AbstractLocalPathPageProcessor
    implements PageProcessor
{
    /**
     * このPageProcessorが使用するLocalPathResolverオブジェクトを
     * ComponentContainerから取り出す時のコンポーネント名を指定するための
     * プロパティ名です。
     */
    public static final String PARAM_RESOLVER_ID = "local-path-resolver-id";

    /**
     * LocalPathProcessorのコンポーネント名が指定されなかった場合の
     * コンポーネント名です。
     */
    public static final String COMPONENT_DEFAULTLOCALPATHRESOLVER = "defaultLocalPathResolver";

    protected PageProcessorElement element_;

    protected ServletConfig config_;

    protected ServletContext context_;

    private String resolverId_;

    private ComponentContainer container_;

    private Map<Integer, Reference<LocalPathResolver>> resolverMap_ = Collections
        .synchronizedMap(new HashMap<Integer, Reference<LocalPathResolver>>());


    /*
     * abstract methods
     */

    abstract protected boolean doProcessFile(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest, File file)
        throws ServletException, IOException;


    /*
     * PageProcessor
     */

    public void init(ServletConfig config)
    {
        config_ = config;
        context_ = config.getServletContext();
        container_ = element_.getParent().getParent().getPlugin()
            .getComponentContainer();
        resolverId_ = config.getInitParameter(PARAM_RESOLVER_ID);
        if (resolverId_ == null) {
            resolverId_ = COMPONENT_DEFAULTLOCALPATHRESOLVER;
        }
    }


    public void destroy()
    {
        config_ = null;
        context_ = null;
        container_ = null;
        resolverId_ = null;
        resolverMap_.clear();

        element_ = null;
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        String realPath = getLocalPathResolver(
            pageRequest.getMy().getGardRootPage()).getRealPath(
            pageRequest.getMy().getLocalPathname());
        if (realPath != null) {
            File file = new File(realPath);
            if (file.exists() && file.isFile()) {
                if (doProcessFile(request, response, pageRequest, file)) {
                    return;
                }
            }
        }

        chain.doProcess(request, response, pageRequest);
    }


    /*
     * private scope methods
     */

    private LocalPathResolver getLocalPathResolver(Page gardRootPage)
    {
        Integer key = Integer.valueOf(gardRootPage.getId());
        LocalPathResolver resolver = null;
        Reference<LocalPathResolver> ref = resolverMap_.get(key);
        if (ref != null) {
            resolver = ref.get();
        }
        if (resolver == null) {
            resolver = (LocalPathResolver)container_.getComponent(resolverId_);
            ClassUtils.setProperty(resolver, element_);
            resolver.init(gardRootPage, config_);
            resolverMap_.put(key,
                new SoftReference<LocalPathResolver>(resolver));
        }
        return resolver;
    }


    /*
     * for framework
     */

    public void setElement(PageProcessorElement element)
    {
        element_ = element;
    }
}
