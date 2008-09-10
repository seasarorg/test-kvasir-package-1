package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Locale;

import org.seasar.kvasir.cms.pop.Kind;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.Type;
import org.seasar.kvasir.cms.pop.extension.FormUnitElement;
import org.seasar.kvasir.util.LocaleUtils;

import net.skirnir.freyja.render.html.OptionTag;


public class FormUnitDto
{
    private static final String CLASS_ERROR = "error";

    private FormUnitElement element_;

    private Locale locale_;

    private Kind kind_;

    private Type type_;

    private String htmlId_;

    private String id_;

    private String name_;

    private String description_;

    private String value_;

    private boolean large_;

    private String error_;


    protected FormUnitDto()
    {
    }


    public FormUnitDto(PopContext context, Pop pop, FormUnitElement element,
        Locale locale, String variant, String value, String error)
    {
        element_ = element;
        locale_ = locale;
        kind_ = element.getKind();
        type_ = element.getType();
        error_ = error;
        if (element.getKind() == Kind.PROPERTY
            || element.getKind() == Kind.GROUP) {
            htmlId_ = pop.getId() + ":" + element.getId();
        }
        if (element.getKind() == Kind.PROPERTY) {
            id_ = "property." + element.getId();
            value_ = (value != null ? value : pop.getProperty(context, element
                .getId(), LocaleUtils.getLocale(variant)));
            large_ = (value_ != null && value_.indexOf('\n') >= 0);
        }
        if (element.getKind() == Kind.PROPERTY
            || element.getKind() == Kind.GROUP
            || element.getKind() == Kind.ACTION) {
            name_ = element.getName(locale);
            description_ = element.getDescription(locale);
        }
    }


    public FormUnitElement getElement()
    {
        return element_;
    }


    public boolean isKindIsProperty()
    {
        return (kind_ == Kind.PROPERTY);
    }


    public boolean isKindIsAction()
    {
        return (kind_ == Kind.ACTION);
    }


    public boolean isKindIsGroup()
    {
        return (kind_ == Kind.GROUP);
    }


    public String getDescription()
    {
        return description_;
    }


    public String getHtmlId()
    {
        return htmlId_;
    }


    public String getId()
    {
        return id_;
    }


    public String getName()
    {
        return name_;
    }


    public String getValue()
    {
        return value_;
    }


    public boolean isValueIsTrue()
    {
        return "true".equals(value_);
    }


    public boolean isLarge()
    {
        return large_;
    }


    public boolean isBoolean()
    {
        return (type_ == Type.BOOLEAN);
    }


    public boolean isSelect()
    {
        return (type_ == Type.SELECT);
    }


    public boolean isPage()
    {
        return (type_ == Type.PAGE);
    }


    public OptionTag[] getOptions()
    {
        String[] options = element_.getOptions();
        OptionTag[] optionTags = new OptionTag[options.length];
        for (int i = 0; i < options.length; i++) {
            optionTags[i] = new OptionTag(options[i], getOptionName(options[i]))
                .setSelected(options[i].equals(value_));
        }
        return optionTags;
    }


    String getOptionName(String value)
    {
        String name = element_.getParent().getPlugin().getProperty(
            "pop." + element_.getParent().getId() + "." + element_.getId()
                + ".option." + value + ".name", locale_);
        return (name != null ? name : value);
    }


    public boolean isText()
    {
        return (type_ == null);
    }


    public String getError()
    {
        return error_;
    }


    public String getStyleClass()
    {
        if (error_ != null) {
            return CLASS_ERROR;
        } else {
            return null;
        }
    }
}
