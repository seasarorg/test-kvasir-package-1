package org.seasar.kvasir.cms.webdav.impl;

import java.util.Hashtable;

import javax.naming.directory.DirContext;

import org.apache.naming.resources.ProxyDirContext;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.cms.webdav.WebdavPlugin;
import org.seasar.kvasir.cms.webdav.naming.ElementFactory;
import org.seasar.kvasir.cms.webdav.naming.page.PageElementFactory;
import org.seasar.kvasir.cms.webdav.naming.page.PageElementDirContext;
import org.seasar.kvasir.cms.webdav.setting.WebdavPluginSettings;
import org.seasar.kvasir.page.PageAlfr;


public class WebdavPluginImpl extends AbstractPlugin<WebdavPluginSettings>
    implements WebdavPlugin
{
    private PageAlfr pageAlfr_;

    private DirContext dirContext_;

    private PageElementFactory[] elementFactories_;


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    @SuppressWarnings("unchecked")
    protected boolean doStart()
    {
        ElementFactory[] elementFactories = getExtensionComponents(ElementFactory.class);
        elementFactories_ = new PageElementFactory[elementFactories.length];
        for (int i = 0; i < elementFactories.length; i++) {
            elementFactories_[i] = (PageElementFactory)elementFactories[i];
        }

        if (isWebdavEnabled()) {
            Hashtable env = new Hashtable();
            dirContext_ = new ProxyDirContext(env, new PageElementDirContext(
                env, elementFactories_, this, pageAlfr_));
        }

        return true;
    }


    protected void doStop()
    {
        pageAlfr_ = null;
        dirContext_ = null;
        elementFactories_ = null;
    }


    @Override
    public Class<WebdavPluginSettings> getSettingsClass()
    {
        return WebdavPluginSettings.class;
    }


    public boolean isWebdavEnabled()
    {
        return settings_.isWebdavEnabled();
    }


    public DirContext getDirContext()
    {
        return dirContext_;
    }
}
