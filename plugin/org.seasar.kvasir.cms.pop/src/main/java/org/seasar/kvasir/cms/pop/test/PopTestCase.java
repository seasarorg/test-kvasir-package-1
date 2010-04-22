package org.seasar.kvasir.cms.pop.test;

import java.util.HashMap;
import java.util.Locale;

import org.seasar.framework.mock.servlet.MockHttpServletRequest;
import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.framework.mock.servlet.MockHttpServletResponse;
import org.seasar.framework.mock.servlet.MockHttpServletResponseImpl;
import org.seasar.framework.mock.servlet.MockServletContext;
import org.seasar.framework.mock.servlet.MockServletContextImpl;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.base.mock.plugin.MockPlugin;
import org.seasar.kvasir.base.plugin.descriptor.Extension;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.mock.MockPluginDescriptor;
import org.seasar.kvasir.base.util.XOMUtils;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.mock.MockPageRequest;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.extension.ExtensionPoints;
import org.seasar.kvasir.cms.pop.extension.PopElement;
import org.seasar.kvasir.cms.pop.impl.PopContextImpl;
import org.seasar.kvasir.cms.pop.mock.MockPopPlugin;
import org.seasar.kvasir.cms.pop.pop.GenericPop;
import org.seasar.kvasir.cms.zpt.impl.ZptContentHandler;
import org.seasar.kvasir.cms.zpt.impl.ZptPageProcessor;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.mock.MockContentPlugin;
import org.seasar.kvasir.page.test.PageTestCase;
import org.seasar.kvasir.test.ProjectMetaData;
import org.seasar.kvasir.util.io.Resource;

import net.skirnir.xom.Element;


abstract public class PopTestCase<P extends Pop> extends PageTestCase
{
    protected static final String CONTEXT = "/context";


    abstract protected Class<P> getPopClass();


    abstract protected String getPopId();


    abstract protected String getPluginId();


    protected P newPopInstance()
        throws Exception
    {
        P pop = getPopClass().newInstance();
        pop.setElement(getPopElement(getPopId()));
        if (pop instanceof GenericPop) {
            ((GenericPop)pop).setPageAlfr(getPageAlfr());
        }
        return pop;
    }


    PopElement getPopElement(String popId)
        throws Exception
    {
        ProjectMetaData metaData = new ProjectMetaData(getClass());
        Resource pluginDirectory = metaData.getProjectDirectory()
            .getChildResource("src/main/plugin");
        Resource xmlResource = pluginDirectory.getChildResource("plugin.xml");
        if (!xmlResource.exists()) {
            throw new RuntimeException("Can't find plugin.xml");
        }

        MockPluginDescriptor descriptor = (MockPluginDescriptor)XOMUtils
            .toBean(xmlResource.getInputStream(), PluginDescriptor.class,
                new MockPluginDescriptor(pluginDirectory, metaData
                    .getTestHomeDirectory().getChildResource(
                        Globals.CONFIGURATION_DIR + "/" + getPluginId())));
        new MockPlugin<EmptySettings>(getPluginId(), metaData
            .getProjectDirectory(), descriptor);
        Extension[] extensions = descriptor.getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            if (!(PopPlugin.ID + "." + ExtensionPoints.POPS)
                .equals(extensions[i].getPoint())) {
                continue;
            }
            Element[] elements = extensions[i].getElements();
            for (int j = 0; j < elements.length; j++) {
                PopElement popElement = XOMUtils.toBean(elements[j],
                    PopElement.class);
                if (popId.equals(getPluginId() + "." + popElement.getId())) {
                    popElement.setParent(extensions[i]);
                    return initPopElement(popElement);
                }
            }
        }
        throw new RuntimeException("Can't find pop entry for: popId=" + popId
            + ": in plugin.xml: " + xmlResource.getURL());
    }


    PopElement initPopElement(PopElement popElement)
    {
        MockContentPlugin contentPlugin = new MockContentPlugin();
        ZptContentHandler zptContentHandler = new ZptContentHandler();
        ZptPageProcessor zptPageProcessor = new ZptPageProcessor();
        zptPageProcessor.start();
        zptContentHandler.setZptPageProcessor(zptPageProcessor);
        contentPlugin.setContentHandler(ZptContentHandler.MEDIATYPE,
            zptContentHandler);
        popElement.init(contentPlugin, new MockPopPlugin());
        return popElement;
    }


    protected PopContext newContext(Page containerPage)
    {
        return newContext(containerPage, null, Locale.ENGLISH);
    }


    protected PopContext newContext(Page containerPage, Page thatPage)
    {
        return newContext(containerPage, thatPage, Locale.ENGLISH);
    }


    protected PopContext newContext(Page containerPage, Page thatPage,
        Locale locale)
    {
        if (thatPage == null) {
            return new PopContextImpl(new HashMap<Object, Object>(),
                containerPage, locale, null, null, null, null);
        } else {
            String path = thatPage.getPathname();
            if (path.length() == 0) {
                // MockServletRequestのため。
                path = "/";
            }
            MockServletContext servletContext = new MockServletContextImpl(
                CONTEXT);
            MockHttpServletRequest request = new MockHttpServletRequestImpl(
                servletContext, path);
            request.setAttribute(CmsPlugin.ATTR_CONTEXT_PATH, CONTEXT);

            MockHttpServletResponse response = new MockHttpServletResponseImpl(
                request);
            PageRequest pageRequest = new MockPageRequest(CONTEXT, thatPage,
                locale, getPageAlfr());

            return new PopContextImpl(new HashMap<Object, Object>(),
                containerPage, pageRequest.getLocale(), servletContext,
                request, response, pageRequest);
        }
    }


    protected void setProperty(String id, String value)
    {
        setProperty(id, Page.VARIANT_DEFAULT, value);
    }


    protected void setProperty(String id, String variant, String value)
    {
        Page rootPage = getPageAlfr().getPage(Page.ID_ROOT);
        rootPage.getAbility(PropertyAbility.class).setProperty(
            "pop." + getPopId() + "." + id, variant, value);
    }
}
