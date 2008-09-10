package org.seasar.kvasir.base.plugin.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.skirnir.xom.Element;
import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Parent;
import net.skirnir.xom.annotation.Required;


public class Extension
{
    private PluginDescriptor parent_;

    private String point_;

    private List<Element> elementList_ = new ArrayList<Element>();


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<extension");
        if (point_ != null) {
            sb.append(" point=\"").append(point_).append("\"");
        }
        sb.append(">");
        for (Iterator<Element> itr = elementList_.iterator(); itr.hasNext();) {
            sb.append(itr.next());
        }
        sb.append("</extension>");
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


    public String getPoint()
    {
        return point_;
    }


    @Attribute
    @Required
    public void setPoint(String point)
    {
        point_ = point;
    }


    public Element[] getElements()
    {
        return elementList_.toArray(new Element[0]);
    }


    @Child("*")
    public void addElement(Element element)
    {
        elementList_.add(element);
    }


    public void setElements(Element[] elements)
    {
        elementList_.clear();
        elementList_.addAll(Arrays.asList(elements));
    }
}
