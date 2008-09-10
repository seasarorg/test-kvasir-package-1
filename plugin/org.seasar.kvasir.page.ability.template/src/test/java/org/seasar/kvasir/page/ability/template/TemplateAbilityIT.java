package org.seasar.kvasir.page.ability.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.test.KvasirPluginTestCase;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.impl.ByteArrayInputStreamFactory;


public class TemplateAbilityIT extends
    KvasirPluginTestCase<TemplateAbilityPlugin>
{
    private PageAlfr pageAlfr_;


    @Override
    protected String getTargetPluginId()
    {
        return TemplateAbilityPlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(TemplateAbilityIT.class);
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


    private TemplateAbility prepareTemplateAbilityOfTestPage()
        throws Exception
    {
        return prepareTestPage().getAbility(TemplateAbility.class);
    }


    public void testGetAndSetTemplate()
        throws Exception
    {
        TemplateAbility target = prepareTemplateAbilityOfTestPage();
        assertNull(target.getTemplate());

        target.setTemplate("template");
        Template actual = target.getTemplate();
        assertNotNull(actual);
        assertEquals("template", actual.getBody());
        assertNotNull(actual.getBodyResource());

        target.setTemplate("template2");
        assertEquals("上書き更新できること", "template2", target.getTemplate().getBody());

        target.removeTemplate();
        assertNull(target.getTemplate());
    }


    public void testGetAndSetTemplate_variant()
        throws Exception
    {
        TemplateAbility target = prepareTemplateAbilityOfTestPage();
        assertNull(target.getTemplate("ja_JP"));

        target.setTemplate("ja_JP", "template_ja_JP");
        target.setTemplate("template");
        assertEquals("template_ja_JP", target.getTemplate("ja_JP").getBody());
        assertEquals("template", target.getTemplate().getBody());

        target.setTemplate("ja_JP", "template2");
        assertEquals("上書き更新できること", "template2", target.getTemplate("ja_JP")
            .getBody());
        assertEquals("template", target.getTemplate().getBody());

        target.removeTemplate("ja_JP");
        assertNull(target.getTemplate("ja_JP"));
        assertEquals("template", target.getTemplate().getBody());
    }


    public void testRemoveAndClearTemplates_getVariants_getAndSetType_getAndSetResponseContentType()
        throws Exception
    {
        TemplateAbility target = prepareTemplateAbilityOfTestPage();
        assertEquals(1, target.getVariants().length);
        assertEquals("", target.getType());
        assertEquals("", target.getResponseContentType());

        target.setType("type");
        target.setResponseContentType("responseContentType");
        assertEquals("type", target.getType());
        assertEquals("responseContentType", target.getResponseContentType());

        target.setTemplate("template");
        target.setTemplate("ja_JP", "template2");
        target.setTemplate("en_US", "template3");
        assertEquals(3, target.getVariants().length);
        assertEquals("", target.getVariants()[0]);
        assertEquals("en_US", target.getVariants()[1]);
        assertEquals("ja_JP", target.getVariants()[2]);

        target.removeTemplate("ja_JP");
        assertEquals("template", target.getTemplate().getBody());
        assertEquals("template3", target.getTemplate("en_US").getBody());
        assertEquals(2, target.getVariants().length);
        assertEquals("", target.getVariants()[0]);
        assertEquals("en_US", target.getVariants()[1]);

        target.clearAllTemplates();
        assertEquals(1, target.getVariants().length);
        assertEquals("", target.getType());
        assertEquals("", target.getResponseContentType());
    }


    public void testAttributeNames()
        throws Exception
    {
        TemplateAbility target = prepareTemplateAbilityOfTestPage();

        assertFalse(target.attributeNames(Page.VARIANT_DEFAULT).hasNext());
        assertFalse(target.attributeNames("ja_JP").hasNext());

        target.setTemplate("template");
        target.setTemplate("ja_JP", "template_ja_JP");

        List<String> list = new ArrayList<String>();
        for (Iterator<String> itr = target.attributeNames(Page.VARIANT_DEFAULT); itr
            .hasNext();) {
            list.add(itr.next());
        }
        assertEquals(4, list.size());
        assertEquals("type", list.get(0));
        assertEquals("responseContentType", list.get(1));
        assertEquals("lastModified", list.get(2));
        assertEquals("body", list.get(3));

        list = new ArrayList<String>();
        for (Iterator<String> itr = target.attributeNames("ja_JP"); itr
            .hasNext();) {
            list.add(itr.next());
        }
        assertEquals(2, list.size());
        assertEquals("lastModified", list.get(0));
        assertEquals("body", list.get(1));

        target.clearAllTemplates();
        assertFalse(target.attributeNames(Page.VARIANT_DEFAULT).hasNext());
        assertFalse(target.attributeNames("ja_JP").hasNext());
    }


    public void testGetAndSetAttribute()
        throws Exception
    {
        TemplateAbility target = prepareTemplateAbilityOfTestPage();
        assertNull(target.getAttribute("body", Page.VARIANT_DEFAULT));
        assertNull(target.getAttribute("lastModified", Page.VARIANT_DEFAULT));
        assertEquals("", target.getAttribute("type", Page.VARIANT_DEFAULT)
            .getString(""));
        assertEquals("", target.getAttribute("responseContentType",
            Page.VARIANT_DEFAULT).getString(""));
        assertNull(target.getAttribute("type", "ja_JP"));
        assertNull(target.getAttribute("responseContentType", "ja_JP"));

        Attribute attribute = new Attribute();
        attribute.setStream("", new ByteArrayInputStreamFactory("template"
            .getBytes("UTF-8")));
        target.setAttribute("body", Page.VARIANT_DEFAULT, attribute);
        Template actual = target.getTemplate();
        assertNotNull(actual);
        assertEquals("template", actual.getBody());
        Attribute actual2 = target.getAttribute("body", Page.VARIANT_DEFAULT);
        assertNotNull(actual2);
        assertEquals("template", IOUtils.readString(actual2.getStream("")
            .getInputStream(), "UTF-8", false));

        target.removeAttribute("body", Page.VARIANT_DEFAULT);
        assertNull(target.getTemplate());
        actual2 = target.getAttribute("body", Page.VARIANT_DEFAULT);
        assertNull(actual2);
    }
}
