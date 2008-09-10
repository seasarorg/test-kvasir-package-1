package org.seasar.kvasir.page.dao;

import org.seasar.dao.annotation.tiger.Bean;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 *
 * @author YOKOTA Takehiko
 */
@Bean(table = "properties")
public class PropertiesDto
{
    private Integer pageId_;

    private String variant_;

    private String body_;


    public PropertiesDto()
    {
    }


    public PropertiesDto(Integer pageId, String variant, String body)
    {
        pageId_ = pageId;
        variant_ = variant;
        body_ = body;
    }


    public Integer getPageId()
    {
        return pageId_;
    }


    public void setPageId(Integer pageId)
    {
        pageId_ = pageId;
    }


    public String getVariant()
    {
        return variant_;
    }


    public void setVariant(String variant)
    {
        variant_ = variant;
    }


    public String getBody()
    {
        return body_;
    }


    public void setBody(String body)
    {
        body_ = body;
    }
}
