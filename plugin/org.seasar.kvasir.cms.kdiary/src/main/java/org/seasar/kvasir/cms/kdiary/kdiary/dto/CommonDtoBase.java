package org.seasar.kvasir.cms.kdiary.kdiary.dto;

public class CommonDtoBase
{
    protected String author_;

    protected String commentAnchor_;

    protected String cssURL_;

    protected String footer_;

    protected String header_;

    protected String mailAddress_;

    protected boolean showComment_;

    protected String title_;


    public CommonDtoBase()
    {
    }

    public CommonDtoBase(String author, String commentAnchor, String cssURL, String footer, String header, String mailAddress, boolean showComment, String title)
    {
        author_ = author;
        commentAnchor_ = commentAnchor;
        cssURL_ = cssURL;
        footer_ = footer;
        header_ = header;
        mailAddress_ = mailAddress;
        showComment_ = showComment;
        title_ = title;
    }

    public String getAuthor()
    {
        return author_;
    }

    public void setAuthor(String author)
    {
        author_ = author;
    }

    public String getCommentAnchor()
    {
        return commentAnchor_;
    }

    public void setCommentAnchor(String commentAnchor)
    {
        commentAnchor_ = commentAnchor;
    }

    public String getCssURL()
    {
        return cssURL_;
    }

    public void setCssURL(String cssURL)
    {
        cssURL_ = cssURL;
    }

    public String getFooter()
    {
        return footer_;
    }

    public void setFooter(String footer)
    {
        footer_ = footer;
    }

    public String getHeader()
    {
        return header_;
    }

    public void setHeader(String header)
    {
        header_ = header;
    }

    public String getMailAddress()
    {
        return mailAddress_;
    }

    public void setMailAddress(String mailAddress)
    {
        mailAddress_ = mailAddress;
    }

    public boolean isShowComment()
    {
        return showComment_;
    }

    public void setShowComment(boolean showComment)
    {
        showComment_ = showComment;
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
