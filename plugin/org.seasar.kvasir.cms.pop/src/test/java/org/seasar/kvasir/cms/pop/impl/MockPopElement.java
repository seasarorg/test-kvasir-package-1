package org.seasar.kvasir.cms.pop.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.base.mock.plugin.MockPlugin;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopPropertyMetaData;
import org.seasar.kvasir.cms.pop.extension.FormUnitElement;
import org.seasar.kvasir.cms.pop.extension.PopElement;


/**
 * @author YOKOTA Takehiko
 */
public class MockPopElement extends PopElement
{
    private Map<String, PopPropertyMetaData> metaDataMap_ = new HashMap<String, PopPropertyMetaData>();


    public MockPopElement()
    {
        FormUnitElement element1 = new FormUnitElement();
        element1.setId(PROP_TITLE);
        FormUnitElement element2 = new FormUnitElement();
        element2.setId(PROP_BODY);
        metaDataMap_.put(PROP_TITLE, element1);
        metaDataMap_.put(PROP_BODY, element2);
    }


    public String getId()
    {
        return "text";
    }


    @Override
    public String getFullId()
    {
        return "full.text";
    }


    @Override
    public String renderBodyWithoutException(String body, String bodyType,
        PopContext context)
    {
        return body;
    }


    @Override
    public String getProperty(String id, Locale locale)
    {
        if (PROP_TITLE.equals(id)) {
            return "DEFAULT_Title_" + locale.toString();
        } else if (PROP_BODY.equals(id)) {
            return "DEFAULT_Body_" + locale.toString();
        } else {
            return null;
        }
    }


    @Override
    public PopPropertyMetaData[] getPropertyMetaDatas()
    {
        return metaDataMap_.values().toArray(new PopPropertyMetaData[0]);
    }


    @Override
    public PopPropertyMetaData getPropertyMetaData(String id)
    {
        return metaDataMap_.get(id);
    }


    @Override
    public Plugin<?> getPlugin()
    {
        return new MockPlugin<Object>() {
            @Override
            public boolean isUnderDevelopment()
            {
                return false;
            }
        };
    }
}
