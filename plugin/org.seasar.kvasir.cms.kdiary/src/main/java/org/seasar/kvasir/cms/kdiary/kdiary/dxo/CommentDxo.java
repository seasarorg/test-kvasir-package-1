package org.seasar.kvasir.cms.kdiary.kdiary.dxo;

import java.util.List;
import org.seasar.kvasir.cms.kdiary.kdiary.dao.Comment;
import org.seasar.kvasir.cms.kdiary.kdiary.dto.CommentDto;


public interface CommentDxo
{
    CommentDto convert(Comment comment);


    Comment convert(CommentDto commentDto);


    void convert(CommentDto src, Comment dest);


    void convert(Comment src, CommentDto dest);


    CommentDto[] convert(Comment[] comments);


    CommentDto[] convert(List<Comment> list);


    Comment[] convert(CommentDto[] commentDtos);


    org.seasar.kvasir.cms.kdiary.kdiary.dao.Comment convert(
        org.seasar.kvasir.cms.kdiary.kdiary.web.DayPage dayPage);
}
