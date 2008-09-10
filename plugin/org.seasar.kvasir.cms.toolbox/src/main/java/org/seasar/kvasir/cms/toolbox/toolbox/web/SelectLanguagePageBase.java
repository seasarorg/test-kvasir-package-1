package org.seasar.kvasir.cms.toolbox.toolbox.web;

public class SelectLanguagePageBase extends org.seasar.kvasir.cms.ymir.web.PageBase
{
    protected String here_;

    protected String language_;


    public String getHere()
    {
        return here_;
    }

    public void setHere(String here)
    {
        here_ = here;
    }

    public String getLanguage()
    {
        return language_;
    }

    public void setLanguage(String language)
    {
        language_ = language;
    }

    public String _post()
    {
        return "redirect:/";
    }

    public void _render()
    {

    }
}
