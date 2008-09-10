package org.seasar.kvasir.cms.kdiary;

import junit.framework.Test;

import org.seasar.cms.ymir.MatchedPathMapping;
import org.seasar.cms.ymir.PathMapping;
import org.seasar.framework.container.S2Container;
import org.seasar.kvasir.test.KvasirPluginTestCase;
import org.seasar.kvasir.util.el.VariableResolver;


public class KdiaryPluginIT extends KvasirPluginTestCase<KdiaryPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.cms.kdiary";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(KdiaryPluginIT.class);
    }


    private MatchedPathMapping findMatchedPathMapping(String path,
        String method, PathMapping[] mappings)
    {
        VariableResolver resolver = null;
        if (mappings != null) {
            for (int i = 0; i < mappings.length; i++) {
                resolver = mappings[i].match(path, method);
                if (resolver != null) {
                    return new MatchedPathMapping(mappings[i], resolver);
                }
            }
        }
        return null;
    }


    public void testPathMapping()
        throws Exception
    {
        S2Container container = (S2Container)getPlugin()
            .getComponentContainer().getRawContainer();
        PathMapping[] mappings = (PathMapping[])container
            .findLocalComponents(PathMapping.class);

        MatchedPathMapping matched = findMatchedPathMapping("", "GET", mappings);
        assertNotNull(matched);
        assertEquals("_RootPage", matched.getComponentName());

        matched = findMatchedPathMapping("/article/200609/15.html", "GET",
            mappings);
        assertNotNull(matched);
        assertEquals("dayPage", matched.getComponentName());

        matched = findMatchedPathMapping("/article/200609", "GET", mappings);
        assertNotNull(matched);
        assertEquals("monthPage", matched.getComponentName());

        matched = findMatchedPathMapping("/action/update.do", "POST", mappings);
        assertNotNull(matched);
        assertEquals("updateActionPage", matched.getComponentName());
        assertEquals("_post", matched.getActionName());
    }
}
