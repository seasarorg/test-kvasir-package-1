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
public interface PropertyAbility
    extends PageAbility
{
    String PROP_PAGEGARD_ID = "_framework.pageGard.id";

    String PROP_PAGEGARD_VERSION = "_framework.pageGard.version";

    String PROPPREFIX_PAGEGARD_INSTALLED = "_framework.pageGard.installed.";

    String PROPPREFIX_PAGEGARD_VERSION = "_framework.pageGard.version.";

    String PROP_LABEL = "label";

    String PROP_DESCRIPTION = "description";

    String PROP_SUBTYPE = "subType";


    /**
     * 指定された名前のプロパティの値を返します。
     * <p>このメソッドは<code>getProperty(name, Page.VARIANT_DEFAULT)</code>
     * と同じです。
     * </p>
     * 
     * @param name プロパティ名。
     * @return 値。
     * プロパティの値が存在しない場合はnullが返されます。
     */
    String getProperty(String name);


    /**
     * 指定された名前のプロパティの値のうち指定されたロケールに対応するものを返します。
     * 
     * @param name プロパティ名。
     * @param locale ロケール。nullを指定してはいけません。
     * @return 値。
     * プロパティの値が存在しない場合はnullが返されます。
     */
    String getProperty(String name, Locale locale);


    String findProperty(String name, Locale locale);


    Page findPageHoldingProperty(String name, Locale locale);


    boolean containsPropertyName(String name, Locale locale);


    /**
     * 指定された名前のプロパティの値のうち指定されたバリアントに対応するものを返します。
     * 
     * @param name プロパティ名。
     * @param locale バリアント。nullを指定してはいけません。
     * @return 値。
     * プロパティの値が存在しない場合はnullが返されます。
     */
    String getProperty(String name, String variant);


    void setProperty(String name, String value);


    void setProperty(String name, String variant, String value);


    void setProperties(String variant, PropertyHandler prop);


    void setProperties(PropertyHandler prop);


    void removeProperty(String name);


    void removeProperty(String name, String variant);


    void clearProperties();


    void clearProperties(String variant);


    void clearAllProperties();


    boolean containsPropertyName(String name);


    boolean containsPropertyName(String name, String variant);


    Enumeration<String> propertyNames();


    Enumeration<String> propertyNames(String variant);
}
