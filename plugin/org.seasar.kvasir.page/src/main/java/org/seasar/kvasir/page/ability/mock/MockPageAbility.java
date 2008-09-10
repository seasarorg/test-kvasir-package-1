package org.seasar.kvasir.page.ability.mock;

import java.util.Iterator;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.AttributeFilter;
import org.seasar.kvasir.page.ability.PageAbility;


public class MockPageAbility
    implements PageAbility
{
    private Page page_;


    public MockPageAbility(Page page)
    {
        page_ = page;
    }


    protected Page getPage()
    {
        return page_;
    }


    public Iterator<String> attributeNames(String variant)
    {
        return null;
    }


    public Iterator<String> attributeNames(String variant,
        AttributeFilter filter)
    {
        return null;
    }


    public void clearAttributes()
    {
    }


    public boolean containsAttribute(String name, String variant)
    {
        return false;
    }


    public void delete()
    {
    }


    public Attribute getAttribute(String name, String variant)
    {
        return null;
    }


    public String[] getVariants()
    {
        return null;
    }


    public void removeAttribute(String name, String variant)
    {
    }


    public void setAttribute(String name, String variant, Attribute value)
    {
    }
}
