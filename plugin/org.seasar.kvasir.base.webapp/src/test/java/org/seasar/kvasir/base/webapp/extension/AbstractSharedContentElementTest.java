package org.seasar.kvasir.base.webapp.extension;

import java.io.OutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.seasar.kvasir.base.webapp.Content;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.MapResource;


public class AbstractSharedContentElementTest extends TestCase
{
    private AbstractSharedContentElement target_;


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        final Resource root = MapResource.newInstance();
        Resource libraryDir = root.getChildResource("js/prototype");
        libraryDir.mkdirs();
        libraryDir.getChildResource("prototype.js").getOutputStream().close();
        OutputStream out = libraryDir.getChildResource("sarusaru.js")
            .getOutputStream();
        out.write("sarusaru".getBytes("UTF-8"));
        out.close();

        target_ = new AbstractSharedContentElement() {
            @Override
            protected String getBasePath()
            {
                return "/plugins/js";
            }


            @Override
            Resource getBaseResource()
            {
                return root;
            }


            @Override
            protected String getDefaultContentType()
            {
                return "text/javascript";
            }
        };
        target_.setId("prototype");
        target_.setResourcePath("js/prototype");
    }


    public void testGetExpandedPaths()
        throws Exception
    {
        String[] actual = target_.getExpandedPaths();
        Arrays.sort(actual);

        assertEquals(2, actual.length);
        assertEquals("/plugins/js/prototype/prototype.js", actual[0]);
        assertEquals("/plugins/js/prototype/sarusaru.js", actual[1]);
    }


    public void testGetContent()
        throws Exception
    {
        Content actual = target_.getContent(
            "/plugins/js/prototype/sarusaru.js", null);

        assertNotNull(actual);
        assertEquals("sarusaru", IOUtils.readString(actual.getInputStream(),
            "UTF-8", false));
        assertEquals("text/javascript", actual.getContentType());
    }
}
