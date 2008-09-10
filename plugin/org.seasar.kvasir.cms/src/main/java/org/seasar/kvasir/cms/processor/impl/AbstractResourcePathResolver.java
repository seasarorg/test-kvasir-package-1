package org.seasar.kvasir.cms.processor.impl;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;

import org.seasar.kvasir.cms.processor.LocalPathResolver;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このクラスは{@link #init(Page,ServletConfig)}
 * メソッドの呼び出し以降はスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class AbstractResourcePathResolver
    implements LocalPathResolver
{
    abstract protected Resource getBaseDirectory();


    protected AbstractResourcePathResolver()
    {
    }


    /*
     * LocalPathResolver
     */

    public void init(Page gardRootPage, ServletConfig config)
    {
    }


    public String getRealPath(String path)
    {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Resource resource = getBaseDirectory().getChildResource(path);
        if (resource.exists()) {
            File file = resource.toFile();
            if (file != null) {
                return file.getPath();
            }
        }
        return null;
    }


    public URL getResource(String path)
    {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Resource resource = getBaseDirectory().getChildResource(path);
        if (resource.exists()) {
            return resource.getURL();
        } else {
            return null;
        }
    }


    public Set<String> getResourcePaths(String path)
    {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        Resource resource = getBaseDirectory().getChildResource(path);
        if (!resource.exists()) {
            return null;
        }
        Set<String> set = new HashSet<String>();
        Resource[] resources = resource.listResources();
        if (resources != null) {
            for (int i = 0; i < resources.length; i++) {
                if (resources[i].isDirectory()) {
                    set.add(path + "/" + resources[i].getName() + "/");
                } else {
                    set.add(path + "/" + resources[i].getName());
                }
            }
        }

        return set;
    }
}
