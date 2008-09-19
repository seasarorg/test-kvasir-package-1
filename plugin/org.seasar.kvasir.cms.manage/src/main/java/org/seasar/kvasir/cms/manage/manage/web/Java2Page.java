package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.kvasir.cms.java.Base;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.condition.Formula;
import org.seasar.kvasir.page.condition.PageCondition;


public class Java2Page extends Base
{
    public void execute()
        throws Exception
    {
        Page root = that.getPage().getRoot();
        Page dir = root.getChild("dir");

        PageCondition cond = new PageCondition();
        Formula option = new Formula("property('status')=?");
        option.setString(1, "0");
        cond.setOption(option);
        cond.setRecursive(true);

        long time = System.currentTimeMillis();

        Page[] children = dir.getChildren(cond);
        System.out.println("************COUNT=" + children.length);

        System.out.println("********************SECOND="
            + ((System.currentTimeMillis() - time) / 1000.));
    }
}
