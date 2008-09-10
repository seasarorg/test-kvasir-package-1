package org.seasar.kvasir.cms.zpt.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.io.IORuntimeException;

import net.skirnir.freyja.Element;
import net.skirnir.freyja.TemplateEvaluator;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ZptContentHandler
    implements ContentHandler, Lifecycle
{
    public static final String MEDIATYPE = "text/x-zpt";

    private ZptPageProcessor zptPageProcessor_;


    /*
     * ContentHandler
     */

    public String toHTML(InputStream in, String encoding, String type,
        VariableResolver resolver)
    {
        return toHTML(compile(in, encoding, type), resolver);
    }


    public String toHTML(Object compiled, VariableResolver resolver)
    {
        if (compiled instanceof Element[]) {
            return zptPageProcessor_.evaluate((HttpServletRequest)resolver
                .getValue(HttpServletRequest.class),
                (HttpServletResponse)resolver
                    .getValue(HttpServletResponse.class),
                (ServletContext)resolver.getValue(ServletContext.class),
                (Locale)resolver.getValue(Locale.class),
                new VariableResolverAdapter(resolver, null), "text/html", null,
                (Element[])compiled);
        } else {
            return "";
        }
    }


    public Object compile(InputStream in, String encoding, String type)
    {
        try {
            return getEvaluator().parse(new InputStreamReader(in, encoding));
        } catch (UnsupportedEncodingException ex) {
            throw new IORuntimeException("Unknown encoding: " + encoding, ex);
        }
    }


    TemplateEvaluator getEvaluator()
    {
        return zptPageProcessor_.getEvaluator();
    }


    /*
     * Lifecycle
     */

    public boolean start()
    {
        return true;
    }


    public void stop()
    {
        zptPageProcessor_ = null;
    }


    /*
     * for framework
     */

    @Binding("zptPageProcessor")
    public void setZptPageProcessor(ZptPageProcessor zptPageProcessor)
    {
        zptPageProcessor_ = zptPageProcessor;
    }
}
