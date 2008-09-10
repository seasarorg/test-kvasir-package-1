package org.seasar.kvasir.system.container.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.skirnir.xom.annotation.Child;


public class Components
{
    private List<Include> includeList_ = new ArrayList<Include>();

    private List<Component> componentList_ = new ArrayList<Component>();


    public Include[] getIncludes()
    {
        return includeList_.toArray(new Include[0]);
    }


    @Child
    public void addInclude(Include include)
    {
        includeList_.add(include);
    }


    public void setIncludes(Include[] includes)
    {
        includeList_.clear();
        includeList_.addAll(Arrays.asList(includes));
    }


    public Component[] getComponents()
    {
        return componentList_.toArray(new Component[0]);
    }


    @Child
    public void addComponent(Component component)
    {
        componentList_.add(component);
    }


    public void setComponents(Component[] components)
    {
        componentList_.clear();
        componentList_.addAll(Arrays.asList(components));
    }
}
