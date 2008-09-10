package org.seasar.kvasir.util.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありませんが、
 * このインタフェースの利用クラスによっては特に読み出しに関して
 * スレッドセーフであることが条件とされるケースがありますので、
 * 極力スレッドセーフな実装にしておくことが望ましいでしょう。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface Resource
{
    boolean delete();

    boolean exists();

    Resource getChildResource(String child);

    InputStream getInputStream()
        throws ResourceNotFoundException;

    long getLastModifiedTime();

    String getName();

    OutputStream getOutputStream()
        throws ResourceNotFoundException;

    Resource getParentResource();

    long getSize();

    URL getURL();

    URL[] getURLs();

    boolean isDirectory();

    boolean mkdir();

    boolean mkdirs();

    String[] list();

    Resource[] listResources();

    boolean renameTo(Resource dest);

    boolean setLastModifiedTime(long time);

    File toFile();

    File[] toFiles();
}
