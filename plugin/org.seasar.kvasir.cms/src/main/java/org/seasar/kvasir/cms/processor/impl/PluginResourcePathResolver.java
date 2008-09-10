package org.seasar.kvasir.cms.processor.impl;

import javax.servlet.ServletConfig;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.util.io.Resource;


/**
 * <code>/path/to/page</code>に対応するリソースとして、
 * プラグインの持つリソース<code>/path/to/page</code>を返すような
 * LocalPathResolverです。
 * <p>基準とするプラグインとしては、このコンポーネントを定義しているプラグインが使用されますが、
 * <code>useGardDefiningPluginAsBase</code>プロパティがtrueである場合は
 * <code>init()</code>メソッドに渡されたgardを定義しているプラグインが使用されます。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@SuppressWarnings("unchecked")
public class PluginResourcePathResolver extends AbstractResourcePathResolver
{
    public static final String PARAM_LOCALPATHRESOLVER_DIRECTORYPATH = "local-path-resolver.directory-path";

    private ExtensionElement element_;

    private static final String EL_GARDID = "${gard-id}";

    private PagePlugin pagePlugin_;

    private boolean initialized_ = false;

    private Resource baseDir_;

    private boolean useGardDefiningPluginAsBase_;


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
        String gardId = gardRootPage.getGardId();
        if (dirPath == null) {
            dirPath = "";
        } else {
            StringBuffer sb = new StringBuffer();
            boolean endsWithSlash = dirPath.endsWith("/");
            int el = dirPath.indexOf(EL_GARDID);
            if (el >= 0) {
                sb.append(dirPath.substring(0, el)).append(getShortId(gardId))
                    .append(
                        dirPath.substring(el + EL_GARDID.length(), dirPath
                            .length()
                            - (endsWithSlash ? 1 : 0)));
            } else {
                sb.append(dirPath.substring(0, dirPath.length()
                    - (endsWithSlash ? 1 : 0)));
            }
            dirPath = sb.toString();
        }

        if (useGardDefiningPluginAsBase_) {
            baseDir_ = pagePlugin_.getPlugin(gardId).getHomeDirectory()
                .getChildResource(dirPath);
        } else {
            baseDir_ = getPlugin().getHomeDirectory().getChildResource(dirPath);
        }
    }


    protected Plugin<?> getPlugin()
    {
        return element_.getPlugin();
    }


    String getShortId(String gardId)
    {
        int dot = gardId.lastIndexOf('.');
        if (dot >= 0) {
            return gardId.substring(dot + 1);
        } else {
            return gardId;
        }
    }


    public PagePlugin getPagePlugin()
    {
        return pagePlugin_;
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    /*
     * for framework
     */

    @Binding(bindingType = BindingType.NONE)
    public void setElement(ExtensionElement element)
    {
        element_ = element;
    }


    public boolean isUseGardDefiningPluginAsBase()
    {
        return useGardDefiningPluginAsBase_;
    }


    public void setUseGardDefiningPluginAsBase(
        boolean useGardDefiningPluginAsBase)
    {
        useGardDefiningPluginAsBase_ = useGardDefiningPluginAsBase;
    }
}
