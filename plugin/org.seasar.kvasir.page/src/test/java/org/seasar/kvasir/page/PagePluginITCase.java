package org.seasar.kvasir.page;

import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
abstract public class PagePluginITCase extends KvasirPluginTestCase<PagePlugin>
{
    @Override
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.page";
    }
}
