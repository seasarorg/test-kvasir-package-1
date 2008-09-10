package org.seasar.kvasir.system.plugin;

import org.seasar.kvasir.base.plugin.descriptor.Extension;

import net.skirnir.xom.Element;


class ElementPair
{
    private Element element_;

    private Extension extension_;


    public ElementPair(Element element, Extension extension)
    {
        element_ = element;
        extension_ = extension;
    }


    public Element getElement()
    {
        return element_;
    }


    public Extension getExtension()
    {
        return extension_;
    }
}