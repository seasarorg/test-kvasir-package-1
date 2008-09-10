package org.seasar.kvasir.cms.kdiary.kdiary.web;

public class DayPageBase extends
    org.seasar.kvasir.cms.kdiary.kdiary.web.PageBase
{
    protected String                                           body_;

    protected org.seasar.kvasir.cms.kdiary.kdiary.dto.EntryDto entry_;

    protected String                                           mail_;

    protected String                                           message_;

    protected String                                           name_;


    public String getBody()
    {
        return body_;
    }


    public void setBody(String body)
    {
        body_ = body;
    }


    public org.seasar.kvasir.cms.kdiary.kdiary.dto.EntryDto getEntry()
    {
        return entry_;
    }


    public String getMail()
    {
        return mail_;
    }


    public void setMail(String mail)
    {
        mail_ = mail;
    }


    public String getMessage()
    {
        return message_;
    }


    public String getName()
    {
        return name_;
    }


    public void setName(String name)
    {
        name_ = name;
    }


    public void _get()
    {

    }


    public void _post()
    {

    }
}
