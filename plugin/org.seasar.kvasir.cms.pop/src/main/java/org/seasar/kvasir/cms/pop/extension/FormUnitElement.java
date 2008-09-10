package org.seasar.kvasir.cms.pop.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.pop.Kind;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.PopPropertyMetaData;
import org.seasar.kvasir.cms.pop.Type;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Parent;
import net.skirnir.xom.annotation.Required;


@Bean("form-unit")
public class FormUnitElement
    implements PopPropertyMetaData
{
    private PopElement parent_;

    private Kind kind_;

    private String id_;

    private String name_;

    private String description_;

    private boolean humanReadable_ = true;

    private Type type_;

    private String default_;

    private String path_;

    private List<String> optionList_ = new ArrayList<String>();


    public String getPath()
    {
        return path_;
    }


    @Attribute
    public void setPath(String path)
    {
        path_ = path;
    }


    public String getDescription()
    {
        return description_;
    }


    public String findDescription()
    {
        if (description_ != null) {
            return description_;
        } else {
            return "%pop." + getParent().getId() + "." + id_ + ".description";
        }
    }


    public String getDescription(Locale locale)
    {
        return resolveString(findDescription(), "%pop." + id_ + ".description",
            locale);
    }


    @Attribute
    public void setDescription(String description)
    {
        description_ = description;
    }


    public String getId()
    {
        return id_;
    }


    @Attribute
    public void setId(String id)
    {
        id_ = id;
    }


    public String getName()
    {
        return name_;
    }


    public String findName()
    {
        if (name_ != null) {
            return name_;
        } else {
            return "%pop." + getParent().getId() + "." + id_ + ".name";
        }
    }


    @Attribute
    public void setName(String name)
    {
        name_ = name;
    }


    public String getName(Locale locale)
    {
        return resolveString(findName(), "%pop." + id_ + ".name", locale);
    }


    public boolean isHumanReadable()
    {
        return humanReadable_;
    }


    @Attribute
    @Default("true")
    public void setHumanReadable(boolean humanReadable)
    {
        humanReadable_ = humanReadable;
    }


    public Type getType()
    {
        return type_;
    }


    public String getTypeString()
    {
        if (type_ != null) {
            return type_.getName();
        } else {
            return null;
        }
    }


    @Attribute("type")
    public void setTypeString(String type)
    {
        type_ = Type.getType(type);
    }


    public Kind getKind()
    {
        return kind_;
    }


    public String getKindString()
    {
        if (kind_ != null) {
            return kind_.getName();
        } else {
            return null;
        }
    }


    @Attribute("kind")
    @Required
    public void setKindString(String kind)
    {
        kind_ = Kind.getKind(kind);
    }


    public String getDefault()
    {
        return default_;
    }


    @Attribute
    public void setDefault(String def)
    {
        default_ = def;
    }


    public String[] getOptions()
    {
        return optionList_.toArray(new String[0]);
    }


    @Child
    public void addOption(String option)
    {
        optionList_.add(option);
    }


    public void setOptions(String[] options)
    {
        optionList_.clear();
        optionList_.addAll(Arrays.asList(options));
    }


    public PopElement getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(PopElement parent)
    {
        parent_ = parent;
    }


    protected Plugin<?> getPlugin()
    {
        return parent_.getPlugin();
    }


    protected PopPlugin getPopPlugin()
    {
        return Asgard.getKvasir().getPluginAlfr().getPlugin(PopPlugin.class);
    }


    protected String resolveString(String key, String defaultKey, Locale locale)
    {
        if (key == null) {
            return null;
        }

        String resolved = getPlugin().resolveString(key, locale, true);
        if (resolved == null) {
            resolved = getPlugin().resolveString(defaultKey, locale, true);
            if (resolved == null) {
                resolved = getPopPlugin().resolveString(defaultKey, locale,
                    true);
                if (resolved == null) {
                    resolved = "!" + key.substring(1) + "!";
                }
            }
        }
        return resolved;
    }
}
