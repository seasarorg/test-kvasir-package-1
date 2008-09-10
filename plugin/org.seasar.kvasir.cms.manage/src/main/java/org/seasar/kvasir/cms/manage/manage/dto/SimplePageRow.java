package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Locale;

import org.seasar.kvasir.cms.util.PresentationUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;


public class SimplePageRow
{
    private String styleClass_;

    private String name_;

    private String pathname_;

    private String iconURL_;

    private String label_;


    public SimplePageRow(Page page, Locale locale, boolean even)
    {
        styleClass_ = even ? "row-hilite" : null;
        name_ = page.getName();
        pathname_ = page.getPathname();
        iconURL_ = PresentationUtils.getIconURL(page);
        PropertyAbility prop = page.getAbility(PropertyAbility.class);
        label_ = prop.getProperty(PropertyAbility.PROP_LABEL, locale);
        if (label_ == null) {
            label_ = name_;
        }
    }


    public String getName()
    {
        return name_;
    }


    public String getIconURL()
    {
        return iconURL_;
    }


    public String getLabel()
    {
        return label_;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public String getStyleClass()
    {
        return styleClass_;
    }
}
