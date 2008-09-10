package org.seasar.kvasir.page;

import java.util.Date;

import org.seasar.kvasir.page.type.User;


/**
 *
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageMold
{
    private String type_;

    private String name_;

    private Integer orderNumber_;

    private Date createDate_;

    private Date modifyDate_;

    private Date revealDate_;

    private Date concealDate_;

    private User ownerUser_;

    private Boolean node_;

    private Boolean asFile_;

    private Boolean listing_;

    private boolean omitCreationProcessForAbilityAlfr_ = false;


    public PageMold()
    {
    }


    public PageMold(String name)
    {
        setName(name);
    }


    public String getName()
    {
        return name_;
    }


    public PageMold setName(String name)
    {
        name_ = name;

        return this;
    }


    public Integer getOrderNumber()
    {
        return orderNumber_;
    }


    public PageMold setOrderNumber(int orderNumber)
    {
        orderNumber_ = new Integer(orderNumber);

        return this;
    }


    public Date getRevealDate()
    {
        return revealDate_;
    }


    public PageMold setRevealDate(Date revealDate)
    {
        revealDate_ = revealDate;

        return this;
    }


    public Date getConcealDate()
    {
        return concealDate_;
    }


    public PageMold setConcealDate(Date concealDate)
    {
        concealDate_ = concealDate;

        return this;
    }


    public Date getCreateDate()
    {
        return createDate_;
    }


    public PageMold setCreateDate(Date createDate)
    {
        createDate_ = createDate;

        return this;
    }


    public Date getModifyDate()
    {
        return modifyDate_;
    }


    public PageMold setModifyDate(Date modifyDate)
    {
        modifyDate_ = modifyDate;

        return this;
    }


    public User getOwnerUser()
    {
        return ownerUser_;
    }


    public PageMold setOwnerUser(User ownerUser)
    {
        ownerUser_ = ownerUser;

        return this;
    }


    public String getType()
    {
        return type_;
    }


    public PageMold setType(String type)
    {
        type_ = type;

        return this;
    }


    public Boolean getNode()
    {
        return node_;
    }


    public PageMold setNode(boolean node)
    {
        node_ = Boolean.valueOf(node);

        return this;
    }


    public Boolean getAsFile()
    {
        return asFile_;
    }


    public PageMold setAsFile(boolean asFile)
    {
        asFile_ = Boolean.valueOf(asFile);

        return this;
    }


    public Boolean getListing()
    {
        return listing_;
    }


    public PageMold setListing(boolean listing)
    {
        listing_ = Boolean.valueOf(listing);

        return this;
    }


    public boolean isOmitCreationProcessForAbilityAlfr()
    {
        return omitCreationProcessForAbilityAlfr_;
    }


    public PageMold setOmitCreationProcessForAbilityAlfr(
        boolean omitCreationProcessForAbilityAlfr)
    {
        omitCreationProcessForAbilityAlfr_ = omitCreationProcessForAbilityAlfr;
        return this;
    }
}
