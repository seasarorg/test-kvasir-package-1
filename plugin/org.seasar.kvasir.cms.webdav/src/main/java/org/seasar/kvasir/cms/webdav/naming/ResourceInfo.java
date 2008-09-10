package org.seasar.kvasir.cms.webdav.naming;

import java.io.IOException;
import java.io.InputStream;


public interface ResourceInfo
{
    long getSize();


    long getLastModifiedTime();


    InputStream getInputStream()
        throws IOException;


    long getCreationTime();
}
