package org.seasar.kvasir.cms.toolbox.toolbox.web;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.toolbox.toolbox.dto.SearchResultDto;
import org.seasar.kvasir.cms.toolbox.toolbox.dto.SearchResultIndicatorDto;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.search.ParseException;
import org.seasar.kvasir.page.search.SearchContext;
import org.seasar.kvasir.page.search.SearchPlugin;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.page.search.SearchSystem;
import org.seasar.kvasir.page.type.Directory;


/**
 * 検索ページを作成するためのベースとなるクラスです。
 *
 * @author YOKOTA Takehiko
 */
public class SearchPageBase
{
    protected static final String ATTR_DEFAULTSEARCHSYSTEM = "searchSystem.default";

    protected static final String ATTR_SEARCHCONTEXT = "searchSystem.context";

    protected static final int QUERY_LENGTH = 10;

    protected static final int INIDCATOR_DISPLAY_RANGE = 4;

    @Binding(bindingType = BindingType.MUST)
    protected Plugin<?> plugin_;

    @Binding(bindingType = BindingType.MUST)
    protected PagePlugin pagePlugin_;

    @Binding(bindingType = BindingType.MUST)
    protected AuthPlugin authPlugin_;

    @Binding(bindingType = BindingType.MUST)
    protected SearchPlugin searchPlugin_;

    @Binding(bindingType = BindingType.MUST)
    protected PageRequest pageRequest_;

    @Binding(bindingType = BindingType.MUST)
    protected HttpServletRequest request_;

    /*
     * for presentation tier
     */

    protected SearchResultIndicatorDto indicator_;

    protected int offset_ = SearchQuery.OFFSET_FIRST;

    protected String query_ = "";

    protected boolean reset_ = true;

    protected SearchResultDto[] results_;

    protected String topPathname_;

    protected int total_;

    protected String type_ = Page.TYPE + "," + Directory.TYPE;


    protected String doGet()
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

        total_ = 0;
        results_ = new SearchResultDto[0];
        do {
            if (query_.length() == 0) {
                session.removeAttribute(ATTR_SEARCHCONTEXT);
                break;
            }

            SearchQuery query = createSearchQuery();

            SearchContext context = (SearchContext)session
                .getAttribute(ATTR_SEARCHCONTEXT);
            if (context != null) {
                if (!query.equals(context.getQuery())) {
                    context = null;
                }
            }
            if (reset_ || context == null) {
                context = system.newContext();
                try {
                    context.setQuery(query);
                } catch (ParseException ex) {
                    break;
                }
                session.setAttribute(ATTR_SEARCHCONTEXT, context);
            }

            Locale locale = pageRequest_.getLocale();
            SearchResult[] results = system.search(context, offset_,
                QUERY_LENGTH);
            results_ = new SearchResultDto[results.length];
            total_ = system.getResultsCount(context);
            for (int i = 0; i < results.length; i++) {
                results_[i] = buildDto(results[i], locale);
            }
        } while (false);

        if (total_ > QUERY_LENGTH) {
            indicator_ = new SearchResultIndicatorDto(total_, QUERY_LENGTH,
                offset_, INIDCATOR_DISPLAY_RANGE);
        }

        return "/search.html";
    }


    protected SearchResultDto buildDto(SearchResult searchResult, Locale locale)
    {
        return new SearchResultDto(searchResult, locale);
    }


    protected SearchQuery createSearchQuery()
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
