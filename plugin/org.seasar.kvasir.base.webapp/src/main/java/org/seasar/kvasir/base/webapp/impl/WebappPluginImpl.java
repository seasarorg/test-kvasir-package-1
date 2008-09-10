package org.seasar.kvasir.base.webapp.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.seasar.kvasir.base.mime.MimeMappings;
import org.seasar.kvasir.base.mime.MimePlugin;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.webapp.WebappPlugin;
import org.seasar.kvasir.base.webapp.Content;
import org.seasar.kvasir.base.webapp.extension.CssElement;
import org.seasar.kvasir.base.webapp.extension.JavascriptElement;
import org.seasar.kvasir.base.webapp.extension.UseCssElement;
import org.seasar.kvasir.base.webapp.extension.UseJavascriptElement;
import org.seasar.kvasir.webapp.Globals;
import org.seasar.kvasir.webapp.filter.RequestFilter;
import org.seasar.kvasir.webapp.handler.ExceptionHandler;
import org.seasar.kvasir.webapp.processor.RequestProcessor;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class WebappPluginImpl extends AbstractPlugin<EmptySettings>
    implements WebappPlugin
{
    private static final String COMPONENT_STATICCONTENTPROCESSOR = "staticContentProcessor";

    private MimePlugin mimePlugin_;

    private StaticContentProcessor staticContentProcessor_;

    private String[] cssPathnames_;

    private String[] javascriptPathnames_;

    private Set<String> javascriptPathnameSet_;

    private Set<String> cssPathnameSet_;


    public void setMimePlugin(MimePlugin mimePlugin)
    {
        mimePlugin_ = mimePlugin;
    }


    /*
     * WebappPlugin
     */

    public void registerStaticContent(String path, Content content)
    {
        staticContentProcessor_.registerStaticContent(path, content);
    }


    public String[] getCssPathnames()
    {
        return cssPathnames_;
    }


    public String[] getJavascriptPathnames()
    {
        return javascriptPathnames_;
    }


    /*
     * AbstractPlugin
     */

    @Override
    protected boolean doStart()
    {
        staticContentProcessor_ = (StaticContentProcessor)getComponentContainer()
            .getComponent(COMPONENT_STATICCONTENTPROCESSOR);
        MimeMappings mappings = mimePlugin_.getMimeMappings();

        // JavaScriptを登録する。
        JavascriptElement[] javascriptElements = getExtensionElements(JavascriptElement.class);
        for (int i = 0; i < javascriptElements.length; i++) {
            String[] paths = javascriptElements[i].getExpandedPaths();
            for (int j = 0; j < paths.length; j++) {
                registerStaticContent(paths[j], javascriptElements[i]
                    .getContent(paths[j], mappings));
            }
        }

        // CSSを登録する。
        CssElement[] cssElements = getExtensionElements(CssElement.class);
        for (int i = 0; i < cssElements.length; i++) {
            String[] paths = cssElements[i].getExpandedPaths();
            for (int j = 0; j < paths.length; j++) {
                registerStaticContent(paths[j], cssElements[i].getContent(
                    paths[j], mappings));
            }
        }

        // サイトで使うJavaScriptのパスを収集する。
        UseJavascriptElement[] useJavascriptElements = getExtensionElements(UseJavascriptElement.class);
        javascriptPathnameSet_ = new LinkedHashSet<String>();
        for (int i = 0; i < useJavascriptElements.length; i++) {
            javascriptPathnameSet_.add(useJavascriptElements[i]
                .getModulePathname());
        }
        javascriptPathnames_ = javascriptPathnameSet_
            .toArray(new String[0]);

        // サイトで使うCSSのパスを収集する。
        UseCssElement[] useCssElements = getExtensionElements(UseCssElement.class);
        cssPathnameSet_ = new LinkedHashSet<String>();
        for (int i = 0; i < useCssElements.length; i++) {
            cssPathnameSet_.add(useCssElements[i].getModulePathname());
        }
        cssPathnames_ = cssPathnameSet_.toArray(new String[0]);

        return true;
    }


    @Override
    protected void doStop()
    {
        // ExceptionHandlerを破棄する。
        ExceptionHandler[] handlers = (ExceptionHandler[])getKvasir()
            .getAttribute(Globals.ATTR_EXCEPTIONHANDLERS);
        if (handlers != null) {
            getKvasir().removeAttribute(Globals.ATTR_EXCEPTIONHANDLERS);
            for (int i = 0; i < handlers.length; i++) {
                try {
                    handlers[i].destroy();
                } catch (Throwable t) {
                    log_.warn("Can't stop exceptionHandler gracefully", t);
                }
            }
        }

        // Filterを破棄する。
        RequestFilter[] filters = (RequestFilter[])getKvasir().getAttribute(
            Globals.ATTR_REQUESTFILTERS);
        if (filters != null) {
            getKvasir().removeAttribute(Globals.ATTR_REQUESTFILTERS);
            for (int i = 0; i < filters.length; i++) {
                try {
                    filters[i].destroy();
                } catch (Throwable t) {
                    log_.warn("Can't stop filter gracefully", t);
                }
            }
        }

        // RequestProcessorを破棄する。
        RequestProcessor[] processors = (RequestProcessor[])getKvasir()
            .getAttribute(Globals.ATTR_REQUESTPROCESSORS);
        if (processors != null) {
            getKvasir().removeAttribute(Globals.ATTR_REQUESTPROCESSORS);
            for (int i = 0; i < processors.length; i++) {
                try {
                    processors[i].destroy();
                } catch (Throwable t) {
                    log_.warn("Can't stop requestProcessor gracefully", t);
                }
            }
        }

        cssPathnames_ = null;
        javascriptPathnames_ = null;
        cssPathnameSet_ = null;
        javascriptPathnameSet_ = null;
        mimePlugin_ = null;
    }


    public boolean containsCss(String basePath, String id, String moduleName)
    {
        return containsCss(basePath + "/" + id + "/" + moduleName);
    }


    public boolean containsCss(String id, String moduleName)
    {
        return containsCss(PATH_CSS, id, moduleName);
    }


    public boolean containsCss(String path)
    {
        return cssPathnameSet_.contains(path);
    }


    public boolean containsJavascript(String basePath, String id,
        String moduleName)
    {
        return containsJavascript(basePath + "/" + id + "/" + moduleName);
    }


    public boolean containsJavascript(String id, String moduleName)
    {
        return containsJavascript(PATH_JS, id, moduleName);
    }


    public boolean containsJavascript(String path)
    {
        return javascriptPathnameSet_.contains(path);
    }
}
