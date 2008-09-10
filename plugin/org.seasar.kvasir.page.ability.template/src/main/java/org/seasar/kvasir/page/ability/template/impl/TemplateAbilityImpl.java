package org.seasar.kvasir.page.ability.template.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.AbstractPageAbility;
import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.TemplateAbility;
import org.seasar.kvasir.page.ability.template.TemplateAbilityAlfr;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class TemplateAbilityImpl extends AbstractPageAbility
    implements TemplateAbility
{
    private TemplateAbilityAlfr alfr_;


    public TemplateAbilityImpl(TemplateAbilityAlfr alfr, Page page)
    {
        super(alfr, page);
        alfr_ = alfr;
    }


    public Template getTemplate()
    {
        return alfr_.getTemplate(page_);
    }


    public void setTemplate(String template)
    {
        alfr_.setTemplate(page_, template);
    }


    public Template getTemplate(String variant)
    {
        return alfr_.getTemplate(page_, variant);
    }


    public void setTemplate(String variant, String template)
    {
        alfr_.setTemplate(page_, variant, template);
    }


    public void removeTemplate()
    {
        alfr_.removeTemplate(page_);
    }


    public void removeTemplate(String variant)
    {
        alfr_.removeTemplate(page_, variant);
    }


    public void clearAllTemplates()
    {
        alfr_.clearAllTemplates(page_);
    }


    public String getType()
    {
        return alfr_.getType(page_);
    }


    public void setType(String type)
    {
        alfr_.setType(page_, type);
    }


    public String getResponseContentType()
    {
        return alfr_.getResponseContentType(page_);
    }


    public void setResponseContentType(String responseContentType)
    {
        alfr_.setResponseContentType(page_, responseContentType);
    }
}
