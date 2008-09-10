package org.seasar.kvasir.webapp.extension;

import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;

public class InitParam
{
    private String paramName_;

    private String paramValue_;


    public String getParamName()
    {
        return paramName_;
    }


    @Child
    @Required
    public void setParamName(String paramName)
    {
        paramName_ = paramName;
    }


    public String getParamValue()
    {
        return paramValue_;
    }


    @Child
    @Required
    public void setParamValue(String paramValue)
    {
        paramValue_ = paramValue;
    }
}
