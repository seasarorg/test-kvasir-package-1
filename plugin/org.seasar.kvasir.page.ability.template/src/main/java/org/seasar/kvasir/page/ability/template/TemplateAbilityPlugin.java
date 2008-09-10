package org.seasar.kvasir.page.ability.template;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.ability.template.extension.TemplateHandlerElement;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface TemplateAbilityPlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.page.ability.template";

    String ID_PATH = ID.replace('.', '/');

    String PROP_REVISION_REMAIN = "revision.remain";


    TemplateHandlerElement[] getTemplateHandlerElements();


    TemplateHandlerElement getTemplateHandlerElement(String type);


    TemplateAbilityAlfr getTemplateAbilityAlfr();
}
