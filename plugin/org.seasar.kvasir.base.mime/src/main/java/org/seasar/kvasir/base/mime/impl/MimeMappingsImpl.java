package org.seasar.kvasir.base.mime.impl;

import java.util.Map;
import java.util.TreeMap;

import org.seasar.kvasir.base.mime.MimeMapping;
import org.seasar.kvasir.base.mime.MimeMappings;


public class MimeMappingsImpl
    implements MimeMappings
{
    private MimeMapping[] mappings_;

    private Map<String, MimeMapping> byExtensionMap_ = new TreeMap<String, MimeMapping>();

    private Map<String, MimeMapping> byMimeTypeMap_ = new TreeMap<String, MimeMapping>();


    public MimeMappingsImpl()
    {
    }


    public MimeMappingsImpl(MimeMapping[] mappings)
    {
        mappings_ = mappings;
        for (int i = 0; i < mappings.length; i++) {
            byExtensionMap_.put(mappings[i].getExtension().toLowerCase(),
                mappings[i]);
            byMimeTypeMap_.put(mappings[i].getMimeType().toLowerCase(),
                mappings[i]);
        }
    }


    public String getExtension(String mimeType)
    {
        MimeMapping mapping = byMimeTypeMap_.get(mimeType.toLowerCase());
        if (mapping != null) {
            return mapping.getExtension();
        } else {
            return null;
        }
    }


    public MimeMapping[] getMappings()
    {
        return mappings_;
    }


    public String getMimeType(String file)
    {
        int dot = file.lastIndexOf('.');
        if (dot < 0) {
            return null;
        }
        MimeMapping mapping = byExtensionMap_.get(file.substring(dot + 1)
            .toLowerCase());
        if (mapping != null) {
            return mapping.getMimeType();
        } else {
            return null;
        }
    }
}
