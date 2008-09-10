package org.seasar.kvasir.webapp.util;

import java.io.File;

import junit.framework.TestCase;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;
import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.webapp.mock.MockHttpServletRequest;


public class ServletUtilsTest extends TestCase
{
    private static final String GETRESPONSE_EXPECTED = "<html>\r\n  <head>\r\n    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" />\r\n  </head>\r\n  <body>\r\n  </body>\r\n</html>\r\n";

    private Server jettyServer_;


    public void testGetNativePath()
        throws Exception
    {
        String actual = ServletUtils
            .getNativePath(new MockHttpServletRequest() {
                @Override
                public String getServletPath()
                {
                    return "";
                }


                @Override
                public String getPathInfo()
                {
                    return "/";
                }


                @Override
                public String getRequestURI()
                {
                    return "/context";
                }
            });

        // servlet-mappingが'/*'の時はコンテキストルート（'/'なし）へのリクエストを受けても
        // pathInfoは'/'になってしまう。この場合でも正しく元々のパスを取得できること。
        assertEquals("'/context'に関して正しくパスを取得できること", "", actual);

        actual = ServletUtils.getNativePath(new MockHttpServletRequest() {
            @Override
            public String getServletPath()
            {
                return "";
            }


            @Override
            public String getPathInfo()
            {
                return "/";
            }


            @Override
            public String getRequestURI()
            {
                return "/context/";
            }
        });

        assertEquals("'/context/'に関して正しくパスを取得できること", "/", actual);
    }


    public void testGetNativeRequestPath()
        throws Exception
    {
        String actual = ServletUtils
            .getNativeRequestPath(new MockHttpServletRequest() {
                @Override
                public String getServletPath()
                {
                    return "";
                }


                @Override
                public String getPathInfo()
                {
                    return "/";
                }


                @Override
                public String getRequestURI()
                {
                    return "/context";
                }
            });

        // servlet-mappingが'/*'の時はコンテキストルート（'/'なし）へのリクエストを受けても
        // pathInfoは'/'になってしまう。この場合でも正しく元々のパスを取得できること。
        assertEquals("'/context'に関して正しくパスを取得できること", "", actual);

        actual = ServletUtils
            .getNativeRequestPath(new MockHttpServletRequest() {
                @Override
                public String getServletPath()
                {
                    return "";
                }


                @Override
                public String getPathInfo()
                {
                    return "/";
                }


                @Override
                public String getRequestURI()
                {
                    return "/context/";
                }
            });

        assertEquals("'/context/'に関して正しくパスを取得できること", "/", actual);
    }


    public void testGetURI()
        throws Exception
    {
        assertNull(ServletUtils.getURI(null, new MockHttpServletRequest()));

        MockHttpServletRequest request = new MockHttpServletRequest() {
            @Override
            public String getContextPath()
            {
                return "/context";
            }
        };
        assertEquals("/context/path/to/page", ServletUtils.getURI(
            "/path/to/page", request));

        assertEquals("/context/path/to/page?a=b", ServletUtils.getURI(
            "/path/to/page?a=b", request));

        assertEquals("/context/path/to/page;jssessionid=1?a=b", ServletUtils
            .getURI("/path/to/page;jssessionid=1?a=b", request));

        request = new MockHttpServletRequest() {
            @Override
            public String getContextPath()
            {
                return "";
            }
        };
        assertEquals("/", ServletUtils.getURI("", request));

        assertEquals("/?a=b", ServletUtils.getURI("?a=b", request));

        assertEquals("/;jssessionid=1?a=b", ServletUtils.getURI(
            ";jssessionid=1?a=b", request));
    }


    public void testGetResponse()
        throws Exception
    {
        startJetty();
        try {
            Response actual = ServletUtils
                .getResponse("http://localhost:8888/test/index.html");
            assertNotNull(actual);
            assertEquals(200, actual.getStatus());
            assertEquals("text/html", actual.getMediaType());
            assertEquals("UTF-8", actual.getCharset());
            assertEquals(GETRESPONSE_EXPECTED, actual.getString());
        } finally {
            stopJetty();
        }
    }


    public void testGetResponse_リダイレクト後のコンテンツが取得できること()
        throws Exception
    {
        startJetty();
        try {
            Response actual = ServletUtils
                .getResponse("http://localhost:8888/test");
            assertNotNull(actual);
            assertEquals(200, actual.getStatus());
            assertEquals("text/html", actual.getMediaType());
            assertEquals("UTF-8", actual.getCharset());
            assertEquals(GETRESPONSE_EXPECTED, actual.getString());
        } finally {
            stopJetty();
        }
    }


    public void testGetResponse_gifが取得できること()
        throws Exception
    {
        startJetty();
        try {
            Response actual = ServletUtils
                .getResponse("http://localhost:8888/test/outer.gif");
            assertNotNull(actual);
            assertEquals(200, actual.getStatus());
            assertEquals("image/gif", actual.getMediaType());
            byte[] content = actual.getBytes();
            byte[] expected = IOUtils.readBytes(getClass().getResourceAsStream(
                "outer.gif"));
            assertEquals(expected.length, content.length);
            for (int i = 0; i < expected.length; i++) {
                if (expected[i] != content[i]) {
                    fail();
                }
            }
        } finally {
            stopJetty();
        }
    }


    private void startJetty()
        throws Exception
    {
        jettyServer_ = new Server();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        jettyServer_.setHandler(contexts);

        SocketConnector connector = new SocketConnector();
        connector.setPort(8888);
        jettyServer_.setConnectors(new Connector[] { connector });

        WebAppContext context = new WebAppContext();
        String resourceBase = new File(ClassUtils.getBaseDirectory(
            ServletUtilsTest.class).getParentFile().getParentFile(),
            "src/test/webapp").getAbsolutePath();
        System.out.println(resourceBase);
        context.setResourceBase(resourceBase);
        context.setClassLoader(ServletUtilsTest.class.getClassLoader());
        context.setContextPath("/test");
        contexts.addHandler(context);

        jettyServer_.start();
    }


    private void stopJetty()
        throws Exception
    {
        jettyServer_.stop();
    }
}
