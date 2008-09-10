package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.cms.manage.manage.web.PageBase;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PrivilegeLevel;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.type.Role;

import net.skirnir.freyja.render.html.OptionTag;


public class PermissionRow
{
    private static final String PREFIX_PRIVILEGE = "app.privilege.";

    private int roleId_;

    private String roleLabel_;

    private String rolePathname_;

    private OptionTag[] options_;


    public PermissionRow(Role role, Privilege priv, PageBase pageBase)
    {
        Page page = pageBase.getPage();
        roleId_ = role.getId();
        PropertyAbility prop = role.getAbility(PropertyAbility.class);
        roleLabel_ = prop.getProperty(PropertyAbility.PROP_LABEL, pageBase
            .getLocale());
        rolePathname_ = (page.getHeimId() == role.getHeimId()) ? PageUtils
            .encodePathname(page, role.getPathname()) : "/"
            + role.getPathname();

        PrivilegeLevel level = priv.getLevel();
        List<OptionTag> optionList = new ArrayList<OptionTag>();
        optionList.add(newOptionTag(Privilege.ACCESS_NEVER, level, pageBase));
        optionList.add(newOptionTag(Privilege.ACCESS_NONE, level, pageBase));
        optionList.add(newOptionTag(Privilege.ACCESS_PEEK, level, pageBase));
        optionList.add(newOptionTag(Privilege.ACCESS_VIEW, level, pageBase));
        optionList.add(newOptionTag(Privilege.ACCESS_COMMENT, level, pageBase));
        optionList.add(newOptionTag(Privilege.ACCESS, level, pageBase));
        options_ = optionList.toArray(new OptionTag[0]);
    }


    OptionTag newOptionTag(Privilege privilege, PrivilegeLevel level,
        PageBase page)
    {
        return new OptionTag(privilege.getName(), page
            .getResource(PREFIX_PRIVILEGE + privilege.getName()))
            .setSelected(level == privilege.getLevel());
    }


    public int getRoleId()
    {
        return roleId_;
    }


    public String getRoleLabel()
    {
        return roleLabel_;
    }


    public String getRolePathname()
    {
        return rolePathname_;
    }


    public OptionTag[] getOptions()
    {
        return options_;
    }
}
