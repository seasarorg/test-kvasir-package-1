package org.seasar.kvasir.page.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.search.PositionRecorder;
import org.seasar.kvasir.page.search.QueryStringParser;
import org.seasar.kvasir.page.search.SearchContext;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.page.search.SearchResultHandler;
import org.seasar.kvasir.page.search.SearchSystem;
import org.seasar.kvasir.page.search.extension.SearchSystemElement;
import org.seasar.kvasir.util.PropertyUtils;


public abstract class SearchSystemBase
    implements SearchSystem
{
    private SearchSystemElement element_;

    private QueryStringParser queryStringParser_;

    protected KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    /*
     *  SearchSystem
     */

    public String getId()
    {
        return element_.getFullId();
    }


    public QueryStringParser getQueryStringParser()
    {
        return queryStringParser_;
    }


    public SearchResult[] search(SearchContext context)
    {
        return search(context, SearchQuery.OFFSET_FIRST, SearchQuery.LENGTH_ALL);
    }


    /*
     * protected scope methods
     */

    protected SearchSystemElement getElement()
    {
        return element_;
    }


    protected SearchResult[] createSearchResults(SearchContext context,
        SearchResultHandler handler, int offset, int length)
        throws IOException
    {
        List<SearchResult> list = new ArrayList<SearchResult>();
        Set<Page> pageSet = new HashSet<Page>();

        PositionRecorder recorder = context.getPositionRecorder();
        int rawPosition = recorder.getRawPosition(offset);
        for (int i = 0; i < rawPosition && handler.hasNext(); i++) {
            handler.next();
        }
        if (!handler.hasNext()) {
            return new SearchResult[0];
        }
        int end = offset + length;
        for (int raw = 0, cooked = offset; cooked < end && handler.hasNext(); raw++) {
            SearchResult result = handler.next();
            boolean visible = isVisible(context, result, pageSet);
            if (visible) {
                list.add(result);
                recorder.record(cooked, raw);
                cooked++;
            }
        }

        return list.toArray(new SearchResult[0]);
    }


    protected boolean isVisible(SearchContext context, SearchResult result,
        Set<Page> pageSet)
    {
        Page page = result.getPage();
        if (page == null) {
            return false;
        }

        if (pageSet.contains(page)) {
            return false;
        }

        SearchQuery query = context.getQuery();

        // 前INDEXEDで今INDEXEDでなくなったページを無視するため。
        PropertyAbility prop = page.getAbility(PropertyAbility.class);
        if (!PropertyUtils.valueOf(prop.getProperty(PROP_INDEXED), true)) {
            return false;
        }

        if (!query.isIncludeConcealed() && page.isConcealed()) {
            return false;
        }

        if (!query.containsPageType(page.getType())) {
            return false;
        }

        if (query.getUser() != null) {
            PermissionAbility perm = page.getAbility(PermissionAbility.class);
            if (!perm.permits(query.getUser(), query.getPrivilege())) {
                return false;
            }
        }

        if (!isContentVisible(page, result.getVariant(), query.getLocale())) {
            return false;
        }

        if (!query.containsInTopPages(page)) {
            return false;
        }

        if (query.getHeimId() != null
            && query.getHeimId().intValue() != page.getHeimId()) {
            return false;
        }

        pageSet.add(page);

        return true;
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
}
