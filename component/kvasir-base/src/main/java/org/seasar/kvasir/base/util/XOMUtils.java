package org.seasar.kvasir.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.seasar.kvasir.base.xom.KvasirBeanAccessorFactory;

import net.skirnir.xom.Element;
import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.ValidationException;
import net.skirnir.xom.XMLDeclaration;
import net.skirnir.xom.XMLDocument;
import net.skirnir.xom.XMLParser;
import net.skirnir.xom.XMLParserFactory;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;
import net.skirnir.xom.impl.XMLDocumentImpl;


public class XOMUtils
{
    private static XMLParser parser_ = XMLParserFactory.newInstance();


    private XOMUtils()
    {
    }


    public static <T> T toBean(InputStream in, Class<T> beanClass)
        throws ValidationException, IllegalSyntaxException, IOException
    {
        return toBean(new InputStreamReader(in, "UTF-8"), beanClass);
    }


    public static <T> T toBean(InputStream in, Class<? super T> beanClass,
        T bean)
        throws ValidationException, IllegalSyntaxException, IOException
    {
        return toBean(new InputStreamReader(in, "UTF-8"), beanClass, bean);
    }


    public static <T> T toBean(String xml, Class<T> beanClass)
        throws ValidationException, IllegalSyntaxException, IOException
    {
        return toBean(new StringReader(xml), beanClass);
    }


    public static <T> T toBean(String xml, Class<? super T> beanClass, T bean)
        throws ValidationException, IllegalSyntaxException, IOException
    {
        return toBean(new StringReader(xml), beanClass, bean);
    }


    public static <T> T toBean(Reader reader, Class<T> beanClass)
        throws ValidationException, IllegalSyntaxException, IOException
    {
        return toBean(parser_.parse(reader).getRootElement(), beanClass);
    }


    public static <T> T toBean(Reader reader, Class<? super T> beanClass, T bean)
        throws ValidationException, IllegalSyntaxException, IOException
    {
        return toBean(parser_.parse(reader).getRootElement(), beanClass, bean);
    }


    @SuppressWarnings("unchecked")
    public static <T> T toBean(Element element, Class<T> beanClass)
        throws ValidationException
    {
        return (T)newMapper().toBean(element, beanClass);
    }


    @SuppressWarnings("unchecked")
    public static <T> T toBean(Element element, Class<? super T> beanClass,
        T bean)
        throws ValidationException
    {
        return (T)newMapper().toBean(element, beanClass, bean);
    }


    public static void toXML(Object bean, OutputStream os)
        throws IOException, ValidationException
    {
        toXML(bean, new OutputStreamWriter(os, "UTF-8"));
    }


    public static void toXML(Object bean, Writer writer)
        throws IOException, ValidationException
    {
        XOMapper mapper = newMapper();
        XMLDocument document = new XMLDocumentImpl();
        XMLDeclaration declaration = new XMLDeclaration();
        declaration.setVersion("1.0");
        declaration.setEncoding("UTF-8");
        document.setXMLDeclaration(declaration);
        document.setRootElement(mapper.toElement(bean));
        mapper.toXML(document, writer);
    }


    public static String toXML(Object bean)
        throws ValidationException
    {
        if (bean == null) {
            return null;
        }

        StringWriter sw = new StringWriter();
        try {
            toXML(bean, sw);
        } catch (IOException ex) {
            throw new RuntimeException("Can't happen!", ex);
        }
        return sw.toString();
    }


    public static XOMapper newMapper()
    {
        return XOMapperFactory.newInstance().setBeanAccessorFactory(
            new KvasirBeanAccessorFactory());
    }


    public static Element toElement(String xml)
        throws IllegalSyntaxException, IOException
    {
        return toElement(new StringReader(xml));
    }


    public static Element toElement(InputStream in)
        throws IllegalSyntaxException, IOException
    {
        return toElement(new InputStreamReader(in, "UTF-8"));
    }


    public static Element toElement(Reader reader)
        throws IllegalSyntaxException, IOException
    {
        return parser_.parse(reader).getRootElement();
    }

}
