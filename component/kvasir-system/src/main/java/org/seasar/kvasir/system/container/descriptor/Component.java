package org.seasar.kvasir.system.container.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.skirnir.xom.Merger;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Content;
import net.skirnir.xom.annotation.Id;


public class Component
    implements Merger
{
    private String name_;

    private String className_;

    private String instance_;

    private List<Property> propertyList_ = new ArrayList<Property>();

    private List<Arg> argList_ = new ArrayList<Arg>();

    private String content_;


    public String getClassName()
    {
        return className_;
    }


    @Attribute("class")
    public void setClassName(String className)
    {
        className_ = className;
    }


    public String getName()
    {
        return name_;
    }


    @Attribute
    @Id
    public void setName(String name)
    {
        name_ = name;
    }


    public String getInstance()
    {
        return instance_;
    }


    @Attribute
    public void setInstance(String instance)
    {
        instance_ = instance;
    }


    public Property[] getProperties()
    {
        return propertyList_.toArray(new Property[0]);
    }


    @Child
    public void addProperty(Property property)
    {
        propertyList_.add(property);
    }


    public Arg[] getArgs()
    {
        return argList_.toArray(new Arg[0]);
    }


    @Child
    public void addArg(Arg arg)
    {
        argList_.add(arg);
    }


    public void setArgs(Arg[] args)
    {
        argList_.clear();
        argList_.addAll(Arrays.asList(args));
    }


    public String getContent()
    {
        return content_;
    }


    @Content
    public void setContent(String content)
    {
        content_ = content;
    }


    /*
     * Merger
     */

    public void merge(Object bean, Object merged, XOMapper mapper)
    {
        // マージではなく置き換えを行なう。
        Component empty = new Component();
        mapper.mergeAttributes(empty, bean, merged);
        mapper.mergeChildren(empty, bean, merged);
        mapper.mergeContent(empty, bean, merged);
    }
}
