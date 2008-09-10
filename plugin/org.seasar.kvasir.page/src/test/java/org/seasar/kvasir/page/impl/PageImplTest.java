package org.seasar.kvasir.page.impl;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageWrapper;


/**
 * @author YOKOTA Takehiko
 */
public class PageImplTest extends TestCase
{
    public void testGetNonExistentName()
        throws Exception
    {
        final Map<String, PageWrapper> nameMap = new HashMap<String, PageWrapper>();
        nameMap.put("name", new PageWrapper(null) {});
        nameMap.put("name2", new PageWrapper(null) {});
        nameMap.put("acc2", new PageWrapper(null) {});
        PageImpl page = new PageImpl() {
            @Override
            public Page getChild(String name)
            {
                return nameMap.get(name);
            }
        };

        assertEquals("重複する名前がある場合はそれを避けて名前文字列を取得できること", "name3", page
            .getNonExistentChildName("name"));
        assertEquals("重複する名前がない場合は指定した名前文字列をそのまま返すこと", "acc", page
            .getNonExistentChildName("acc"));
    }
}
