package org.seasar.kvasir.page.ability.content;

import java.util.Date;
import java.util.Locale;

import junit.framework.Test;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class ContentAbilityIT extends
    KvasirPluginTestCase<ContentAbilityPlugin>
{
    private PageAlfr pageAlfr_;

    @Override
    protected String getTargetPluginId()
    {
        return ContentAbilityPlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(ContentAbilityIT.class);
    }


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        pageAlfr_ = getKvasir().getPluginAlfr().getPlugin(PagePlugin.class)
            .getPageAlfr();
    }


    private Page prepareTestPage()
        throws Exception
    {
        Page root = pageAlfr_.getRootPage(PathId.HEIM_MIDGARD);
        Page testPage = root.getChild("test");
        if (testPage != null) {
            testPage.delete();
        }

        return root.createChild(new PageMold().setName("test"));
    }


    private ContentAbility prepareContentAbilityOfTestPage()
        throws Exception
    {
        return prepareTestPage().getAbility(ContentAbility.class);
    }


    public void testGetContent_getLatestContent_setContent_updateContent_clearContents_clearAllContents()
        throws Exception
    {
        ContentAbility ability = prepareContentAbilityOfTestPage();

        assertNull(ability.getContent(Page.VARIANT_DEFAULT, 1));

        ability.setContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setMediaType("text/hogehoge").setBodyString("default"));

        Content actual = ability.getContent(Page.VARIANT_DEFAULT, 1);
        assertEquals("text/hogehoge", actual.getMediaType());
        assertEquals("default", actual.getBodyString());
        assertEquals("default", ability
            .getContent(new Locale("ja"), new Date()).getBodyString());
        assertEquals(1, ability.getLatestContent(Page.VARIANT_DEFAULT)
            .getRevisionNumber());

        ability.setContent("ja", new ContentMold().setBodyString("ja"));

        assertEquals("mediaTypeが無指定の場合はデフォルトバリアントのmediaTypeが引き継がれること",
            "text/hogehoge", actual.getMediaType());
        assertEquals("ja", ability.getContent("ja", 1).getBodyString());
        assertEquals("ja", ability.getContent(new Locale("ja"), new Date())
            .getBodyString());
        assertEquals(1, ability.getLatestContent("ja").getRevisionNumber());

        ability.setContent("ja", new ContentMold().setBodyString("ja2")
            .setMediaType("text/hogehoge2"));
        actual = ability.getContent("ja", 1);
        assertEquals("text/hogehoge2", actual.getMediaType());
        assertEquals("ja2", actual.getBodyString());
        assertEquals("ja2", ability.getContent(new Locale("ja"), new Date())
            .getBodyString());
        assertEquals(1, ability.getLatestContent("ja").getRevisionNumber());

        ability.setContent("ja", new ContentMold()
            .setMediaType("text/hogehoge2a"));

        actual = ability.getContent("ja", 1);
        assertEquals("setContent()ではメタデータだけを設定できること", "text/hogehoge2a", actual
            .getMediaType());
        assertEquals("ja2", actual.getBodyString());
        assertEquals(1, ability.getLatestContent("ja").getRevisionNumber());

        ability.updateContent("ja", new ContentMold().setBodyString("ja3"));

        assertEquals("ja2", ability.getContent("ja", 1).getBodyString());
        actual = ability.getContent("ja", 2);
        assertEquals("無指定の場合は以前のリビジョンのmediaTypeが引き継がれること", "text/hogehoge2a",
            actual.getMediaType());
        assertEquals("ja3", actual.getBodyString());
        assertEquals("ja3", ability.getContent(new Locale("ja"), new Date())
            .getBodyString());
        assertEquals(2, ability.getLatestContent("ja").getRevisionNumber());

        ability.clearContents("ja");

        assertNull(ability.getContent("ja", 1));
        assertNull(ability.getContent("ja", 2));
        assertEquals("default", ability
            .getContent(new Locale("ja"), new Date()).getBodyString());
        assertNull(ability.getLatestContent("ja"));
        assertEquals(0, ability.getLatestRevisionNumber("ja"));

        ability.clearAllContents();
        assertNull(ability.getContent(Page.VARIANT_DEFAULT, 1));
    }


    public void testGetModifyDate()
        throws Exception
    {
        ContentAbility ability = prepareContentAbilityOfTestPage();

        assertEquals(0L, ability.getModifyDate().getTime());

        ability.setContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString(""));

        assertTrue(ability.getModifyDate().getTime() > 0L);
    }


    public void testGetEarliestRevisionNumber()
        throws Exception
    {
        ContentAbility ability = prepareContentAbilityOfTestPage();

        assertEquals(0, ability.getEarliestRevisionNumber(Page.VARIANT_DEFAULT));
        assertEquals(0, ability.getEarliestRevisionNumber("ja"));

        ability.setContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString(""));

        assertEquals(1, ability.getEarliestRevisionNumber(Page.VARIANT_DEFAULT));
        assertEquals(0, ability.getEarliestRevisionNumber("ja"));

        ability.setContent("ja", new ContentMold().setBodyString("ja"));

        assertEquals(1, ability.getEarliestRevisionNumber(Page.VARIANT_DEFAULT));
        assertEquals(1, ability.getEarliestRevisionNumber("ja"));

        ability.clearContents(Page.VARIANT_DEFAULT);

        assertEquals(0, ability.getEarliestRevisionNumber(Page.VARIANT_DEFAULT));
        assertEquals(1, ability.getEarliestRevisionNumber("ja"));

        ability.clearAllContents();

        assertEquals(0, ability.getEarliestRevisionNumber(Page.VARIANT_DEFAULT));
        assertEquals(0, ability.getEarliestRevisionNumber("ja"));
    }


    public void testClearContents_本文が消されること()
        throws Exception
    {
        ContentAbility ability = prepareContentAbilityOfTestPage();

        ability.updateContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString(""));
        Content content1 = ability.getContent(Page.VARIANT_DEFAULT, 1);
        ability.updateContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString(""));
        Content content2 = ability.getContent(Page.VARIANT_DEFAULT, 1);

        assertTrue(content1.getBodyResource().exists());
        assertTrue(content2.getBodyResource().exists());

        ability.clearAllContents();

        assertFalse(content1.getBodyResource().exists());
        assertFalse(content2.getBodyResource().exists());
    }


    public void testSetContent_最初はリビジョン番号1が振られること()
        throws Exception
    {
        ContentAbility ability = prepareContentAbilityOfTestPage();

        ability.setContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString("body"));

        Content actual = ability.getContent(Page.VARIANT_DEFAULT, 1);
        assertNotNull(actual);
        assertEquals("body", actual.getBodyString());
        assertEquals("UTF-8", actual.getEncoding());
    }


    public void testUpdateContent_最初はリビジョン番号1が振られること()
        throws Exception
    {
        ContentAbility ability = prepareContentAbilityOfTestPage();

        ability.updateContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString("body"));

        Content actual = ability.getContent(Page.VARIANT_DEFAULT, 1);
        assertNotNull(actual);
        assertEquals("body", actual.getBodyString());
        assertEquals("UTF-8", actual.getEncoding());
    }


    public void testSetContent_encodingを指定しなかった場合は以前のリビジョンのencodingが使われること()
        throws Exception
    {
        ContentAbility ability = prepareContentAbilityOfTestPage();

        ability.updateContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString("body").setEncoding("ISO-8859-1"));
        ability.updateContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString("body2"));

        assertEquals("ISO-8859-1", ability.getContent(Page.VARIANT_DEFAULT, 2)
            .getEncoding());
    }


    public void testRemoveContentsBefore()
        throws Exception
    {
        ContentAbility ability = prepareContentAbilityOfTestPage();

        ability.updateContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString("body"));
        ability.updateContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString("body"));
        ability.updateContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setBodyString("body"));

        ability.removeContentsBefore(Page.VARIANT_DEFAULT, 2);

        assertEquals(2, ability.getEarliestRevisionNumber(Page.VARIANT_DEFAULT));
    }


    public void test_variantを引数に取るgetLatestContentメソッド等でcompiledBodyが正しく設定されること()
        throws Exception
    {
        ContentAbility ability = prepareContentAbilityOfTestPage();

        ability.setContent(Page.VARIANT_DEFAULT, new ContentMold()
            .setMediaType("text/plain").setBodyString("test"));

        Content actual = ability.getContent(Page.VARIANT_DEFAULT, 1);
        assertNotNull(actual);
        assertEquals("test", actual.getBodyString());
        assertEquals("test", actual.getBodyHTMLString(null));
    }


    public void test_コンテンツを持たない場合は属性も持たないこと()
        throws Exception
    {
        Page page = prepareTestPage();

        ContentAbility ability = page.getAbility(ContentAbility.class);
        assertFalse("コンテンツを持たない場合は属性も持たないこと", ability.attributeNames(
            Page.VARIANT_DEFAULT).hasNext());

        page.getAbility(ContentAbility.class).setContent(Page.VARIANT_DEFAULT,
            new ContentMold().setBodyString(""));

        assertTrue(ability.attributeNames(Page.VARIANT_DEFAULT).hasNext());

        page.getAbility(ContentAbility.class).clearAllContents();

        assertFalse("コンテンツを全て削除した場合は属性を持たないこと", ability.attributeNames(
            Page.VARIANT_DEFAULT).hasNext());
    }
}
