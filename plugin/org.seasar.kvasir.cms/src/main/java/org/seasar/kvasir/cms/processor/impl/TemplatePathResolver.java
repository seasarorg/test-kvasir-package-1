package org.seasar.kvasir.cms.processor.impl;

import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.TemplateAbility;
import org.seasar.kvasir.util.io.Resource;


/**
 * 指定されたパスに対応するリソースとしてPageのテンプレートを返すようなLocalPathResolverです。
 * <p><code>init()</code>メソッドにnullを渡すと、各メソッドの呼び出し時に
 * gardRootPageが決定されるようになります。
 * 具体的には、<code>getRealPath()</code>などが呼び出された時点のheimのルートページを
 * gardRootPageとみなすようになります。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@SuppressWarnings("unchecked")
public class TemplatePathResolver extends AbstractPageResourcePathResolver
{
    private PageAlfr pageAlfr_;


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    @Override
    protected Page getGardRootPage()
    {
        Page gardRootPage = super.getGardRootPage();
        if (gardRootPage != null) {
            return gardRootPage;
        } else {
            return pageAlfr_.getRootPage(CmsUtils.getHeimId());
        }
    }


    @Override
    protected Resource getResourceObject(Page page)
    {
        Template template = page.getAbility(TemplateAbility.class)
            .getTemplate();
        if (template != null) {
            return template.getBodyResource();
        } else {
            return null;
        }
    }
}
