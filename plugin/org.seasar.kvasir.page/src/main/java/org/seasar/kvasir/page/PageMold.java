package org.seasar.kvasir.page;

import java.util.Date;

import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.type.User;


/**
 * ページを作成するための情報を保持するためのクラスです。
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


    /**
     * このクラスのオブジェクトを構築します。
     */
    public PageMold()
    {
    }


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param name 作成するページの名前。
     */
    public PageMold(String name)
    {
        setName(name);
    }


    /**
     * 作成するページの名前を返します。
     * 
     * @return 名前。
     * @see Page#getName()
     */
    public String getName()
    {
        return name_;
    }


    /**
     * 作成するページの名前を設定します。
     * 
     * @param name 名前。
     * @return このオブジェクト。
     */
    public PageMold setName(String name)
    {
        name_ = name;

        return this;
    }


    /**
     * 作成するページの順序番号を返します。
     * 
     * @return 順序番号。
     * @see Page#getOrderNumber()
     */
    public Integer getOrderNumber()
    {
        return orderNumber_;
    }


    /**
     * 作成するページの順序番号を設定します。
     * <p>設定しなかった場合は親を同じくする他の子ページが持つ最大の順序番号に1を加えた番号になります。
     * 親が他の子ページを持たない場合は1になります。
     * </p>
     * 
     * @param orderNumber 順序番号。
     * @return このオブジェクト。
     */
    public PageMold setOrderNumber(int orderNumber)
    {
        orderNumber_ = new Integer(orderNumber);

        return this;
    }


    /**
     * 作成するページのページが可視状態になる日時を返します。
     * 
     * @return 可視状態になる日時。
     * @see Page#getRevealDate()
     */
    public Date getRevealDate()
    {
        return revealDate_;
    }


    /**
     * 作成するページのページが可視状態になる日時を設定します。
     * <p>設定しなかった場合は作成日時と同じ日時が設定されます。
     * </p>
     * 
     * @param revealDate 可視状態になる日時。
     * @return このオブジェクト。
     */
    public PageMold setRevealDate(Date revealDate)
    {
        revealDate_ = revealDate;

        return this;
    }


    /**
     * 作成するページのページが不可視状態になる日時を返します。
     * 
     * @return 不可視状態になる日時。
     * @see Page#getConcealDate()
     */
    public Date getConcealDate()
    {
        return concealDate_;
    }


    /**
     * 作成するページのページが不可視状態になる日時を設定します。
     * <p>設定しなかった場合は{@link Page#DATE_RAGNAROK}が設定されます。
     * </p>
     * 
     * @param concealDate 不可視状態になる日時。
     * @return このオブジェクト。
     */
    public PageMold setConcealDate(Date concealDate)
    {
        concealDate_ = concealDate;

        return this;
    }


    /**
     * 作成するページの作成日時を返します。
     * 
     * @return 作成日時。
     * @see Page#getCreateDate()
     */
    public Date getCreateDate()
    {
        return createDate_;
    }


    /**
     * 作成するページの作成日時を設定します。
     * <p>設定しなかった場合は現在日時が設定されます。
     * </p>
     * 
     * @param createDate 作成日時。
     * @return このオブジェクト。
     */
    public PageMold setCreateDate(Date createDate)
    {
        createDate_ = createDate;

        return this;
    }


    /**
     * 作成するページの変更日時を返します。
     * 
     * @return 変更日時。
     * @see Page#getModifyDate()
     */
    public Date getModifyDate()
    {
        return modifyDate_;
    }


    /**
     * 作成するページの変更日時を設定します。
     * <p>設定しなかった場合は現在日時が設定されます。
     * </p>
     * 
     * @param modifyDate 変更日時。
     * @return このオブジェクト。
     */
    public PageMold setModifyDate(Date modifyDate)
    {
        modifyDate_ = modifyDate;

        return this;
    }


    /**
     * 作成するページの所有者ユーザを返します。
     * 
     * @return 所有者ユーザ。
     * @see Page#getOwnerUser()
     */
    public User getOwnerUser()
    {
        return ownerUser_;
    }


    /**
     * 作成するページの所有者ユーザを設定します。
     * <p>設定しなかった場合はサイト管理者が設定されます。
     * </p>
     * 
     * @param ownerUser 所有者ユーザ。
     * @return このオブジェクト。
     */
    public PageMold setOwnerUser(User ownerUser)
    {
        ownerUser_ = ownerUser;

        return this;
    }


    /**
     * 作成するページのタイプを返します。
     * 
     * @return ページタイプ。
     * @see Page#getType()
     */
    public String getType()
    {
        return type_;
    }


    /**
     * 作成するページのタイプを設定します。
     * <p>設定しなかった場合は{@link Page#TYPE}が設定されます。
     * </p>
     * 
     * @param type ページタイプ。
     * @return このオブジェクト。
     */
    public PageMold setType(String type)
    {
        type_ = type;

        return this;
    }


    /**
     * 作成するページがNodeであるかどうかを返します。
     * 
     * @return Nodeであるかどうか。
     * @see Page#isNode()
     */
    public Boolean getNode()
    {
        return node_;
    }


    /**
     * 作成するページがNodeであるかどうかを設定します。
     * <p>設定しなかった場合はNodeではないと設定されます。
     * </p>
     * 
     * @param node Nodeであるかどうか。
     * @return このオブジェクト。
     */
    public PageMold setNode(boolean node)
    {
        node_ = Boolean.valueOf(node);

        return this;
    }


    /**
     * 作成するページを表示する際に生のファイルとして扱うかどうかを返します。
     * 
     * @return 表示する際に生のファイルとして扱うかどうか。
     * @see Page#isAsFile()
     */
    public Boolean getAsFile()
    {
        return asFile_;
    }


    /**
     * 作成するページを表示する際に生のファイルとして扱うかどうかを設定します。
     * <p>設定しなかった場合は生ファイルとして扱わない（＝埋め込みファイルとして扱う）ように設定されます。
     * </p>
     * 
     * @param asFile 表示する際に生のファイルとして扱うかどうか。
     * @return このオブジェクト。
     */
    public PageMold setAsFile(boolean asFile)
    {
        asFile_ = Boolean.valueOf(asFile);

        return this;
    }


    /**
     * 作成するページを一覧表示に含めるかを返します。
     * 
     * @return 一覧表示に含めるか。
     * @see Page#isListing()
     */
    public Boolean getListing()
    {
        return listing_;
    }


    /**
     * 作成するページを一覧表示に含めるかを設定します。
     * <p>設定しなかった場合は一覧表示に含めるよう設定されます。
     * </p>
     * 
     * @param listing 一覧表示に含めるか。
     * @return このオブジェクト。
     */
    public PageMold setListing(boolean listing)
    {
        listing_ = Boolean.valueOf(listing);

        return this;
    }


    /**
     * ページを作成した際に各{@link PageAbilityAlfr}によって行なわれるページ初期化処理を
     * スキップするかどうかを返します。
     * 
     * @return PageAbilityAlfrによるページ初期化処理をスキップするかどうか。
     */
    public boolean isOmitCreationProcessForAbilityAlfr()
    {
        return omitCreationProcessForAbilityAlfr_;
    }


    /**
     * ページを作成した際に各{@link PageAbilityAlfr}によって行なわれるページ初期化処理を
     * スキップするかどうかを設定します。
     * <p>設定しなかった場合は初期化処理をスキップしないように設定されます。
     * </p>
     * 
     * @param omitCreationProcessForAbilityAlfr PageAbilityAlfrによるページ初期化処理をスキップするかどうか。
     * @return このオブジェクト。
     */
    public PageMold setOmitCreationProcessForAbilityAlfr(
        boolean omitCreationProcessForAbilityAlfr)
    {
        omitCreationProcessForAbilityAlfr_ = omitCreationProcessForAbilityAlfr;
        return this;
    }
}
