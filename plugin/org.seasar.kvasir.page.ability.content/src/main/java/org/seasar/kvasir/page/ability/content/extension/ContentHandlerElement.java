package org.seasar.kvasir.page.ability.content.extension;

import java.util.Locale;

import org.seasar.kvasir.base.descriptor.AbstractGenericElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.page.ability.content.ContentHandler;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MUST, isa = ContentHandler.class)
@Bean("content-handler")
public class ContentHandlerElement extends AbstractGenericElement
{
    public static final String TYPE_ALL = "*";

    public static final String SUBTYPE_ALL = "/*";

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


    public ContentHandler getContentHandler()
    {
        return (ContentHandler)getComponent();
    }
}
