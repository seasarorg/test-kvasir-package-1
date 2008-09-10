package org.seasar.kvasir.page.ability;

import java.util.Enumeration;
import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PropertyAbilityAlfr
    extends PageAbilityAlfr
{
    String SHORTID = "property";

    String SUBNAME_TYPE = "type";


    String getProperty(Page page, String name);


    String getProperty(Page page, String name, Locale locale);


    String findProperty(Page page, String name, Locale locale);


    Page findPageHoldingProperty(Page page, String name, Locale locale);


    boolean containsPropertyName(Page page, String name, Locale locale);


    String getProperty(Page page, String name, String variant);


    void setProperty(Page page, String name, String variant);


    void setProperty(Page page, String name, String variant, String value);


    void setProperties(Page page, PropertyHandler prop);


    void setProperties(Page page, String variant, PropertyHandler prop);


    void removeProperty(Page page, String name);


    void removeProperty(Page page, String name, String variant);


    void clearProperties(Page page);


    void clearProperties(Page page, String variant);


    void clearAllProperties(Page page);


    boolean containsPropertyName(Page page, String name);


    boolean containsPropertyName(Page page, String name, String variant);


    Enumeration<String> propertyNames(Page page);


    Enumeration<String> propertyNames(Page page, String variant);
}
