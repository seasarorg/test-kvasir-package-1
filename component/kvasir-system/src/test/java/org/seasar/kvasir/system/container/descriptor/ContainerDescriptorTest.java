package org.seasar.kvasir.system.container.descriptor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.XMLDocument;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessorFactory;
import net.skirnir.xom.impl.XMLParserImpl;
import net.skirnir.xom.impl.XOMapperImpl;

import junit.framework.TestCase;


public class ContainerDescriptorTest extends TestCase
{
    private XOMapper mapper_ = new XOMapperImpl()
        .setBeanAccessorFactory(new AnnotationBeanAccessorFactory());


    private XMLDocument readXMLDocument(String name)
        throws IOException, IllegalSyntaxException,
        UnsupportedEncodingException
    {
        return new XMLParserImpl().parse(new InputStreamReader(
            getClass().getClassLoader()
                .getResourceAsStream(
                    getClass().getName().replace('.', '/').concat("_").concat(
                        name)), "UTF-8"));
    }


    public void testToBean1()
        throws Exception
    {
        XMLDocument document = readXMLDocument("testToBean1.dicon");
        try {
            mapper_.toBean(document.getRootElement(), Components.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }
}
