package org.seasar.kvasir.page.ability.content.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.annotation.ForTest;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.util.el.VariableResolver;


public class ContentUtils
{
    private static final String ENCODING = "UTF-8";

    private static ContentAbilityPlugin plugin_;


    protected ContentUtils()
    {
    }


    public static String getBodyHTMLString(String mediaType, String rawBody,
        VariableResolver resolver)
    {
        try {
            return getBodyHTMLString(mediaType, new ByteArrayInputStream(
                rawBody.getBytes(ENCODING)), ENCODING, resolver);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Can't happen!", ex);
        }
    }


    public static String getBodyHTMLString(String mediaType, InputStream in,
        String encoding, VariableResolver resolver)
    {
        ContentHandler handler = getPlugin().getContentHandler(mediaType);
        return handler.toHTML(handler.compile(in, encoding, mediaType),
            resolver);
    }


    public static ContentAbilityPlugin getPlugin()
    {
        if (plugin_ != null) {
            return plugin_;
        } else {
            return Asgard.getKvasir().getPluginAlfr().getPlugin(
                ContentAbilityPlugin.class);
        }
    }


    @ForTest
    static void setPlugin(ContentAbilityPlugin plugin)
    {
        plugin_ = plugin;
    }
}
