package org.seasar.kvasir.page.gard.delta.impl;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.gard.delta.Delta;
import org.seasar.kvasir.page.gard.delta.PageDifference;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class PageDifferenceImpl
    implements PageDifference
{
    private int type_ = Delta.TYPE_NONE;

    private Entry fieldEntry_ = new Entry();

    private Map<Class<? extends PageAbilityAlfr>, Map<String, Map<String, Delta>>> namesByAbilityAlfrMap_ = new HashMap<Class<? extends PageAbilityAlfr>, Map<String, Map<String, Delta>>>();


    public PageDifferenceImpl()
    {
    }


    public void setType(int type)
    {
        type_ = type;
    }


    public void addFieldDelta(Delta delta)
    {
        if (delta == null) {
            return;
        }

        fieldEntry_.addDelta(delta);

        if (type_ == Delta.TYPE_NONE) {
            type_ = Delta.TYPE_CHANGE;
        }
    }


    public void addAbilityAlfrDelta(
        Class<? extends PageAbilityAlfr> pageAbilityAlfrClass, String variant,
        Delta delta)
    {
        if (delta == null) {
            return;
        }

        Map<String, Map<String, Delta>> variantsByNameMap = namesByAbilityAlfrMap_
            .get(pageAbilityAlfrClass);
        if (variantsByNameMap == null) {
            variantsByNameMap = new HashMap<String, Map<String, Delta>>();
            namesByAbilityAlfrMap_.put(pageAbilityAlfrClass, variantsByNameMap);
        }
        String name = delta.getName();
        Map<String, Delta> deltaByVariantMap = variantsByNameMap.get(name);
        if (deltaByVariantMap == null) {
            deltaByVariantMap = new HashMap<String, Delta>();
            variantsByNameMap.put(name.intern(), deltaByVariantMap);
        }
        deltaByVariantMap.put(variant.intern(), delta);

        if (type_ == Delta.TYPE_NONE) {
            type_ = Delta.TYPE_CHANGE;
        }
    }


    /*
     * PageDifference
     */

    public int getType()
    {
        return type_;
    }


    public Delta[] getFieldDeltas(int type)
    {
        return fieldEntry_.getDeltas(type);
    }


    public String[] getAbilityDeltaNames(
        Class<? extends PageAbilityAlfr> pageAbilityAlfrClass)
    {
        Map<String, Map<String, Delta>> variantsByNameMap = namesByAbilityAlfrMap_
            .get(pageAbilityAlfrClass);
        if (variantsByNameMap == null) {
            return new String[0];
        }
        return variantsByNameMap.keySet().toArray(new String[0]);
    }


    public String[] getAbilityDeltaVariants(
        Class<? extends PageAbilityAlfr> pageAbilityAlfrClass, String name)
    {
        Map<String, Map<String, Delta>> variantsByNameMap = namesByAbilityAlfrMap_
            .get(pageAbilityAlfrClass);
        if (variantsByNameMap == null) {
            return new String[0];
        }
        Map<String, Delta> deltaByVariantMap = variantsByNameMap.get(name);
        if (deltaByVariantMap == null) {
            return new String[0];
        }
        return deltaByVariantMap.keySet().toArray(new String[0]);
    }


    public Delta getAbilityDelta(
        Class<? extends PageAbilityAlfr> pageAbilityAlfrClass, String name,
        String variant)
    {
        Map<String, Map<String, Delta>> variantsByNameMap = namesByAbilityAlfrMap_
            .get(pageAbilityAlfrClass);
        if (variantsByNameMap == null) {
            return null;
        }
        Map<String, Delta> deltaByVariantMap = variantsByNameMap.get(name);
        if (deltaByVariantMap == null) {
            return null;
        }
        return deltaByVariantMap.get(variant);
    }
}
