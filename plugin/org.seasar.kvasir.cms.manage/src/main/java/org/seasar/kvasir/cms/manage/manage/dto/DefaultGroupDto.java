package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Locale;

import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopPlugin;


public class DefaultGroupDto extends FormUnitDto
{
    public static final String ID_DEFAULTGROUP = "group.default";

    private static final String PROP_NAME = "pop." + ID_DEFAULTGROUP + ".name";

    private String name_;

    private String htmlId_;


    public DefaultGroupDto(Pop pop, PopPlugin popPlugin, Locale locale)
    {
        name_ = popPlugin.getProperty(PROP_NAME, locale);
        htmlId_ = pop.getId() + ":" + ID_DEFAULTGROUP;
    }


    @Override
    public String getName()
    {
        return name_;
    }


    @Override
    public String getHtmlId()
    {
        return htmlId_;
    }
}
