package org.seasar.kvasir.cms.ymir.impl;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cms.ymir.extension.zpt.TemplatePathResolver;
import org.seasar.cms.ymir.extension.zpt.Zpt;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.cms.zpt.impl.ZptPageProcessor;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.TemplateEvaluator;


public class KvasirZpt
    implements Zpt
{
    private ZptPageProcessor processor_;

    private TemplatePathResolver resolver_ = new KvasirTemplatePathResolver();


    public KvasirZpt()
    {
    }


    public void buildTemplateContext(TemplateContext context,
        ServletContext servletContext, HttpServletRequest request,
        HttpServletResponse response, Locale locale, String path)
    {
        processor_.buildTemplateContext(context, servletContext, request,
            response, locale, path);
    }


    public TemplateEvaluator getTemplateEvaluator()
    {
        return processor_.newEvaluator();
    }


    public TemplatePathResolver getTemplatePathResolver()
    {
        return resolver_;
    }


    @Binding(bindingType = BindingType.MUST, value = "zptPageProcessor")
    public void setZptPageProcessor(ZptPageProcessor processor)
    {
        processor_ = processor;
    }
}
