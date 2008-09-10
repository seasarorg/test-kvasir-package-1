package org.seasar.kvasir.cms.webdav.setting;

import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Required;


public class ContentExtension
{
    private String name_;

    private String mimeType_;

    private String charset_;

    private boolean embedded_;


    public String getCharset()
    {
        return charset_;
    }


    @Child
    public void setCharset(String charset)
    {
        charset_ = charset;
    }


    public boolean isEmbedded()
    {
        return embedded_;
    }


    @Child
    @Default("false")
    public void setEmbedded(boolean embed)
    {
        embedded_ = embed;
    }


    public String getMimeType()
    {
        return mimeType_;
    }


    @Child
    public void setMimeType(String mimeType)
    {
        mimeType_ = mimeType;
    }


    public String getName()
    {
        return name_;
    }


    @Child
    @Required
    public void setName(String name)
    {
        name_ = name;
    }
}
