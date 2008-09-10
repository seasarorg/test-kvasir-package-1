package org.seasar.kvasir.util.io.impl;

import java.io.IOException;
import java.io.InputStream;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.InputStreamFactory;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class ResourceInputStreamFactory extends AbstractInputStreamFactory
    implements InputStreamFactory
{
    private Resource        resource_;


    public ResourceInputStreamFactory(Resource resource)
    {
        resource_ = resource;
    }


    public InputStream getInputStream()
    {
        try {
            return resource_.getInputStream();
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
    }
}
