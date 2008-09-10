package org.seasar.kvasir.base.mime;

public interface MimeMappings
{
    MimeMapping[] getMappings();


    String getMimeType(String file);


    String getExtension(String mimeType);
}
