package org.seasar.kvasir.cms.ymir;

import org.seasar.cms.ymir.Application;
import org.seasar.kvasir.util.io.Resource;


public interface YmirApplication
    extends Application
{
    String getClassesDirectory();


    Resource getWebappRootResource();
}
