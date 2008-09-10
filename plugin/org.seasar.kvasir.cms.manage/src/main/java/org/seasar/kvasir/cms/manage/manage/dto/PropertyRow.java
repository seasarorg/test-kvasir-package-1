package org.seasar.kvasir.cms.manage.manage.dto;

import org.seasar.kvasir.util.html.HTMLUtils;


public class PropertyRow
{
    private String listHeaderStyleClass_;

    private String name_;

    private String renderedValue_;

    private String value_;

    private boolean large_;


    public PropertyRow(String name, String value, boolean even)
    {
        listHeaderStyleClass_ = even ? "list-header-hilite" : "list-header";
        name_ = name;
        value_ = value;
        renderedValue_ = prepareRenderedValue(name, value);

        if (value_ != null) {
            for (int i = 0; i < value_.length(); i++) {
                char ch = value_.charAt(i);
                if (ch == '\n' || ch == '\r') {
                    large_ = true;
                    break;
                }
            }
        }
    }


    public String getListHeaderStyleClass()
    {
        return listHeaderStyleClass_;
    }


    public String getName()
    {
        return name_;
    }


    public String getRenderedValue()
    {
        return renderedValue_;
    }


    public String getValue()
    {
        return value_;
    }


    public boolean isLarge()
    {
        return large_;
    }


    /*
     * private scope methods
     */

    private String prepareRenderedValue(String name, String value)
    {
        if (large_) {
            return HTMLUtils.filterLines(value, true);
        } else {
            return HTMLUtils.filter(value);
        }
    }
}
