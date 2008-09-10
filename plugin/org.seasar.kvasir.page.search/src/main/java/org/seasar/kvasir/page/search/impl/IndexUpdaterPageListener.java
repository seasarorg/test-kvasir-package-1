package org.seasar.kvasir.page.search.impl;

import org.seasar.kvasir.page.DeletedPageEvent;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageEvent;
import org.seasar.kvasir.page.PageListener;
import org.seasar.kvasir.page.UpdatedPageEvent;
import org.seasar.kvasir.page.search.SearchPlugin;
import org.seasar.kvasir.page.search.SearchSystem;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class IndexUpdaterPageListener
    implements PageListener
{
    private SearchPlugin plugin_;


    public void notifyChanged(PageEvent event)
    {
        SearchSystem[] systems = plugin_.getSearchSystems();
        switch (event.getType()) {
        case PageEvent.UPDATED:
            UpdatedPageEvent updatedE = (UpdatedPageEvent)event;
            Page[] pages = updatedE.getPages();
            for (int i = 0; i < systems.length; i++) {
                systems[i].addToIndex(pages);
            }
            break;

        case PageEvent.DELETED:
            DeletedPageEvent deletedE = (DeletedPageEvent)event;
            int id = deletedE.getId();
            for (int i = 0; i < systems.length; i++) {
                systems[i].removeFromIndex(new int[] { id });
            }
            break;

        }
    }


    /*
     * for framework
     */

    public void setSearchPlugin(SearchPlugin plugin)
    {
        plugin_ = plugin;
    }
}
