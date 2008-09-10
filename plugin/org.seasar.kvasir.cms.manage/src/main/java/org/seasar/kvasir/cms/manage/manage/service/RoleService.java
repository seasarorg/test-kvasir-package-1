package org.seasar.kvasir.cms.manage.manage.service;

import java.util.Map;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;

import net.skirnir.freyja.render.Notes;


public class RoleService extends GenericPageService
{
    public Page createPage(Page parent, PageMold mold,
        Map<String, String[]> fieldMap)
        throws DuplicatePageException
    {
        return super.createPage(parent, mold, fieldMap);
    }


    @Override
    public Notes updatePage(Page page, Map<String, String[]> fieldMap)
    {
        return super.updatePage(page, fieldMap);
    }
}
