package org.seasar.kvasir.base.cache.setting;

import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;


@Bean("cache")
public class CacheElement
{
    private String id_;

    private Integer maxSize_;

    private Integer maxAge_;


    public Integer getMaxSize()
    {
        return maxSize_;
    }


    public String getId()
    {
        return id_;
    }


    @Child
    public void setId(String id)
    {
        id_ = id;
    }


    @Child
    public void setMaxSize(Integer maxSize)
    {
        maxSize_ = maxSize;
    }


    public Integer getMaxAge()
    {
        return maxAge_;
    }


    @Child
    public void setMaxAge(Integer maxAge)
    {
        maxAge_ = maxAge;
    }
}
