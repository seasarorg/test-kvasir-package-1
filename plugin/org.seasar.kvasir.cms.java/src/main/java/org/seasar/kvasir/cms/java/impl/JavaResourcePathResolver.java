package org.seasar.kvasir.cms.java.impl;

import java.net.URL;

import javax.servlet.ServletConfig;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.cms.processor.impl.AbstractResourcePathResolver;
import org.seasar.kvasir.util.io.Resource;


/**
 * <code>/path/to/page</code>に対応するリソースとして、
 * プラグインの持つリソース<code>/path/to/page.java</code>を返すような
 * LocalPathResolverです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class JavaResourcePathResolver extends AbstractResourcePathResolver
{
    public static final String PARAM_LOCALPATHRESOLVER_DIRECTORYPATH = "local-path-resolver.directory-path";

    private ExtensionElement element_;

    private boolean initialized_ = false;

    private Resource baseDir_;


    /*
     * AbstractResourcePathResolver
     */

    protected Resource getBaseDirectory()
    {
        return baseDir_;
    }


    /*
     * LocalPathResolver
     */

    public void init(Page gardRootPage, ServletConfig config)
    {
        if (initialized_) {
            return;
        }
        initialized_ = true;

        super.init(gardRootPage, config);

        String dirPath = config
            .getInitParameter(PARAM_LOCALPATHRESOLVER_DIRECTORYPATH);
        if (dirPath == null) {
            dirPath = "";
        } else if (!dirPath.startsWith("/")) {
            dirPath = "/" + dirPath;
        }

        Plugin<?> plugin = element_.getPlugin();
        baseDir_ = plugin.getHomeDirectory().getChildResource(dirPath);
    }


    @Override
    public String getRealPath(String path)
    {
        return super.getRealPath(toJavaPath(path));
    }


    @Override
    public URL getResource(String path)
    {
        return super.getResource(toJavaPath(path));
    }


    String toJavaPath(String path)
    {
        StringBuffer sb = new StringBuffer();
        int slash = path.lastIndexOf('/');
        int dot = path.lastIndexOf('.');
        if (dot >= 0 && (slash < 0 || slash < dot)) {
            sb.append(path.substring(0, dot));
        } else {
            sb.append(path);
        }
        sb.append(".java");

        return sb.toString();
    }


    /*
     * for framework
     */

    @Binding(bindingType = BindingType.NONE)
    public void setElement(ExtensionElement element)
    {
        element_ = element;
    }
}
