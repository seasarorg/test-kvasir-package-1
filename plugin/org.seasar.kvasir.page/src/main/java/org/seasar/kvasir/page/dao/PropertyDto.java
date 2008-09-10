package org.seasar.kvasir.page.dao;

import org.seasar.dao.annotation.tiger.Bean;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 *
 * @author YOKOTA Takehiko
 */
@Bean(table = "property")
public class PropertyDto
{
    private Integer pageId_;

    private String name_;

    private String value_;


    public PropertyDto()
    {
        super();
    }


    public PropertyDto(Integer pageId, String name, String value)
    {
        pageId_ = pageId;
        name_ = name;
        value_ = value;
    }


    public Integer getPageId()
    {
        return pageId_;
    }


    public void setPageId(Integer pageId)
    {
        pageId_ = pageId;
    }


    public String getName()
    {
        return name_;
    }


    public void setName(String name)
    {
        name_ = name;
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
