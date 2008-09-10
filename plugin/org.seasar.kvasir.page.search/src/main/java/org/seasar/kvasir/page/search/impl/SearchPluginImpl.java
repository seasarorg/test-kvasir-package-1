package org.seasar.kvasir.page.search.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.kvasir.base.KvasirUtils;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.page.search.SearchPlugin;
import org.seasar.kvasir.page.search.SearchSystem;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class SearchPluginImpl extends AbstractPlugin<EmptySettings>
    implements SearchPlugin
{
    private Map<Object, SearchSystem> searchSystemMap_;

    private SearchSystem[] searchSystems_;


    /*
     * constructors
     */

    public SearchPluginImpl()
    {
    }


    /*
     * SearchPlugin
     */

    public SearchSystem getDefaultSearchSystem()
    {
        return getSearchSystem(DEFAULT_SEARCHSYSTEMID);
    }


    public SearchSystem getSearchSystem(Object key)
    {
        return (SearchSystem)searchSystemMap_.get(key);
    }


    public SearchSystem[] getSearchSystems()
    {
        return searchSystems_;
    }


    /*
     * AbstractPlugin
     */

    protected boolean doStart()
    {
        searchSystemMap_ = new LinkedHashMap<Object, SearchSystem>();
        searchSystems_ = getExtensionComponents(SearchSystem.class);
        KvasirUtils.start(searchSystems_);
        for (int i = 0; i < searchSystems_.length; i++) {
            searchSystemMap_.put(searchSystems_[i].getClass(),
                searchSystems_[i]);
            searchSystemMap_.put(searchSystems_[i].getId(), searchSystems_[i]);
        }

        return true;
    }


    protected void doStop()
    {
        KvasirUtils.stop(searchSystems_);
        searchSystemMap_ = null;
        searchSystems_ = null;
    }

    /*
     * for framework
     */
}
