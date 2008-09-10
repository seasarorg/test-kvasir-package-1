package org.seasar.kvasir.cms.kdiary.kdiary.dto;

public class EntryDtoBase
{
    protected String URL_;

    protected String name_;

    protected String body_;

    protected int commentCount_;

    protected org.seasar.kvasir.cms.kdiary.kdiary.dto.CommentDto[] comments_;

    protected String date_;

    protected boolean moreComments_;

    protected String title_;


    public EntryDtoBase()
    {
    }

    public EntryDtoBase(String URL, String name, String body, int commentCount, org.seasar.kvasir.cms.kdiary.kdiary.dto.CommentDto[] comments, String date, boolean moreComments, String title)
    {
        URL_ = URL;
        name_ = name;
        body_ = body;
        commentCount_ = commentCount;
        comments_ = comments;
        date_ = date;
        moreComments_ = moreComments;
        title_ = title;
    }

    public String getURL()
    {
        return URL_;
    }

    public void setURL(String URL)
    {
        URL_ = URL;
    }

    public String getName()
    {
        return name_;
    }

    public void setName(String name)
    {
        name_ = name;
    }

    public String getBody()
    {
        return body_;
    }

    public void setBody(String body)
    {
        body_ = body;
    }

    public int getCommentCount()
    {
        return commentCount_;
    }

    public void setCommentCount(int commentCount)
    {
        commentCount_ = commentCount;
    }

    public org.seasar.kvasir.cms.kdiary.kdiary.dto.CommentDto[] getComments()
    {
        return comments_;
    }

    public void setComments(org.seasar.kvasir.cms.kdiary.kdiary.dto.CommentDto[] comments)
    {
        comments_ = comments;
    }

    public String getDate()
    {
        return date_;
    }

    public void setDate(String date)
    {
        date_ = date;
    }

    public boolean isMoreComments()
    {
        return moreComments_;
    }

    public void setMoreComments(boolean moreComments)
    {
        moreComments_ = moreComments;
    }

    public String getTitle()
    {
        return title_;
    }

    public void setTitle(String title)
    {
        title_ = title;
    }
}
