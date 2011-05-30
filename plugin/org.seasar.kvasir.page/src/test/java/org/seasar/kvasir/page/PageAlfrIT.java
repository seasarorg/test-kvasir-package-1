package org.seasar.kvasir.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import junit.framework.Test;

import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.Directory;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


/**
 * @author YOKOTA Takehiko
 */
public class PageAlfrIT extends PagePluginITCase
{
    private PageAlfr target_;


    public static Test suite()
        throws Exception
    {
        return createTestSuite(PageAlfrIT.class, false);
    }


    @Override
    protected void setUp()
        throws Exception
    {
        target_ = ((PagePlugin)getPlugin()).getPageAlfr();
    }


    public void testGenerated()
    {
        assertNotNull(target_);
    }


    public void testGetPage()
    {
        assertNull(target_.getPage(Page.ID_NONE));

        Page page = target_.getPage(Page.ID_ROOT);
        assertNotNull(page);
        assertTrue(page instanceof Directory);
        assertEquals(Page.ID_ROOT, page.getId());
        assertEquals(Page.PATHNAME_ROOT, page.getPathname());

        page = target_.getPage(Page.ID_ADMINISTRATOR_USER);
        assertNotNull(page);
        assertTrue(page instanceof User);
        assertEquals(Page.ID_ADMINISTRATOR_USER, page.getId());
        assertEquals(Page.PATHNAME_ADMINISTRATOR_USER, page.getPathname());

        page = target_.getPage(Page.ID_ANONYMOUS_USER);
        assertNotNull(page);
        assertTrue(page instanceof User);
        assertEquals(Page.ID_ANONYMOUS_USER, page.getId());
        assertEquals(Page.PATHNAME_ANONYMOUS_USER, page.getPathname());

        page = target_.getPage(Page.ID_ADMINISTRATOR_GROUP);
        assertNotNull(page);
        assertTrue(page instanceof Group);
        assertEquals(Page.ID_ADMINISTRATOR_GROUP, page.getId());
        assertEquals(Page.PATHNAME_ADMINISTRATOR_GROUP, page.getPathname());

        page = target_.getPage(Page.ID_ALL_GROUP);
        assertNotNull(page);
        assertTrue(page instanceof Group);
        assertEquals(Page.ID_ALL_GROUP, page.getId());
        assertEquals(Page.PATHNAME_ALL_GROUP, page.getPathname());

        page = target_.getPage(Page.ID_ADMINISTRATOR_ROLE);
        assertNotNull(page);
        assertTrue(page instanceof Role);
        assertEquals(Page.ID_ADMINISTRATOR_ROLE, page.getId());
        assertEquals(Page.PATHNAME_ADMINISTRATOR_ROLE, page.getPathname());

        page = target_.getPage(Page.ID_ANONYMOUS_ROLE);
        assertNotNull(page);
        assertTrue(page instanceof Role);
        assertEquals(Page.ID_ANONYMOUS_ROLE, page.getId());
        assertEquals(Page.PATHNAME_ANONYMOUS_ROLE, page.getPathname());

        page = target_.getPage(Page.ID_ALL_ROLE);
        assertNotNull(page);
        assertTrue(page instanceof Role);
        assertEquals(Page.ID_ALL_ROLE, page.getId());
        assertEquals(Page.PATHNAME_ALL_ROLE, page.getPathname());

        page = target_.getPage(Page.ID_OWNER_ROLE);
        assertNotNull(page);
        assertTrue(page instanceof Role);
        assertEquals(Page.ID_OWNER_ROLE, page.getId());
        assertEquals(Page.PATHNAME_OWNER_ROLE, page.getPathname());

        page = target_.getPage(Page.ID_ALFHEIM_ROOT);
        assertNotNull(page);
        assertTrue(page instanceof Directory);
        assertEquals(Page.ID_ALFHEIM_ROOT, page.getId());
        assertEquals("", page.getPathname());

        page = target_.getPage(Page.ID_USERS);
        assertNotNull(page);
        assertTrue(page instanceof Directory);
        assertEquals(Page.ID_USERS, page.getId());
        assertEquals(Page.PATHNAME_USERS, page.getPathname());

        page = target_.getPage(Page.ID_GROUPS);
        assertNotNull(page);
        assertTrue(page instanceof Directory);
        assertEquals(Page.ID_GROUPS, page.getId());
        assertEquals(Page.PATHNAME_GROUPS, page.getPathname());

        page = target_.getPage(Page.ID_ROLES);
        assertNotNull(page);
        assertTrue(page instanceof Directory);
        assertEquals(Page.ID_ROLES, page.getId());
        assertEquals(Page.PATHNAME_ROLES, page.getPathname());

        page = target_.getPage(Page.ID_TEMPLATES);
        assertNotNull(page);
        assertTrue(page instanceof Directory);
        assertEquals(Page.ID_TEMPLATES, page.getId());
        assertEquals(Page.PATHNAME_TEMPLATES, page.getPathname());

        page = target_.getPage(Page.ID_SYSTEM);
        assertNotNull(page);
        assertTrue(page instanceof Directory);
        assertEquals(Page.ID_SYSTEM, page.getId());
        assertEquals(Page.PATHNAME_SYSTEM, page.getPathname());

        page = target_.getPage(Page.ID_PLUGINS);
        assertNotNull(page);
        assertTrue(page instanceof Directory);
        assertEquals(Page.ID_PLUGINS, page.getId());
        assertEquals(Page.PATHNAME_PLUGINS, page.getPathname());
    }


    public void testPageDtoCache()
    {
        Page page1 = target_.getPage(Page.ID_ADMINISTRATOR_USER);
        Page page2 = target_.getPage(Page.ID_ADMINISTRATOR_USER);
        assertEquals(page1, page2);
        assertNotSame(page1, page2);
        assertSame(page1.getDto(), page2.getDto());
    }


    public void testOrderByPathname()
        throws Exception
    {
        Page[] pages;
        try {
            pages = target_.getRootPage(PathId.HEIM_MIDGARD).getChildren(
                new PageCondition().setOrder(new Order(
                    PageCondition.FIELD_PATHNAME, false)));
        } catch (Throwable t) {
            t.printStackTrace();
            fail("ダミーフィールド「pathname」を指定して並べ替えができること");
            return;
        }

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < pages.length; i++) {
            list.add(pages[i].getPathname());
        }
        String[] actual = list.toArray(new String[0]);
        String[] expected = list.toArray(new String[0]);
        Arrays.sort(expected, new Comparator<String>() {
            public int compare(String o1, String o2)
            {
                return o2.compareTo(o1);
            }
        });
        for (int i = 0; i < actual.length; i++) {
            assertEquals("pathnameの逆順に結果が返ってくること", expected[i], actual[i]);
        }
    }
}
