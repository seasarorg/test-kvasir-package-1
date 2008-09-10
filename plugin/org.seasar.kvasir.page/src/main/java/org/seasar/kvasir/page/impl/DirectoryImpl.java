package org.seasar.kvasir.page.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageWrapper;
import org.seasar.kvasir.page.type.Directory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class DirectoryImpl extends PageWrapper
    implements Directory
{
    public DirectoryImpl(Page page)
    {
        super(page);
    }
}
