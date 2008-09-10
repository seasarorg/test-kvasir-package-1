package org.seasar.kvasir.cms.zpt.impl;

import org.seasar.kvasir.cms.processor.impl.TemplatePathResolver;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.template.TemplateAbility;


public class ZptTemplatePathResolver extends TemplatePathResolver
{
    private static final String TEMPLATETYPE_ZPT = "zpt";


    @Override
    protected String getRealPath(Page page)
    {
        String realPath = super.getRealPath(page);
        if (realPath != null) {
            TemplateAbility template = page.getAbility(TemplateAbility.class);
            if (!TEMPLATETYPE_ZPT.equals(template.getType())) {
                realPath = null;
            }
        }
        return realPath;
    }
}
