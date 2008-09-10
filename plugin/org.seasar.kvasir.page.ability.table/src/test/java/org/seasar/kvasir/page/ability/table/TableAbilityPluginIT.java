package org.seasar.kvasir.page.ability.table;

import junit.framework.Test;

import org.seasar.kvasir.test.KvasirPluginTestCase;
import org.seasar.kvasir.util.io.ResourceUtils;


/**
 * @author YOKOTA Takehiko
 */
public class TableAbilityPluginIT extends
    KvasirPluginTestCase<TableAbilityPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.page.ability.table";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(TableAbilityPluginIT.class);
    }


    @Override
    public void preEstablish()
    {
        ResourceUtils.copy(getTestClassesDirectory(), getTestHomeDirectory()
            .getChildResource("plugins/fortest/test-classes"));
    }


    public void testGetTableNames()
        throws Exception
    {
        String[] tableNames = ((TableAbilityPlugin)getPlugin())
            .getTableNames(null);
        assertEquals("1", 1, tableNames.length);
        assertEquals("2", "SAMPLE", tableNames[0]);
    }
}
