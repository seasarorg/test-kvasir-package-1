package org.seasar.kvasir.cms.webdav.naming.impl;

import java.util.Properties;
import javax.naming.NameParser;
import javax.naming.Name;
import javax.naming.CompoundName;
import javax.naming.NamingException;


public class ElementNameParser
    implements NameParser
{
    private static Properties       syntax_ = new Properties();


    /*
     * static initializer
     */

    static
    {
        syntax_.put("jndi.syntax.direction", "left_to_right");
        syntax_.put("jndi.syntax.separator", "/");
        syntax_.put("jndi.syntax.ignorecase", "false");
        syntax_.put("jndi.syntax.trimblanks", "true");
    }


    /*
     * public scope methods
     */

    public Name parse(String name)
        throws NamingException
    {
        return new CompoundName(name, syntax_);
    }
}
