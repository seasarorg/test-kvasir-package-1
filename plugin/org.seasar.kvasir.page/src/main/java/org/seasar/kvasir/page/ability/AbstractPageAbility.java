package org.seasar.kvasir.page.ability;

import java.util.Iterator;

import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * この抽象クラスの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
abstract public class AbstractPageAbility
    implements PageAbility
{
    private PageAbilityAlfr     alfr_;

    protected Page              page_;


    protected AbstractPageAbility(PageAbilityAlfr alfr, Page page)
    {
        alfr_ = alfr;
        page_ = page;
    }


    public void delete()
    {
        alfr_.delete(page_);
    }


    public String[] getVariants()
    {
        return alfr_.getVariants(page_);
    }


    public Attribute getAttribute(String name, String variant)
    {
        return alfr_.getAttribute(page_, name, variant);
    }


    public void setAttribute(String name, String variant, Attribute value)
    {
        alfr_.setAttribute(page_, name, variant, value);
    }


    public void removeAttribute(String name, String variant)
    {
        alfr_.removeAttribute(page_, name, variant);
    }


    public void clearAttributes()
    {
        alfr_.clearAttributes(page_);
    }


    public boolean containsAttribute(String name, String variant)
    {
        return alfr_.containsAttribute(page_, name, variant);
    }


    public Iterator<String> attributeNames(String variant)
    {
        return alfr_.attributeNames(page_, variant);
    }


    public Iterator<String> attributeNames(String variant, AttributeFilter filter)
    {
        return alfr_.attributeNames(page_, variant, filter);
    }
}
