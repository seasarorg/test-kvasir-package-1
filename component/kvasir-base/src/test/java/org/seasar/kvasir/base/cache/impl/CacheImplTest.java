package org.seasar.kvasir.base.cache.impl;

import junit.framework.TestCase;

import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.impl.AgeRefreshingStrategy;
import org.seasar.kvasir.base.cache.impl.CacheImpl;
import org.seasar.kvasir.base.cache.impl.ImmediateRefreshingStrategy;
import org.seasar.kvasir.base.cache.impl.LRUMapCacheStorage;


public class CacheImplTest extends TestCase
{
    private TemplateProvider templateProvider_ = new TemplateProvider();

    private CacheImpl<String, Template> target_ = (CacheImpl<String, Template>)new CacheImpl<String, Template>()
        .setCacheStorage(new LRUMapCacheStorage<String, Template>(100))
        .setObjectProvider(templateProvider_).setRefreshingStrategy(
            new AgeRefreshingStrategy<String, Template>(2));


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        templateProvider_.add("a", new Template("a", "typea", "templatea"));
    }


    public void testGetEntry1()
        throws Exception
    {
        CachedEntry<String, Template> entry = target_.getEntry("a");
        assertNotNull(entry);

        CachedEntry<String, Template> entry2 = target_.getEntry("a");
        assertNotNull(entry2);
        assertSame("リフレッシュされるまでは同じエントリが返されること", entry, entry2);

        CachedEntry<String, Template> entry3 = target_.getEntry("a");
        assertNotNull(entry3);
        assertNotSame("リフレッシュされたらエントリが再生成されて返されること", entry2, entry3);
    }


    public void testPing1()
        throws Exception
    {
        CachedEntry<String, Template> entry = target_.getEntry("a");
        target_.ping();
        CachedEntry<String, Template> entry2 = target_.getEntry("a");

        assertSame("pingに反応しないRefreshingStrategyを使っている場合はエントリが再生成されないこと",
            entry, entry2);
    }


    public void testPing2()
        throws Exception
    {
        target_
            .setRefreshingStrategy(new ImmediateRefreshingStrategy<String, Template>());

        CachedEntry<String, Template> entry = target_.getEntry("a");
        target_.ping();
        CachedEntry<String, Template> entry2 = target_.getEntry("a");

        assertNotSame("pingに反応するRefreshingStrategyを使っている場合はエントリが再生成されること",
            entry, entry2);
    }


    public void testRefresh()
        throws Exception
    {
        CachedEntry<String, Template> entry = target_.getEntry("a");
        target_.refresh();
        CachedEntry<String, Template> entry2 = target_.getEntry("a");

        assertNotSame("明示的にリフレッシュした場合はエントリが再生成されること", entry, entry2);
    }
}
