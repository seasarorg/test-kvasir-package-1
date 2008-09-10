package org.seasar.kvasir.system.container;

import junit.framework.TestCase;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.impl.S2ContainerImpl;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentNotFoundRuntimeException;


abstract public class S2ComponentContainerFactoryTestCase extends TestCase
{
    protected S2ComponentContainerFactory target_;

    private ComponentContainer leaf_;

    private ComponentContainer sub1_;

    private ComponentContainer sub2_;

    private ComponentContainer root1_;

    private ComponentContainer root2_;

    private ComponentContainer root3_;


    abstract public S2ComponentContainerFactory newTarget();


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        target_ = newTarget();
        target_.prepare();

        leaf_ = target_.createContainer("org/seasar/kvasir/system/container/leaf.dicon",
            null);
        sub1_ = target_.createContainer("org/seasar/kvasir/system/container/sub1.dicon",
            null, new ComponentContainer[] { leaf_ });
        sub2_ = target_.createContainer("org/seasar/kvasir/system/container/sub2.dicon",
            null, new ComponentContainer[] { leaf_ });
        root1_ = target_.createContainer(
            "org/seasar/kvasir/system/container/root1.dicon", null,
            new ComponentContainer[] { sub1_, sub2_ });
        root2_ = target_.createContainer(
            "org/seasar/kvasir/system/container/root2.dicon", null,
            new ComponentContainer[] { sub2_ });
        root3_ = target_.createContainer(
            "org/seasar/kvasir/system/container/root3.dicon", null,
            new ComponentContainer[] { sub1_, sub2_ });

        target_.freeze();
    }


    @Override
    protected void tearDown()
        throws Exception
    {
        target_.stop();
        super.tearDown();
    }


    public void testCreateContainer1()
        throws Exception
    {
        ComponentContainer actual = target_.createContainer(null, null,
            new ComponentContainer[0]);

        assertNotNull("新しくコンテナインスタンスが生成されて返されること", actual);
    }


    public void testCreateContainer2()
        throws Exception
    {
        S2ContainerImpl s2container1 = new S2ContainerImpl();
        S2ComponentContainer container1 = new S2ComponentContainer(s2container1);
        S2ContainerImpl s2container2 = new S2ContainerImpl();
        S2ComponentContainer container2 = new S2ComponentContainer(s2container2);
        ComponentContainer actual = target_.createContainer(null, null,
            new ComponentContainer[] { container1, container2 });

        assertNotNull("依存コンテナを子に持つコンテナインスタンスが返されること", actual);
        S2Container s2container = ((S2ComponentContainer)actual)
            .getS2Container();
        assertEquals(2, s2container.getChildSize());
        assertSame(s2container1, s2container.getChild(0));
        assertSame(s2container2, s2container.getChild(1));
    }


    /*
     * rootが正しく設定されているかのテスト。
     */
    public void test1()
    {
        S2Container root = ((S2ComponentContainer)target_.getRootContainer())
            .getS2Container();
        assertSame(root, ((S2ComponentContainer)leaf_).getS2Container()
            .getRoot());
        assertSame(root, ((S2ComponentContainer)sub1_).getS2Container()
            .getRoot());
        assertSame(root, ((S2ComponentContainer)sub2_).getS2Container()
            .getRoot());
        assertSame(root, ((S2ComponentContainer)root1_).getS2Container()
            .getRoot());
        assertSame(root, ((S2ComponentContainer)root2_).getS2Container()
            .getRoot());
    }


    /*
     * 依存関係をたどってコンポーネントを取得できるかのテスト。
     */
    public void test2()
    {
        Object obj = root1_.getComponent("root1");
        assertEquals("root1", obj.toString());
        obj = root1_.getComponent("sub1");
        assertEquals("sub1", obj.toString());
        obj = root2_.getComponent("sub2");
        assertEquals("sub2", obj.toString());
        obj = root1_.getComponent("leaf");
        assertEquals("leaf", obj.toString());
        try {
            obj = root2_.getComponent("sub1");
            fail();
        } catch (ComponentNotFoundRuntimeException ex) {
            ;
        }
        obj = root1_.getComponent("duplicate");
        assertEquals("root1", obj.toString());
    }


    /*
     * 依存関係をたどってDIできているかを確認するテスト。
     */
    public void test3()
    {
        Mock2 mock2 = (Mock2)root1_.getComponent("mock2");
        Mock mock = mock2.getMock();
        assertNotNull(mock);
        assertEquals("leaf", mock.toString());
    }


    /*
     * インクルードされたコンテナにインクルード元と同じ依存関係が設定
     * されているかのテスト。
     */
    public void test4()
    {
        Mock2 mock2 = (Mock2)root3_.getComponent("mock2");
        Mock mock = mock2.getMock();
        assertNotNull(mock);
        assertEquals("leaf", mock.toString());
    }
}
