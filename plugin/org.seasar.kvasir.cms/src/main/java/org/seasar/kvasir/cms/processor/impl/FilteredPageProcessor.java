package org.seasar.kvasir.cms.processor.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.GardIdProvider;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.impl.AbstractPageMatcher;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;


public class FilteredPageProcessor extends AbstractPageMatcher
    implements PageProcessor
{
    private PageProcessor processor_;

    private Set<String> methodSet_;


    public FilteredPageProcessor(PageProcessor processor, String what,
        String how, String except, boolean not, boolean regex,
        GardIdProvider gardIdProvider, String[] methods)
    {
        super(what, how, except, not, regex, gardIdProvider);
        processor_ = processor;
        if (methods == null) {
            methodSet_ = null;
        } else {
            methodSet_ = new HashSet<String>();
            for (int i = 0; i < methods.length; i++) {
                methodSet_.add(methods[i].toUpperCase());
            }
        }
    }


    public String toString()
    {
        return "FilteredPageProcessor(" + processor_ + ")";
    }


    public void destroy()
    {
        processor_.destroy();
    }


    public void init(ServletConfig config)
    {
        processor_.init(config);
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        if (isMatched(request) && isMatched(pageRequest)) {
            processor_.doProcess(request, response, pageRequest, chain);
        } else {
            chain.doProcess(request, response, pageRequest);
        }
    }


    boolean isMatched(HttpServletRequest request)
    {
        return (methodSet_ == null || methodSet_.contains(request.getMethod()
            .toUpperCase()));
    }
}
