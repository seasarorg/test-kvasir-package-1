package org.seasar.kvasir.cms.ymir.impl;

import org.seasar.cms.pluggable.Configuration;
import org.seasar.cms.pluggable.impl.ConfigurationImpl;


public class DelegatingConfiguration extends ConfigurationImpl
{
    private Configuration configuration_;


    public DelegatingConfiguration(Configuration configuration)
    {
        configuration_ = configuration;
    }


    public String getProperty(String key)
    {
        String value = super.getProperty(key);
        if (value == null) {
            value = configuration_.getProperty(key);
        }
        return value;
    }


    public String getProperty(String key, String defaultValue)
    {
        String value = getProperty(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
}
