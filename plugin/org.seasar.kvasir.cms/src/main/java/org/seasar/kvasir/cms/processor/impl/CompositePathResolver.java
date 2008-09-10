package org.seasar.kvasir.cms.processor.impl;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;

import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.cms.processor.LocalPathResolver;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.ClassUtils;


/**
 * 2つのLocalPathResolverを組み合わせるためのLocalPathResolverです。
 * <p><b>同期化：</b>
 * このクラスは{@link #init(Page,ServletConfig)}
 * メソッドの呼び出し以降はスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class CompositePathResolver
    implements LocalPathResolver
{
    private LocalPathResolver primary_;

    private LocalPathResolver secondary_;


    public CompositePathResolver(LocalPathResolver primary,
        LocalPathResolver secondary)
    {
        primary_ = primary;
        secondary_ = secondary;
    }


    /*
     * LocalPathResolver
     */

    public void init(Page gardRootPage, ServletConfig config)
    {
        primary_.init(gardRootPage, config);
        secondary_.init(gardRootPage, config);
    }


    public String getRealPath(String path)
    {
        String realPath = primary_.getRealPath(path);
        if (realPath == null) {
            realPath = secondary_.getRealPath(path);
        }
        return realPath;
    }


    public URL getResource(String path)
    {
        URL resource = primary_.getResource(path);
        if (resource == null) {
            resource = secondary_.getResource(path);
        }
        return resource;
    }


    @SuppressWarnings("unchecked")
    public Set getResourcePaths(String path)
    {
        Set<String> resourceSet = new HashSet<String>();
        Set<String> set = primary_.getResourcePaths(path);
        if (set != null) {
            resourceSet.addAll(set);
        }
        set = secondary_.getResourcePaths(path);
        if (set != null) {
            resourceSet.addAll(set);
        }
        if (resourceSet.size() == 0) {
            resourceSet = null;
        }
        return resourceSet;
    }


    public void setElement(ExtensionElement element)
    {
        ClassUtils.setProperty(primary_, element);
        ClassUtils.setProperty(secondary_, element);
    }
}
