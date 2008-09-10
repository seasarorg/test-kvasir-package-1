package org.seasar.kvasir.cms.kdiary.kdiary.web;

import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.DIR_THEME;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_AUTHORMAILADDRESS;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_AUTHORNAME;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_BODYMEDIATYPE;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_COMMENTANCHOR;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_COMMENTLIMIT;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_CSS;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_DATEFORMAT;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_FOOTER;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_HEADER;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_INDEXPAGE;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_JAVADATEFORMAT;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_LATESTLIMIT;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_PARAGRAPHANCHOR;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_SHOWCOMMENT;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_THEME;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_TITLE;

import org.seasar.kvasir.cms.kdiary.kdiary.KdiaryUtils;
import org.seasar.kvasir.cms.kdiary.kdiary.dto.CommonDto;
import org.seasar.kvasir.cms.ymir.constraint.HasPrivilege;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.util.PropertyUtils;

import net.skirnir.freyja.render.html.OptionTag;


@HasPrivilege(Privilege.ACCESS)
public class ConfigActionPage extends ConfigActionPageBase
{
    @Override
    public CommonDto createCommon()
    {
        CommonDto common = super.createCommon();
        common.setTitle(common.getTitle() + " ["
            + getLocalizedMessage("config.title") + "]");
        return common;
    }


    @Override
    public void _render()
    {
        super._render();

        PropertyAbility property = getPropertyAbility();

        authorName_ = property.getProperty(PROP_AUTHORNAME);
        authorMailAddress_ = property.getProperty(PROP_AUTHORMAILADDRESS);
        indexPage_ = property.getProperty(PROP_INDEXPAGE);

        htmlTitle_ = property.getProperty(PROP_TITLE);
        header_ = property.getProperty(PROP_HEADER);
        footer_ = property.getProperty(PROP_FOOTER);

        paragraphAnchor_ = property.getProperty(PROP_PARAGRAPHANCHOR);
        commentAnchor_ = property.getProperty(PROP_COMMENTANCHOR);
        dateFormat_ = property.getProperty(PROP_DATEFORMAT);
        latestLimit_ = property.getProperty(PROP_LATESTLIMIT);

        prepareForThemeOptions();

        showComment_ = property.getProperty(PROP_SHOWCOMMENT);
        showCommentOptions_ = new OptionTag[] {
            new OptionTag("true", getLocalizedMessage("label.show.true"))
                .setSelected("true".equals(showComment_)),
            new OptionTag("false", getLocalizedMessage("label.show.false"))
                .setSelected("false".equals(showComment_)) };
        commentLimit_ = property.getProperty(PROP_COMMENTLIMIT);

        bodyMediaType_ = property.getProperty(PROP_BODYMEDIATYPE);
    }


    void prepareForThemeOptions()
    {
        String theme = getProperty(PROP_THEME);
        Page[] themes = getThemeDirectory().getChildren(
            new PageCondition().setIncludeConcealed(false).setOnlyListed(true));
        themeOptions_ = new OptionTag[themes.length];
        for (int i = 0; i < themes.length; i++) {
            String name = themes[i].getName();
            themeOptions_[i] = new OptionTag(name, PropertyUtils.valueOf(
                themes[i].getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_LABEL), name)).setSelected(name
                .equals(theme));
        }
        if (theme.length() == 0) {
            css_ = getProperty(PROP_CSS);
        }
    }


    @Override
    public void _post_saveconf()
    {
        PropertyAbility property = getPropertyAbility();

        if (authorName_ != null) {
            property.setProperty(PROP_AUTHORNAME, authorName_);
        }
        if (authorMailAddress_ != null) {
            property.setProperty(PROP_AUTHORMAILADDRESS, authorMailAddress_);
        }
        if (indexPage_ != null) {
            property.setProperty(PROP_INDEXPAGE, indexPage_);
        }

        if (htmlTitle_ != null) {
            property.setProperty(PROP_TITLE, htmlTitle_);
        }
        if (header_ != null) {
            property.setProperty(PROP_HEADER, header_);
        }
        if (footer_ != null) {
            property.setProperty(PROP_FOOTER, footer_);
        }

        if (paragraphAnchor_ != null) {
            property.setProperty(PROP_PARAGRAPHANCHOR, paragraphAnchor_);
        }
        if (commentAnchor_ != null) {
            property.setProperty(PROP_COMMENTANCHOR, commentAnchor_);
        }
        if (dateFormat_ != null) {
            property.setProperty(PROP_DATEFORMAT, dateFormat_);
            property.setProperty(PROP_JAVADATEFORMAT, KdiaryUtils
                .toJavaDateFormat(dateFormat_));
        }
        if (latestLimit_ != null) {
            property.setProperty(PROP_LATESTLIMIT, latestLimit_);
        }

        if (theme_ != null && css_ != null) {
            String css;
            if (theme_.length() > 0) {
                css = "@/" + DIR_THEME + "/" + theme_ + "/" + theme_ + ".css";
            } else {
                css = css_;
            }
            property.setProperty(PROP_THEME, theme_);
            property.setProperty(PROP_CSS, css);
        }

        if (showComment_ != null) {
            property.setProperty(PROP_SHOWCOMMENT, showComment_);
        }
        if (commentLimit_ != null) {
            property.setProperty(PROP_COMMENTLIMIT, commentLimit_);
        }

        if (bodyMediaType_ != null) {
            property.setProperty(PROP_BODYMEDIATYPE, bodyMediaType_);
        }
    }
}
