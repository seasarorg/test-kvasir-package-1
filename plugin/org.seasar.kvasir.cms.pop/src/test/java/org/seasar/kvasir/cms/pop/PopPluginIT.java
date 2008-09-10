package org.seasar.kvasir.cms.pop;

import junit.framework.Test;

import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class PopPluginIT extends KvasirPluginTestCase<PopPlugin>
{
    private static final String ID_INTRODUCTIONPOP = PopPlugin.ID
        + ".introductionPop";


    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.cms.pop";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(PopPluginIT.class);
    }


    public void test削除されたPOPのインスタンスIDが再利用されること()
        throws Exception
    {
        PopPlugin plugin = getPlugin();
        Pop pop = plugin
            .getPop(PathId.HEIM_MIDGARD, ID_INTRODUCTIONPOP + "--1");
        int expected = pop.getInstanceId();
        plugin.removePop(PathId.HEIM_MIDGARD, ID_INTRODUCTIONPOP, pop
            .getInstanceId());
        pop = plugin.getPop(PathId.HEIM_MIDGARD, ID_INTRODUCTIONPOP + "--1");

        assertEquals(expected, pop.getInstanceId());
    }
}
