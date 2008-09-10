package org.seasar.kvasir.system.container.descriptor;

import net.skirnir.xom.annotation.Content;


public class Arg
{
    private String content_;


    public String getContent()
    {
        return content_;
    }


    @Content
    public void setContent(String content)
    {
        content_ = content;
    }
}
