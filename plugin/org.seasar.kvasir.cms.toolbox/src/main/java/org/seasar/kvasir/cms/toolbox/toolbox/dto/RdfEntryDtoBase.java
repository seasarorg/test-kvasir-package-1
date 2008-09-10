package org.seasar.kvasir.cms.toolbox.toolbox.dto;

public class RdfEntryDtoBase
{
    protected String URL_;

    protected String date_;

    protected String description_;

    protected String subject_;

    protected String title_;


    public RdfEntryDtoBase()
    {
    }

    public RdfEntryDtoBase(String URL, String date, String description, String subject, String title)
    {
        URL_ = URL;
        date_ = date;
        description_ = description;
        subject_ = subject;
        title_ = title;
    }

    public String getURL()
    {
        return URL_;
    }

    public void setURL(String URL)
    {
        URL_ = URL;
    }

    public String getDate()
    {
        return date_;
    }

    public void setDate(String date)
    {
        date_ = date;
    }

    public String getDescription()
    {
        return description_;
    }

    public void setDescription(String description)
    {
        description_ = description;
    }

    public String getSubject()
    {
        return subject_;
    }

    public void setSubject(String subject)
    {
        subject_ = subject;
    }

    public String getTitle()
    {
        return title_;
    }

    public void setTitle(String title)
    {
        title_ = title;
    }
}
