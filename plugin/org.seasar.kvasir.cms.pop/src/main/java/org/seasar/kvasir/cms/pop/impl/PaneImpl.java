package org.seasar.kvasir.cms.pop.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.seasar.framework.util.ArrayUtil;
import org.seasar.kvasir.cms.pop.Pane;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.PropertyUtils;


public class PaneImpl
    implements Pane
{
    private PopPlugin plugin_;

    private PageAlfr pageAlfr_;

    private int heimId_;

    private String id_;

    private Pop[] pops_;


    public PaneImpl(PopPlugin plugin, PageAlfr pageAlfr, int heimId, String id)
    {
        plugin_ = plugin;
        pageAlfr_ = pageAlfr;
        heimId_ = heimId;
        id_ = id;

        prepareForPops();
    }


    void prepareForPops()
    {
        PropertyAbility prop = pageAlfr_.getRootPage(heimId_).getAbility(
            PropertyAbility.class);

        List<Pop> popList = new ArrayList<Pop>();
        String[] popIds = PropertyUtils.toLines(prop.getProperty(getPopsKey()));
        for (int j = 0; j < popIds.length; j++) {
            String popId;
            int instanceId = Pop.INSTANCEID_DEFAULT;
            int delim = popIds[j].indexOf(Pop.INSTANCE_DELIMITERCHAR);
            if (delim < 0) {
                popId = popIds[j];
            } else {
                popId = popIds[j].substring(0, delim);
                try {
                    instanceId = Integer.parseInt(popIds[j]
                        .substring(delim + 1));
                } catch (NumberFormatException ignore) {
                }
            }
            Pop pop = plugin_.getPop(heimId_, popId, instanceId);
            if (pop == null) {
                plugin_.getLog().warn(
                    "Pop not found: id=" + popId + ", paneId=" + id_);
                continue;
            }

            popList.add(pop);
        }

        pops_ = popList.toArray(new Pop[0]);
    }


    String getPropertySuffix()
    {
        return PopPlugin.ID + ".pane." + id_ + ".";
    }


    String getPopsKey()
    {
        return getPropertySuffix() + "pops";
    }


    public String getId()
    {
        return id_;
    }


    public String getLabel(Locale locale)
    {
        return pageAlfr_.getRootPage(heimId_).getAbility(PropertyAbility.class)
            .getProperty(getPropertySuffix() + "label", locale);
    }


    public synchronized void addPop(Pop pop)
    {
        pops_ = (Pop[])ArrayUtil.add(pops_, pop);
        serializePops();
    }


    public synchronized void setPops(Pop[] pops)
    {
        pops_ = pops;
        serializePops();
    }


    public Pop[] getPops()
    {
        return pops_;
    }


    void serializePops()
    {
        Pop[] pops = getPops();
        PropertyAbility prop = pageAlfr_.getRootPage(heimId_).getAbility(
            PropertyAbility.class);
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (int i = 0; i < pops.length; i++) {
            sb.append(delim).append(pops[i].getId());
            delim = ",";
        }
        prop.setProperty(getPopsKey(), sb.toString());
    }
}
