package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.GardIdProvider;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.cms.impl.AbstractPageMatcher;
import org.seasar.kvasir.webapp.Dispatcher;


public class FilteredPageFilter extends AbstractPageMatcher
    implements PageFilter
{
    private PageFilter filter_;

    private Set<Dispatcher> dispatcherSet_ = new HashSet<Dispatcher>();


    public FilteredPageFilter(PageFilter filter, String what, String how,
        String except, boolean not, boolean regex,
        GardIdProvider gardIdProvider, Dispatcher[] dispatchers)
    {
        super(what, how, except, not, regex, gardIdProvider);
        filter_ = filter;
        dispatcherSet_.addAll(Arrays.asList(dispatchers));
    }


    public String toString()
    {
        return "FilteredPageFilter(" + filter_ + ")" + super.toString();
    }


    public void destroy()
    {
        filter_.destroy();
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        if (dispatcherSet_.contains(dispatcher) && isMatched(pageRequest)) {
            filter_.doFilter(request, response, dispatcher, pageRequest, chain);
        } else {
            chain.doFilter(request, response, dispatcher, pageRequest);
        }
    }


    public void init(FilterConfig config)
    {
        filter_.init(config);
    }
}
