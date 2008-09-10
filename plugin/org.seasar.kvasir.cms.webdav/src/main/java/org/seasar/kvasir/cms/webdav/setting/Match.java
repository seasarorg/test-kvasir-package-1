package org.seasar.kvasir.cms.webdav.setting;

import java.util.regex.Pattern;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Required;


public class Match
{
    private String path_;

    private boolean regex_;

    private String charset_;

    private Boolean embedded_;

    private String internalCharset_;

    private Boolean keepConcealedWhenCreated_;


    public boolean isMatched(String path)
    {
        if (regex_) {
            return Pattern.compile(path_).matcher(path).find();
        } else {
            return (path.equals(path_) || path.startsWith(path_)
                && path.charAt(path_.length()) == '/');
        }
    }


    public String getPath()
    {
        return path_;
    }


    @Attribute
    @Required
    public void setPath(String pathname)
    {
        path_ = pathname;
    }


    public boolean isRegex()
    {
        return regex_;
    }


    @Child
    @Default("false")
    public void setRegex(boolean regex)
    {
        regex_ = regex;
    }


    public String getCharset()
    {
        return charset_;
    }


    @Child
    public void setCharset(String charset)
    {
        charset_ = charset;
    }


    public Boolean getEmbedded()
    {
        return embedded_;
    }


    @Child
    @Default("false")
    public void setEmbedded(Boolean embedded)
    {
        embedded_ = embedded;
    }


    public String getInternalCharset()
    {
        return internalCharset_;
    }


    @Child
    public void setInternalCharset(String internalCharset)
    {
        internalCharset_ = internalCharset;
    }


    public Boolean getKeepConcealedWhenCreated()
    {
        return keepConcealedWhenCreated_;
    }


    @Child
    @Default("false")
    public void setKeepConcealedWhenCreated(Boolean keepConcealedWhenCreated)
    {
        keepConcealedWhenCreated_ = keepConcealedWhenCreated;
    }
}
