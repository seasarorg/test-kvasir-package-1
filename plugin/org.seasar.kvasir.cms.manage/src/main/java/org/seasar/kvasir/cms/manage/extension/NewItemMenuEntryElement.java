package org.seasar.kvasir.cms.manage.extension;

import java.util.Locale;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.manage.menu.NewItemMenuEntry;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Required;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MUST, isa = NewItemMenuEntry.class, replace = true)
@Bean("new-item-menu-entry")
public class NewItemMenuEntryElement extends AbstractElement
{
    private Plugin<?> plugin_;

    private String displayName_;

    private String name_;

    private String path_;

    private String parameter_;

    private String category_;


    public String getCategory()
    {
        if (category_ == null) {
            return NewItemMenuEntry.CATEGORY_PAGE;
        } else {
            return category_;
        }
    }


    @Attribute
    public void setCategory(String category)
    {
        category_ = category;
    }


    public String getDisplayName()
    {
        return displayName_;
    }


    @Attribute
    public void setDisplayName(String displayName)
    {
        displayName_ = displayName;
    }


    public String getDisplayName(Locale locale)
    {
        return plugin_.resolveString(displayName_, locale);
    }


    public String getName()
    {
        return name_;
    }


    @Attribute
    public void setName(String name)
    {
        name_ = name;
    }


    public String getParameter()
    {
        if (parameter_ == null) {
            return "";
        } else {
            return parameter_;
        }
    }


    @Attribute
    public void setParameter(String parameter)
    {
        parameter_ = parameter;
    }


    public String getPath()
    {
        return path_;
    }


    @Attribute
    @Required
    public void setPath(String path)
    {
        path_ = path;
    }


    public void setPlugin(Plugin<?> plugin)
    {
        plugin_ = plugin;
    }
}
