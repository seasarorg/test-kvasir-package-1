package org.seasar.kvasir.cms.kdiary.kdiary.web;

import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_TITLE;

import java.util.Date;

import org.seasar.cms.ymir.extension.ConstraintType;
import org.seasar.cms.ymir.extension.annotation.SuppressConstraints;
import org.seasar.cms.ymir.extension.constraint.Required;
import org.seasar.kvasir.cms.kdiary.kdiary.KdiaryDate;
import org.seasar.kvasir.cms.kdiary.kdiary.dto.CommonDto;
import org.seasar.kvasir.cms.ymir.constraint.HasPrivilege;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentMold;
import org.seasar.kvasir.page.type.Directory;


@HasPrivilege(Privilege.ACCESS)
public class EditActionPage extends EditActionPageBase
{
    private static final String KEY_TITLE_EDIT   = "edit.title.edit";

    private static final String KEY_TITLE_APPEND = "edit.title.append";

    private KdiaryDate          kdiaryDate_      = new KdiaryDate();

    private boolean             append_          = true;


    public void setDate(String date)
    {
        try {
            kdiaryDate_ = new KdiaryDate(date);
            append_ = false;
        } catch (RuntimeException ignore) {
        }
    }


    @Override
    public CommonDto createCommon()
    {
        CommonDto common = super.createCommon();
        common.setTitle(common.getTitle() + " ["
            + getLocalizedMessage(append_ ? KEY_TITLE_APPEND : KEY_TITLE_EDIT)
            + "]");
        return common;
    }


    @Override
    @SuppressConstraints(ConstraintType.VALIDATION)
    public void _get()
    {
        year_ = kdiaryDate_.getYear();
        month_ = kdiaryDate_.getMonth();
        day_ = kdiaryDate_.getDay();
    }


    @Override
    public void _render()
    {
        super._render();

        submitName_ = append_ ? "append" : "replace";
        submitValue_ = getLocalizedMessage(append_ ? KEY_TITLE_APPEND
            : KEY_TITLE_EDIT);
        Page article = getArticle(kdiaryDate_);
        if (article != null) {
            hide_ = article.isConcealed();
        }
        if (!append_) {
            title_ = getTitle(article);
            body_ = getBodyString(article);
        }
    }


    @Required("body")
    public String _post_append()
    {
        return update(new KdiaryDate(year_, month_, day_), true, hide_);
    }


    public String _post_edit()
    {
        date_ = new KdiaryDate(year_, month_, day_).getString();
        return "redirect:/action/edit.do(date)";
    }


    @Required( { "title", "body" })
    public String _post_replace()
    {
        return update(new KdiaryDate(year_, month_, day_), false, hide_);
    }


    String update(KdiaryDate kdiaryDate, boolean append, boolean hide)
    {
        Directory dir = getMonthDirectory(kdiaryDate, true);
        String name = kdiaryDate.getArticleName();
        Page article = dir.getChild(name);
        if (article == null) {
            try {
                article = dir.createChild(new PageMold().setName(name));
            } catch (DuplicatePageException ex) {
                // FIXME エラーメッセージを出すようにしよう。
                return PASSTHROUGH;
            }
        }
        if (!append || title_ != null && title_.length() > 0) {
            article.getAbility(PropertyAbility.class).setProperty(PROP_TITLE,
                title_);
        }

        ContentAbility ability = article.getAbility(ContentAbility.class);
        ContentMold mold = new ContentMold().setMediaType("text/x-wiki");
        StringBuilder sb = new StringBuilder();
        if (append) {
            // 追記。
            Content content = ability.getLatestContent(Page.VARIANT_DEFAULT);
            if (content != null) {
                sb.append(content.getBodyString());
            }
        }
        sb.append(System.getProperty("line.separator"));
        sb.append(body_);
        mold.setBodyString(sb.toString());
        ability.setContent(Page.VARIANT_DEFAULT, mold);

        if (hide) {
            if (!article.isConcealed()) {
                article.setConcealDate(new Date());
            }
        } else {
            if (article.isConcealed()) {
                article.setRevealDate(new Date());
                article.setConcealDate(Page.DATE_RAGNAROK);
            }
        }

        article.touch();
        append_ = append;
        targetPathname_ = article.getPathname();

        return "/templates/action/edit_update";
    }
}
