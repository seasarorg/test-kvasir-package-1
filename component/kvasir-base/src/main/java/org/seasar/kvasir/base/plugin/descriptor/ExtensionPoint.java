package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.plugin.Plugin;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Id;
import net.skirnir.xom.annotation.Parent;
import net.skirnir.xom.annotation.Required;


public class ExtensionPoint
{
    private static final String DEFAULT_PREFIX_DESCRIPTION = "%extension-point.";

    private static final String DEFAULT_SUFFIX_DESCRIPTION = ".description";

    private PluginDescriptor parent_;

    private String id_;

    private String elementClassName_;

    private Class<? extends ExtensionElement> elementClass_;

    private ElementClassMetaData elementClassMetaData_;

    private String description_;


    public ExtensionPoint()
    {
    }


    public ExtensionPoint(String id,
        Class<? extends ExtensionElement> elementClass)
    {
        id_ = id;
        elementClassName_ = elementClass.getName();
        setElementClass(elementClass);
    }


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<extension-point");
        if (id_ != null) {
            sb.append(" id=\"").append(id_).append("\"");
        }
        if (elementClassName_ != null) {
            sb.append(" element-class=\"").append(elementClassName_).append(
                "\"");
        }
        if (description_ != null) {
            sb.append(" description=\"").append(description_).append("\"");
        }
        sb.append(" />");
        return sb.toString();
    }


    public PluginDescriptor getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(PluginDescriptor parent)
    {
        parent_ = parent;
    }


    public String getElementClassName()
    {
        return elementClassName_;
    }


    @Attribute("element-class")
    @Required
    public void setElementClassName(String elementClassName)
    {
        elementClassName_ = elementClassName;
    }


    @SuppressWarnings("unchecked")
    public Class<? extends ExtensionElement> getElementClass()
    {
        if (elementClass_ == null) {
            if (elementClassName_ != null) {
                Class<? extends ExtensionElement> elementClass;
                try {
                    elementClass = (Class<? extends ExtensionElement>)Class
                        .forName(elementClassName_, true, getParent()
                            .getPlugin().getInnerClassLoader());
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(
                        "Can't find element class for extension point (point="
                            + getParent().getId() + "." + id_ + "): "
                            + elementClassName_, ex);
                }
                setElementClass(elementClass);
            }
        }
        return elementClass_;
    }


    void setElementClass(Class<? extends ExtensionElement> elementClass)
    {
        elementClass_ = elementClass;
        elementClassMetaData_ = new ElementClassMetaData(elementClass,
            getClassLoader());
    }


    ClassLoader getClassLoader()
    {
        if (parent_ != null) {
            Plugin<?> plugin = parent_.getPlugin();
            if (plugin != null) {
                return plugin.getInnerClassLoader();
            }
        }
        return null;
    }


    public ElementClassMetaData getElementClassMetaData()
    {
        return elementClassMetaData_;
    }


    public String getId()
    {
        return id_;
    }


    @Attribute
    @Required
    @Id
    public void setId(String id)
    {
        id_ = id;
    }


    public String getFullId()
    {
        return parent_.getId() + "." + id_;
    }


    public String getDescription()
    {
        return description_;
    }


    public String findDescription()
    {
        String description = getDescription();
        if (description == null) {
            description = DEFAULT_PREFIX_DESCRIPTION + id_
                + DEFAULT_SUFFIX_DESCRIPTION;
        }
        return description;
    }


    @Attribute
    public void setDescription(String description)
    {
        description_ = description;
    }
}
