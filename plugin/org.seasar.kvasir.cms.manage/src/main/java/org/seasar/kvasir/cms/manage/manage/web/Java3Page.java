package org.seasar.kvasir.cms.manage.manage.web;

import java.util.Locale;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.cms.java.Base;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.search.ParseException;
import org.seasar.kvasir.page.search.SearchPlugin;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.SearchRequest;
import org.seasar.kvasir.page.search.SearchSystem;


public class Java3Page extends Base
{
    public void execute()
        throws Exception
    {
        try {
            SearchPlugin plugin = (SearchPlugin)Asgard.getKvasir()
                .getPluginAlfr().getPlugin(SearchPlugin.class);
            SearchSystem system = plugin.getDefaultSearchSystem();
            SearchRequest searchRequest = system.newSearchRequest();

            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setQueryString("ymir");
            searchQuery.setMaxLength(1000);
            searchQuery.setLocale(Locale.getDefault());
            searchQuery.setHeimId(new Integer(PathId.HEIM_MIDGARD));

            long time = System.currentTimeMillis();
            System.out.println("********************START");

            try {
                System.out.println("**********TOTAL="
                    + searchRequest.getResultsCount(searchQuery));
                searchRequest.search(searchQuery, 0, 20);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            System.out.println("********************SECOND="
                + ((System.currentTimeMillis() - time) / 1000.));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
