package org.seasar.kvasir.base.util;

import org.seasar.kvasir.base.util.XOMUtils;

import junit.framework.TestCase;

import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.ValidationException;


public class XOMUtilsTest extends TestCase
{
    private static final String SP = System.getProperty("line.separator");


    public void testToBean()
        throws Exception
    {
        try {
            XOMUtils.toBean(getClass().getResourceAsStream(
                getClass().getName().substring(
                    getClass().getName().lastIndexOf('.') + 1).concat(
                    "_testToBean.xml")), Hoe.class);
        } catch (ValidationException ex) {
            fail();
        } catch (IllegalSyntaxException ex) {
            fail();
        }
    }


    public void testToXML()
        throws Exception
    {
        String actual = XOMUtils.toXML(new Hoe());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + SP
            + "<hoe />" + SP, actual);
    }
}
