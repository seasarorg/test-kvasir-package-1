package org.seasar.kvasir.page.dao;

import java.util.Date;

import org.seasar.cms.beantable.annotation.VersionNo;
import org.seasar.dao.annotation.tiger.Bean;
import org.seasar.dao.annotation.tiger.Column;
import org.seasar.dao.annotation.tiger.Id;
import org.seasar.dao.annotation.tiger.IdType;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@Bean(table = "page", noPersistentProperty = { "parentPathname", "pathname" })
public class PageDto
{
    public static final String PARENT_DUMMY = "_";

    private Integer id_;

    private String type_;

    private Integer heimId_;

    private Integer lordId_;

    private String rawParentPathname_;

    private String name_;

    private Integer orderNumber_;

    private Date createDate_;

    private Date modifyDate_;

    private Date revealDate_;

    private Date concealDate_;

    private Integer ownerUserId_;

    private Boolean node_;

    private Boolean asFile_;

    private Boolean listing_;

    private Integer version_;

    private String pathname_;


    public Integer getId()
    {
        return id_;
    }


    @Id(IdType.IDENTITY)
    public void setId(Integer id)
    {
        id_ = id;
    }


    public String getType()
    {
        return type_;
    }


    public void setType(String type)
    {
        type_ = type;
    }


    public Integer getHeimId()
    {
        return heimId_;
    }


    public void setHeimId(Integer heimId)
    {
        heimId_ = heimId;
    }


    public Integer getLordId()
    {
        return lordId_;
    }


    public void setLordId(Integer lordId)
    {
        lordId_ = lordId;
    }


    @Column("PARENTPATHNAME")
    public String getRawParentPathname()
    {
        return rawParentPathname_;
    }


    public void setRawParentPathname(String rawParentPathname)
    {
        rawParentPathname_ = rawParentPathname;
    }


    public String getParentPathname()
    {
        if (PARENT_DUMMY.equals(rawParentPathname_)) {
            return null;
        } else {
            return rawParentPathname_;
        }
    }


    public String getPathname()
    {
        if (pathname_ == null) {
            String parentPathname = getParentPathname();
            if (parentPathname == null) {
                pathname_ = "";
            } else {
                pathname_ = parentPathname + "/" + name_;
            }
        }
        return pathname_;
    }


    public void setParentPathname(String parentPathname)
    {
        if (parentPathname == null) {
            rawParentPathname_ = PARENT_DUMMY;
        } else {
            rawParentPathname_ = parentPathname;
        }
    }


    public String getName()
    {
        return name_;
    }


    public void setName(String name)
    {
        name_ = name;
    }


    public Integer getOrderNumber()
    {
        return orderNumber_;
    }


    public void setOrderNumber(Integer orderNumber)
    {
        orderNumber_ = orderNumber;
    }


    public Date getCreateDate()
    {
        return createDate_;
    }


    public void setCreateDate(Date createDate)
    {
        createDate_ = createDate;
    }


    public Date getModifyDate()
    {
        return modifyDate_;
    }


    public void setModifyDate(Date modifyDate)
    {
        modifyDate_ = modifyDate;
    }


    public Date getRevealDate()
    {
        return revealDate_;
    }


    public void setRevealDate(Date revealDate)
    {
        revealDate_ = revealDate;
    }


    public Date getConcealDate()
    {
        return concealDate_;
    }


    public void setConcealDate(Date concealDate)
    {
        concealDate_ = concealDate;
    }


    public Integer getOwnerUserId()
    {
        return ownerUserId_;
    }


    public void setOwnerUserId(Integer ownerUserId)
    {
        ownerUserId_ = ownerUserId;
    }


    public Boolean getNode()
    {
        return node_;
    }


    public void setNode(Boolean node)
    {
        node_ = node;
    }


    public Boolean getAsFile()
    {
        return asFile_;
    }


    public void setAsFile(Boolean asFile)
    {
        asFile_ = asFile;
    }


    public Boolean getListing()
    {
        return listing_;
    }


    public void setListing(Boolean listing)
    {
        listing_ = listing;
    }


    @VersionNo
    public Integer getVersion()
    {
        return version_;
    }


    public void setVersion(Integer version)
    {
        version_ = version;
    }
}
