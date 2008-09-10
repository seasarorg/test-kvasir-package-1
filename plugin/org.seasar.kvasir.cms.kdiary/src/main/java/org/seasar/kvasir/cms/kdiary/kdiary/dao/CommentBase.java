package org.seasar.kvasir.cms.kdiary.kdiary.dao;

import java.sql.Timestamp;

import org.seasar.dao.annotation.tiger.Id;
import org.seasar.dao.annotation.tiger.IdType;
import org.seasar.kvasir.page.ability.table.annotation.PageId;


abstract public class CommentBase
{
    protected String body_;

    protected String commentator_;

    protected Timestamp commenttime_;

    protected Integer id_;

    private String mail_;

    private int pageid_;


    public CommentBase()
    {
    }


    public CommentBase(String body, String commentator, Timestamp commenttime,
        String mail, int pageid)
    {
        body_ = body;
        commentator_ = commentator;
        commenttime_ = commenttime;
        mail_ = mail;
        pageid_ = pageid;
    }


    public String getBody()
    {
        return body_;
    }


    public void setBody(String body)
    {
        body_ = body;
    }


    public String getCommentator()
    {
        return commentator_;
    }


    public void setCommentator(String commentator)
    {
        commentator_ = commentator;
    }


    public Timestamp getCommenttime()
    {
        return commenttime_;
    }


    public void setCommenttime(Timestamp commenttime)
    {
        commenttime_ = commenttime;
    }


    public String getMail()
    {
        return mail_;
    }


    public void setMail(String mail)
    {
        mail_ = mail;
    }


    @PageId
    public int getPageid()
    {
        return pageid_;
    }


    public void setPageid(int pageid)
    {
        pageid_ = pageid;
    }


    @Id(IdType.IDENTITY)
    public Integer getId()
    {
        return id_;
    }


    public void setId(Integer id)
    {
        id_ = id;
    }
}
