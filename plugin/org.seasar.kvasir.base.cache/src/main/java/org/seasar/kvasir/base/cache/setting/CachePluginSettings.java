package org.seasar.kvasir.base.cache.setting;

import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Default;


public class CachePluginSettings
{
    private int maxAge_ = 20;


    public int getMaxAge()
    {
        return maxAge_;
    }


    @Child
    @Default("20")
    public void setMaxAge(int maxAge)
    {
        maxAge_ = maxAge;
    }
}
