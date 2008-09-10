package org.seasar.kvasir.cms.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.skirnir.xom.annotation.Child;


public class CmsPluginSettings
{
    private List<HeimElement> heimList_ = new ArrayList<HeimElement>();


    public HeimElement[] getHeims()
    {
        return heimList_.toArray(new HeimElement[0]);
    }


    @Child
    public void addHeim(HeimElement heim)
    {
        heimList_.add(heim);
    }


    public void setHeims(HeimElement[] heims)
    {
        heimList_.clear();
        heimList_.addAll(Arrays.asList(heims));
    }
}
