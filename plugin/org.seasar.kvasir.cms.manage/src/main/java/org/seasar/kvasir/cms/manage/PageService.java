package org.seasar.kvasir.cms.manage;

import java.util.Map;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;

import net.skirnir.freyja.render.Notes;


public interface PageService
{
    String FIELD_NAME = "name";

    String FIELD_OWNERUSERID = "ownerUserId";

    String FIELD_REVEALDATESTRING = "revealDateString";

    String FIELD_CONCEALDATESTRING = "concealDateString";

    String FIELD_LABEL = "label";

    String FIELD_DESCRIPTION = "description";

    String FIELD_ASFILE = "asFile";

    String FIELD_LISTING = "listing";

    String FIELD_INDEXED = "indexed";

    String PROP_NAME = "name";

    int USERID_UNKNOWN = -1;


    Page createPage(Page parent, PageMold mold, Map<String, String[]> fieldMap)
        throws DuplicatePageException;


    Notes updatePage(Page page, Map<String, String[]> fieldMap);
}
