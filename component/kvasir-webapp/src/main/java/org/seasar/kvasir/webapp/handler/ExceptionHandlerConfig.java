package org.seasar.kvasir.webapp.handler;

import java.util.Enumeration;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface ExceptionHandlerConfig
{
    String getInitParameter(String name);


    Enumeration<String> getInitParameterNames();


    String getErrorHandlerName();
}
