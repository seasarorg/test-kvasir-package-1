package org.seasar.kvasir.util.io.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.seasar.kvasir.util.io.IORuntimeException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class FileInputStreamFactory extends AbstractInputStreamFactory
{
    private File        file_;


    public FileInputStreamFactory(File file)
    {
        file_ = file;
    }


    public InputStream getInputStream()
    {
        try {
            return new FileInputStream(file_);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
    }
}
