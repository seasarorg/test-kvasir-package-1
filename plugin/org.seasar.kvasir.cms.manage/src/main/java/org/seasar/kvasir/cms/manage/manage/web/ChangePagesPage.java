package org.seasar.kvasir.cms.manage.manage.web;

import java.util.Date;

import org.seasar.kvasir.cms.manage.manage.dto.SimplePageRow;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.search.SearchSystem;
import org.seasar.kvasir.util.PropertyUtils;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class ChangePagesPage extends MainPanePage
{
    private String[] names_;

    private Entry[] entries_;

    private boolean commit_;

    private Boolean publishing_;

    private Boolean listing_;

    private Boolean asFile_;

    private Boolean indexed_;


    public void setNames(String[] names)
    {
        names_ = names;
    }


    public void setCommit(boolean commit)
    {
        commit_ = commit;
    }


    public void setAsFile(Boolean asFile)
    {
        asFile_ = asFile;
    }


    public void setIndexed(Boolean indexed)
    {
        indexed_ = indexed;
    }


    public void setListing(Boolean listing)
    {
        listing_ = listing;
    }


    public void setPublishing(Boolean publishing)
    {
        publishing_ = publishing;
    }


    public Object do_execute()
    {
        if (names_ == null || names_.length == 0) {
            setNotes(new Notes().add(new Note(
                "app.note.childPages.namesIsEmpty")));
        } else {
            if (!commit_) {
                return render();
            }

            Page page = getPage();
            for (int i = 0; i < names_.length; i++) {
                update(page.getChild(names_[i]));
            }

            setNotes(new Notes().add(new Note("app.note.changePages.succeed")));
        }
        return getRedirection("/list-page.do" + getPathname());
    }


    void update(Page page)
    {
        if (page == null) {
            return;
        }
        if (publishing_ != null) {
            if (publishing_.booleanValue()) {
                if (page.isConcealed()) {
                    page.setRevealDate(new Date());
                    page.setConcealDate(Page.DATE_RAGNAROK);
                }
            } else {
                if (!page.isConcealed()) {
                    page.setRevealDate(Page.DATE_RAGNAROK);
                    page.setConcealDate(Page.DATE_RAGNAROK);
                }
            }
        }
        if (listing_ != null) {
            page.setListing(listing_.booleanValue());
        }
        if (asFile_ != null) {
            page.setAsFile(asFile_.booleanValue());
        }
        if (indexed_ != null) {
            PropertyAbility ability = page.getAbility(PropertyAbility.class);
            boolean indexed = PropertyUtils.valueOf(ability
                .getProperty(SearchSystem.PROP_INDEXED), true);
            if (indexed_.booleanValue()) {
                if (!indexed) {
                    ability.removeProperty(SearchSystem.PROP_INDEXED);
                }
            } else {
                if (indexed) {
                    ability.setProperty(SearchSystem.PROP_INDEXED, String
                        .valueOf(false));
                }
            }
        }
    }


    private Object render()
    {
        enableLocationBar(true);

        Page page = getPage();
        entries_ = new Entry[names_.length];
        for (int i = 0; i < names_.length; i++) {
            entries_[i] = new Entry(page.getChild(names_[i]), (i % 2 == 0));
        }

        return "/change-pages.html";
    }


    public Entry[] getPages()
    {
        return entries_;
    }


    public class Entry extends SimplePageRow
    {
        private static final String PRORPREFIX = "app.label.changePages.";

        private String publishing_;

        private String listing_;

        private String asFile_;

        private String indexed_;


        public Entry(Page page, boolean even)
        {
            super(page, getLocale(), even);
            publishing_ = getResource(PRORPREFIX + "publishing."
                + !page.isConcealed());
            listing_ = getResource(PRORPREFIX + "listing." + page.isListing());
            asFile_ = getResource(PRORPREFIX + "asFile." + page.isAsFile());
            indexed_ = getResource(PRORPREFIX
                + "indexed."
                + PropertyUtils.valueOf(page.getAbility(PropertyAbility.class)
                    .getProperty(SearchSystem.PROP_INDEXED), true));
        }


        public String getAsFile()
        {
            return asFile_;
        }


        public String getIndexed()
        {
            return indexed_;
        }


        public String getListing()
        {
            return listing_;
        }


        public String getPublishing()
        {
            return publishing_;
        }
    }
}
