package org.seasar.kvasir.page.gard;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このインスタンスの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface PageGardImporter
{
    Page imports(Page parent, String name, Resource dir)
        throws DuplicatePageException;


    void imports(Page page, Resource dir);
}
