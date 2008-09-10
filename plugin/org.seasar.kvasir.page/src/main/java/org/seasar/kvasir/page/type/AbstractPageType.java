package org.seasar.kvasir.page.type;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.extension.PageTypeElement;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このクラスのサブクラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class AbstractPageType
    implements PageType
{
    private static final String SUFFIX_CONCEALED = "_concealed";

    private PagePlugin plugin_;

    private PageTypeElement element_;

    private String iconResourcePath_;

    private Resource iconResource_;

    private Resource concealedIconResource_;


    /*
     * PageType
     */

    public String getIconResourcePath()
    {
        return iconResourcePath_;
    }


    public Resource getIconResource()
    {
        return iconResource_;
    }


    public Resource getConcealedIconResource()
    {
        return concealedIconResource_;
    }


    public void processAfterCreated(Page page, PageMold mold)
    {
    }


    public void processBeforeDeleting(Page page)
    {
    }


    /*
     * protected scope methods
     */

    protected PagePlugin getPlugin()
    {
        return plugin_;
    }


    /*
     * for framework
     */

    public void setPlugin(PagePlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setElement(PageTypeElement element)
    {
        element_ = element;
        setIconResourcePath(element.getIconResourcePath());
    }


    void setIconResourcePath(String iconResourcePath)
    {
        iconResourcePath_ = iconResourcePath;

        Plugin<?> plugin = element_.getPlugin();
        Resource baseDir = plugin.getHomeDirectory();
        String path = iconResourcePath_;
        String concealedPath;
        int dot = path.lastIndexOf('.');
        if (dot >= 0) {
            concealedPath = path.substring(0, dot) + SUFFIX_CONCEALED
                + path.substring(dot);
        } else {
            concealedPath = path + SUFFIX_CONCEALED;
        }
        iconResource_ = baseDir.getChildResource(path);
        concealedIconResource_ = baseDir.getChildResource(concealedPath);
    }
}
