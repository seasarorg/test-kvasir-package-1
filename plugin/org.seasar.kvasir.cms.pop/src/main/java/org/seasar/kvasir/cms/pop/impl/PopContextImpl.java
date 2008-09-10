package org.seasar.kvasir.cms.pop.impl;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.extension.PopElement;
import org.seasar.kvasir.cms.pop.util.PopUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.collection.AttributeReader;
import org.seasar.kvasir.util.el.impl.MapVariableResolver;


public class PopContextImpl extends MapVariableResolver
    implements PopContext
{
    private Map<Object, Object> map_;

    private boolean inPreviewMode_;


    public PopContextImpl(Map<Object, Object> map, Page containerPage,
        Locale locale, ServletContext servletContext,
        HttpServletRequest request, HttpServletResponse response,
        PageRequest pageRequest)
    {
        super(map);
        map_ = map;
        map.put(VARNAME_CONTAINERPAGE, containerPage);
        map.put(VARNAME_LOCALE, locale);
        map.put(Locale.class, locale);
        map.put(VARNAME_PAGEREQUEST, pageRequest);
        map.put(PageRequest.class, pageRequest);
        map.put(VARNAME_MY, (pageRequest != null ? pageRequest.getMy() : null));
        map.put(VARNAME_THAT, (pageRequest != null ? pageRequest.getThat()
            : null));
        map.put(VARNAME_REQUEST, request);
        map.put(HttpServletRequest.class, request);
        map.put(VARNAME_RESPONSE, response);
        map.put(HttpServletResponse.class, response);
        map.put(VARNAME_APPLICATION, servletContext);
        map.put(ServletContext.class, servletContext);

        HttpSession session = null;
        if (request != null) {
            session = request.getSession(false);
        }
        if (session != null) {
            map.put(VARNAME_SESSION, session);
            map.put(HttpSession.class, session);
            map.put(VARNAME_TRANSIENT, new Transient());
        }
    }


    public Page getContainerPage()
    {
        return (Page)getValue(VARNAME_CONTAINERPAGE);
    }


    public Locale getLocale()
    {
        return (Locale)getValue(Locale.class);
    }


    public HttpServletRequest getRequest()
    {
        return (HttpServletRequest)getValue(HttpServletRequest.class);
    }


    public HttpServletResponse getResponse()
    {
        return (HttpServletResponse)getValue(HttpServletResponse.class);
    }


    public HttpSession getSession()
    {
        return (HttpSession)getValue(HttpSession.class);
    }


    public ServletContext getApplication()
    {
        return (ServletContext)getValue(ServletContext.class);
    }


    public PageRequest getPageRequest()
    {
        return (PageRequest)getValue(PageRequest.class);
    }


    public PageDispatch getMy()
    {
        return (PageDispatch)getValue(VARNAME_MY);
    }


    public Page getMyPage()
    {
        PageDispatch my = getMy();
        if (my != null) {
            return my.getPage();
        } else {
            return null;
        }
    }


    public PageDispatch getThat()
    {
        return (PageDispatch)getValue(VARNAME_THAT);
    }


    public Page getThatPage()
    {
        PageDispatch that = getThat();
        if (that != null) {
            return that.getPage();
        } else {
            return null;
        }
    }


    public Plugin<?> getPlugin()
    {
        Pop pop = getPop();
        if (pop != null) {
            PopElement element = pop.getElement();
            if (element != null) {
                return element.getPlugin();
            }
        }
        return null;
    }


    public Pop getPop()
    {
        return (Pop)getValue(Pop.class);
    }


    public Pop setPop(Pop pop)
    {
        Pop old = getPop();
        map_.put(VARNAME_POP, pop);
        map_.put(Pop.class, pop);
        return old;
    }


    public boolean isInPreviewMode()
    {
        return inPreviewMode_;
    }


    public boolean setInPreviewMode(boolean inPreviewMode)
    {
        boolean old = inPreviewMode_;
        inPreviewMode_ = inPreviewMode;
        return old;
    }


    public class Transient
        implements AttributeReader
    {
        public Object getAttribute(String name)
        {
            Pop pop = getPop();
            if (pop != null) {
                HttpSession session = getSession();
                String key = PopUtils.getTransientKey(pop.getId(), name);
                Object value = session.getAttribute(key);
                if (value != null) {
                    session.removeAttribute(key);
                }
                return value;
            } else {
                return null;
            }
        }
    }
}
