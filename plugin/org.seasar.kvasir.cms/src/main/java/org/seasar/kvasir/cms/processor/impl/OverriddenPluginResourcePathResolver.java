package org.seasar.kvasir.cms.processor.impl;

import javax.servlet.ServletConfig;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.OverriddenResource;


/**
 * XXX 使用することがあるのか？
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class OverriddenPluginResourcePathResolver extends
    PluginResourcePathResolver
{
    /**
     * gard root page毎の変更可能なリソースを格納するディレクトリの名前です。
     */
    public static final String GARDRESOURCES_DIR = "gard-resources";

    private boolean initialized_ = false;

    private Resource baseDir_;


    /*
     * PluginResourcePathResolver
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

        baseDir_ = new OverriddenResource(getBaseDirectory(), getPlugin()
            .getConfigurationDirectory().getChildResource(
                GARDRESOURCES_DIR + PageUtils.getFilePath(gardRootPage.getId())
                    + dirPath));
    }
}
