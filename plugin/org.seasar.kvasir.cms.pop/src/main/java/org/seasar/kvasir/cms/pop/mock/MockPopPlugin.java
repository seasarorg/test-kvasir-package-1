package org.seasar.kvasir.cms.pop.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.mock.plugin.MockPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.pop.Pane;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.extension.PopElement;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.io.Resource;


public class MockPopPlugin extends MockPlugin<EmptySettings>
    implements PopPlugin
{
    public MockPopPlugin()
    {
        setId(PopPlugin.ID);
    }


    public Resource getDefaultPopImageResource()
    {
        return null;
    }


    public void updatePanes(int heimId)
    {
    }


    public Pane getPane(int heimId, String paneId)
    {
        return null;
    }


    public Pane getPane(int heimId, String paneId, boolean create)
    {
        return null;
    }


    public Pane[] getPanes(int heimId)
    {
        return null;
    }


    public Pop getPop(int heimId, Object key, int instanceId)
    {
        return null;
    }


    public Pop getPop(int heimId, Object key, int instanceId, boolean create)
    {
        return null;
    }


    public Pop getPop(int heimId, String id)
    {
        return null;
    }


    public Pop getPop(int heimId, String id, boolean create)
    {
        return null;
    }


    public PopElement getPopElement(Object key)
    {
        return null;
    }


    public PopElement[] getPopElements()
    {
        return new PopElement[0];
    }


    public PopElement[] getPopElements(String gardId)
    {
        return new PopElement[0];
    }


    public Pop[] getPops(int heimId)
    {
        return null;
    }


    public PopContext newContext(Page containerPage,
        HttpServletRequest request, HttpServletResponse response,
        PageRequest pageRequest)
    {
        return null;
    }


    public PopContext newContext(Page containerPage,
        HttpServletRequest request, HttpServletResponse response)
    {
        return null;
    }


    public void removePane(int heimId, String paneId)
    {
    }


    public void removePop(int heimId, Object key, int instanceId)
    {
    }


    public void removePop(Pop pop)
    {
    }
}
