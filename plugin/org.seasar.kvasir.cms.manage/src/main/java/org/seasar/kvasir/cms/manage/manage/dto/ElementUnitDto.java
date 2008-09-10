package org.seasar.kvasir.cms.manage.manage.dto;

public class ElementUnitDto extends ElementDtoBase
{
    private String id_;

    private String value_;

    private boolean large_;

    private boolean isBoolean_;

    private boolean required_;


    public ElementUnitDto(String label, String value, String description,
        String name, Integer index, boolean isBoolean, String styleClass,
        boolean required)
    {
        super(label, description, name, index, styleClass);
        id_ = getIndexedName();
        value_ = value;
        isBoolean_ = isBoolean;
        required_ = required;
    }


    public boolean isRequired()
    {
        return required_;
    }


    public boolean isBoolean()
    {
        return isBoolean_;
    }


    public String getId()
    {
        return id_;
    }


    public String getValue()
    {
        return value_;
    }


    public boolean isLarge()
    {
        return large_;
    }
}
