package org.seasar.kvasir.page.gard.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.extension.PageGardElement;
import org.seasar.kvasir.page.gard.PageGardUtils;


public class CompatibilityPageGardElement extends PageGardElement
{
    private String oldId_;


    public void setOldId(String oldName)
    {
        oldId_ = oldName;
    }


    @Override
    public boolean preInstallProcess(Page gardRootPage)
    {
        if (oldId_ == null) {
            throw new IllegalStateException("Specify oldId.");
        }

        return !PageGardUtils.isInstalled(oldId_, gardRootPage.getRoot());
    }
}
