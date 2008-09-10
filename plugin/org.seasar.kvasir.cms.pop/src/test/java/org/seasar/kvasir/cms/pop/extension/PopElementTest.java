package org.seasar.kvasir.cms.pop.extension;

import java.util.Locale;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;
import org.seasar.kvasir.base.mock.plugin.MockPlugin;
import org.seasar.kvasir.base.plugin.Plugin;


public class PopElementTest extends XOMBeanTestCase
{
    public void testGetBodyTemplate()
        throws Exception
    {
        PopElement target = new PopElement() {
            @Override
            public Plugin<?> getPlugin()
            {
                return new MockPlugin<Object>() {
                    @Override
                    public String getProperty(String name, Locale locale)
                    {
                        if (name.equals("pop.popId.body")) {
                            return "bodyTemplate";
                        } else {
                            return null;
                        }
                    }
                };
            }
        };
        target.setId("popId");

        assertEquals("bodyResourcePathが設定されていない場合はPluginのプロパティからボディを取得すること",
            "bodyTemplate", target.findBody(new Locale("ja")));
    }


    public void testToXML()
        throws Exception
    {
        assertBeanEquals("<pop />", new PopElement());
    }
}
