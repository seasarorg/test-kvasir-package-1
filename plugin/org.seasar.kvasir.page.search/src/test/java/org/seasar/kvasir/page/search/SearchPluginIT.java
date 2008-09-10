package org.seasar.kvasir.page.search;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class SearchPluginIT extends KvasirPluginTestCase<SearchPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.page.search";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(SearchPluginIT.class);
    }
}
