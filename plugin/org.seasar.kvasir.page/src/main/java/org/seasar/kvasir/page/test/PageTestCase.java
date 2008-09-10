package org.seasar.kvasir.page.test;

import java.io.InputStream;

import junit.framework.TestCase;

import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.mock.MockPageAlfr;
import org.seasar.kvasir.util.StringUtils;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;


abstract public class PageTestCase extends TestCase
{
    private MockPageAlfr pageAlfr_;


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        pageAlfr_ = new MockPageAlfr();
    }


    public MockPageAlfr getPageAlfr()
    {
        return pageAlfr_;
    }


    public MockPage registerPage(MockPage page)
    {
        pageAlfr_.register(page);
        return page;
    }


    public String normalizeText(String text)
    {
        return StringUtils.normalizeLineSeparator(text);
    }


    public String readText(String resourceName)
    {
        String resourcePath = getClass().getName().replace('.', '/')
            .concat("_").concat(resourceName);
        InputStream in = getClass().getClassLoader().getResourceAsStream(
            resourcePath);
        if (in == null) {
            throw new IORuntimeException("Specified resource does not exist: "
                + resourcePath);
        }
        return IOUtils.readString(in, "UTF-8", true);
    }
}
