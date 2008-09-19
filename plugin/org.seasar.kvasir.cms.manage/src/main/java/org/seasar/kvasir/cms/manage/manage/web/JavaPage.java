package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.kvasir.cms.java.Base;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentMold;
import org.seasar.kvasir.page.type.DirectoryMold;


public class JavaPage extends Base
{
    public void execute()
        throws Exception
    {
        long time = System.currentTimeMillis();

        System.out.println("********************START");

        Page root = that.getPage().getRoot();
        Page dir = root.getChild("dir");
        if (dir != null) {
            dir.delete();
        }
        PageMold mold = new DirectoryMold("dir");
        dir = root.createChild(mold);

        System.out.println("(INIT)********************SECOND="
            + ((System.currentTimeMillis() - time) / 1000.));

        time = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            mold = new DirectoryMold("sub" + i);
            Page sub = dir.createChild(mold);
            for (int j = 0; j < 1000; j++) {
                mold = new PageMold("document" + i * 10 + j);
                Page doc = sub.createChild(mold);
                PropertyAbility prop = (PropertyAbility)doc
                    .getAbility(PropertyAbility.class);
                prop.setProperty("status", String.valueOf(j % 3));
                ContentAbility cont = (ContentAbility)doc
                    .getAbility(ContentAbility.class);
                ContentMold cm = new ContentMold();
                cm.setMediaType("text/plain");
                cm.setEncoding("UTF-8");
                cm.setBodyString("id:shot6さんのT2、id:yone098さんのTeeda、"
                    + "id:skirnirさんのymirと、盟友たちの気合いの入ったステージがあり、"
                    + "大変楽しめました。T2はかなりみんな聞き入っていて、" + "なるほどね的な顔をしている方も多かったです。"
                    + "いろんなものとつながるつなげるというコンセプトは、よく伝わっていたと思います。"
                    + "Teedaは、170枚超というパワポ枚数だけに、"
                    + "ガチでムリだろうという前評判を完全に覆す完璧なペース配分で、"
                    + "むしろ感動しました。ymirはTeedaとかぶっていて見れなかったのですが、"
                    + "パワポ自体が相当な雰囲気で、当人もウケたと満足げだったので、"
                    + "ここ最近のskirnirさんの調子を考えると１００％いい内容だったのではないかと思います。");
                cont.setContent("", cm);
                doc.touch(false);
            }
        }

        System.out.println("********************SECOND="
            + ((System.currentTimeMillis() - time) / 1000.));
    }
}
