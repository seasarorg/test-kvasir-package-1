package org.seasar.kvasir.cms;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public interface RequestSnapshot
{
    ServletContext getServletConext();


    HttpServletRequest getHttpServletRequest();
}
