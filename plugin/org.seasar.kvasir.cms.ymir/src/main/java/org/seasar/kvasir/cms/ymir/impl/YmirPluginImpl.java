package org.seasar.kvasir.cms.ymir.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.seasar.cms.ymir.ApplicationManager;
import org.seasar.cms.ymir.Ymir;
import org.seasar.cms.ymir.YmirContext;
import org.seasar.cms.ymir.extension.zpt.ZptAnalyzer;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.util.ArrayUtils;
import org.seasar.kvasir.cms.ymir.ApplicationMapping;
import org.seasar.kvasir.cms.ymir.DelegatingApplication;
import org.seasar.kvasir.cms.ymir.ExternalTemplate;
import org.seasar.kvasir.cms.ymir.YmirApplication;
import org.seasar.kvasir.cms.ymir.YmirPlugin;
import org.seasar.kvasir.cms.ymir.extension.ApplicationElement;
import org.seasar.kvasir.cms.ymir.extension.ApplicationMappingElement;
import org.seasar.kvasir.cms.zpt.impl.KvasirNoteLocalizer;
import org.seasar.kvasir.cms.zpt.impl.ZptPageProcessor;


public class YmirPluginImpl extends AbstractPlugin<EmptySettings>
    implements YmirPlugin
{
    private ApplicationManager applicationManager_;

    private Ymir ymir_;

    private Map<String, YmirApplication> applicationMap_ = new LinkedHashMap<String, YmirApplication>();

    private YmirApplication[] applications_ = new YmirApplication[0];

    private Map<String, ApplicationMapping[]> applicationMappingMap_ = new HashMap<String, ApplicationMapping[]>();

    private KvasirTemplateUpdater updater_;

    private String[] gardIds_;

    private Set<String> gardIdSet_;


    public YmirApplication getApplication(String gardId)
    {
        return applicationMap_.get(gardId);
    }


    public YmirApplication[] getApplications()
    {
        return applications_;
    }


    public String[] getGardIds()
    {
        return gardIds_;
    }


    public boolean containsGardId(String gardId)
    {
        return gardIdSet_.contains(gardId);
    }


    public ApplicationMapping[] getApplicationMappings(String id)
    {
        ApplicationMapping[] mappings = applicationMappingMap_.get(id);
        if (mappings != null) {
            return mappings;
        } else {
            return new ApplicationMapping[0];
        }
    }


    @Override
    protected boolean doStart()
    {
        ComponentContainer container = getKvasir().getRootComponentContainer();

        ApplicationElement[] elements = getExtensionElements(ApplicationElement.class);
        gardIdSet_ = new LinkedHashSet<String>();

        Map<String, YmirApplication> applicationByIdMap = new LinkedHashMap<String, YmirApplication>();
        for (int i = 0; i < elements.length; i++) {
            String gardId = elements[i].getGardId();
            Plugin<?> plugin = elements[i].getPlugin();
            DelegatingApplication application = elements[i].getApplication();

            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(
                    elements[i].getPlugin().getInnerClassLoader());

                application.setApplication(new YmirApplicationImpl(elements[i]
                    .getFullId(), plugin, gardId, plugin.getHomeDirectory()
                    .getChildResource(elements[i].getRootPath()), elements[i]
                    .getLandmark(), plugin.isUnderDevelopment()));
            } finally {
                Thread.currentThread().setContextClassLoader(oldCl);
            }

            applicationManager_.addApplication(application);
            applicationByIdMap.put(application.getId(), application);
            applicationMap_.put(gardId, application);
            gardIdSet_.add(gardId);

            String rootPackageName = application.getRootPackageName();
            if (rootPackageName == null) {
                log_.info("rootPackageName is null: plugin-id="
                    + plugin.getId() + ", gard-id=" + gardId);
            }
        }
        applications_ = applicationMap_.values()
            .toArray(new YmirApplication[0]);
        gardIds_ = gardIdSet_.toArray(new String[0]);

        ApplicationMappingElement[] ames = getExtensionElements(ApplicationMappingElement.class);
        for (int i = 0; i < ames.length; i++) {
            if (!applicationByIdMap.containsKey(ames[i].getTargetId())) {
                throw new IllegalArgumentException(
                    "Application object not found for target-id: "
                        + ames[i].getTargetId() + ": " + ames[i]);
            }

            YmirApplication application = applicationByIdMap.get(ames[i]
                .getForwardedId());
            if (application == null) {
                throw new IllegalArgumentException(
                    "Application object not found for forwarded-id: "
                        + ames[i].getForwardedId() + ": " + ames[i]);
            }
            ames[i].setForwardedApplication(application);

            ApplicationMapping[] mappings = applicationMappingMap_.get(ames[i]
                .getTargetId());
            if (mappings == null) {
                mappings = new ApplicationMapping[] { ames[i] };
            } else {
                mappings = ArrayUtils.add(mappings, ames[i]);
            }
            applicationMappingMap_.put(ames[i].getTargetId(), mappings);
        }

        // ZptPageProcessorのPathResolverとしてYmirPathResolverを追加しておく。
        ZptPageProcessor zptPageProcessor = container
            .getComponent(ZptPageProcessor.class);
        // TODO Ymir-1.0系を使うようにした際にはYmirが持つYmirPathResolverを使うようにする。
        zptPageProcessor.addPathResolver(new YmirPathResolver()
            .setNoteLocalizer(new KvasirNoteLocalizer()));

        // ZptAnalyzerが使うTemplateEvaluatorオブジェクトなどを差し替えておく。
        ZptAnalyzer zptAnalyzer = container.getComponent(ZptAnalyzer.class);
        zptAnalyzer.setTemplateEvaluator(zptPageProcessor.newEvaluator());

        if (updater_ != null) {
            updater_
                .setExternalTemplates(getExtensionElements(ExternalTemplate.class));
        }

        return true;
    }


    @Override
    public void notifyKvasirStarted()
    {
        // Ymirは全てのコンポーネントの初期化が終わってからgetしないといろいろ問題があるので
        // こうしている。
        ymir_ = getComponentContainer().getComponent(Ymir.class);
        YmirContext.setYmir(ymir_);
        ymir_.init();
    }


    @Override
    protected void doStop()
    {
        applicationMappingMap_.clear();
        applicationMap_.clear();
        applications_ = new YmirApplication[0];
        gardIds_ = null;
        gardIdSet_ = null;
        if (ymir_ != null) {
            ymir_.destroy();
        }
        ymir_ = null;
        YmirContext.setYmir(null);
        applicationManager_ = null;
        updater_ = null;
    }


    public Ymir getYmir()
    {
        return ymir_;
    }


    public void setApplicationManager(ApplicationManager applicationManager)
    {
        applicationManager_ = applicationManager;
    }


    @Binding(bindingType = BindingType.MAY, value = "kvasirTemplateUpdater")
    public void setUpdater(KvasirTemplateUpdater updater)
    {
        updater_ = updater;
    }
}
