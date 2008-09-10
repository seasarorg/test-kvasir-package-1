package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Locale;

import junit.framework.TestCase;

import org.seasar.kvasir.base.util.XOMUtils;
import org.seasar.kvasir.cms.manage.manage.web.Fuga;
import org.seasar.kvasir.cms.manage.manage.web.Hoe;
import org.seasar.kvasir.cms.manage.manage.web.Hoho;


public class ElementDtoTest extends TestCase
{
    public void testConstructor()
        throws Exception
    {
        Hoe hoe = new Hoe();
        hoe.setAttribute("ATTRIBUTE");
        hoe.setChild("CHILD");
        Fuga fuga = new Fuga();
        fuga.setHehe("HEHE0");
        hoe.addFuga(fuga);
        fuga = new Fuga();
        fuga.setHehe("HEHE1");
        hoe.addFuga(fuga);
        Hoho hoho = new Hoho();
        hoho.setHehe("HEHE");
        hoe.setHoho(hoho);
        hoe.addString("STRING0");
        hoe.addString("STRING1");

        ElementDto actual = new ElementDto(hoe, XOMUtils.newMapper()
            .getBeanAccessor(Hoe.class), new Locale(""), "", "", "name", null,
            null);

        assertEquals("name:(attribute)attribute", actual.getAttributes()[0]
            .getIndexedName());
        assertEquals("name:(child)child", actual.getAttributes()[1]
            .getIndexedName());
        assertEquals("name:(child)string", actual.getAttributes()[2].getName());
        assertEquals("name:(child)string[0]", actual.getAttributes()[2]
            .getIndexedName());
        assertEquals("name:(child)string[1]", actual.getAttributes()[3]
            .getIndexedName());
        assertEquals("フォームでの項目追加用のダミーエントリが正しく追加されること", "name:(child)string[2]",
            actual.getAttributes()[4].getIndexedName());
        assertEquals("name:(child)fuga", actual.getChildren()[0].getName());
        assertEquals("name:(child)fuga[0]", actual.getChildren()[0]
            .getIndexedName());
        assertEquals("name:(child)fuga[0]:(attribute)hehe", actual
            .getChildren()[0].getAttributes()[0].getIndexedName());
        assertEquals("name:(child)fuga[1]", actual.getChildren()[1]
            .getIndexedName());
        assertEquals("name:(child)fuga[1]:(attribute)hehe", actual
            .getChildren()[1].getAttributes()[0].getIndexedName());
        assertEquals("name:(child)fuga[2]:(attribute)hehe", actual
            .getChildren()[2].getAttributes()[0].getIndexedName());
        assertEquals("フォームでの項目追加用のダミーエントリが正しく追加されること",
            "name:(child)hoho:(attribute)hehe", actual.getChildren()[3]
                .getAttributes()[0].getIndexedName());
    }
}
