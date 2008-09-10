package org.seasar.kvasir.cms.mock;

import java.util.Locale;

import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;


public class MockPageRequest
    implements PageRequest
{
    private String contextPath_;

    private Locale locale_;

    private PageDispatch my_;

    private PageDispatch that_;

    private Page rootPage_;


    /*
     * constructors
     */

    public MockPageRequest(String contextPath, Page page, Locale locale,
        PageAlfr pageAlfr)
    {
        contextPath_ = contextPath;
        locale_ = locale;
        rootPage_ = pageAlfr.getRootPage(PathId.HEIM_MIDGARD);

        MockPageDispatch pageDispatch = new MockPageDispatch();
        pageDispatch.setPage(page);

        my_ = pageDispatch;
        that_ = pageDispatch;
    }


    public String getContextPath()
    {
        return contextPath_;
    }


    public Locale getLocale()
    {
        return locale_;
    }


    public PageDispatch getMy()
    {
        return my_;
    }


    public PageDispatch setMy(PageDispatch my)
    {
        PageDispatch preMy = my_;
        my_ = my;
        return preMy;
    }


    public PageDispatch getThat()
    {
        return that_;
    }


    public Page getRootPage()
    {
        return rootPage_;
    }
}
