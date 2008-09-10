package org.seasar.kvasir.maven.plugin;

import java.io.File;


public class MojoUtils
{
    protected MojoUtils()
    {
    }


    public static File getAsFile(File baseFile, String path, String defaultPath)
    {
        return new File(baseFile, (path != null ? path : defaultPath));
    }


    public static File getAsFile(String basePath, String path,
        String defaultPath)
    {
        return new File(basePath + "/" + (path != null ? path : defaultPath));
    }
}
