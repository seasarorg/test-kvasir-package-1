package org.seasar.kvasir.cms.kdiary.kdiary.dto;

public class EntryDto extends EntryDtoBase
{
    public EntryDto()
    {
    }


    public EntryDto(String URL, String name, String body, int commentCount,
        CommentDto[] comments, String date, String title)
    {
        super(URL, name, body, commentCount, comments, date,
            (commentCount > comments.length), title);
    }
}
