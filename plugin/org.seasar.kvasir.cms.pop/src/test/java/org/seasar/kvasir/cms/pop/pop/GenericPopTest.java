package org.seasar.kvasir.cms.pop.pop;

import java.util.HashMap;
import java.util.Locale;

import junit.framework.TestCase;

import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.impl.MockPopElement;
import org.seasar.kvasir.cms.pop.impl.MockRootPage;
import org.seasar.kvasir.cms.pop.impl.PopContextImpl;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.mock.MockPageAlfr;


/**
 * @author YOKOTA Takehiko
 */
public class GenericPopTest extends TestCase
{
    private PageAlfr pageAlfr_ = new MockPageAlfr() {
        @Override
        public Page getRootPage(int heimId)
        {
            return new MockRootPage();
        }
    };


    public void test()
    {
        GenericPop pop = new GenericPop();
        pop.setPageAlfr(pageAlfr_);
        pop.setElement(new MockPopElement() {
            @Override
            public String findBodyType()
            {
                return "text/x-zpt";
            }
        });
        PopContext context = new PopContextImpl(new HashMap<Object, Object>(),
            null, new Locale("ja"), null, null, null, null);
        RenderedPop bean = pop.render(context, new String[0]);
        assertEquals("タイトルがルートページのプロパティから正しく取得できること", "Title_ja", bean
            .getTitle());
        assertEquals("ボディがルートページのプロパティから正しく取得できること", "Body_ja", bean.getBody());
    }


    public void test2()
    {
        GenericPop pop = new GenericPop();
        pop.setPageAlfr(pageAlfr_);
        pop.setElement(new MockPopElement() {
            @Override
            public String findBodyType()
            {
                return "text/x-zpt";
            }
        });
        pop.setHeimId(PathId.HEIM_MIDGARD);
        pop.setInstanceId(1);
        PopContext context = new PopContextImpl(new HashMap<Object, Object>(),
            null, new Locale("ja"), null, null, null, null);
        RenderedPop bean = pop.render(context, new String[0]);
        assertEquals("タイトルがPopElementから正しく取得できること", "DEFAULT_Title_ja", bean
            .getTitle());
        assertEquals("ボディがPopElementから正しく取得できること", "DEFAULT_Body_ja", bean
            .getBody());
    }
}
