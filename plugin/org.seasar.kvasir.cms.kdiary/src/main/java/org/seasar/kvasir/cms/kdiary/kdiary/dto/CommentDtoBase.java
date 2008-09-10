package org.seasar.kvasir.cms.kdiary.kdiary.dto;

public class CommentDtoBase
{
    protected String anchor_;

    protected String body_;

    protected String commentator_;

    protected java.sql.Timestamp commenttime_;

    protected String commenttimeString_;


    public CommentDtoBase()
    {
    }

    public CommentDtoBase(String anchor, String body, String commentator, java.sql.Timestamp commenttime, String commenttimeString)
    {
        anchor_ = anchor;
        body_ = body;
        commentator_ = commentator;
        commenttime_ = commenttime;
        commenttimeString_ = commenttimeString;
    }

    public String getAnchor()
    {
        return anchor_;
    }

    public void setAnchor(String anchor)
    {
        anchor_ = anchor;
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

    public java.sql.Timestamp getCommenttime()
    {
        return commenttime_;
    }

    public void setCommenttime(java.sql.Timestamp commenttime)
    {
        commenttime_ = commenttime;
    }

    public void setCommenttimeString(String commenttimeString)
    {
        commenttimeString_ = commenttimeString;
    }
}
