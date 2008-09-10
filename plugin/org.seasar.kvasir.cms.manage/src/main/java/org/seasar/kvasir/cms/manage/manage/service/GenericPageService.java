package org.seasar.kvasir.cms.manage.manage.service;

import static org.seasar.kvasir.page.ability.PropertyAbility.PROP_DESCRIPTION;
import static org.seasar.kvasir.page.ability.PropertyAbility.PROP_LABEL;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.seasar.kvasir.base.mime.MimePlugin;
import org.seasar.kvasir.cms.manage.PageService;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.search.SearchSystem;
import org.seasar.kvasir.page.type.User;

import net.skirnir.freyja.render.Notes;


public class GenericPageService
    implements PageService
{
    private MimePlugin mimePlugin_;


    public void setMimePlugin(MimePlugin mimePlugin)
    {
        mimePlugin_ = mimePlugin;
    }


    public Page createPage(Page parent, PageMold mold,
        Map<String, String[]> fieldMap)
        throws DuplicatePageException
    {
        String name = getString(fieldMap, FIELD_NAME).trim();
        mold.setName(name);

        Date revealDate = getDate(fieldMap, FIELD_REVEALDATESTRING);
        if (revealDate != null) {
            mold.setRevealDate(revealDate);
        }

        Date concealDate = getDate(fieldMap, FIELD_CONCEALDATESTRING);
        if (concealDate != null) {
            mold.setRevealDate(concealDate);
        }

        Boolean asFile = getBoolean(fieldMap, FIELD_ASFILE);
        if (asFile != null) {
            mold.setAsFile(asFile.booleanValue());
        }

        Boolean listing = getBoolean(fieldMap, FIELD_LISTING);
        if (listing != null) {
            mold.setListing(listing.booleanValue());
        } else {
            String mimeType = mimePlugin_.getMimeMappings().getMimeType(name);
            if (mimeType != null && !mimeType.startsWith("text/")) {
                mold.setAsFile(true);
            }
        }

        Page page = parent.createChild(mold);

        PropertyAbility prop = page.getAbility(PropertyAbility.class);

        String label = getString(fieldMap, FIELD_LABEL);
        if (label == null || label.trim().length() == 0) {
            label = name;
        } else {
            label = label.trim();
        }
        prop.setProperty(PROP_LABEL, label);

        String description = getString(fieldMap, FIELD_DESCRIPTION);
        if (description != null) {
            prop.setProperty(PROP_DESCRIPTION, description);
        }

        Boolean indexed = getBoolean(fieldMap, FIELD_INDEXED);
        if (indexed != null && !indexed.booleanValue()) {
            prop.setProperty(SearchSystem.PROP_INDEXED, String.valueOf(false));
        }

        return page;
    }


    Date getDate(Map<String, String[]> fieldMap, String fieldName)
    {
        String string = getString(fieldMap, fieldName);
        if (string == null) {
            return null;
        }
        string = string.trim();
        if (string.length() == 0) {
            return null;
        }
        try {
            return PageUtils.parseDate(string);
        } catch (ParseException ex) {
            // FIXME HTMLでエラーメッセージを画面に表示するようにしたい。
            return null;
        }
    }


    public Notes updatePage(Page page, Map<String, String[]> fieldMap)
    {
        Integer ownerUserId = getInteger(fieldMap, FIELD_OWNERUSERID);
        if (ownerUserId != null && ownerUserId.intValue() != USERID_UNKNOWN) {
            User ownerUser = page.getAlfr().getPage(User.class,
                ownerUserId.intValue());
            if (ownerUser != null && !ownerUser.equals(page.getOwnerUser())) {
                page.setOwnerUser(ownerUser);
            }
        }

        String revealDateString = getString(fieldMap, FIELD_REVEALDATESTRING);
        if (!page.getRevealDateString().equals(revealDateString)) {
            Date revealDate = getDate(fieldMap, FIELD_REVEALDATESTRING);
            page.setRevealDate(revealDate != null ? revealDate : new Date());
        }

        String concealDateString = getString(fieldMap, FIELD_CONCEALDATESTRING);
        if (!page.getConcealDateString().equals(concealDateString)) {
            Date concealDate = getDate(fieldMap, FIELD_CONCEALDATESTRING);
            page.setConcealDate(concealDate != null ? concealDate
                : Page.DATE_RAGNAROK);
        }

        Boolean asFile = getBoolean(fieldMap, FIELD_ASFILE);
        if (asFile != null && asFile.booleanValue() != page.isAsFile()) {
            page.setAsFile(asFile.booleanValue());
        }

        Boolean listing = getBoolean(fieldMap, FIELD_LISTING);
        if (listing != null && listing.booleanValue() != page.isListing()) {
            page.setListing(listing.booleanValue());
        }

        PropertyAbility prop = page.getAbility(PropertyAbility.class);

        String name = getString(fieldMap, FIELD_LABEL);
        if (name != null && !name.equals(prop.getProperty(PROP_LABEL))) {
            prop.setProperty(PROP_LABEL, name);
        }

        String description = getString(fieldMap, FIELD_DESCRIPTION);
        if (description != null
            && !description.equals(prop.getProperty(PROP_DESCRIPTION))) {
            prop.setProperty(PROP_DESCRIPTION, description);
        }

        Boolean indexed = getBoolean(fieldMap, FIELD_INDEXED);
        if (indexed != null) {
            if (indexed.booleanValue()) {
                prop.removeProperty(SearchSystem.PROP_INDEXED);
            } else {
                prop.setProperty(SearchSystem.PROP_INDEXED, String
                    .valueOf(false));
            }
        }

        return null;
    }


    /*
     * protected scope methods
     */

    protected final String getString(Map<String, String[]> map, String key)
    {
        String[] obj = map.get(key);
        if (obj == null) {
            return null;
        } else {
            return obj[0];
        }
    }


    protected final Boolean getBoolean(Map<String, String[]> map, String key)
    {
        String[] obj = map.get(key);
        if (obj == null) {
            return null;
        } else {
            return Boolean.valueOf("true".equalsIgnoreCase(obj[0]));
        }
    }


    protected final Integer getInteger(Map<String, String[]> map, String key)
    {
        String[] obj = map.get(key);
        if (obj == null) {
            return null;
        } else {
            try {
                return Integer.valueOf(obj[0]);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
