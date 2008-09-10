package org.seasar.kvasir.page.ability.content.impl;

import static org.seasar.kvasir.page.ability.content.extension.ContentHandlerElement.SUBTYPE_ALL;
import static org.seasar.kvasir.page.ability.content.extension.ContentHandlerElement.TYPE_ALL;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.kvasir.base.KvasirUtils;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.plugin.SettingsEvent;
import org.seasar.kvasir.base.plugin.impl.AbstractSettingsListener;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.content.ContentAbilityAlfr;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.page.ability.content.extension.ContentHandlerElement;
import org.seasar.kvasir.page.ability.content.setting.ContentAbilityPluginSettings;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ContentAbilityPluginImpl extends
    AbstractPlugin<ContentAbilityPluginSettings>
    implements ContentAbilityPlugin
{
    private ContentHandler[] contentHandlers_;

    private Map<String, ContentHandler> contentHandlerMap_;

    private Pattern[] contentHandlerPatterns_;

    private ContentHandlerElement[] contentHandlerElements_;

    private PagePlugin pagePlugin_;

    private ContentAbilityAlfr contentAbilityAlfr_;

    protected Resource contentsDirectory_;


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    /*
     * ContentAbilityPlugin
     */

    public ContentHandler getContentHandler(String type)
    {
        ContentHandler handler = contentHandlerMap_.get(type);
        if (handler == null) {
            int slash = type.indexOf('/');
            if (slash >= 0) {
                String mainType = type.substring(0, slash);
                for (int i = 0; i < contentHandlerPatterns_.length; i++) {
                    Pattern pattern = contentHandlerPatterns_[i];
                    if (mainType.equals(pattern.getMainType())) {
                        handler = pattern.getContentHandler();
                        break;
                    }
                }
            }
        }
        if (handler == null) {
            handler = contentHandlerMap_.get(TYPE_ALL);
        }
        return handler;
    }


    public ContentHandlerElement[] getContentHandlerElements()
    {
        return contentHandlerElements_;
    }


    public Resource getContentResource(int id)
    {
        return contentsDirectory_.getChildResource(PageUtils.getFilePath(id)
            + ".body");
    }


    public ContentAbilityAlfr getContentAbilityAlfr()
    {
        return contentAbilityAlfr_;
    }


    /*
     * AbstractPlugin
     */

    @Override
    public Class<ContentAbilityPluginSettings> getSettingsClass()
    {
        return ContentAbilityPluginSettings.class;
    }


    protected boolean doStart()
    {
        ContentHandlerElement[] elements = getExtensionElements(ContentHandlerElement.class);
        Map<String, ContentHandler> contentHandlerMap = new HashMap<String, ContentHandler>();
        Set<Pattern> contentHandlerPatternSet = new LinkedHashSet<Pattern>();
        Map<String, ContentHandlerElement> contentHandlerElementMap = new LinkedHashMap<String, ContentHandlerElement>();
        for (int i = 0; i < elements.length; i++) {
            ContentHandlerElement element = elements[i];
            String type = element.getType();

            ContentHandler handler = element.getContentHandler();
            KvasirUtils.start(handler);
            if (type.endsWith(SUBTYPE_ALL)) {
                String prefix = type.substring(0, type.length()
                    - SUBTYPE_ALL.length());
                contentHandlerPatternSet.add(new Pattern(prefix, handler));
            } else {
                ContentHandler old = contentHandlerMap.get(type);
                if (old != null) {
                    KvasirUtils.stop(old);
                }
                contentHandlerMap.put(type, handler);
                if (!TYPE_ALL.equals(type)) {
                    contentHandlerElementMap.put(type, element);
                }
            }
        }
        List<ContentHandler> contentHandlerList = new ArrayList<ContentHandler>();
        for (Iterator<ContentHandler> itr = contentHandlerMap.values()
            .iterator(); itr.hasNext();) {
            contentHandlerList.add(itr.next());
        }
        contentHandlers_ = contentHandlerList.toArray(new ContentHandler[0]);
        contentHandlerMap_ = contentHandlerMap;
        contentHandlerPatterns_ = contentHandlerPatternSet
            .toArray(new Pattern[0]);
        contentHandlerElements_ = contentHandlerElementMap.values().toArray(
            new ContentHandlerElement[0]);

        contentAbilityAlfr_ = pagePlugin_
            .getPageAbilityAlfr(ContentAbilityAlfr.class);

        addSettingsListener(new AbstractSettingsListener<ContentAbilityPluginSettings>() {
            @Override
            public void notifyUpdated(
                SettingsEvent<ContentAbilityPluginSettings> event)
            {
                ContentAbilityPluginSettings settings = event.getNewSettings();
                if (settings.getContentsDirectory().length() > 0) {
                    contentsDirectory_ = new FileResource(new File(settings
                        .getContentsDirectory()));
                } else {
                    contentsDirectory_ = getConfigurationDirectory()
                        .getChildResource("contents");
                }
            }
        });

        return true;
    }


    protected void doStop()
    {
        KvasirUtils.stop(contentHandlers_);
        contentHandlers_ = null;
        contentHandlerMap_ = null;
        for (int i = 0; i < contentHandlerPatterns_.length; i++) {
            KvasirUtils.stop(contentHandlerPatterns_[i].getContentHandler());
        }
        contentHandlerPatterns_ = null;
        pagePlugin_ = null;
        contentAbilityAlfr_ = null;
    }


    /*
     * inner classes
     */

    private static class Pattern
    {
        private String type_;

        private ContentHandler contentHandler_;


        public Pattern(String type, ContentHandler contentHandler)
        {
            type_ = type;
            contentHandler_ = contentHandler;
        }


        public String getMainType()
        {
            return type_;
        }


        public ContentHandler getContentHandler()
        {
            return contentHandler_;
        }
    }
}
