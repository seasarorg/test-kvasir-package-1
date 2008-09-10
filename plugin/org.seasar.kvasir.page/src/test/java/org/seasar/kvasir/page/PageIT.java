package org.seasar.kvasir.page;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Test;

import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.condition.Formula;
import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.page.type.UserMold;


/**
 * @author YOKOTA Takehiko
 */
public class PageIT extends PagePluginITCase
{
    private PagePlugin plugin_;

    private PageAlfr pageAlfr_;

    private Page target_;

    private DateFormat df_ = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    public static Test suite()
        throws Exception
    {
        return createTestSuite(PageIT.class);
    }


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        pageAlfr_ = getPlugin().getPageAlfr();
        target_ = pageAlfr_.getPage(Page.ID_ADMINISTRATOR_USER);
    }


    public void testGetId()
    {
        assertEquals(Page.ID_ADMINISTRATOR_USER, target_.getId());
    }


    public void testGetPathname()
    {
        assertEquals(Page.PATHNAME_ADMINISTRATOR_USER, target_.getPathname());
    }


    public void testGetType()
    {
        assertEquals("user", target_.getType());
    }


    public void testGetLordPathname()
    {
        assertEquals("", target_.getLordPathname());
    }


    public void testGetLocalPathname()
    {
        assertEquals(Page.PATHNAME_ADMINISTRATOR_USER, target_
            .getLocalPathname());
    }


    public void testGetLord()
    {
        assertEquals(Page.ID_ROOT, target_.getLord().getId());
    }


    public void testGetParentPathname()
    {
        assertEquals(Page.PATHNAME_USERS, target_.getParentPathname());
    }


    public void testGetParent()
    {
        assertEquals(Page.ID_USERS, target_.getParent().getId());
    }


    public void testGetName()
    {
        assertEquals("administrator", target_.getName());
    }


    public void testGetOrderNumber()
    {
        assertEquals(1, target_.getOrderNumber());
    }


    public void testGetOwnerUser()
    {
        assertEquals(Page.ID_ADMINISTRATOR_USER, target_.getOwnerUser().getId());
    }


    public void testIsConcealed()
    {
        assertFalse(target_.isConcealed());
    }


    public void testIsNode()
    {
        assertFalse(target_.isNode());
        assertTrue(pageAlfr_.getPage(Page.ID_USERS).isNode());
    }


    public void testGetHeim()
    {
        assertEquals(PathId.HEIM_MIDGARD, target_.getHeimId());
        assertEquals(PathId.HEIM_ALFHEIM, pageAlfr_.getPage(
            Page.ID_ALFHEIM_ROOT).getHeimId());
    }


    public void testIsRoot()
    {
        assertFalse(target_.isRoot());
        assertTrue(pageAlfr_.getRootPage(PathId.HEIM_MIDGARD).isRoot());
    }


    public void testIsRoot2()
    {
        assertTrue(pageAlfr_.getPage(Page.ID_ALFHEIM_ROOT).isRoot());
    }


    public void testCreateChild()
        throws Exception
    {
        Page testPage;
        try {
            testPage = prepareTestPage();
        } catch (DuplicatePageException ex) {
            fail("Can't happen!");
            return;
        }

        assertEquals("test", testPage.getName());
        assertEquals("/test", testPage.getPathname());
        assertTrue(testPage.isConcealed());
        assertFalse(testPage.isNode());
        assertFalse(testPage.isAsFile());
        assertTrue(testPage.isListing());
        Date cdate = df_.parse("2005/01/02 03:04:05");
        Date mdate = df_.parse("2005/02/03 04:05:06");
        assertEquals(df_.format(cdate), df_.format(testPage.getCreateDate()));
        assertEquals(df_.format(mdate), df_.format(testPage.getModifyDate()));
        assertEquals(2, testPage.getOrderNumber());
        assertEquals(Page.ID_ANONYMOUS_USER, testPage.getOwnerUser().getId());
        assertEquals("type", testPage.getType());
        assertEquals("test", testPage.getName());
    }


    public void testCreateChild2()
    {
        Page root = pageAlfr_.getRootPage(PathId.HEIM_MIDGARD);
        Page test = root.getChild("test");
        if (test != null) {
            test.delete();
        }

        try {
            root.createChild(new PageMold().setName("test"));
        } catch (Throwable t) {
            fail("Can't create page: " + t);
        }
    }


    public void testCreateChild3()
    {
        Page root = pageAlfr_.getPage(Page.ID_ALFHEIM_ROOT);
        Page test = root.getChild("test");
        if (test != null) {
            test.delete();
        }

        Page child = null;
        try {
            child = root.createChild(new PageMold().setName("test"));
        } catch (Throwable t) {
            fail("Can't create page: " + t);
        }

        assertEquals(PathId.HEIM_ALFHEIM, child.getHeimId());
    }


    public void testSet()
        throws Exception
    {
        Page testPage;
        try {
            testPage = prepareTestPage();
        } catch (DuplicatePageException ex) {
            fail("Can't happen!");
            return;
        }

        testPage.setOrderNumber(3);
        assertEquals(3, testPage.getOrderNumber());

        Date cdate = df_.parse("2004/01/02 03:04:05");
        testPage.setCreateDate(cdate);
        assertEquals(df_.format(cdate), df_.format(testPage.getCreateDate()));

        Date mdate = df_.parse("2004/01/03 03:04:05");
        testPage.setModifyDate(mdate);
        assertEquals(df_.format(mdate), df_.format(testPage.getModifyDate()));

        User admin = pageAlfr_.getAdministrator();
        testPage.setOwnerUser(admin);
        assertEquals(admin.getId(), testPage.getOwnerUser().getId());

        testPage.setNode(true);
        assertTrue(testPage.isNode());
    }


    public void testDelete()
        throws Exception
    {
        Page testPage;
        try {
            testPage = prepareTestPage();
        } catch (DuplicatePageException ex) {
            fail("Can't happen!");
            return;
        }

        testPage.delete();
        testPage = pageAlfr_.getRootPage(PathId.HEIM_MIDGARD).getChild("test");
        assertNull(testPage);

        try {
            testPage = prepareTestPage();
        } catch (DuplicatePageException ex) {
            fail("Can't happen!");
            return;
        }

        testPage.setNode(true);
        testPage.delete();
        assertNull(pageAlfr_.getPage(PathId.HEIM_MIDGARD, "/test/c1"));
        assertNull(pageAlfr_.getPage(PathId.HEIM_MIDGARD, "/test/c2"));
    }


    public void testDelete_ユーザを削除した場合はユーザが所有するPageの所有者がAdministratorに変更されること()
        throws Exception
    {
        Page users = pageAlfr_.getPage(Page.ID_USERS);
        User testUser = (User)users.getChild("test");
        if (testUser != null) {
            testUser.delete();
        }
        testUser = (User)users.createChild(new UserMold("test"));

        Page testPage;
        try {
            testPage = prepareTestPage();
        } catch (DuplicatePageException ex) {
            fail("Can't happen!");
            return;
        }

        testPage.setOwnerUser(testUser);

        assertEquals(testUser, testPage.getOwnerUser());

        testUser.delete();

        assertEquals("deleteされただけでは中身は変化しないこと", testUser.getId(), testPage
            .getDto().getOwnerUserId().intValue());

        testPage.refresh();

        assertEquals("情報を更新して初めて中身が変わること", Page.ID_ADMINISTRATOR_USER, testPage
            .getOwnerUser().getId());
    }


    public void testSetAsLord()
        throws Exception
    {
        Page testPage;
        try {
            testPage = prepareTestPage();
        } catch (DuplicatePageException ex) {
            fail("Can't happen!");
            return;
        }
        testPage.setNode(true);
        Page child = testPage.createChild(new PageMold().setName("child"));
        testPage.setAsLord(true);
        assertEquals(testPage, testPage.getLord());
        child.refresh();
        assertEquals(testPage, child.getLord());

        testPage.setAsLord(false);
        assertEquals(pageAlfr_.getRootPage(PathId.HEIM_MIDGARD), testPage
            .getLord());
        child.refresh();
        assertEquals(pageAlfr_.getRootPage(PathId.HEIM_MIDGARD), child
            .getLord());
    }


    public void testMoveTo()
        throws Exception
    {
        Page testPage;
        try {
            testPage = prepareTestPage();
        } catch (DuplicatePageException ex) {
            fail("Can't happen!");
            return;
        }
        testPage.setNode(true);
        Page c = testPage
            .createChild(new PageMold().setName("c").setNode(true));
        Page c2 = testPage.createChild(new PageMold().setName("c2").setNode(
            true));
        Page cc = c.createChild(new PageMold().setName("cc").setNode(true));

        try {
            c.moveTo(cc, null);
            fail("Loop must be detected");
        } catch (LoopDetectedException ex) {
            ;
        }

        c.moveTo(c2, null);
        assertNotNull(pageAlfr_.getPage(PathId.HEIM_MIDGARD, "/test/c2/c"));
        assertNotNull(pageAlfr_.getPage(PathId.HEIM_MIDGARD, "/test/c2/c/cc"));
        assertNull(pageAlfr_.getPage(PathId.HEIM_MIDGARD, "/test/c"));
        assertNull(pageAlfr_.getPage(PathId.HEIM_MIDGARD, "/test/c/cc"));
    }


    public void testGetChild()
    {
        Page page = pageAlfr_.getPage(Page.ID_USERS);
        Page child = page.getChild("administrator");
        assertNotNull(child);
        assertEquals(Page.PATHNAME_ADMINISTRATOR_USER, child.getPathname());
    }


    public void testGetChild2()
    {
        Page page = pageAlfr_.getPage(Page.ID_USERS);
        Page child = page.getChild("notexist");
        assertNull(child);
    }


    public void testGetChildren()
    {
        Page page = pageAlfr_.getPage(Page.ID_USERS);
        Page[] children = page.getChildren();
        assertNotNull(children);
        assertEquals(2, children.length);
        int idx = 0;
        assertEquals(Page.PATHNAME_ADMINISTRATOR_USER, children[idx]
            .getPathname());
        idx++;
        assertEquals(Page.PATHNAME_ANONYMOUS_USER, children[idx].getPathname());
    }


    public void testGetChildren_PageCondition()
        throws Exception
    {
        Page testPage;
        try {
            testPage = prepareTestPage();
        } catch (DuplicatePageException ex) {
            fail("Can't happen!");
            return;
        }
        testPage.setNode(true);

        Page c1 = testPage.createChild(new PageMold().setName("c1")
            .setOrderNumber(3).setNode(true));
        Page c2 = testPage.createChild(new PageMold().setName("c2")
            .setOrderNumber(2).setListing(false));
        Page c3 = testPage.createChild(new PageMold().setName("c3")
            .setOrderNumber(1).setRevealDate(Page.DATE_RAGNAROK)
            .setConcealDate(Page.DATE_RAGNAROK));
        c1.createChild(new PageMold().setName("c1"));
        c1.createChild(new PageMold().setName("c2"));
        c1.createChild(new PageMold().setName("c3"));

        PermissionAbility perm1 = (PermissionAbility)c1
            .getAbility(PermissionAbility.class);
        perm1.clearPermissions();
        perm1.clearPermissions();
        perm1.grantPrivilege((Role)pageAlfr_.getPage(Page.ID_ANONYMOUS_ROLE),
            Privilege.ACCESS_VIEW);
        ((PermissionAbility)c2.getAbility(PermissionAbility.class))
            .clearPermissions();
        ((PermissionAbility)c3.getAbility(PermissionAbility.class))
            .clearPermissions();

        // 条件指定なし
        Page[] children = testPage.getChildren(new PageCondition());
        assertNotNull(children);
        assertEquals(3, children.length);
        int idx = 0;
        assertEquals("c3", children[idx].getName());
        idx++;
        assertEquals("c2", children[idx].getName());
        idx++;
        assertEquals("c1", children[idx].getName());

        // offset
        children = testPage.getChildren(new PageCondition().setOffset(1));
        assertNotNull(children);
        assertEquals(2, children.length);
        idx = 0;
        assertEquals("c2", children[idx].getName());
        idx++;
        assertEquals("c1", children[idx].getName());

        // length
        children = testPage.getChildren(new PageCondition().setLength(2));
        assertNotNull(children);
        assertEquals(2, children.length);
        idx = 0;
        assertEquals("c3", children[idx].getName());
        idx++;
        assertEquals("c2", children[idx].getName());

        // offset+length
        children = testPage.getChildren(new PageCondition().setOffset(1)
            .setLength(1));
        assertNotNull(children);
        assertEquals(1, children.length);
        idx = 0;
        assertEquals("c2", children[idx].getName());

        // order (name)
        children = testPage.getChildren(new PageCondition().setOrder(new Order(
            PageCondition.FIELD_NAME)));
        assertNotNull(children);
        assertEquals(3, children.length);
        idx = 0;
        assertEquals("c1", children[idx].getName());
        idx++;
        assertEquals("c2", children[idx].getName());
        idx++;
        assertEquals("c3", children[idx].getName());

        // order (name desc)
        children = testPage.getChildren(new PageCondition().setOrder(new Order(
            PageCondition.FIELD_NAME, false)));
        assertNotNull(children);
        assertEquals(3, children.length);
        idx = 0;
        assertEquals("c3", children[idx].getName());
        idx++;
        assertEquals("c2", children[idx].getName());
        idx++;
        assertEquals("c1", children[idx].getName());

        // option
        children = testPage.getChildren(new PageCondition()
            .setOption(new Formula(PageCondition.FIELD_NAME + "=?").setString(
                1, "c2")));
        assertNotNull(children);
        assertEquals(1, children.length);
        idx = 0;
        assertEquals("c2", children[idx].getName());

        // recursive
        children = testPage.getChildren(new PageCondition().setOption(
            new Formula(PageCondition.FIELD_NAME + "=?").setString(1, "c2"))
            .setRecursive(true).setOrder(
                new Order(PageCondition.FIELD_PARENTPATHNAME)));
        assertNotNull(children);
        assertEquals(2, children.length);
        idx = 0;
        assertEquals("/test/c2", children[idx].getPathname());
        idx++;
        assertEquals("/test/c1/c2", children[idx].getPathname());

        // includeConcealed
        children = testPage.getChildren(new PageCondition()
            .setIncludeConcealed(false));
        assertNotNull(children);
        assertEquals(2, children.length);
        idx = 0;
        assertEquals("c2", children[idx].getName());
        idx++;
        assertEquals("c1", children[idx].getName());

        // user+privilege
        children = testPage.getChildren(new PageCondition().setUser(
            (User)pageAlfr_.getPage(Page.ID_ANONYMOUS_USER)).setPrivilege(
            Privilege.ACCESS_PEEK));
        assertNotNull(children);
        assertEquals(1, children.length);
        idx = 0;
        assertEquals("c1", children[idx].getName());

        // onlyListed
        children = testPage
            .getChildren(new PageCondition().setOnlyListed(true));
        assertNotNull(children);
        assertEquals(2, children.length);
        idx = 0;
        assertEquals("c3", children[idx].getName());
        idx++;
        assertEquals("c1", children[idx].getName());
    }


    public void testGetChildNames()
    {
        Page page = pageAlfr_.getPage(Page.ID_USERS);
        String[] names = page.getChildNames();
        assertNotNull(names);
        assertEquals(2, names.length);
        int idx = 0;
        assertEquals("administrator", names[idx]);
        idx++;
        assertEquals("anonymous", names[idx]);
    }


    public void testRevealAndCocneal()
        throws Exception
    {
        Page testPage;
        try {
            testPage = prepareTestPage();
        } catch (DuplicatePageException ex) {
            fail("Can't happen!");
            return;
        }
        testPage.setNode(true);

        // デフォルト - 作成と同時に見えて不可視にはならない。
        testPage.createChild(new PageMold().setName("c1"));
        // ある時点で不可視になる。
        testPage.createChild(new PageMold().setName("c2").setConcealDate(
            newDate("2108-01-01 00:00:00")));
        // ある時点から見えるようになる。
        testPage.createChild(new PageMold().setName("c3").setRevealDate(
            newDate("2105-01-01 00:00:00")));
        // ある期間だけ見える。
        testPage.createChild(new PageMold().setName("c4").setRevealDate(
            newDate("2106-01-01 00:00:00")).setConcealDate(
            newDate("2107-01-01 00:00:00")));
        // ずっと見えない。
        testPage.createChild(new PageMold().setName("c5").setRevealDate(
            Page.DATE_RAGNAROK));

        Page[] children = testPage.getChildren(new PageCondition().setOrder(
            new Order(PageCondition.FIELD_ID)).setIncludeConcealed(false)
            .setCurrentDate(newDate("2100-01-01 00:00:00")));
        assertEquals(2, children.length);
        assertEquals("c1", children[0].getName());
        assertEquals("c2", children[1].getName());

        children = testPage.getChildren(new PageCondition().setOrder(
            new Order(PageCondition.FIELD_ID)).setIncludeConcealed(false)
            .setCurrentDate(newDate("2105-07-01 00:00:00")));
        assertEquals(3, children.length);
        assertEquals("c1", children[0].getName());
        assertEquals("c2", children[1].getName());
        assertEquals("c3", children[2].getName());

        children = testPage.getChildren(new PageCondition().setOrder(
            new Order(PageCondition.FIELD_ID)).setIncludeConcealed(false)
            .setCurrentDate(newDate("2106-07-01 00:00:00")));
        assertEquals(4, children.length);
        assertEquals("c1", children[0].getName());
        assertEquals("c2", children[1].getName());
        assertEquals("c3", children[2].getName());
        assertEquals("c4", children[3].getName());

        children = testPage.getChildren(new PageCondition().setOrder(
            new Order(PageCondition.FIELD_ID)).setIncludeConcealed(false)
            .setCurrentDate(newDate("2107-07-01 00:00:00")));
        assertEquals(3, children.length);
        assertEquals("c1", children[0].getName());
        assertEquals("c2", children[1].getName());
        assertEquals("c3", children[2].getName());

        children = testPage.getChildren(new PageCondition().setOrder(
            new Order(PageCondition.FIELD_ID)).setIncludeConcealed(false)
            .setCurrentDate(newDate("2108-07-01 00:00:00")));
        assertEquals(2, children.length);
        assertEquals("c1", children[0].getName());
        assertEquals("c3", children[1].getName());
    }


    Date newDate(String dateString)
        throws Exception
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
    }


    public void testAsFileAndListing()
        throws Exception
    {
        Page root = pageAlfr_.getRootPage(PathId.HEIM_MIDGARD);
        Page testPage = root.getChild("test");
        if (testPage != null) {
            testPage.delete();
        }

        testPage = root.createChild(new PageMold("test"));
        assertFalse(testPage.isAsFile());
        assertTrue(testPage.isListing());

        testPage.delete();
        testPage = root.createChild(new PageMold("test").setAsFile(true)
            .setListing(false));
        assertTrue(testPage.isAsFile());
        assertFalse(testPage.isListing());

        testPage.setAsFile(false);
        testPage.setListing(true);
        assertFalse(testPage.isAsFile());
        assertTrue(testPage.isListing());
    }


    /*
     * private scope methods
     */

    private Page prepareTestPage()
        throws DuplicatePageException, Exception
    {
        Page root = pageAlfr_.getRootPage(PathId.HEIM_MIDGARD);
        Page testPage = root.getChild("test");
        if (testPage != null) {
            testPage.delete();
        }

        PageMold mold = new PageMold();
        mold.setName("test");
        Date cdate = df_.parse("2005/01/02 03:04:05");
        mold.setCreateDate(cdate);
        Date mdate = df_.parse("2005/02/03 04:05:06");
        mold.setModifyDate(mdate);
        mold.setRevealDate(Page.DATE_RAGNAROK);
        mold.setConcealDate(Page.DATE_RAGNAROK);
        mold.setOrderNumber(2);
        mold.setOwnerUser((User)pageAlfr_.getPage(Page.ID_ANONYMOUS_USER));
        mold.setType("type");
        return root.createChild(mold);
    }
}
