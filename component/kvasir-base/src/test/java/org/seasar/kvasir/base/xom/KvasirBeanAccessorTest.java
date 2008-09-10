package org.seasar.kvasir.base.xom;

import java.util.Locale;

import junit.framework.TestCase;

import net.skirnir.xom.XOMapperFactory;


public class KvasirBeanAccessorTest extends TestCase
{
    public void test説明文が正しく取得できること()
        throws Exception
    {
        KvasirBeanAccessor target = new KvasirBeanAccessor();
        target.setMapper(XOMapperFactory.newInstance().setBeanAccessorFactory(
            new KvasirBeanAccessorFactory()));
        target.setBeanClass(Hoe.class);

        assertGetDescription(target);
    }


    public void testスーパークラスが持つ説明文が正しく取得できること()
        throws Exception
    {
        KvasirBeanAccessor target = new KvasirBeanAccessor();
        target.setMapper(XOMapperFactory.newInstance().setBeanAccessorFactory(
            new KvasirBeanAccessorFactory()));
        target.setBeanClass(HoeSub.class);

        assertGetDescription(target);
    }


    public void testスーパーインタフェースが持つ説明文が正しく取得できること()
        throws Exception
    {
        KvasirBeanAccessor target = new KvasirBeanAccessor();
        target.setMapper(XOMapperFactory.newInstance().setBeanAccessorFactory(
            new KvasirBeanAccessorFactory()));
        target.setBeanClass(IHoeSub.class);

        assertGetDescription(target);
    }


    void assertGetDescription(KvasirBeanAccessor target)
    {
        assertEquals("ほえ", target.getDescription(new Locale("ja")));
        assertEquals("Hoe", target.getDescription(new Locale("en")));

        assertEquals("属性", target.getAttributeDescriptor("attribute")
            .getDescription(new Locale("ja")));
        assertEquals("Attribute", target.getAttributeDescriptor("attribute")
            .getDescription(new Locale("en")));

        assertEquals("子", target.getChildDescriptor("hehe").getDescription(
            new Locale("ja")));
        assertEquals("Child", target.getChildDescriptor("hehe").getDescription(
            new Locale("en")));

        assertEquals(
            "子要素がスカラでない場合で、説明文が親のリソースに定義されていない場合は子のリソースに定義されているものが使われること",
            "ふが", target.getChildDescriptor("fuga").getDescription(
                new Locale("ja")));
        assertEquals("Fuga", target.getChildDescriptor("fuga").getDescription(
            new Locale("en")));
    }
}
