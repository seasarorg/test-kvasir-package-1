package org.seasar.kvasir.cms.kdiary.kdiary.dao;

import java.sql.Timestamp;

import org.seasar.cms.ymir.extension.beantable.Managed;


@Managed
public class Comment extends CommentBase
{
    public Comment()
    {
    }


    public Comment(String body, String commentator, Timestamp commenttime,
        String mail, int pageid)
    {
        super(body, commentator, commenttime, mail, pageid);
    }
}
