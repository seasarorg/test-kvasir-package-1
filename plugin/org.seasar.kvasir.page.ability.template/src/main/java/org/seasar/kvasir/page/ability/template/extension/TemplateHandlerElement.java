package org.seasar.kvasir.page.ability.template.extension;

import java.util.Locale;

import org.seasar.kvasir.base.descriptor.AbstractGenericElement;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Bean("template-handler")
public class TemplateHandlerElement extends AbstractGenericElement
{
    public static final String TYPE_ALL = "*";

    private String type_;

    private String displayName_;


    public String getType()
    {
        if (type_ == null) {
            return TYPE_ALL;
        } else {
            return type_;
        }
    }


    public String getDisplayName()
    {
        return displayName_;
    }


    public String resolveDisplayName(Locale locale)
    {
        return getParent().getParent().getPlugin().resolveString(displayName_,
            locale);
    }


    @Attribute
    public void setType(String type)
    {
        type_ = type;
    }


    @Attribute
    public void setDisplayName(String displayName)
    {
        displayName_ = displayName;
    }
}
