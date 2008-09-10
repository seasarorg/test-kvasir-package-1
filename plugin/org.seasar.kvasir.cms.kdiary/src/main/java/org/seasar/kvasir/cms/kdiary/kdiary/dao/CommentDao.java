package org.seasar.kvasir.cms.kdiary.kdiary.dao;

import org.seasar.dao.annotation.tiger.Query;
import org.seasar.dao.annotation.tiger.S2Dao;


@S2Dao(bean = Comment.class)
public interface CommentDao
{
    Comment[] selectAll();


    int insert(Comment comment);


    int update(Comment comment);


    int delete(Comment comment);


    @Query("pageid=? ORDER BY id")
    Comment[] selectAllByPageId(int pageId);
}
