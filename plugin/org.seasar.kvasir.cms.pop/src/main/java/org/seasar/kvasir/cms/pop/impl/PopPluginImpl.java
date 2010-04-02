package org.seasar.kvasir.cms.pop.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.pop.Pane;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.extension.PopElement;
import org.seasar.kvasir.cms.processor.impl.VirtualHttpServletRequest;
import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.webapp.util.ServletUtils;


/**
 * <p><b>同期化：</b>
 * このクラスは<code>start()</code>
 * が呼び出された後はスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class PopPluginImpl extends AbstractPlugin<EmptySettings>
    implements PopPlugin
{
    private static final String SUFFIX_POP = "Pop";

    private static final String PATH_DEFAULTPOPIMAGE = "resources/defaultPopImage.jpg";

    private ContentAbilityPlugin contentPlugin_;

    private CmsPlugin cmsPlugin_;

    private PageAlfr pageAlfr_;

    private Map<Object, PopElement> popElementMap_;

    private PopElement[] popElements_;

    private Map<Integer, Map<String, Pane>> paneMap_;

    private Map<Integer, SortedMap<PopKey, Pop>> popMap_;

    private Resource defaultPopImageResource_;


    /*
     * PopPlugin
     */

    public void setCmsPlugin(CmsPlugin cmsPlugin)
    {
        cmsPlugin_ = cmsPlugin;
    }


    public PopElement[] getPopElements()
    {
        return popElements_;
    }


    public PopElement[] getPopElements(String gardId)
    {
        List<PopElement> elmementList = new ArrayList<PopElement>();
        for (int i = 0; i < popElements_.length; i++) {
            String gid = popElements_[i].getGardId();
            if ((gid == null || gardId.equals(gid))
                && !popElements_[i].isEmbedded()) {
                elmementList.add(popElements_[i]);
            }
        }
        return elmementList.toArray(new PopElement[0]);
    }


    public PopElement getPopElement(Object key)
    {
        if (key instanceof String) {
            String keyString = (String)key;
            int delim = keyString.indexOf(Pop.INSTANCE_DELIMITERCHAR);
            if (delim >= 0) {
                key = keyString.substring(0, delim);
            }
        }
        return popElementMap_.get(key);
    }


    public PopContext newContext(Page containerPage,
        HttpServletRequest request, HttpServletResponse response)
    {
        return newContext(containerPage, request, response, CmsUtils
            .getPageRequest(request));
    }


    public PopContext newContext(Page containerPage,
        HttpServletRequest request, HttpServletResponse response,
        PageRequest pageRequest)
    {
        // POPはどんなProcessor/Filterから呼び出されても同じ挙動をすべき。
        // そのため、VirtualHttpServletRequestが指定された場合は
        // オリジナルのHttpServletRequestに差し替えておく。
        return new PopContextImpl(new HashMap<Object, Object>(), containerPage,
            pageRequest.getLocale(), ServletUtils.getOriginalServletContext(),
            unwrap(request), response, pageRequest);
    }


    HttpServletRequest unwrap(HttpServletRequest request)
    {
        if (request instanceof VirtualHttpServletRequest) {
            return (HttpServletRequest)((VirtualHttpServletRequest)request)
                .getRequest();
        } else {
            return request;
        }
    }


    public synchronized void updatePanes(int heimId)
    {
        Map<String, Pane> map = paneMap_.get(heimId);
        if (map == null) {
            map = new LinkedHashMap<String, Pane>();
            paneMap_.put(heimId, map);
        } else {
            map.clear();
        }

        String[] paneIds = PropertyUtils.toLines(pageAlfr_.getRootPage(heimId)
            .getAbility(PropertyAbility.class).getProperty(ID + ".panes"));
        for (int i = 0; i < paneIds.length; i++) {
            map.put(paneIds[i], new PaneImpl(this, pageAlfr_, heimId,
                paneIds[i]));
        }
    }


    public Pane getPane(int heimId, String paneId)
    {
        return getPane(heimId, paneId, true);
    }


    public synchronized Pane getPane(int heimId, String paneId, boolean create)
    {
        Pane pane = null;
        Map<String, Pane> map = paneMap_.get(heimId);
        if (map != null) {
            pane = map.get(paneId);
        } else {
            map = new LinkedHashMap<String, Pane>();
            paneMap_.put(heimId, map);
        }
        if (pane == null && create) {
            pane = new PaneImpl(this, pageAlfr_, heimId, paneId);
            map.put(paneId, pane);
            serializePanes(heimId);
        }
        return pane;
    }


    public synchronized Pane[] getPanes(int heimId)
    {
        Map<String, Pane> map = paneMap_.get(heimId);
        if (map != null) {
            return map.values().toArray(new Pane[0]);
        } else {
            return new Pane[0];
        }
    }


    public synchronized void removePane(int heimId, String paneId)
    {
        Map<String, Pane> map = paneMap_.get(heimId);
        if (map != null) {
            map.remove(paneId);
            serializePanes(heimId);
        }
    }


    void serializePanes(int heimId)
    {
        Pane[] panes = getPanes(heimId);
        PropertyAbility prop = pageAlfr_.getRootPage(heimId).getAbility(
            PropertyAbility.class);
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (int i = 0; i < panes.length; i++) {
            sb.append(delim).append(panes[i].getId());
            delim = ",";
        }
        prop.setProperty(ID + ".panes", sb.toString());
    }


    public Pop getPop(int heimId, Object key, int instanceId)
    {
        return getPop(heimId, key, instanceId, true);
    }


    public synchronized Pop getPop(int heimId, Object key, int instanceId,
        boolean create)
    {
        PopElement popElement = getPopElement(key);
        if (popElement == null) {
            return null;
        }

        SortedMap<PopKey, Pop> map = popMap_.get(heimId);
        if (map == null) {
            map = new TreeMap<PopKey, Pop>();
            popMap_.put(heimId, map);
        }

        String popId = popElement.getFullId();
        if (create && instanceId == Pop.INSTANCEID_NEW) {
            PopKey[] popKeys = map.subMap(
                new PopKey(popId, Pop.INSTANCEID_DEFAULT),
                new PopKey(popId, Integer.MAX_VALUE)).keySet().toArray(
                new PopKey[0]);
            if (popKeys.length == 0) {
                instanceId = Pop.INSTANCEID_DEFAULT + 1;
            } else {
                instanceId = popKeys[popKeys.length - 1].getInstanceId() + 1;
            }
        }

        PopKey popKey = new PopKey(popId, instanceId);
        Pop pop = map.get(popKey);
        if (pop == null && create) {
            pop = popElement.newPop(heimId, instanceId);
            map.put(popKey, pop);
        }

        return pop;
    }


    public Pop getPop(int heimId, String id)
    {
        return getPop(heimId, id, true);
    }


    public Pop getPop(int heimId, String id, boolean create)
    {
        String popId;
        int instanceId = Pop.INSTANCEID_DEFAULT;
        int delim = id.indexOf(Pop.INSTANCE_DELIMITERCHAR);
        if (delim < 0) {
            popId = id;
        } else {
            popId = id.substring(0, delim);
            try {
                instanceId = Integer.parseInt(id.substring(delim + 1));
            } catch (NumberFormatException ignore) {
            }
        }
        return getPop(heimId, popId, instanceId, create);
    }


    String getPopId(Object key)
    {
        PopElement popElement = getPopElement(key);
        if (popElement != null) {
            return popElement.getFullId();
        } else {
            return null;
        }
    }


    public synchronized Pop[] getPops(int heimId)
    {
        return popMap_.values().toArray(new Pop[0]);
    }


    public void removePop(int heimId, Object key, int instanceId)
    {
        removePop(getPop(heimId, key, instanceId, false));
    }


    public synchronized void removePop(Pop pop)
    {
        if (pop == null) {
            return;
        }

        pop.notifyRemoving();

        SortedMap<PopKey, Pop> map = popMap_.get(pop.getHeimId());
        map.remove(new PopKey(pop.getPopId(), pop.getInstanceId()));
    }


    public Resource getDefaultPopImageResource()
    {
        return defaultPopImageResource_;
    }


    /*
     * AbstractPlugin
     */

    protected boolean doStart()
    {
        popElements_ = getExtensionElements(PopElement.class);
        popElementMap_ = new LinkedHashMap<Object, PopElement>();
        for (int i = 0; i < popElements_.length; i++) {
            PopElement element = popElements_[i];
            element.init(contentPlugin_, this);
            String id = element.getId();
            popElementMap_.put(id, element);
            String fullId = element.getFullId();
            popElementMap_.put(fullId, element);
            if (id.endsWith(SUFFIX_POP)) {
                popElementMap_.put(id.substring(0, id.length()
                    - SUFFIX_POP.length()), element);
                popElementMap_.put(fullId.substring(0, fullId.length()
                    - SUFFIX_POP.length()), element);
            }
        }

        defaultPopImageResource_ = getHomeDirectory().getChildResource(
            PATH_DEFAULTPOPIMAGE);

        return true;
    }


    @Override
    public void notifyKvasirStarted()
    {
        // 一番最初だけはdistribution gardのインストールが終わってからでないと
        // rootが持っているpane/pop情報を取得できないので。
        popMap_ = new HashMap<Integer, SortedMap<PopKey, Pop>>();
        paneMap_ = new HashMap<Integer, Map<String, Pane>>();
        int[] heimIds = cmsPlugin_.getHeimIds();
        for (int i = 0; i < heimIds.length; i++) {
            updatePanes(heimIds[i]);
        }
    }


    protected void doStop()
    {
        contentPlugin_ = null;
        pageAlfr_ = null;

        popElementMap_ = null;
        popElements_ = null;
        paneMap_ = null;
        popMap_ = null;
    }


    /*
     * for framework
     */

    public void setContentAbilityPlugin(ContentAbilityPlugin contentPlugin)
    {
        contentPlugin_ = contentPlugin;
    }


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }
}
