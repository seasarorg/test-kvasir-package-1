package org.seasar.kvasir.page.ability.content.dao;

import java.util.Date;

import org.seasar.dao.annotation.tiger.Bean;
import org.seasar.dao.annotation.tiger.Id;
import org.seasar.dao.annotation.tiger.IdType;


@Bean(table = "contents")
public class ContentsDto
{
    private Integer id_;

    private Integer pageId_;

    private String variant_;

    private Integer revisionNumber_;

    private Date createDate_;

    private Date modifyDate_;

    private Boolean latest_;

    private Integer version_;

    private String mediaType_;

    private String encoding_;


    public ContentsDto()
    {
    }


    public ContentsDto(Integer pageId, String variant, Integer revisionNumber,
        Date createDate, Date modifyDate, Boolean latest, String mediaType,
        String encoding)
    {
        pageId_ = pageId;
        variant_ = variant;
        revisionNumber_ = revisionNumber;
        createDate_ = createDate;
        modifyDate_ = modifyDate;
        latest_ = latest;
        mediaType_ = mediaType;
        encoding_ = encoding;
    }


    public Date getCreateDate()
    {
        return createDate_;
    }


    public void setCreateDate(Date createDate)
    {
        createDate_ = createDate;
    }


    public String getEncoding()
    {
        return encoding_;
    }


    public void setEncoding(String encoding)
    {
        encoding_ = encoding;
    }


    public Integer getId()
    {
        return id_;
    }


    @Id(IdType.IDENTITY)
    public void setId(Integer id)
    {
        id_ = id;
    }


    public Boolean getLatest()
    {
        return latest_;
    }


    public void setLatest(Boolean latest)
    {
        latest_ = latest;
    }


    public String getMediaType()
    {
        return mediaType_;
    }


    public void setMediaType(String mediaType)
    {
        mediaType_ = mediaType;
    }


    public Date getModifyDate()
    {
        return modifyDate_;
    }


    public void setModifyDate(Date modifyDate)
    {
        modifyDate_ = modifyDate;
    }


    public Integer getPageId()
    {
        return pageId_;
    }


    public void setPageId(Integer pageId)
    {
        pageId_ = pageId;
    }


    public Integer getRevisionNumber()
    {
        return revisionNumber_;
    }


    public void setRevisionNumber(Integer revision)
    {
        revisionNumber_ = revision;
    }


    public String getVariant()
    {
        return variant_;
    }


    public void setVariant(String variant)
    {
        variant_ = variant;
    }


    public Integer getVersion()
    {
        return version_;
    }


    public void setVersion(Integer version)
    {
        version_ = version;
    }
}
