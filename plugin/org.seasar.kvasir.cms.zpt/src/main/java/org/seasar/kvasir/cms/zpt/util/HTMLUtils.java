package org.seasar.kvasir.cms.zpt.util;

import java.io.StringReader;

import net.skirnir.freyja.Attribute;
import net.skirnir.freyja.Element;
import net.skirnir.freyja.IllegalSyntaxException;
import net.skirnir.freyja.TagElement;
import net.skirnir.freyja.TemplateParser;


public class HTMLUtils extends org.seasar.kvasir.util.html.HTMLUtils
{
    private static final TemplateParser PARSER = new TemplateParser(
        new String[0], new String[] { "id" });


    protected HTMLUtils()
    {
    }


    public static TagElement getElementById(String document, String id)
    {
        Element[] elements;
        try {
            elements = PARSER.parse(new StringReader(document));
        } catch (IllegalSyntaxException ex) {
            return null;
        }

        return traverse(elements, id);
    }


    static TagElement traverse(Element[] elements, String id)
    {
        if (elements == null) {
            return null;
        }
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] instanceof TagElement) {
                TagElement element = (TagElement)elements[i];
                Attribute[] attributes = element.getAttributes();
                for (int j = 0; j < attributes.length; j++) {
                    if ("id".equals(attributes[j].getName())
                        && id.equals(attributes[j].getValue())) {
                        return element;
                    }
                }
                TagElement result = traverse(element.getBodyElements(), id);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }


    public static String toString(Element element)
    {
        if (element == null) {
            return null;
        }
        return element.toString();
    }


    public static String innerHTML(TagElement element)
    {
        if (element == null) {
            return null;
        }
        Element[] children = element.getBodyElements();
        StringBuilder sb = new StringBuilder();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                sb.append(children[i]);
            }
        }
        return sb.toString();
    }
}
