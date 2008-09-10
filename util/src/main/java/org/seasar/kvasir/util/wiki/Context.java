package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.seasar.kvasir.util.AttributeHandler;


public class Context
    implements AttributeHandler
{
    private WikiEngine              wikiEngine_;
    private StringWriter            sw_;
    private PrintWriter             writer_;
    private Map                     attributeMap_ = new HashMap();


    /*
     * constructors
     */

    public Context(WikiEngine wikiEngine)
    {
        wikiEngine_ = wikiEngine;
        sw_ = new StringWriter();
        writer_ = new PrintWriter(sw_);
    }


    /*
     * public scope methods
     */
    
    public PrintWriter getWriter()
    {
        return writer_;
    }


    public WikiEngine getWikiEngine()
    {
        return wikiEngine_;
    }


    public String getString()
    {
        writer_.flush();
        return sw_.toString();
    }


    /**
     * 指定された名前の属性を返します。
     * <p>属性が存在しない場合はnullを返します。</p>
     *
     * @param name 属性名。
     * @return 値。
     * @see #setAttribute(String,Object)
     */
    public Object getAttribute(String name)
    {
        return attributeMap_.get(name);
    }


    /**
     * このテンプレート内コンテキストが持つ全ての属性の名前の列挙を返します。
     * <p>属性が存在しない場合は空の列挙を返します。</p>
     *
     * @return 属性名の列挙。
     */
    public Enumeration getAttributeNames()
    {
        Set set = attributeMap_.keySet();
        Vector vector = new Vector(set.size());
        for (Iterator itr = set.iterator(); itr.hasNext(); ) {
            vector.add(itr.next());
        }
        return vector.elements();
    }


    /**
     * 指定された名前の属性を設定します。
     *
     * @param name 属性名。
     * @param o 値。
     */
    public void setAttribute(String name, Object o)
    {
        if (o == null) {
            removeAttribute(name);
        } else {
            attributeMap_.put(name, o);
        }
    }


    /**
     * 指定された名前の属性を削除します。
     *
     * @param name 属性名。
     * @see #setAttribute(String,Object)
     */
    public void removeAttribute(String name)
    {
        attributeMap_.remove(name);
    }
}
