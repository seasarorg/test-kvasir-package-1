package org.seasar.kvasir.cms.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.plugin.SettingsEvent;
import org.seasar.kvasir.base.plugin.SettingsListener;
import org.seasar.kvasir.base.webapp.WebappPlugin;
import org.seasar.kvasir.base.webapp.impl.ResourceContent;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.RequestSnapshot;
import org.seasar.kvasir.cms.TemporaryContent;
import org.seasar.kvasir.cms.extension.PageFilterPhaseElement;
import org.seasar.kvasir.cms.extension.PageProcessorPhaseElement;
import org.seasar.kvasir.cms.setting.CmsPluginSettings;
import org.seasar.kvasir.cms.setting.HeimElement;
import org.seasar.kvasir.cms.util.PresentationUtils;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.SerializationUtils;
import org.seasar.kvasir.webapp.util.ServletUtils;


/**
 * {@link CmsPlugin}の実装クラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class CmsPluginImpl extends AbstractPlugin<CmsPluginSettings>
    implements CmsPlugin
{
    private WebappPlugin webappPlugin_;

    private PagePlugin pagePlugin_;

    private PageAlfr pageAlfr_;

    private String[] pageFilterPhases_;

    private String pageFilterDefaultPhase_;

    private String[] pageProcessorPhases_;

    private String pageProcessorDefaultPhase_;

    private int[] heimIds_;

    private Map<String, Integer> heimIdBySiteMap_;

    private Map<Integer, String> siteByHeimIdMap_;


    public void setWebappPlugin(WebappPlugin webappPlugin)
    {
        webappPlugin_ = webappPlugin;
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    /*
     * CmsPlugin
     */

    public void login(HttpServletRequest request, User user)
    {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            if (log_.isDebugEnabled()) {
                log_.debug("SESSION INVALIDATED!!!!!!!!!!!");
            }
        }
        if (!user.isAnonymous()) {
            if (log_.isDebugEnabled()) {
                log_.debug("GET SESSION(TRUE)!!!!!!!!!!!");
            }
            session = request.getSession();
            session.setAttribute(ATTR_USERID, new Integer(user.getId()));
        }
    }


    public User getUser(HttpServletRequest request)
    {
        User user = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer userId = (Integer)session.getAttribute(ATTR_USERID);
            if (userId != null) {
                user = (User)pageAlfr_.getPage(User.class, userId.intValue());
                if (user == null) {
                    // ユーザが存在しないのでセッションから除去しておく。
                    session.removeAttribute(ATTR_USERID);
                }
            }
        }
        return user;
    }


    public void logout(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            if (log_.isDebugEnabled()) {
                log_.debug("SESSION INVALIDATED!!!!!!!!!!!");
            }
        }
    }


    public RequestSnapshot enter(ServletContext servletContext,
        HttpServletRequest httpRequest)
    {
        ComponentContainer container = getKvasir().getRootComponentContainer();

        RequestSnapshot snapshot = new RequestSnapshotImpl(ServletUtils
            .getContextServletContext(), (HttpServletRequest)container
            .getRequest());

        ServletUtils.setContextServletContext(servletContext);
        container.setRequest(httpRequest);

        return snapshot;
    }


    public void leave(RequestSnapshot snapshot)
    {
        ComponentContainer container = getKvasir().getRootComponentContainer();

        container.setRequest(snapshot.getHttpServletRequest());
        ServletUtils.setContextServletContext(snapshot.getServletConext());
    }


    public String[] getPageFilterPhases()
    {
        return pageFilterPhases_;
    }


    public String getPageFilterDefaultPhase()
    {
        return pageFilterDefaultPhase_;
    }


    public String[] getPageProcessorPhases()
    {
        return pageProcessorPhases_;
    }


    public String getPageProcessorDefaultPhase()
    {
        return pageProcessorDefaultPhase_;
    }


    /*
     * AbstractPlugin
     */

    @Override
    public Class<CmsPluginSettings> getSettingsClass()
    {
        return CmsPluginSettings.class;
    }


    protected boolean doStart()
    {
        pageAlfr_ = pagePlugin_.getPageAlfr();

        PageType[] pageTypes = pagePlugin_.getPageTypes();
        for (int i = 0; i < pageTypes.length; i++) {
            webappPlugin_.registerStaticContent(
                PresentationUtils.ICON_PATH_PREFIX + "/" + pageTypes[i].getId()
                    + PresentationUtils.ICON_PATH_SUFFIX, new ResourceContent(
                    pageTypes[i].getIconResource()));
            webappPlugin_.registerStaticContent(
                PresentationUtils.ICON_PATH_PREFIX + "/" + pageTypes[i].getId()
                    + "_concealed" + PresentationUtils.ICON_PATH_SUFFIX,
                new ResourceContent(pageTypes[i].getConcealedIconResource()));
        }

        PageFilterPhaseElement[] pfpElements = getExtensionElements(
            PageFilterPhaseElement.class, true);
        pageFilterPhases_ = new String[pfpElements.length];
        for (int i = 0; i < pfpElements.length; i++) {
            pageFilterPhases_[i] = pfpElements[i].getId();
            if (pfpElements[i].isDefault()) {
                pageFilterDefaultPhase_ = pfpElements[i].getId();
            }
        }
        if (pageFilterDefaultPhase_ == null) {
            log_.error("Default phase for page-filter-phase is not set");
            return false;
        }

        PageProcessorPhaseElement[] pppElements = getExtensionElements(
            PageProcessorPhaseElement.class, true);
        pageProcessorPhases_ = new String[pppElements.length];
        for (int i = 0; i < pppElements.length; i++) {
            pageProcessorPhases_[i] = pppElements[i].getId();
            if (pppElements[i].isDefault()) {
                pageProcessorDefaultPhase_ = pppElements[i].getId();
            }
        }
        if (pageProcessorDefaultPhase_ == null) {
            log_.error("Default phase for page-processor-phase is not set");
            return false;
        }

        addSettingsListener(new SettingsListener<CmsPluginSettings>() {
            public void notifyUpdated(SettingsEvent<CmsPluginSettings> event)
            {
                prepareForHeims();
            }
        });

        return true;
    }


    void prepareForHeims()
    {
        Set<Integer> heimIdSet = new TreeSet<Integer>();
        heimIdSet.add(PathId.HEIM_MIDGARD);
        Map<String, Integer> heimIdBySiteMap = new HashMap<String, Integer>();
        Map<Integer, String> siteByHeimIdMap = new HashMap<Integer, String>();
        HeimElement[] heims = settings_.getHeims();
        for (int i = 0; i < heims.length; i++) {
            Integer heimId = Integer.valueOf(heims[i].getId());

            heimIdSet.add(heimId);

            String[] sites = heims[i].getSites();
            for (int j = 0; j < sites.length; j++) {
                String site = sites[j];
                if (site.endsWith("/")) {
                    site = site.substring(0, site.length() - 1);
                }
                heimIdBySiteMap.put(site, heimId);
                if (!siteByHeimIdMap.containsKey(heimId)) {
                    siteByHeimIdMap.put(heimId, site);
                }
            }
        }
        heimIds_ = new int[heimIdSet.size()];
        int i = 0;
        for (Iterator<Integer> itr = heimIdSet.iterator(); itr.hasNext();) {
            int heimId = itr.next();
            heimIds_[i++] = heimId;
            createRootDirectoryIfNotExists(heimId);
        }
        heimIdBySiteMap_ = heimIdBySiteMap;
        siteByHeimIdMap_ = siteByHeimIdMap;
    }


    void createRootDirectoryIfNotExists(int heimId)
    {
        if (pageAlfr_.getRootPage(heimId) == null) {
            try {
                pageAlfr_.createRootPage(heimId);
            } catch (DuplicatePageException ignore) {
            }
        }
    }


    protected void doStop()
    {
        heimIds_ = null;
        heimIdBySiteMap_ = null;
        siteByHeimIdMap_ = null;

        webappPlugin_ = null;
        pagePlugin_ = null;

        pageAlfr_ = null;
    }


    public int determineHeimId(HttpServletRequest request)
    {
        int heimId = PathId.HEIM_MIDGARD;
        if (request != null) {
            heimId = determineHeimId(ServletUtils.getDomainURL(request));
        }
        return heimId;
    }


    public int determineHeimId(String site)
    {
        int heimId = PathId.HEIM_MIDGARD;
        if (site != null) {
            if (site.endsWith("/")) {
                site = site.substring(0, site.length() - 1);
            }
            if (heimIdBySiteMap_.containsKey(site)) {
                heimId = heimIdBySiteMap_.get(site);
            }
        }
        return heimId;
    }


    public int[] getHeimIds()
    {
        return heimIds_;
    }


    public String getSite(int heimId)
    {
        return siteByHeimIdMap_.get(heimId);
    }


    public boolean enterInSitePreviewMode(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        synchronized (session.getId().intern()) {
            boolean oldStatus = session.getAttribute(ATTR_SITEPREVIEWMODE) != null;
            session.setAttribute(ATTR_SITEPREVIEWMODE, Boolean.TRUE);
            return oldStatus;
        }
    }


    public boolean isInSitePreviewMode(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        synchronized (session.getId().intern()) {
            return session.getAttribute(ATTR_SITEPREVIEWMODE) != null;
        }
    }


    public void leaveSitePreviewMode(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        synchronized (session.getId().intern()) {
            session.removeAttribute(ATTR_SITEPREVIEWMODE);
        }
    }


    public TemporaryContent getTemporaryContent(Page page, String variant)
    {
        if (page == null) {
            return null;
        }

        PropertyAbility prop = page.getAbility(PropertyAbility.class);
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getInnerClassLoader());
            return (TemporaryContent)SerializationUtils.deserialize(prop
                .getProperty(PROP_TEMPORARYCONTENT, variant));
        } catch (Throwable t) {
            log_.warn("Cannot deserialize temporary content: page="
                + page.toString() + ", variant=" + variant, t);
            return null;
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }


    public void setTemporaryContent(Page page, String variant,
        TemporaryContent temporaryContent)
    {
        if (page == null) {
            return;
        }

        if (temporaryContent == null) {
            removeTemporaryContent(page, variant);
            return;
        }

        if (temporaryContent.getMediaType() == null) {
            Content content = page.getAbility(ContentAbility.class)
                .getLatestContent(variant);
            temporaryContent.setMediaType(content != null ? content
                .getMediaType() : "text/plain");
        }
        if (temporaryContent.getBodyString() == null) {
            temporaryContent.setBodyString("");
        }
        if (temporaryContent.getCreateDate() == null) {
            temporaryContent.setCreateDate(new Date());
        }

        page.getAbility(PropertyAbility.class).setProperty(
            PROP_TEMPORARYCONTENT, variant,
            SerializationUtils.serialize(temporaryContent));
    }


    public void removeTemporaryContent(Page page, String variant)
    {
        if (page == null) {
            return;
        }

        page.getAbility(PropertyAbility.class).removeProperty(
            PROP_TEMPORARYCONTENT, variant);
    }
}
