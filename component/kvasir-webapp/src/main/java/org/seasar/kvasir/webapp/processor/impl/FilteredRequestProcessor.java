package org.seasar.kvasir.webapp.processor.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.webapp.processor.RequestProcessor;
import org.seasar.kvasir.webapp.processor.RequestProcessorChain;


public class FilteredRequestProcessor
    implements RequestProcessor
{
    private RequestProcessor processor_;

    private Set<String> methodSet_;


    public FilteredRequestProcessor(RequestProcessor processor, String[] methods)
    {
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
        return "FilteredRequestProcessor(" + processor_ + ")";
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
        HttpServletResponse response, RequestProcessorChain chain)
        throws ServletException, IOException
    {
        if (isMatched(request)) {
            processor_.doProcess(request, response, chain);
        } else {
            chain.doProcess(request, response);
        }
    }


    boolean isMatched(HttpServletRequest request)
    {
        return (methodSet_ == null || methodSet_.contains(request.getMethod()
            .toUpperCase()));
    }
}
