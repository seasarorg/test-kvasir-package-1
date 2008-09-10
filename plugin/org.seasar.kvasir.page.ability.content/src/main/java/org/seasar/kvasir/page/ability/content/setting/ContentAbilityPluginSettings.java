package org.seasar.kvasir.page.ability.content.setting;

import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Default;


public class ContentAbilityPluginSettings
{
    private String contentsDirectory_ = "";


    public String getContentsDirectory()
    {
        return contentsDirectory_;
    }


    @Child
    @Default("")
    public void setContentsDirectory(String contentsDirectory)
    {
        contentsDirectory_ = contentsDirectory;
    }
}
