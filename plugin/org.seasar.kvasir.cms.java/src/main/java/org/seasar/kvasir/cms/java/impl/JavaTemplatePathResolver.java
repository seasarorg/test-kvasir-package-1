package org.seasar.kvasir.cms.java.impl;

import org.seasar.kvasir.cms.processor.impl.AbstractPageResourcePathResolver;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.TemplateAbility;
import org.seasar.kvasir.util.io.Resource;


/**
 * <code>/path/to/page</code>に対応するリソースとして、
 * ページの持つテンプレートのうち「java」バリアントの内容を返すような
 * LocalPathResolverです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class JavaTemplatePathResolver extends AbstractPageResourcePathResolver
{
    public static final String VARIANT_JAVA = "java";


    protected Resource getResourceObject(Page page)
    {
        Template template = page.getAbility(TemplateAbility.class).getTemplate(
            VARIANT_JAVA);
        if (template != null) {
            return template.getBodyResource();
        } else {
            return null;
        }
    }
}
