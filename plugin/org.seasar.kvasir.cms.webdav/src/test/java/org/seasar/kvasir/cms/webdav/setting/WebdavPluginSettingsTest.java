package org.seasar.kvasir.cms.webdav.setting;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class WebdavPluginSettingsTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        assertBeanEquals("<webdav-plugin-settings />",
            new WebdavPluginSettings());
    }


    public void testToXML2()
        throws Exception
    {
        WebdavPluginSettings target = new WebdavPluginSettings();
        Match match = new Match();
        match.setPath("PATHNAME");
        target.addMatch(match);

        assertBeanEquals(
            "<webdav-plugin-settings><match path=\"PATHNAME\" /></webdav-plugin-settings>",
            target);
    }
}
