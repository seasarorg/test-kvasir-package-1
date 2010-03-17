package org.seasar.kvasir.cms.ymir.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.cms.ymir.Application;
import org.seasar.cms.ymir.Request;
import org.seasar.cms.ymir.Response;
import org.seasar.cms.ymir.Updater;
import org.seasar.cms.ymir.extension.creator.ClassDescBag;
import org.seasar.cms.ymir.extension.creator.PathMetaData;
import org.seasar.cms.ymir.extension.creator.SourceCreator;
import org.seasar.cms.ymir.impl.DefaultRequestProcessor;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.annotation.ForPreparingMode;
import org.seasar.kvasir.cms.ymir.ExternalTemplate;
import org.seasar.kvasir.cms.ymir.ExternalTemplate.ResourceEntry;
import org.seasar.kvasir.util.io.Resource;


public class KvasirTemplateUpdater
    implements Updater
{
    private static final String PREFIX_CHECKEDTIME = "KvasirTemplateUpdater.checkedTime.";

    public static final String TEMPLATE_PREFIX = "template/";

    public static final String TEMPLATE_SUFFIX = ".zpt";

    private SourceCreator sourceCreator_;

    private Map<String, ExternalTemplate> resourceMap_;

    private KvasirUpdateClassesAction action_;


    @Binding(bindingType = BindingType.MUST)
    public void setSourceCreator(SourceCreator sourceCreator)
    {
        sourceCreator_ = sourceCreator;
        action_ = new KvasirUpdateClassesAction(sourceCreator);
    }


    @ForPreparingMode
    public void setExternalTemplates(ExternalTemplate[] externalTemplates)
    {
        resourceMap_ = new HashMap<String, ExternalTemplate>();
        for (int i = 0; i < externalTemplates.length; i++) {
            resourceMap_.put(externalTemplates[i].getApplicationId(),
                externalTemplates[i]);
        }
    }


    public Response update(Request request, Response response)
    {
        Application application = sourceCreator_.getApplication();
        if (!sourceCreator_.shouldUpdate(application)) {
            return response;
        }

        // SourceCreatorとは違い、リダイレクトなどの場合でも処理を行なう。
        // これは、POPの場合リダイレクト後にYmirの外に制御が移ってしまうことがあり、
        // その場合にexternalTemplatesが解析されなくなってしまうから。

        String task = request.getParameter(SourceCreator.PARAM_TASK);
        boolean doUpdate = "kvasirUpdateClasses".equals(task);

        ExternalTemplate externalTemplate = resourceMap_.get(application
            .getId());
        ResourceEntry[] entries = getExternalTemplateResourcesForUpdate(
            externalTemplate, doUpdate);
        if (entries.length == 0) {
            return response;
        }

        Object self = request.getAttribute(DefaultRequestProcessor.ATTR_SELF);
        if (response.getType() == Response.TYPE_REDIRECT) {
            // リダイレクトの時は念のためupdate処理の間は
            // requestからselfを消しておく。
            request.removeAttribute(DefaultRequestProcessor.ATTR_SELF);
        }
        try {
            PathMetaData[] metaDatas = new PathMetaData[entries.length];
            for (int i = 0; i < metaDatas.length; i++) {
                metaDatas[i] = new ResourcePathMetaData(entries[i]
                    .getResource());
            }
            ClassDescBag bag = sourceCreator_.gatherClassDescs(metaDatas, null,
                externalTemplate.getIgnoreVariables());

            if (doUpdate) {
                return action_.actUpdate(request, response, entries, bag);
            } else {
                return action_.actDefault(request, response, entries, bag);
            }
        } finally {
            request.setAttribute(DefaultRequestProcessor.ATTR_SELF, self);
        }
    }


    public Response updateByException(Request request, Throwable t)
    {
        return null;
    }


    ResourceEntry[] getExternalTemplateResourcesForUpdate(
        ExternalTemplate externalTemplate, boolean updateTimestamp)
    {
        if (externalTemplate == null) {
            return new ResourceEntry[0];
        }

        ResourceEntry[] entries = externalTemplate.getResourceEntries();
        Properties prop = sourceCreator_.getSourceCreatorProperties();
        List<ResourceEntry> list = new ArrayList<ResourceEntry>();
        for (int i = 0; i < entries.length; i++) {
            getExternalTemplateResourcesForUpdate(entries[i], list, prop,
                updateTimestamp);
        }
        if (updateTimestamp) {
            sourceCreator_.saveSourceCreatorProperties();
        }
        return list.toArray(new ResourceEntry[0]);
    }


    void getExternalTemplateResourcesForUpdate(ResourceEntry entry,
        List<ResourceEntry> list, Properties prop, boolean updateTimestamp)
    {
        if (!entry.getResource().exists() || entry.getResource().isDirectory()) {
            return;
        }

        if (shouldUpdate(entry, prop, updateTimestamp)) {
            list.add(entry);
        }
    }


    boolean shouldIgnore(Resource resource)
    {
        String name = resource.getName();
        return "CVS".equals(name) || ".svn".equals(name) || "_svn".equals(name);
    }


    boolean shouldUpdate(ResourceEntry entry, Properties prop,
        boolean updateCheckedTime)
    {
        boolean shouldUpdate = (entry.getResource().getLastModifiedTime() > getCheckedTime(
            entry, prop));
        if (shouldUpdate && updateCheckedTime) {
            updateCheckedTime(entry, prop);
        }
        return shouldUpdate;
    }


    long getCheckedTime(ResourceEntry entry, Properties prop)
    {
        String key = PREFIX_CHECKEDTIME + entry.getPath();
        String timeString = prop.getProperty(key);
        long time;
        if (timeString == null) {
            time = 0L;
        } else {
            time = Long.parseLong(timeString);
        }

        return time;
    }


    void updateCheckedTime(ResourceEntry entry, Properties prop)
    {
        prop.setProperty(PREFIX_CHECKEDTIME + entry.getPath(), String
            .valueOf(System.currentTimeMillis()));
    }


    public String filterResponse(String response)
    {
        return response;
    }
}
