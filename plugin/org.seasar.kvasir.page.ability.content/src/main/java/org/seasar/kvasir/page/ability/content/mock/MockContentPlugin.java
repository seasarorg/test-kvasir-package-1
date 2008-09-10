package org.seasar.kvasir.page.ability.content.mock;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.base.mock.plugin.MockPlugin;
import org.seasar.kvasir.page.ability.content.ContentAbilityAlfr;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.page.ability.content.extension.ContentHandlerElement;
import org.seasar.kvasir.page.ability.content.setting.ContentAbilityPluginSettings;
import org.seasar.kvasir.util.io.Resource;


public class MockContentPlugin extends MockPlugin<ContentAbilityPluginSettings>
    implements ContentAbilityPlugin
{

    private Map<String, ContentHandler> contentHandlerMap_ = new HashMap<String, ContentHandler>();


    public MockContentPlugin()
    {
        setId(ContentAbilityPlugin.ID);
    }


    public ContentAbilityAlfr getContentAbilityAlfr()
    {
        return null;
    }


    public ContentHandler getContentHandler(String type)
    {
        return contentHandlerMap_.get(type);
    }


    public ContentHandlerElement[] getContentHandlerElements()
    {
        return null;
    }


    public Resource getContentResource(int id)
    {
        return null;
    }


    public MockContentPlugin setContentHandler(String type,
        ContentHandler handler)
    {
        contentHandlerMap_.put(type, handler);
        return this;
    }
}
