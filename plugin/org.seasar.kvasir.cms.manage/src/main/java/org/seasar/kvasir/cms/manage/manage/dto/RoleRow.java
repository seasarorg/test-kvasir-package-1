package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.type.Role;


public class RoleRow
{
    private Role role_;

    private String pathname_;

    private String label_;


    public RoleRow(Role role, Page basePage, Locale locale)
    {
        role_ = role;
        pathname_ = (basePage.getHeimId() == role.getHeimId()) ? PageUtils
            .encodePathname(basePage, role.getPathname()) : "/"
            + role.getPathname();
        label_ = role.getAbility(PropertyAbility.class).getProperty(
            PropertyAbility.PROP_LABEL, locale);
    }


    public Role getRole()
    {
        return role_;
    }


    public int getId()
    {
        return role_.getId();
    }


    public String getLabel()
    {
        return label_;
    }


    public String getPathname()
    {
        return pathname_;
    }
}
