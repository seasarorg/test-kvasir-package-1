package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Locale;

import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.extension.PopElement;


public class PopDto
{
    private String id_;

    private String name_;

    private String description_;

    private boolean padding_;


    public PopDto(Pop pop, Locale locale)
    {
        this(pop.getId(), pop.getElement().findName(locale), pop.getElement()
            .findDescription(locale), false);
    }


    public PopDto(PopElement element, Locale locale)
    {
        this(element.getFullId() + Pop.INSTANCE_DELIMITER + Pop.INSTANCEID_NEW,
            element.findName(locale), element.findDescription(locale), false);
    }


    public PopDto(String id, String name, String description, boolean padding)
    {
        id_ = id;
        name_ = name;
        description_ = description;
        padding_ = padding;
    }


    public String getId()
    {
        return id_;
    }


    public String getName()
    {
        return name_;
    }


    public String getDescription()
    {
        return description_;
    }


    public boolean isPadding()
    {
        return padding_;
    }
}
