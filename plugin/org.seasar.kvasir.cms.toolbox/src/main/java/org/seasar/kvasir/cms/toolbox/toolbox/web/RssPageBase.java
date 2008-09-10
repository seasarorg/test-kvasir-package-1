package org.seasar.kvasir.cms.toolbox.toolbox.web;

public class RssPageBase extends org.seasar.kvasir.cms.ymir.web.PageBase
{
    protected String basePathname_;

    protected String description_;

    protected String lang_;

    protected org.seasar.kvasir.cms.toolbox.toolbox.dto.RdfEntryDto[] rdfEntries_;

    protected String rdfURL_;

    protected String title_;


    public String getBasePathname()
    {
        return basePathname_;
    }

    public String getDescription()
    {
        return description_;
    }

    public String getLang()
    {
        return lang_;
    }

    public org.seasar.kvasir.cms.toolbox.toolbox.dto.RdfEntryDto[] getRdfEntries()
    {
        return rdfEntries_;
    }

    public String getRdfURL()
    {
        return rdfURL_;
    }

    public String getTitle()
    {
        return title_;
    }

    public void _get()
    {

    }

    public void _render()
    {

    }
}
