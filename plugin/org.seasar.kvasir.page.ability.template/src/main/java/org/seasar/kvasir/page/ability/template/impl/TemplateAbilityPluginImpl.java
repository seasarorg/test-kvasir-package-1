package org.seasar.kvasir.page.ability.template.impl;

import static org.seasar.kvasir.page.ability.template.extension.TemplateHandlerElement.TYPE_ALL;

import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.ability.template.TemplateAbilityAlfr;
import org.seasar.kvasir.page.ability.template.TemplateAbilityPlugin;
import org.seasar.kvasir.page.ability.template.extension.TemplateHandlerElement;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class TemplateAbilityPluginImpl extends AbstractPlugin<EmptySettings>
    implements TemplateAbilityPlugin
{
    private TemplateHandlerElement[] templateHandlerElements_;

    private Map<String, TemplateHandlerElement> templateHandlerElementMap_;

    private PagePlugin pagePlugin_;

    private TemplateAbilityAlfr templateAbilityAlfr_;


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    /*
     * TemplateAbilityPlugin
     */

    public TemplateHandlerElement[] getTemplateHandlerElements()
    {
        return templateHandlerElements_;
    }


    public TemplateHandlerElement getTemplateHandlerElement(String type)
    {
        return templateHandlerElementMap_.get(type);
    }


    public TemplateAbilityAlfr getTemplateAbilityAlfr()
    {
        return templateAbilityAlfr_;
    }


    /*
     * AbstractPlugin
     */

    protected boolean doStart()
    {
        TemplateHandlerElement[] elements = getExtensionElements(TemplateHandlerElement.class);
        Map<String, TemplateHandlerElement> templateHandlerElementMap = new LinkedHashMap<String, TemplateHandlerElement>();
        for (int i = 0; i < elements.length; i++) {
            TemplateHandlerElement element = elements[i];
            String type = element.getType();

            if (!TYPE_ALL.equals(type)) {
                templateHandlerElementMap.put(type, element);
            }
        }

        templateHandlerElementMap_ = templateHandlerElementMap;
        templateHandlerElements_ = templateHandlerElementMap.values().toArray(
            new TemplateHandlerElement[0]);

        templateAbilityAlfr_ = pagePlugin_
            .getPageAbilityAlfr(TemplateAbilityAlfr.class);

        return true;
    }


    protected void doStop()
    {
        templateHandlerElements_ = null;
        pagePlugin_ = null;
        templateAbilityAlfr_ = null;
    }
}
