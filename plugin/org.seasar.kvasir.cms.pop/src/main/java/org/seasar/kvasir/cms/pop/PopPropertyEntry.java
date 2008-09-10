package org.seasar.kvasir.cms.pop;

public class PopPropertyEntry
{
    private String id_;

    private String value_;


    public PopPropertyEntry()
    {
    }


    public PopPropertyEntry(String id, String value)
    {
        setId(id);
        setValue(value);
    }


    public String getId()
    {
        return id_;
    }


    public void setId(String id)
    {
        id_ = id;
    }


    public String getValue()
    {
        return value_;
    }


    public void setValue(String value)
    {
        value_ = value;
    }
}
