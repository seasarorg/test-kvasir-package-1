package org.seasar.kvasir.util.io.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class ByteArrayInputStreamFactory extends AbstractInputStreamFactory
{
    private byte[] bytes_;


    public ByteArrayInputStreamFactory(byte[] bytes)
    {
        bytes_ = bytes;
    }


    public InputStream getInputStream()
    {
        return new ByteArrayInputStream(bytes_);
    }
}
