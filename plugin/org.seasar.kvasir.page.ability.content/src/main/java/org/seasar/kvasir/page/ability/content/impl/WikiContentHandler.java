package org.seasar.kvasir.page.ability.content.impl;

import java.io.InputStream;

import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.page.ability.content.extension.ContentHandlerElement;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.wiki.Context;
import org.seasar.kvasir.util.wiki.DefinitionEvaluator;
import org.seasar.kvasir.util.wiki.DeleteEvaluator;
import org.seasar.kvasir.util.wiki.EmphasizeEvaluator;
import org.seasar.kvasir.util.wiki.ListEvaluator;
import org.seasar.kvasir.util.wiki.PreEvaluator;
import org.seasar.kvasir.util.wiki.QuoteEvaluator;
import org.seasar.kvasir.util.wiki.SectionEvaluator;
import org.seasar.kvasir.util.wiki.StrongEvaluator;
import org.seasar.kvasir.util.wiki.TableEvaluator;
import org.seasar.kvasir.util.wiki.VerbatimEvaluator;
import org.seasar.kvasir.util.wiki.WikiEngine;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class WikiContentHandler
    implements ContentHandler, Lifecycle
{
    public static final String ENCODING_DEFAULT = "UTF-8";

    private ContentHandlerElement element_;

    private Kvasir kvasir_;

    private WikiEngine engine_;


    /*
     * ContentHandler
     */

    public String toHTML(InputStream in, String encoding, String type,
        VariableResolver resolver)
    {
        if (in == null) {
            return null;
        }

        if (encoding == null) {
            encoding = ENCODING_DEFAULT;
        }

        return engine_.evaluate(IOUtils.readString(in, encoding, false));
    }


    public String toHTML(Object compiled, VariableResolver resolver)
    {
        if (!(compiled instanceof String)) {
            return null;
        } else {
            return (String)compiled;
        }
    }


    public Object compile(InputStream in, String encoding, String type)
    {
        return toHTML(in, encoding, type, null);
    }


    /*
     * Lifecycle
     */

    public boolean start()
    {
        engine_ = new WikiEngine() {
            protected void generateHeader(Context context)
            {
                context.getWriter().println("<div class=\"wiki\">");
            }


            protected void generateFooter(Context context)
            {
                context.getWriter().println("</div>");
            }
        };

        engine_.registerBlockEvaluator(new DefinitionEvaluator())
            .registerBlockEvaluator(new QuoteEvaluator())
            .registerBlockEvaluator(new ListEvaluator())
            .registerBlockEvaluator(new PreEvaluator()).registerBlockEvaluator(
                new SectionEvaluator()).registerLineEvaluator(
                new KvasirLinkEvaluator()).registerBlockEvaluator(
                new TableEvaluator()).registerLineEvaluator(
                new StrongEvaluator()).registerLineEvaluator(
                new EmphasizeEvaluator()).registerLineEvaluator(
                new DeleteEvaluator());

        if (PropertyUtils.valueOf(element_.getPropertyHandler().getProperty(
            "enableVerbatim"), true)) {
            engine_.registerBlockEvaluator(new VerbatimEvaluator());
        }

        engine_.start();

        return true;
    }


    public void stop()
    {
        element_ = null;
        kvasir_ = null;

        engine_ = null;
    }


    /*
     * for framework
     */

    public void setElement(ContentHandlerElement element)
    {
        element_ = element;
    }


    public void setKvasir(Kvasir kvasir)
    {
        kvasir_ = kvasir;
    }
}
