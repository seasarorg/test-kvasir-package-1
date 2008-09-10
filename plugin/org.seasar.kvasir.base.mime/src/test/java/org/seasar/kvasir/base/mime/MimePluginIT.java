package org.seasar.kvasir.base.mime;

import junit.framework.Test;

import org.seasar.kvasir.test.KvasirPluginTestCase;


public class MimePluginIT extends KvasirPluginTestCase<MimePlugin>
{
    protected String getTargetPluginId()
    {
        return MimePlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(MimePluginIT.class);
    }


    public void testGetMimeMappings()
        throws Exception
    {
        MimeMappings actual = getPlugin().getMimeMappings();

        assertNotNull(actual);
        assertEquals("xml", actual.getExtension("text/xml"));
    }
}
