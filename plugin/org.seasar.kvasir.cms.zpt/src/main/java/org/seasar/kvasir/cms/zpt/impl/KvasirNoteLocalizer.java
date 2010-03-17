package org.seasar.kvasir.cms.zpt.impl;

import java.text.MessageFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.webapp.Globals;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.webapp.ServletVariableResolver;
import net.skirnir.freyja.zpt.tales.NoteLocalizer;


public class KvasirNoteLocalizer
    implements NoteLocalizer
{
    private static final String PROPERTYPREFIX_LABEL = "label.";

    public static final String VARNAME_PLUGIN = "plugin";


    public Locale findLocale(TemplateContext context,
        VariableResolver varResolver)
    {
        Object session = varResolver.getVariable(context,
            ServletVariableResolver.VAR_SESSION);
        if (session instanceof HttpSession) {
            Object locale = ((HttpSession)session)
                .getAttribute(Globals.ATTR_LOCALE);
            if (locale instanceof Locale) {
                return (Locale)locale;
            }
        }
        Object request = varResolver.getVariable(context,
            ServletVariableResolver.VAR_REQUEST);
        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest)request).getLocale();
        }
        return Locale.getDefault();
    }


    public String getMessageResourceValue(TemplateContext context,
        VariableResolver varResolver, String noteValue, Object[] noteParameters)
    {
        Plugin<?> plugin = findPlugin(context, varResolver);
        if (plugin != null) {
            Locale locale = findLocale(context, varResolver);
            String v = plugin.getProperty(noteValue, locale);
            if (v != null) {
                for (int i = 0; i < noteParameters.length; i++) {
                    if (noteParameters[i] instanceof String) {
                        String localizedValue = plugin.getProperty(
                            PROPERTYPREFIX_LABEL + noteParameters[i], locale);
                        if (localizedValue != null) {
                            noteParameters[i] = localizedValue;
                        }
                    }
                }
                noteValue = MessageFormat.format(v, noteParameters);
            }
        }
        return noteValue;
    }


    Plugin<?> findPlugin(TemplateContext context, VariableResolver varResolver)
    {
        Object plugin = varResolver.getVariable(context, VARNAME_PLUGIN);
        if (plugin instanceof Plugin<?>) {
            return (Plugin<?>)plugin;
        }

        Object pageRequest = varResolver.getVariable(context,
            KvasirVariableResolver.VARNAME_PAGEREQUEST);
        if (!(pageRequest instanceof PageRequest)) {
            Object request = varResolver.getVariable(context,
                ServletVariableResolver.VAR_REQUEST);
            if (request instanceof HttpServletRequest) {
                pageRequest = ((HttpServletRequest)request)
                    .getAttribute(CmsPlugin.ATTR_PAGEREQUEST);
            }
        }
        if (pageRequest instanceof PageRequest) {
            return ((PageRequest)pageRequest).getMy().getPlugin();
        } else {
            return null;
        }
    }
}
