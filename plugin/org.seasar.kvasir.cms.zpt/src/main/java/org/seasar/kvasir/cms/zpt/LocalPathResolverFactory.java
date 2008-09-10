package org.seasar.kvasir.cms.zpt;

import javax.servlet.ServletConfig;

import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.cms.processor.LocalPathResolver;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.ClassUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class LocalPathResolverFactory
{
    private ComponentContainer container_;

    private String resolverComponentName_;

    private PageProcessorElement element_;

    private ServletConfig config_;


    public LocalPathResolverFactory(ComponentContainer container,
        String resolverComponentName, PageProcessorElement element,
        ServletConfig config)
    {
        container_ = container;
        resolverComponentName_ = resolverComponentName;
        element_ = element;
        config_ = config;
    }


    public LocalPathResolver newInstance(Page gardRootPage)
    {
        LocalPathResolver resolver = (LocalPathResolver)container_
            .getComponent(resolverComponentName_);
        ClassUtils.setProperty(resolver, element_);
        resolver.init(gardRootPage, config_);
        return resolver;
    }
}
