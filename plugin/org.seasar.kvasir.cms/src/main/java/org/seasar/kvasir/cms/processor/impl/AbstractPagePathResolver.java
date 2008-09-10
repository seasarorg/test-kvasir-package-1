package org.seasar.kvasir.cms.processor.impl;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;

import org.seasar.kvasir.cms.processor.LocalPathResolver;
import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * このクラスは{@link #init(Page,ServletConfig)}
 * メソッドの呼び出し以降はスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class AbstractPagePathResolver
    implements LocalPathResolver
{
    private Page gardRootPage_;


    abstract protected String getRealPath(Page page);


    abstract protected URL getResource(Page page);


    protected AbstractPagePathResolver()
    {
    }


    /*
     * LocalPathResolver
     */

    public void init(Page gardRootPage, ServletConfig config)
    {
        gardRootPage_ = gardRootPage;
    }


    protected Page getGardRootPage()
    {
        return gardRootPage_;
    }


    public String getRealPath(String path)
    {
        Page page;
        if (path.length() == 0) {
            page = getGardRootPage();
        } else {
            page = getGardRootPage().getChild(path);
        }
        if (page != null) {
            return getRealPath(page);
        } else {
            return null;
        }
    }


    public URL getResource(String path)
    {
        Page page;
        if (path.length() == 0) {
            page = getGardRootPage();
        } else {
            page = getGardRootPage().getChild(path);
        }
        if (page != null) {
            return getResource(page);
        } else {
            return null;
        }
    }


    public Set<String> getResourcePaths(String path)
    {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        Page page = getGardRootPage().getChild(path);
        if (page == null) {
            return null;
        }
        Set<String> set = new HashSet<String>();
        Page[] pages = page.getChildren();
        for (int i = 0; i < pages.length; i++) {
            if (pages[i].isNode()) {
                set.add(path + "/" + pages[i].getName() + "/");
            } else {
                set.add(path + "/" + pages[i].getName());
            }
        }

        return set;
    }
}
