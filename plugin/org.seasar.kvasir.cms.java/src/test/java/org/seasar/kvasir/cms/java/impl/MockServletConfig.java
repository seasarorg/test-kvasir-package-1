package org.seasar.kvasir.cms.java.impl;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class MockServletConfig
    implements ServletConfig
{
    public String getServletName()
    {
        return null;
    }


    public ServletContext getServletContext()
    {
        return null;
    }


    public String getInitParameter(String arg0)
    {
        return "UTF-8";
    }


    @SuppressWarnings("unchecked")
    public Enumeration getInitParameterNames()
    {
        return null;
    }
}
