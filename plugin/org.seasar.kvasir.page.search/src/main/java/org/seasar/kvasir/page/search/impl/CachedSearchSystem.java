package org.seasar.kvasir.page.search.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.search.ParseException;
import org.seasar.kvasir.page.search.QueryStringParser;
import org.seasar.kvasir.page.search.RawSearchResults;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.SearchRequest;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.page.search.SearchResultFactory;
import org.seasar.kvasir.page.search.SearchSystem;
import org.seasar.kvasir.page.search.extension.SearchSystemElement;
import org.seasar.kvasir.util.PropertyUtils;


public abstract class CachedSearchSystem
    implements SearchSystem
{
    private SearchSystemElement element_;

    private QueryStringParser queryStringParser_;

    private SearchResultFactory searchResultFactory_;

    protected KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    /*
     * abstract methods
     */

    abstract public SearchResult[] search(SearchQuery query)
        throws ParseException;


    abstract public void addToIndex(Page[] pages);


    abstract public void removeFromIndex(Page[] pages);


    abstract public void clearIndex();


    /*
     * public scope methods
     */

    /*
     *  SearchSystem
     */

    public String getId()
    {
        return element_.getFullId();
    }


    public SearchResultFactory getSearchResultFactory()
    {
        return searchResultFactory_;
    }


    public QueryStringParser getQueryStringParser()
    {
        return queryStringParser_;
    }


    public SearchRequest newSearchRequest()
    {
        return new CachedSearchRequest(this);
    }


    /*
     * protected scope methods
     */

    protected SearchSystemElement getElement()
    {
        return element_;
    }


    protected final SearchResult[] getSearchResults(SearchQuery query,
        Object rawResult)
    {
        if (rawResult == null) {
            return new SearchResult[0];
        }

        Set<Page> pageSet = new HashSet<Page>();
        Privilege privilege = query.getPrivilege();
        boolean includeConcealed = query.isIncludeConcealed();
        int maxLength = query.getMaxLength();
        Page[] topPages = query.getTopPages();

        if (maxLength == 0) {
            return new SearchResult[0];
        }

        Integer heimId = query.getHeimId();
        int[] topHeimIds = null;
        String[] topPathnames = null;
        String[] topPathnames2 = null;
        if (topPages.length > 0) {
            topHeimIds = new int[topPages.length];
            topPathnames = new String[topPages.length];
            topPathnames2 = new String[topPages.length];
            for (int i = 0; i < topPages.length; i++) {
                String pathname = topPages[i].getPathname();
                topHeimIds[i] = topPages[i].getHeimId();
                topPathnames[i] = pathname;
                topPathnames2[i] = pathname + "/";
            }
        }

        RawSearchResults rsr = new RawSearchResults(rawResult);
        List<SearchResult> list = new ArrayList<SearchResult>();
        SearchResult sr;
        while ((sr = searchResultFactory_.next(query, rsr)) != null) {
            Page page = sr.getPage();
            if (page == null) {
                continue;
            }

            if (pageSet.contains(page)) {
                continue;
            }

            // 前INDEXEDで今INDEXEDでなくなったページを無視するため。
            PropertyAbility prop = page.getAbility(PropertyAbility.class);
            if (!PropertyUtils.valueOf(prop.getProperty(PROP_INDEXED), true)) {
                continue;
            }

            if (!includeConcealed && page.isConcealed()) {
                continue;
            }

            if (!query.containsPageType(page.getType())) {
                continue;
            }

            if (query.getUser() != null) {
                PermissionAbility perm = page
                    .getAbility(PermissionAbility.class);
                if (!perm.permits(query.getUser(), privilege)) {
                    continue;
                }
            }

            if (!isContentVisible(page, sr.getVariant(), query.getLocale())) {
                continue;
            }

            int hid = page.getHeimId();
            if (topPages.length > 0) {
                String pathname = page.getPathname();
                boolean matched = false;
                for (int j = 0; j < topPages.length; j++) {
                    if (hid == topHeimIds[j]
                        && (pathname.equals(topPathnames[j]) || pathname
                            .startsWith(topPathnames2[j]))) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    continue;
                }
            } else {
                if (heimId != null && hid != heimId) {
                    continue;
                }
            }

            list.add(sr);
            pageSet.add(page);

            if (maxLength != SearchQuery.LENGTH_ALL && list.size() >= maxLength) {
                break;
            }
        }

        return (SearchResult[])list.toArray(new SearchResult[0]);
    }


    private boolean isContentVisible(Page page, String variant, Locale locale)
    {
        if (variant == null) {
            return true;
        }
        if (locale == null) {
            return (variant.length() == 0);
        }
        Content content = page.getAbility(ContentAbility.class)
            .getLatestContent(locale);
        return (content != null && variant.equals(content.getVariant()));
    }


    /*
     * for framework
     */

    public void setElement(SearchSystemElement element)
    {
        element_ = element;
    }


    public void setQueryStringParser(QueryStringParser queryStringParser)
    {
        queryStringParser_ = queryStringParser;
    }


    public void setSearchResultFactory(SearchResultFactory searchResultFfactory)
    {
        searchResultFactory_ = searchResultFfactory;
    }
}
