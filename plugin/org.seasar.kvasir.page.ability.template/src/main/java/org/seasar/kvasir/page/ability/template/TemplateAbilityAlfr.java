package org.seasar.kvasir.page.ability.template;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface TemplateAbilityAlfr
    extends PageAbilityAlfr
{
    String SHORTID = "template";

    String ATTR_TYPE = "type";

    String ATTR_RESPONSECONTENTTYPE = "responseContentType";

    String ATTR_BODY = "body";

    String ATTR_MODIFYDATE = "lastModified";


    Template getTemplate(Page page);


    Template getTemplate(Page page, String variant);


    void setTemplate(Page page, String template);


    void setTemplate(Page page, String variant, String template);


    void removeTemplate(Page page);


    void removeTemplate(Page page, String variant);


    void clearAllTemplates(Page page);


    String getType(Page page);


    void setType(Page page, String type);


    String getResponseContentType(Page page);


    void setResponseContentType(Page page, String responseContentType);
}
