package org.seasar.kvasir.cms.kdiary.kdiary.web;

public class EditActionPageBase extends
    org.seasar.kvasir.cms.kdiary.kdiary.web.PageBase
{
    protected String  body_;

    protected String  date_;

    protected String  day_;

    protected boolean hide_;

    protected String  month_;

    protected String  submitName_;

    protected String  submitValue_;

    protected String  targetPathname_;

    protected String  title_;

    protected String  year_;


    public String getBody()
    {
        return body_;
    }


    public void setBody(String body)
    {
        body_ = body;
    }


    public String getDate()
    {
        return date_;
    }


    public String getDay()
    {
        return day_;
    }


    @org.seasar.cms.ymir.extension.constraint.Numeric(greaterEqual = 4.9E-324, greaterThan = 4.9E-324, integer = true, lessEqual = 1.7976931348623157E308, lessThan = 1.7976931348623157E308, property = {}, value = {})
    @org.seasar.cms.ymir.extension.constraint.Required( {})
    public void setDay(String day)
    {
        day_ = day;
    }


    public boolean getHide()
    {
        return hide_;
    }


    public void setHide(boolean hide)
    {
        hide_ = hide;
    }


    public String getMonth()
    {
        return month_;
    }


    @org.seasar.cms.ymir.extension.constraint.Numeric(greaterEqual = 4.9E-324, greaterThan = 4.9E-324, integer = true, lessEqual = 1.7976931348623157E308, lessThan = 1.7976931348623157E308, property = {}, value = {})
    @org.seasar.cms.ymir.extension.constraint.Required( {})
    public void setMonth(String month)
    {
        month_ = month;
    }


    public String getSubmitName()
    {
        return submitName_;
    }


    public String getSubmitValue()
    {
        return submitValue_;
    }


    public String getTargetPathname()
    {
        return targetPathname_;
    }


    public String getTitle()
    {
        return title_;
    }


    public void setTitle(String title)
    {
        title_ = title;
    }


    public String getYear()
    {
        return year_;
    }


    @org.seasar.cms.ymir.extension.constraint.Numeric(greaterEqual = 4.9E-324, greaterThan = 4.9E-324, integer = true, lessEqual = 1.7976931348623157E308, lessThan = 1.7976931348623157E308, property = {}, value = {})
    @org.seasar.cms.ymir.extension.constraint.Required( {})
    public void setYear(String year)
    {
        year_ = year;
    }


    public void _get()
    {

    }


    public void _post()
    {

    }
}
