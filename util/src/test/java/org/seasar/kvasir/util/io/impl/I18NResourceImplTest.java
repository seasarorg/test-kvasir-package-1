package org.seasar.kvasir.util.io.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;

import junit.framework.TestCase;

import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.I18NResource;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * @author YOKOTA Takehiko
 */
public class I18NResourceImplTest extends TestCase
{
    public void testGetInputStream_String()
        throws Exception
    {
        I18NResourceImpl resource = newInstance();
        InputStream in = null;
        try {
            in = resource.getInputStream(I18NResource.VARIANT_DEFAULT);
        } catch (ResourceNotFoundException ex) {
            fail("存在するリソースのInputStreamを取得できること");
        }
        assertEquals("指定したVARIANTのリソースのInputStreamを取得できること",
            "BASE\r\n", IOUtils.readString(in, "UTF-8", false));

        try {
            in = resource.getInputStream("fr");
        } catch (ResourceNotFoundException ex) {
            fail("存在するリソースのInputStreamを取得できること");
        }
        assertEquals("指定したVARIANTのリソースのInputStreamを取得できること",
            "FR\r\n", IOUtils.readString(in, "UTF-8", false));

        try {
            resource.getInputStream("hoe");
            fail("存在しないリソースのInputStreamは取得できないこと");
        } catch (ResourceNotFoundException ex) {
            ;
        }
    }


    public void testGetInputStream_Locale()
        throws Exception
    {
        I18NResourceImpl resource = newInstance();
        InputStream in = null;
        try {
            in = resource.getInputStream(new Locale("fr"));
        } catch (ResourceNotFoundException ex) {
            fail("存在するリソースのInputStreamを取得できること");
        }
        assertEquals("指定したVARIANTのリソースのInputStreamを取得できること",
            "FR\r\n",
            IOUtils.readString(in, "UTF-8", false));

        try {
            in = resource.getInputStream(new Locale("hoe"));
        } catch (ResourceNotFoundException ex) {
            fail("存在するリソースのInputStreamを取得できること");
        }
        assertEquals("指定したVARIANTのリソースのInputStreamを取得できること",
            "BASE\r\n", IOUtils
            .readString(in, "UTF-8", false));
    }


    public void testExists_String()
        throws Exception
    {
        I18NResourceImpl resource = newInstance();
        assertTrue("存在するリソースの存在を確認できること",
            resource.exists("fr"));
        assertTrue("存在するリソースの存在を確認できること",
            resource.exists(I18NResource.VARIANT_DEFAULT));
        assertFalse("存在しないリソースの存在を確認できないこと",
            resource.exists("hoe"));
    }


    public void testExists_Locale()
        throws Exception
    {
        I18NResourceImpl resource = newInstance();
        assertTrue("存在するリソースの存在を確認できること",
            resource.exists(new Locale("fr")));
        assertTrue("存在するリソースの存在を確認できること",
            resource.exists(new Locale("hoe")));
    }


    public void testGetLastModifiedTime_String()
        throws Exception
    {
        I18NResourceImpl resource = newInstance();
        assertEquals("存在するリソースの最終更新時刻を正しく取得できること",
            getParentResource().getChildResource("base_fr.xproperties")
            .getLastModifiedTime(),
            resource.getLastModifiedTime("fr"));
    }


    public void testGetLastModifiedTime_Locale()
        throws Exception
    {
        I18NResourceImpl resource = newInstance();
        assertEquals("存在するリソースの最終更新時刻を正しく取得できること",
            getParentResource().getChildResource("base_fr.xproperties")
            .getLastModifiedTime(),
            resource.getLastModifiedTime(new Locale("fr")));
        assertEquals("存在するリソースの最終更新時刻を正しく取得できること",
            getParentResource().getChildResource("base.xproperties")
            .getLastModifiedTime(),
            resource.getLastModifiedTime(new Locale("hoe")));
    }


    public void testGetVariants()
        throws Exception
    {
        I18NResourceImpl resource = newInstance();
        String[] variants = resource.getVariants();
        assertEquals("正しい個数のVARIANTを取得できること",
            3, variants.length);
        Arrays.sort(variants);
        assertEquals("正しくVARIANTを取得できること",
            I18NResource.VARIANT_DEFAULT, variants[0]);
        assertEquals("正しくVARIANTを取得できること",
            "en", variants[1]);
        assertEquals("正しくVARIANTを取得できること",
            "fr", variants[2]);
    }


    /*
     * private scope methods
     */

    private I18NResourceImpl newInstance()
    {
        Resource resource = getParentResource().getChildResource(
            "base.xproperties");
        return new I18NResourceImpl(resource);
    }


    private Resource getParentResource()
    {
        return new FileResource(
            new File(ClassUtils.getParentDirectory(getClass()),
            "i18n"));
    }
}
