package org.seasar.kvasir.base.webapp;

import java.io.IOException;
import java.io.InputStream;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface Content
{
    String getContentType();

    long getLastModifiedTime();

    InputStream getInputStream()
        throws IOException;
}