package org.seasar.kvasir.page.ability.content;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.ability.content.extension.ContentHandlerElement;
import org.seasar.kvasir.page.ability.content.setting.ContentAbilityPluginSettings;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface ContentAbilityPlugin
    extends Plugin<ContentAbilityPluginSettings>
{
    String ID = "org.seasar.kvasir.page.ability.content";

    String ID_PATH = ID.replace('.', '/');


    ContentHandler getContentHandler(String type);


    ContentHandlerElement[] getContentHandlerElements();


    Resource getContentResource(int id);


    ContentAbilityAlfr getContentAbilityAlfr();
}
