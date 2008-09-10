package org.seasar.kvasir.cms.toolbox.toolbox.web;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.toolbox.toolbox.dto.SearchResultDto;
import org.seasar.kvasir.cms.toolbox.toolbox.dto.SearchResultIndicatorDto;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.search.ParseException;
import org.seasar.kvasir.page.search.SearchPlugin;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.SearchRequest;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.page.search.SearchSystem;
import org.seasar.kvasir.page.type.Directory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class SearchPage
{
    private static final String ATTR_DEFAULTSEARCHSYSTEM = "searchSystem.default";

    private static final String ATTR_SEARCHREQUEST = "searchSystem.request";

    private static final int QUERY_MAXLENGTH = 50;

    private static final int QUERY_LENGTH = 10;

    /*
     * for framework
     */

    private Plugin<?> plugin_;

    private PagePlugin pagePlugin_;

    private AuthPlugin authPlugin_;

    private SearchPlugin searchPlugin_;

    private PageRequest pageRequest_;

    private HttpServletRequest request_;

    /*
     * for presentation tier
     */

    private SearchResultIndicatorDto indicator_;

    private int offset_ = SearchQuery.OFFSET_FIRST;

    private String query_ = "";

    private boolean reset_ = true;

    private SearchResultDto[] results_;

    private String topPathname_;

    private int total_;

    private String type_ = Page.TYPE + "," + Directory.TYPE;


    public String _get()
    {
        SearchSystem system = searchPlugin_.getDefaultSearchSystem();
        if (system == null) {
            return "/search-unavailable.html";
        }

        String systemId = system.getId();
        HttpSession session = request_.getSession();
        String currentSystemId = (String)session
            .getAttribute(ATTR_DEFAULTSEARCHSYSTEM);
        if (currentSystemId != null && !currentSystemId.equals(systemId)) {
            reset_ = true;
        }

        if (query_.length() == 0) {
            session.removeAttribute(ATTR_SEARCHREQUEST);

            total_ = 0;
            results_ = new SearchResultDto[0];
        } else {
            SearchRequest searchRequest = (SearchRequest)session
                .getAttribute(ATTR_SEARCHREQUEST);
            if (reset_ || searchRequest == null) {
                searchRequest = system.newSearchRequest();
                session.setAttribute(ATTR_SEARCHREQUEST, searchRequest);
            }

            SearchQuery searchQuery = createSearchQuery();

            Locale locale = pageRequest_.getLocale();
            try {
                total_ = searchRequest.getResultsCount(searchQuery);
                SearchResult[] results = searchRequest.search(searchQuery,
                    offset_, QUERY_LENGTH);
                results_ = new SearchResultDto[results.length];
                for (int i = 0; i < results.length; i++) {
                    results_[i] = new SearchResultDto(results[i], locale);
                }
            } catch (ParseException ex) {
                total_ = 0;
                results_ = new SearchResultDto[0];
            }
        }

        if (total_ > QUERY_LENGTH) {
            indicator_ = new SearchResultIndicatorDto(total_, QUERY_LENGTH,
                offset_);
        }

        return "/search.html";
    }


    private SearchQuery createSearchQuery()
    {
        SearchQuery searchQuery = new SearchQuery();

        if (type_ != null) {
            StringTokenizer st = new StringTokenizer(type_, ",");
            while (st.hasMoreTokens()) {
                String type = st.nextToken().trim();
                if (type.length() > 0) {
                    searchQuery.addPageType(type);
                }
            }
        }

        int heimId = pageRequest_.getRootPage().getHeimId();

        searchQuery.setQueryString(query_);
        searchQuery.setMaxLength(QUERY_MAXLENGTH);
        searchQuery.setLocale(pageRequest_.getLocale());
        searchQuery.setUser(authPlugin_.getCurrentActor());
        searchQuery.setHeimId(heimId);

        if (topPathname_ != null) {
            PageAlfr pageAlf = pagePlugin_.getPageAlfr();
            StringTokenizer st = new StringTokenizer(topPathname_, ",");
            while (st.hasMoreTokens()) {
                String pathname = st.nextToken().trim();
                if (pathname.length() == 0 || pathname.equals("/")) {
                    // ルートアイテムが含まれているのでtopItemを設定しない。
                    searchQuery.clearTopPages();
                    break;
                }
                Page topPage = pageAlf.getPage(heimId, pathname);
                if (topPage != null) {
                    searchQuery.addTopPage(topPage);
                }
            }
        }
        return searchQuery;
    }


    /*
     * for framework
     */

    public void setPlugin(Plugin<?> plugin)
    {
        plugin_ = plugin;
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    public void setSearchPlugin(SearchPlugin searchPlugin)
    {
        searchPlugin_ = searchPlugin;
    }


    public void setPageRequest(PageRequest pageRequest)
    {
        pageRequest_ = pageRequest;
    }


    public void setRequest(HttpServletRequest request)
    {
        request_ = request;
    }


    /*
     * for presentation tier
     */

    public SearchResultIndicatorDto getIndicator()
    {
        return indicator_;
    }


    public String getLineDescription()
    {
        if (query_.length() == 0) {
            return plugin_.getProperty("line.search.description", pageRequest_
                .getLocale());
        } else if (results_.length > 0) {
            return MessageFormat.format(plugin_.getProperty(
                "line.search.description.result", pageRequest_.getLocale()),
                new Object[] { new Integer(total_) });
        } else {
            return plugin_.getProperty("line.search.description.noResult",
                pageRequest_.getLocale());
        }
    }


    public void setOffset(int offset)
    {
        offset_ = offset;
    }


    public String getQuery()
    {
        return query_;
    }


    public void setQuery(String query)
    {
        query_ = query.trim();
    }


    public SearchResultDto[] getResults()
    {
        return results_;
    }


    public void setReset(boolean reset)
    {
        reset_ = reset;
    }


    public void setTopPathname(String topPathname)
    {
        topPathname_ = topPathname;
    }


    public void setType(String type)
    {
        type_ = type;
    }
}
