package org.seasar.kvasir.page.ability.template;

import org.seasar.kvasir.page.ability.PageAbility;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface TemplateAbility
    extends PageAbility
{
    Template getTemplate();


    Template getTemplate(String variant);


    void setTemplate(String template);


    void setTemplate(String variant, String template);


    void removeTemplate();


    void removeTemplate(String variant);


    void clearAllTemplates();


    String getType();


    void setType(String type);


    String getResponseContentType();


    void setResponseContentType(String responseContentType);


    String[] getVariants();
}
