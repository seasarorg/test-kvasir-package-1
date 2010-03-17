package org.seasar.kvasir.cms.processor.impl;

import java.io.File;
import java.net.URL;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class AbstractPageResourcePathResolver extends
    AbstractPagePathResolver
{
    abstract protected Resource getResourceObject(Page page);


    /*
     * AbstractPagePathResolver
     */

    protected String getRealPath(Page page)
    {
        Resource resource = getResourceObject(page);
        if (resource != null) {
            File file = resource.toFile();
            if (file != null) {
                return file.getPath();
            }
        }
        return null;
    }


    @Override
    protected URL getResource(Page page)
    {
        Resource resource = getResourceObject(page);
        if (resource != null && resource.exists()) {
            return resource.getURL();
        } else {
            return null;
        }
    }
}
