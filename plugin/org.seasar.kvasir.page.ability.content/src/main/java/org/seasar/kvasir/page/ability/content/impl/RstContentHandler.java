package org.seasar.kvasir.page.ability.content.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import net.skirnir.rst.converter.HTMLConverter;
import net.skirnir.rst.element.Document;
import net.skirnir.rst.parser.RstParser;

import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.page.ability.content.extension.ContentHandlerElement;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.io.IORuntimeException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class RstContentHandler
    implements ContentHandler, Lifecycle
{
    public static final String ENCODING_DEFAULT = "UTF-8";

    private RstParser parser_ = new RstParser();

    private HTMLConverter converter_ = new HTMLConverter();

    private KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public void setElement(ContentHandlerElement element)
    {
    }


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
        if (!(compiled instanceof Document)) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        converter_.convert((Document)compiled, sb);
        return sb.toString();
    }


    public Object compile(InputStream in, String encoding, String type)
    {
        if (in == null) {
            return null;
        }
        try {
            Reader reader = new InputStreamReader(in,
                (encoding != null ? encoding : ENCODING_DEFAULT));
            in = null;
            return parser_.parse(reader);
        } catch (IOException ex) {
            log_.warn("Can't compile rst text", ex);
            throw new IORuntimeException(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
        }
    }


    public boolean start()
    {
        converter_.setMinimunHLevel(3);
        converter_.setTemplate(getClass().getClassLoader().getResourceAsStream(
            getClass().getName().replace('.', '/').concat("_template.html")),
            ENCODING_DEFAULT);

        return true;
    }


    public void stop()
    {
    }
}
