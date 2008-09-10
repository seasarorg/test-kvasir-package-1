package org.seasar.kvasir.cms.manage.manage.web;

import java.io.IOException;
import java.util.Date;

import org.seasar.cms.ymir.FormFile;
import org.seasar.cms.ymir.Path;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.page.CollisionDetectedRuntimeException;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentMold;
import org.seasar.kvasir.page.type.DirectoryMold;


public class OptionalConsolePage extends LayoutPageBase
{
    private PopPlugin popPlugin_;

    private String name_;

    private String label_;

    private FormFile body_;

    private boolean asDirectory_;

    private boolean listing_;

    private boolean concealed_;


    public void setPopPlugin(PopPlugin popPlugin)
    {
        popPlugin_ = popPlugin;
    }


    public void setName(String name)
    {
        name_ = name;
    }


    public void setLabel(String label)
    {
        label_ = label;
    }


    public void setBody(FormFile body)
    {
        body_ = body;
    }


    public void setAsDirectory(boolean asDirectory)
    {
        asDirectory_ = asDirectory;
    }


    public void setListing(boolean listing)
    {
        listing_ = listing;
    }


    public void setConcealed(boolean concealed)
    {
        concealed_ = concealed;
    }


    public Object do_execute()
    {
        name_ = PageUtils.getName(findStartPathname());
        return "/optionalConsole.html";
    }


    public Object do_addPage()
    {
        Page page = addPage(getCurrentHeimId(), getPathname());
        Page parent = page.getParent();

        return "redirect:!" + getRedirectionPage(parent, page).getPathname();
    }


    Page getRedirectionPage(Page parent, Page page)
    {
        return (parent.isNode() && !page.isAsFile() ? page : parent);
    }


    Page addPage(int heimId, String pathname)
    {
        Page page = getPageAlfr().getRootPage(heimId);
        int pre = 1;
        int idx;
        while ((idx = pathname.indexOf('/', pre)) >= 0) {
            String name = pathname.substring(pre, idx);
            Page child = page.getChild(name);
            if (child == null) {
                try {
                    child = page.createChild(new DirectoryMold().setName(name));
                } catch (DuplicatePageException ex) {
                    throw new CollisionDetectedRuntimeException(ex);
                }
                PropertyAbility prop = child.getAbility(PropertyAbility.class);
                prop.setProperty(PropertyAbility.PROP_LABEL, name);
            }

            page = child;
            pre = idx + 1;
        }

        return addPage(page, pathname.substring(pre), label_, asDirectory_,
            listing_);
    }


    public Object do_deletePage()
    {
        Page page = getPage();
        if (page != null) {
            page.delete();
        }

        return new Path("!" + PageUtils.getParentPathname(getPathname()))
            .setAsNoCache(true);
    }


    public Object do_addChildPage()
    {
        Page page = getPage();
        if (page == null) {
            // おかしいので処理が出来ない。
            // そのため単にページを表示しておく。
            return "redirect:!" + getPathname();
        }

        if (name_ == null || name_.trim().length() == 0) {
            if (body_ != null && body_.getName() != null
                && body_.getName().trim().length() > 0) {
                name_ = body_.getName().trim();
            } else {
                // ページ名が指定されていないので処理が出来ない。
                // そのため単にページを表示しておく。
                return "redirect:!" + getPathname();
            }
        }

        String name = name_.trim();
        String pathname;
        if (name.startsWith("/")) {
            pathname = name;
        } else {
            pathname = page.getPathname().concat("/").concat(name);
        }
        Page child = addPage(page.getHeimId(), pathname);
        return "redirect:!" + getRedirectionPage(page, child).getPathname();
    }


    Page addPage(Page page, String name, String label, boolean asDirectory,
        boolean listing)
    {
        Page child = page.getChild(name);
        boolean created = false;
        if (child == null) {
            created = true;
            PageMold pageMold;
            if (asDirectory) {
                pageMold = new DirectoryMold();
            } else {
                pageMold = new PageMold();
            }
            pageMold.setName(name).setListing(listing);
            try {
                child = page.createChild(pageMold);
            } catch (DuplicatePageException ex) {
                throw new CollisionDetectedRuntimeException(ex);
            }
        }

        if (label == null || label.length() == 0) {
            if (created) {
                label = name;
            } else {
                label = null;
            }
        }
        if (label != null) {
            PropertyAbility prop = child.getAbility(PropertyAbility.class);
            prop.setProperty(PropertyAbility.PROP_LABEL, label);
        }

        if (body_ != null && body_.getSize() > 0) {
            ContentMold mold = new ContentMold();
            String mediaType = body_.getContentType();
            if (mediaType == null && created) {
                mediaType = "application/octet-stream";
            }
            if (mediaType != null) {
                mold.setMediaType(mediaType);
            }
            try {
                mold.setBodyInputStream(body_.getInputStream());
            } catch (IOException ex) {
                log_.warn("Can't read body file: " + body_, ex);
                // TODO 画面にもメッセージを出す？
            }
            child.getAbility(ContentAbility.class).setContent(
                Page.VARIANT_DEFAULT, mold);
            if (mediaType != null && !mediaType.startsWith("text/")) {
                child.setAsFile(true);
            }
            child.touch();
        }

        return child;
    }


    public Object do_setConcealed()
    {
        Page page = getPage();
        if (page != null) {
            setConcealed(page);
        }
        return new Path("!" + getPathname()).setAsNoCache(true);
    }


    void setConcealed(Page page)
    {
        boolean concealed = page.isConcealed();
        if (concealed && !concealed_) {
            page.setRevealDate(new Date());
            page.setConcealDate(Page.DATE_RAGNAROK);
        } else if (!concealed && concealed_) {
            page.setConcealDate(new Date());
        }

        Page[] children = page.getChildren();
        for (int i = 0; i < children.length; i++) {
            setConcealed(children[i]);
        }
    }


    public String do_updatePanes()
    {
        popPlugin_.updatePanes(getCurrentHeimId());
        return "redirect:!" + getPathname();
    }


    public boolean isStartPageAbleToHaveChildren()
    {
        Page page = getStartPage();
        return (page != null);
    }


    public boolean isConcealed()
    {
        Page page = getStartPage();
        return (page != null && page.isConcealed());
    }


    public boolean isDeletable()
    {
        Page page = getStartPage();
        return (page != null && !page.isRoot());
    }


    public String getName()
    {
        return name_;
    }
}
