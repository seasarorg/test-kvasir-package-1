package org.seasar.kvasir.cms.webdav.impl;

import java.io.IOException;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlets.WebdavServlet;
import org.apache.naming.resources.ProxyDirContext;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.cms.processor.impl.ServletPageProcessor;
import org.seasar.kvasir.cms.webdav.WebdavPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.PropertyUtils;


public class WebdavPageProcessor extends ServletPageProcessor
{
    public static final String PROP_WEBDAVENABLE = "webdav.enable";

    private WebdavPlugin webdavPlugin_;

    private ProxyDirContext dirContext_;

    private boolean webdavEnabled_;

    private KvasirLog log_ = KvasirLogFactory.getLog(this.getClass());


    public void setWebdavPlugin(WebdavPlugin webdavPlugin)
    {
        webdavPlugin_ = webdavPlugin;
    }


    @Binding(bindingType = BindingType.MAY)
    public void setDirContext(DirContext dirContext)
    {
        if (dirContext == null) {
            dirContext_ = null;
        } else if (dirContext instanceof ProxyDirContext) {
            dirContext_ = (ProxyDirContext)dirContext;
        } else {
            try {
                dirContext_ = new ProxyDirContext(dirContext.getEnvironment(),
                    dirContext);
            } catch (NamingException ex) {
                throw new RuntimeException("Can't set DirContext: "
                    + dirContext, ex);
            }
        }
    }


    public void init(ServletConfig config)
    {
        super.init(config);

        webdavEnabled_ = webdavPlugin_.isWebdavEnabled();
        if (webdavEnabled_) {
            log_.info("WEBDAV SERVICE START");
        }
        if (dirContext_ == null) {
            setDirContext(webdavPlugin_.getDirContext());
        }
    }


    public void destroy()
    {
        super.destroy();
        log_.info("WEBDAV SERVICE STOP");
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        Page gardRoot = pageRequest.getMy().getGardRootPage();
        PropertyAbility prop = gardRoot.getAbility(PropertyAbility.class);
        if (webdavEnabled_
            && PropertyUtils.valueOf(prop.getProperty(PROP_WEBDAVENABLE), true)) {
            if (log_.isDebugEnabled()) {
                log_.debug("Access to DAV");
            }
            super.doProcess(request, response, pageRequest, chain);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }


    @Override
    protected boolean shouldProcess(Bag bag, PageRequest pageRequest)
    {
        return true;
    }


    @Override
    protected HttpServlet newServlet()
    {
        return new WebdavServlet();
    }


    @Override
    protected void notifyServletInitializing(HttpServlet servlet,
        ServletConfig config)
    {
        config.getServletContext().setAttribute(
            "org.apache.catalina.resources", dirContext_);
    }


    @Override
    protected ClassLoader getWebappClassLoader(PageRequest pageRequest,
        HttpServlet servlet)
    {
        return Thread.currentThread().getContextClassLoader();
    }
}
