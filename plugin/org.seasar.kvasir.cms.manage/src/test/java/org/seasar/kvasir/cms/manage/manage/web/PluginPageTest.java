package org.seasar.kvasir.cms.manage.manage.web;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.seasar.cms.ymir.Request;
import org.seasar.cms.ymir.impl.RequestImpl;
import org.seasar.kvasir.base.util.XOMUtils;


public class PluginPageTest extends TestCase
{
    public void testBuildSettings()
        throws Exception
    {
        final Map<String, String> valueMap = new HashMap<String, String>();
        final Map<String, String[]> valuesMap = new HashMap<String, String[]>();
        valueMap.put("name:(attribute)attribute", "ATTRIBUTE");
        valueMap.put("name:(child)child", "CHILD");
        // 2はフォームでの項目追加用のダミーであり無視されるので、値を用意する必要はない。
        valuesMap.put("name:(child)fuga", new String[] { "1", "0", "2" });
        valueMap.put("name:(child)fuga[0]:(attribute)hehe", "HEHE0");
        valueMap.put("name:(child)fuga[1]:(attribute)hehe", "HEHE1");
        valueMap.put("name:(child)hoho:(attribute)hehe", "HEHE");
        valuesMap.put("name:(child)string", new String[] { "0", "1", "2" });
        valueMap.put("name:(child)string[0]", "STRING0");
        valueMap.put("name:(child)string[1]", "STRING1");
        PluginPage target = new PluginPage() {
            @Override
            public Request getYmirRequest()
            {
                return new RequestImpl() {
                    @Override
                    public String getParameter(String name)
                    {
                        return valueMap.get(name);
                    }


                    @Override
                    public String[] getParameterValues(String name)
                    {
                        return valuesMap.get(name);
                    }
                };
            }
        };

        Hoe actual = new Hoe();
        target.buildSettings(actual, XOMUtils.newMapper().getBeanAccessor(
            Hoe.class), "name");

        assertEquals("ATTRIBUTE", actual.getAttribute());
        assertEquals("CHILD", actual.getChild());
        assertEquals("HEHE", actual.getHoho().getHehe());
        assertEquals("HEHE0", actual.getFugas()[0].getHehe());
        assertEquals("HEHE1", actual.getFugas()[1].getHehe());
        assertEquals("STRING0", actual.getStrings()[0]);
        assertEquals("STRING1", actual.getStrings()[1]);
    }

}
