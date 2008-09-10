package org.seasar.kvasir.util.collection;

import java.io.StringReader;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 */
public class MapPropertiesTest extends TestCase
{
    public void testLoadReader()
        throws Exception
    {
        MapProperties prop = new MapProperties();
        StringReader reader = new StringReader(
            "Truth = Beauty\n");
        prop.load(reader);
        assertEquals("Beauty", prop.getProperty("Truth"));

        reader = new StringReader(
            "       Truth:Beauty\n");
        prop.load(reader);
        assertEquals("Beauty", prop.getProperty("Truth"));

        reader = new StringReader(
            "Truth          :Beauty\n");
        prop.load(reader);
        assertEquals("Beauty", prop.getProperty("Truth"));

        reader = new StringReader(
            "fruits             apple, banana, pear, \\\n" +
            "                   cantaloupe, watermelon, \\\n" +
            "                   kiwi, mango\n");
        prop.load(reader);
        assertEquals(
            "apple, banana, pear, cantaloupe, watermelon, kiwi, mango",
            prop.getProperty("fruits"));
    }
}
