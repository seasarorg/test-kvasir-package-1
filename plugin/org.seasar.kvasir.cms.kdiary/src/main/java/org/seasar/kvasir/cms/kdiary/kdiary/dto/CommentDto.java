package org.seasar.kvasir.cms.kdiary.kdiary.dto;

import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.LIMIT_INFINITY;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.SUFFIX_CONTINUING;

import java.sql.Timestamp;
import java.text.DateFormat;


public class CommentDto extends CommentDtoBase
{
    private DateFormat dateFormat_;

    private int        bodyLength_;


    public CommentDto()
    {
    }


    public CommentDto(String anchor, String body, String commentator,
        Timestamp commenttime)
    {
        super(anchor, body, commentator, commenttime, null);
    }


    public void setDateFormat(DateFormat dateFormat)
    {
        dateFormat_ = dateFormat;
    }


    public String getCommenttimeString()
    {
        return dateFormat_.format(getCommenttime());
    }


    public void setBodyLength(int bodyLength)
    {
        bodyLength_ = bodyLength;
    }


    @Override
    public String getBody()
    {
        if (bodyLength_ == LIMIT_INFINITY || bodyLength_ >= body_.length()) {
            return body_;
        } else {
            return body_.substring(0, bodyLength_) + SUFFIX_CONTINUING;
        }
    }
}
