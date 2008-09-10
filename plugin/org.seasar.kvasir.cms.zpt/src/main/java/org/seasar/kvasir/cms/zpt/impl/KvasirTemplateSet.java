package org.seasar.kvasir.cms.zpt.impl;

import static org.seasar.kvasir.cms.zpt.impl.KvasirVariableResolver.VARNAME_MYLORD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.seasar.kvasir.base.annotation.ForPreparingMode;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.cms.GardIdProvider;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.cms.processor.LocalPathResolver;
import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.cms.zpt.LocalPathResolverFactory;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;

import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.freyja.impl.AbstractPathTemplateSet;


/**
 * ZptPageProcessorのためのTemplateSet実装です。
 * <p><code>addLocalPathResolver()</code>
 * の呼び出しはスレッドセーフになっていません。
 * またTemplateSetが持つメソッドを呼び出す前に
 * 全ての<code>addLocalPathResolver()</code>呼び出しを完了しておいて下さい。
 * </p>
 * <p><b>制限事項：</b>
 * gardのルートであるPageのgardIdが別のgardIdに変更された場合は
 * 正常に動作しないことがあります。
 * （そのような操作を行なうためのAPIをKvasir/Sora CMSでは提供していないので、
 * このことはほとんど問題にならないはずです。）
 * gardのルートPageのgardIdが変更された場合でも正常に動作させるためには、
 * gardIdが変更されたタイミングでlocalPathResolversMap_をクリアして下さい。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはTemplateSetが持つメソッドの呼び出しについてはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirTemplateSet extends AbstractPathTemplateSet
{
    private PageAlfr pageAlfr_;

    private Map<Object, LocalPathResolver[]> localPathResolversMap_ = new HashMap<Object, LocalPathResolver[]>();

    private Map<String, File> fileMap_ = new HashMap<String, File>();

    private ResolverFactoryEntry[] entries_ = new ResolverFactoryEntry[0];

    private LocalPathResolver baseResolver_;


    public KvasirTemplateSet(String pageEncoding, TemplateEvaluator evaluator,
        PageAlfr pageAlfr)
    {
        super(pageEncoding, evaluator);
        pageAlfr_ = pageAlfr;
    }


    /*
     * public scope methods
     */

    @ForPreparingMode
    public void addLocalPathResolver(String gardId,
        ComponentContainer container, String resolverComponentName,
        PageProcessorElement element, ServletConfig config)
    {
        addLocalPathResolver(new ResolverFactoryEntry(gardId,
            new LocalPathResolverFactory(container, resolverComponentName,
                element, config)));
    }


    @ForPreparingMode
    public void addLocalPathResolver(GardIdProvider provider,
        ComponentContainer container, String resolverComponentName,
        PageProcessorElement element, ServletConfig config)
    {
        addLocalPathResolver(new ResolverFactoryEntry(provider,
            new LocalPathResolverFactory(container, resolverComponentName,
                element, config)));
    }


    @ForPreparingMode
    void addLocalPathResolver(ResolverFactoryEntry entry)
    {
        ResolverFactoryEntry[] entries = new ResolverFactoryEntry[entries_.length + 1];
        System.arraycopy(entries_, 0, entries, 1, entries_.length);
        entries[0] = entry;
        entries_ = entries;
    }


    @ForPreparingMode
    public void setBasePathResolver(ComponentContainer container,
        String resolverComponentName, PageProcessorElement element,
        ServletConfig config)
    {
        // newInstance()に渡すgardRootPageとしてはroot pageを指定すべきだが、
        // root pageはリクエスト毎に変わるのでここではnullを仮設定しておく。
        // 実際のgardRootPageは実行時に決定される。
        baseResolver_ = new LocalPathResolverFactory(container,
            resolverComponentName, element, config).newInstance(null);
    }


    /*
     * AbstractPathTemplateSet
     */

    public Long getSerialNumber(String templateName)
    {
        File file = getFile(templateName);
        if (file != null) {
            return new Long(file.lastModified());
        } else {
            return null;
        }
    }


    public boolean hasEntry(String templateName)
    {
        return (getFile(templateName) != null);
    }


    protected InputStream getInputStream(String templateName)
    {
        File file = getFile(templateName);
        if (file != null) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException ex) {
                return null;
            }
        } else {
            return null;
        }
    }


    protected String getCanonicalName0(String baseTemplateName,
        String templateName)
    {
        if (templateName.startsWith(VARNAME_MYLORD)) {
            return pageAlfr_.findNearestPage(CmsUtils.getHeimId(),
                baseTemplateName).getLordPathname()
                + templateName.substring(VARNAME_MYLORD.length());
        } else {
            return super.getCanonicalName0(baseTemplateName, templateName);
        }
    }


    File getFile(String templateName)
    {
        if (baseResolver_ != null) {
            // baseResolverはキャッシュよりも優先させる＆キャッシュにエントリを登録しない。
            String realPath = baseResolver_.getRealPath(templateName);
            if (realPath != null) {
                return new File(realPath);
            }
        }
        synchronized (fileMap_) {
            if (fileMap_.containsKey(templateName)) {
                return (File)fileMap_.get(templateName);
            }
            File file = null;
            int heimId = CmsUtils.getHeimId();
            Page page = pageAlfr_.findNearestPage(heimId, templateName);
            Page[] gardPages = page.getGardRoots();
            for (int i = 1; i < gardPages.length; i++) {
                String gardId = gardPages[i].getGardId();
                String localPathname = templateName.substring(gardPages[i]
                    .getPathname().length());

                file = getFile(gardId, gardPages[i], localPathname);
                if (file != null) {
                    break;
                }
            }
            if (file == null) {
                file = getFile(null, pageAlfr_.getRootPage(heimId),
                    templateName);
            }
            if (file != null) {
                // LocalPathResolverによっては途中でエントリが増えたりするので、
                // file == nullの時は記録しない。
                fileMap_.put(templateName, file);
            }
            return file;
        }
    }


    File getFile(String gardId, Page gardPage, String localPathname)
    {
        synchronized (localPathResolversMap_) {
            Integer gardPageId = Integer.valueOf(gardPage.getId());
            LocalPathResolver[] resolvers = localPathResolversMap_
                .get(gardPageId);
            if (resolvers == null) {
                LocalPathResolverFactory[] factories = findLocalPathResolverFactory(gardId);
                resolvers = new LocalPathResolver[factories.length];
                for (int i = 0; i < factories.length; i++) {
                    resolvers[i] = factories[i].newInstance(gardPage);
                }
                localPathResolversMap_.put(gardPageId, resolvers);
            }
            for (int i = 0; i < resolvers.length; i++) {
                String realPath = resolvers[i].getRealPath(localPathname);
                if (realPath != null) {
                    return new File(realPath);
                }
            }
            return null;
        }
    }


    LocalPathResolverFactory[] findLocalPathResolverFactory(String gardId)
    {
        List<LocalPathResolverFactory> factoryList = new ArrayList<LocalPathResolverFactory>();
        for (int i = 0; i < entries_.length; i++) {
            if (entries_[i].containsGardId(gardId)) {
                factoryList.add(entries_[i].getFactory());
            }
        }
        return factoryList.toArray(new LocalPathResolverFactory[0]);
    }


    private class ResolverFactoryEntry
    {
        private String gardId_;

        private GardIdProvider provider_;

        private LocalPathResolverFactory factory_;


        public ResolverFactoryEntry(String gardId,
            LocalPathResolverFactory factory)
        {
            gardId_ = gardId;
            factory_ = factory;
        }


        public ResolverFactoryEntry(GardIdProvider provider,
            LocalPathResolverFactory factory)
        {
            provider_ = provider;
            factory_ = factory;
        }


        public boolean containsGardId(String gardId)
        {
            if (provider_ != null) {
                return provider_.containsGardId(gardId);
            } else {
                return (gardId_ == null || gardId_.equals(gardId));
            }
        }


        public LocalPathResolverFactory getFactory()
        {
            return factory_;
        }
    }
}
