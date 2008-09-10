package org.seasar.kvasir.cms.manage.manage.dto;

abstract public class ElementDtoBase
{
    public static final String STYLECLASS_PROTOTYPE = "prototype";

    private String name_;

    private String label_;

    private String description_;

    private Integer index_;

    private String styleClass_;


    protected ElementDtoBase(String label, String description, String name,
        Integer index, String styleClass)
    {
        name_ = name;
        index_ = index;
        label_ = label;
        description_ = description;
        styleClass_ = styleClass;
    }


    public boolean isAddable()
    {
        return STYLECLASS_PROTOTYPE.equals(styleClass_);
    }


    public boolean isDeletable()
    {
        return (index_ != null);
    }


    public String getStyleClass()
    {
        return styleClass_;
    }


    public Integer getIndex()
    {
        return index_;
    }


    public String getName()
    {
        return name_;
    }


    public String getIndexedName()
    {
        if (index_ != null) {
            return name_ + "[" + index_ + "]";
        } else {
            return name_;
        }
    }


    public String getDescription()
    {
        return description_;
    }


    public String getLabel()
    {
        return label_;
    }
}
