package org.seasar.kvasir.cms.toolbox.toolbox.dto;

public class EntryDto extends EntryDtoBase
{
    public EntryDto()
    {
    }

    public EntryDto(String URL, String date, String description, String subject, String title)
    {
        super(URL, date, description, subject, title);
    }
}
