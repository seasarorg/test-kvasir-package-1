package org.seasar.kvasir.cms.ymir.impl;

import javax.servlet.ServletConfig;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.cms.processor.impl.AbstractResourcePathResolver;
import org.seasar.kvasir.util.io.Resource;


public class YmirLocalPathResolver extends AbstractResourcePathResolver
{
    private Resource baseDir_;


    public YmirLocalPathResolver(Resource baseDir)
    {
        baseDir_ = baseDir;
    }


    public void init(Page gardRootPage, ServletConfig config)
    {
    }


    @Override
    protected Resource getBaseDirectory()
    {
        return baseDir_;
    }
}
