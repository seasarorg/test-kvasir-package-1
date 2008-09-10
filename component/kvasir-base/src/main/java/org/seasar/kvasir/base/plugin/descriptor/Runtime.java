package org.seasar.kvasir.base.plugin.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Parent;


public class Runtime
{
    private PluginDescriptor parent_;

    private List<Library> libraryList_ = new ArrayList<Library>();


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<runtime>");
        for (Iterator<Library> itr = libraryList_.iterator(); itr.hasNext();) {
            sb.append(itr.next());
        }
        sb.append("</runtime>");
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


    public Library[] getLibraries()
    {
        return libraryList_.toArray(new Library[0]);
    }


    @Child
    public void addLibrary(Library library)
    {
        libraryList_.add(library);
    }


    public void setLibraries(Library[] libraries)
    {
        libraryList_.clear();
        libraryList_.addAll(Arrays.asList(libraries));
    }
}
