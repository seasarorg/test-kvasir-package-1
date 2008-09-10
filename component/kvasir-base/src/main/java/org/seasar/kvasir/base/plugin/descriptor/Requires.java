package org.seasar.kvasir.base.plugin.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Parent;


public class Requires
{
    private PluginDescriptor parent_;

    private List<Import> importList_ = new ArrayList<Import>();


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<requires>");
        for (Iterator<Import> itr = importList_.iterator(); itr.hasNext();) {
            sb.append(itr.next());
        }
        sb.append("</requires>");
        return sb.toString();
    }


    public PluginDescriptor getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(PluginDescriptor parent)
    {
        parent_ = parent;
    }


    public Import[] getImports()
    {
        return importList_.toArray(new Import[0]);
    }


    @Child
    public void addImport(Import i)
    {
        importList_.add(i);
    }


    public void setImports(Import[] imports)
    {
        importList_.clear();
        importList_.addAll(Arrays.asList(imports));
    }
}
