package org.seasar.kvasir.cms.ymir.extension;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.Resourceset;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.base.util.ArrayUtils;
import org.seasar.kvasir.cms.ymir.ExternalTemplate;
import org.seasar.kvasir.util.io.Resource;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;


@Bean("external-template")
@Component(bindingType = BindingType.NONE)
public class ExternalTemplateElement extends AbstractElement
    implements ExternalTemplate
{
    private String applicationId_;

    private Resourceset[] resourcesets_ = new Resourceset[0];

    private String[] ignoreVariables_ = new String[0];


    public String getApplicationId()
    {
        return applicationId_;
    }


    @Attribute
    @Required
    public void setApplicationId(String applicationId)
    {
        applicationId_ = applicationId;
    }


    public Resourceset[] getResourcesets()
    {
        return resourcesets_;
    }


    @Child
    public void addResourceset(Resourceset resourceset)
    {
        resourcesets_ = ArrayUtils.add(resourcesets_, resourceset);
    }


    public void setResourcesets(Resourceset[] resourcesets)
    {
        resourcesets_ = resourcesets;
    }


    public ResourceEntry[] getResourceEntries()
    {
        // 毎回ResourceEntry[]を構築しているのは、開発中は動的にリソースが追加・削除されうるため。
        Resource homeSourceDirectory = getPlugin().getHomeSourceDirectory();
        Set<Resource> set = new LinkedHashSet<Resource>();
        for (int i = 0; i < resourcesets_.length; i++) {
            set.addAll(Arrays.asList(resourcesets_[i]
                .getResources(homeSourceDirectory)));
        }
        ResourceEntry[] entries = new ResourceEntry[set.size()];
        int idx = 0;
        for (Iterator<Resource> itr = set.iterator(); itr.hasNext();) {
            entries[idx++] = new ResourceEntry(homeSourceDirectory, itr.next());
        }
        return entries;
    }


    public String[] getIgnoreVariables()
    {
        return ignoreVariables_;
    }


    public void setIgnoreVariables(String[] ignoreVariables)
    {
        ignoreVariables_ = ignoreVariables;
    }


    @Child
    public void addIgnoreVariable(String ignoreVariable)
    {
        ignoreVariables_ = ArrayUtils.add(ignoreVariables_, ignoreVariable);
    }
}
