package org.seasar.kvasir.cms.manage.extension;

import java.util.Locale;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.manage.tab.Tab;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Required;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MUST, isa = Tab.class, replace = true)
@Bean("tab")
public class TabElement extends AbstractElement
{
    private Plugin<?> plugin_;

    private String displayName_;

    private String name_;

    private String path_;

    private String iconPath_;

    private String iconLinkPath_;


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


    public String getIconPath()
    {
        return iconPath_;
    }


    @Attribute
    public void setIconPath(String iconPath)
    {
        iconPath_ = iconPath;
    }


    public String getIconLinkPath()
    {
        return iconLinkPath_;
    }


    @Attribute
    public void setIconLinkPath(String iconLinkPath)
    {
        iconLinkPath_ = iconLinkPath;
    }


    public void setPlugin(Plugin<?> plugin)
    {
        plugin_ = plugin;
    }
}
