package org.seasar.kvasir.util.collection;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import junit.framework.TestCase;


public class SpikeTest extends TestCase
{
    public void testPropertiesLoad()
        throws Exception
    {
        Properties prop = new Properties();
        prop.setProperty("hoe", "fuga");

        prop.load(new ByteArrayInputStream("key=value".getBytes("ISO-8859-1")));

        assertEquals("value", prop.getProperty("key"));
        assertEquals("Properties#load()では既存のプロパティは消されないこと", "fuga", prop
            .getProperty("hoe"));
    }
}
