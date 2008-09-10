package org.seasar.kvasir.system.container.descriptor;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Required;


public class Include
{
    private String path_;


    public String getPath()
    {
        return path_;
    }


    @Attribute
    @Required
    public void setPath(String path)
    {
        path_ = path;
    }
}
