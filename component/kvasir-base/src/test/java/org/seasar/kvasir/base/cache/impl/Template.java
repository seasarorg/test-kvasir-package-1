package org.seasar.kvasir.base.cache.impl;

public class Template
{
    private String id_;

    private String type_;

    private String template_;


    public Template(String id, String type, String template)
    {
        id_ = id;
        type_ = type;
        template_ = template;
    }


    public String getId()
    {
        return id_;
    }


    public String getTemplate()
    {
        return template_;
    }


    public String getType()
    {
        return type_;
    }
}
