package org.seasar.kvasir.cms.pop;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.page.Page;


public class PopContextWrapper
    implements PopContext
{
    private PopContext context_;

    private Map<String, Object> map_;


    public PopContextWrapper(PopContext context, Map<String, Object> map)
    {
        context_ = context;
        map_ = map;
    }


    public Locale getLocale()
    {
        return context_.getLocale();
    }


    public PageRequest getPageRequest()
    {
        return context_.getPageRequest();
    }


    public HttpServletRequest getRequest()
    {
        return context_.getRequest();
    }


    public HttpServletResponse getResponse()
    {
        return context_.getResponse();
    }


    public Page getContainerPage()
    {
        return context_.getContainerPage();
    }


    public Object getValue(Object key)
    {
        Object value = null;
        if (map_ != null) {
            value = map_.get(key);
        }
        if (value == null) {
            value = context_.getValue(key);
        }
        return value;
    }


    public HttpSession getSession()
    {
        return context_.getSession();
    }


    public ServletContext getApplication()
    {
        return context_.getApplication();
    }


    public PageDispatch getMy()
    {
        return context_.getMy();
    }


    public Page getMyPage()
    {
        return context_.getMyPage();
    }


    public PageDispatch getThat()
    {
        return context_.getThat();
    }


    public Page getThatPage()
    {
        return context_.getThatPage();
    }


    public Plugin<?> getPlugin()
    {
        return context_.getPlugin();
    }


    public Pop getPop()
    {
        return context_.getPop();
    }


    public Pop setPop(Pop pop)
    {
        return context_.setPop(pop);
    }


    public boolean isInPreviewMode()
    {
        return context_.isInPreviewMode();
    }


    public boolean setInPreviewMode(boolean inPreviewMode)
    {
        return context_.setInPreviewMode(inPreviewMode);
    }
}
