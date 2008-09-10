package org.seasar.kvasir.page.gard;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface PageGardInstaller
{
    Page install(Page parent, String name, String gardId)
        throws DuplicatePageException;


    boolean install(Page dest, String gardId)
        throws DuplicatePageException;


    void upgrade(Page page)
        throws DuplicatePageException;


    void uninstall(Page page);


    Page copy(Page parent, String name, Page source, boolean adjustName)
        throws DuplicatePageException, LoopDetectedException;
}
