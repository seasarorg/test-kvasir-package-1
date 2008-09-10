package org.seasar.kvasir.page.ability.content.impl;

import java.io.InputStream;

import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.html.HTMLUtils;
import org.seasar.kvasir.util.io.IOUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class StandardContentHandler
    implements ContentHandler
{
    public static final String ENCODING_DEFAULT = "UTF-8";


    /*
     * constructors
     */
    public StandardContentHandler()
    {
    }


    /*
     * ContentHandler
     */

    public String toHTML(InputStream in, String encoding, String type,
        VariableResolver resolver)
    {
        if (in == null) {
            return null;
        }

        if (type != null && !type.startsWith("text/")) {
            // バイナリコンテントの場合は適当に返す。
            return type;
        }

        if (encoding == null) {
            encoding = ENCODING_DEFAULT;
        }

        String str = IOUtils.readString(in, encoding, false);
        if ("text/html".equals(type)) {
            return HTMLUtils.stripHTML(str);
        } else {
            return HTMLUtils.filterLines(str, true);
        }
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
}
