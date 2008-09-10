package org.seasar.kvasir.cms.manage.manage.service;

import java.util.Map;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.PropertyUtils;

import net.skirnir.freyja.render.Notes;


abstract public class NodePageService extends GenericPageService
{
    public static final String FIELD_LORD = "lord";


    @Override
    public Notes updatePage(Page page, Map<String, String[]> fieldMap)
    {
        Notes notes = super.updatePage(page, fieldMap);
        if (notes != null) {
            return notes;
        }

        boolean lord = PropertyUtils.valueOf(getString(fieldMap, FIELD_LORD),
            false);
        if (page.isLord() != lord) {
            page.setAsLord(lord);
        }

        return null;
    }
}
