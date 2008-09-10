package org.seasar.kvasir.cms.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;


@Bean("heim")
public class HeimElement
{
    private int id_;

    private List<String> siteList_ = new ArrayList<String>();


    public int getId()
    {
        return id_;
    }


    @Attribute
    @Required
    public void setId(int id)
    {
        id_ = id;
    }


    public String[] getSites()
    {
        return siteList_.toArray(new String[0]);
    }


    @Child
    public void addSite(String site)
    {
        siteList_.add(site);
    }


    public void setSites(String[] sites)
    {
        siteList_.clear();
        siteList_.addAll(Arrays.asList(sites));
    }
}
