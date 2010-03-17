package org.seasar.kvasir.cms.processor.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ContentPathResolver extends AbstractPageResourcePathResolver
{
    protected Resource getResourceObject(Page page)
    {
        Content content = page.getAbility(ContentAbility.class)
            .getLatestContent(Page.VARIANT_DEFAULT);
        if (content != null) {
            return content.getBodyResource();
        } else {
            return null;
        }
    }
}
