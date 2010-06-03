package org.seasar.kvasir.cms.impl;

import java.util.Locale;

import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageRequestImpl
    implements PageRequest
{
    private String contextPath_;

    private Locale locale_;

    private PageDispatch my_;

    private PageDispatch that_;

    private Page rootPage_;

    private String pathname_;


    /*
     * constructors
     */

    public PageRequestImpl(String contextPath, Locale locale, PageDispatch my,
        PageDispatch that, Page rootPage, String pathname)
    {
        contextPath_ = contextPath;
        locale_ = locale;
        my_ = my;
        that_ = that;
        rootPage_ = rootPage;
        pathname_ = pathname;
    }


    /*
     * PageRequest
     */

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


    public PageDispatch setThat(PageDispatch that)
    {
        PageDispatch preThat = that_;
        that_ = that;
        return preThat;
    }


    public Page getRootPage()
    {
        return rootPage_;
    }


    public String getPathname()
    {
        return pathname_;
    }
}
