package org.seasar.kvasir.cms.kdiary.kdiary.dto;

public class CommonDto extends CommonDtoBase
{
    public CommonDto()
    {
    }


    public CommonDto(String author, String commentAuthor, String cssURL,
        String footer, String header, String mailAddress, boolean showComment,
        String title)
    {
        super(author, commentAuthor, cssURL, footer, header, mailAddress,
            showComment, title);
    }
}
