package org.seasar.kvasir.page.search.lucene.impl;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.search.SearchContext;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.page.search.SearchResultHandler;
import org.seasar.kvasir.page.search.impl.SearchSystemBase;
import org.seasar.kvasir.page.search.lucene.DocumentCreator;
import org.seasar.kvasir.page.search.lucene.WrappedJapaneseAnalyzer;
import org.seasar.kvasir.page.search.lucene.util.LuceneUtils;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;


public class LuceneSearchSystem extends SearchSystemBase
    implements Lifecycle
{
    public static final String PROP_ANALYZERCLASSNAME = "searchSystems.luceneSearchSystem.analyzerClassName";

    public static final String PROP_SENHOME = "searchSystems.luceneSearchSystem.senHome";

    public static final String PROP_INDEXDIRPATH = "indexDirPath";

    private DocumentCreator documentCreator_;

    private Class<?> analyzerClass_;

    private String indexDirPath_;

    private Resource indexDir_;

    private final KvasirLog log_ = KvasirLogFactory
        .getLog(LuceneSearchSystem.class);

    private PageAlfr pageAlfr_;


    /*
     * public scope methods
     */

    public Analyzer newAnalyzer()
    {
        try {
            return (Analyzer)analyzerClass_.newInstance();
        } catch (Throwable t) {
            log_.warn("Can't create instance: " + analyzerClass_);
            throw new RuntimeException(t);
        }
    }


    /*
     * Lifecycle
     */

    public boolean start()
    {
        Plugin<?> plugin = getElement().getPlugin();
        PropertyHandler prop = getElement().getPropertyHandler();

        String analyzerClassName = plugin.getProperty(PROP_ANALYZERCLASSNAME);
        if (analyzerClassName == null) {
            log_.error("Spefify property: " + PROP_ANALYZERCLASSNAME);
            return false;
        }

        if (WrappedJapaneseAnalyzer.class.getName().equals(analyzerClassName)) {
            String senHome = plugin.getProperty(PROP_SENHOME);
            if (senHome == null || senHome.trim().length() == 0) {
                log_.error("Specify plugin property: " + PROP_SENHOME);
                return false;
            }
            WrappedJapaneseAnalyzer.setSenHome(senHome);
            if (log_.isInfoEnabled()) {
                log_.info("SET sen.home=" + senHome);
            }

            analyzerClass_ = WrappedJapaneseAnalyzer.class;
        } else {
            try {
                analyzerClass_ = Class.forName(analyzerClassName, true, plugin
                    .getInnerClassLoader());
            } catch (ClassNotFoundException ex) {
                log_.error("Can't resolve class: " + analyzerClassName, ex);
                return false;
            }
        }

        String indexDirPath = prop.getProperty(PROP_INDEXDIRPATH);
        if (indexDirPath == null) {
            log_.error("Specify property: " + PROP_INDEXDIRPATH);
            return false;
        }
        indexDir_ = plugin.getConfigurationDirectory().getChildResource(
            indexDirPath);
        if (!indexDir_.exists()) {
            indexDir_.mkdirs();
            if (log_.isInfoEnabled()) {
                log_.info("CREATE INDEX DIR: " + indexDir_);
            }
        }
        try {
            indexDirPath_ = indexDir_.toFile().getCanonicalPath();
        } catch (IOException ex) {
            log_.error("Can't resolve file path: " + indexDirPath, ex);
            return false;
        }

        // インデックスの準備をする。
        IndexWriter writer = null;
        try {
            boolean create = !IndexReader.indexExists(indexDirPath_);
            writer = newIndexWriter(create);
            if (!create) {
                // 既存のインデックスを最適化する。
                try {
                    writer.optimize();
                } catch (Throwable t) {
                    if (log_.isWarnEnabled()) {
                        log_.warn("Can't optimize index", t);
                    }
                    try {
                        writer.close();
                    } catch (Throwable t2) {
                        log_.warn("Can't close indexWriter", t2);
                    }
                    writer = null;

                    if (t instanceof IllegalStateException) {
                        // なぜか docs out of order というエラーが発生する
                        // ことがある。これが一度発生するともうそのインデッ
                        // クスは利用できないので、再構築する。
                        if (log_.isWarnEnabled()) {
                            log_.warn("Re-construct index");
                        }
                        if (ResourceUtils.delete(indexDir_, true)) {
                            indexDir_.mkdirs();
                            writer = newIndexWriter(true);
                        }
                    }
                }
            }
            writer.close();
            writer = null;
        } catch (IOException ex) {
            if (log_.isWarnEnabled()) {
                log_
                    .warn("Can't initialize Lucene index: " + indexDirPath_, ex);
            }
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    if (log_.isWarnEnabled()) {
                        log_.warn("Can't initialize Lucene index: "
                            + indexDirPath_, ex);
                    }
                }
            }
        }

        return true;
    }


    public void stop()
    {
        documentCreator_ = null;
        analyzerClass_ = null;
        indexDirPath_ = null;
        indexDir_ = null;
    }


    /*
     * CachedSearchSystem
     */

    public SearchContext newContext()
    {
        return new LuceneSearchContext(getQueryStringParser());
    }


    public SearchResult[] search(SearchContext context)
    {
        return search(context, SearchQuery.OFFSET_FIRST, SearchQuery.LENGTH_ALL);
    }


    public SearchResult[] search(SearchContext context, int offset, int length)
    {
        LuceneSearchContext luceneContext = (LuceneSearchContext)context;

        Searcher searcher = null;
        try {
            searcher = new IndexSearcher(indexDirPath_);
            return createSearchResults(context, new LuceneSearchResultHandler(
                searcher.search(luceneContext.getLuceneQuery()), pageAlfr_),
                offset, length);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            LuceneUtils.close(searcher);
        }
    }


    @Override
    protected SearchResult[] createSearchResults(SearchContext context,
        SearchResultHandler handler, int offset, int length)
        throws IOException
    {
        LuceneSearchContext luceneContext = ((LuceneSearchContext)context);
        if (luceneContext.getResultCount() == LuceneSearchContext.UNKNOWN) {
            luceneContext.setResultCount(handler.getLength());
        }
        return super.createSearchResults(context, handler, offset, length);
    }


    public int getResultsCount(SearchContext context)
    {
        LuceneSearchContext luceneContext = (LuceneSearchContext)context;

        int count = luceneContext.getResultCount();
        if (count != LuceneSearchContext.UNKNOWN) {
            return count;
        }

        Searcher searcher = null;
        try {
            searcher = new IndexSearcher(indexDirPath_);
            count = new LuceneSearchResultHandler(searcher.search(luceneContext
                .getLuceneQuery()), pageAlfr_).getLength();
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            LuceneUtils.close(searcher);
        }

        luceneContext.setResultCount(count);
        return count;
    }


    public synchronized void addToIndex(Page[] pages)
    {
        removeFromIndex(pages);

        IndexWriter writer = null;
        try {
            writer = newIndexWriter(false);
            for (int i = 0; i < pages.length; i++) {
                if (!PropertyUtils.valueOf(pages[i].getAbility(
                    PropertyAbility.class).getProperty(PROP_INDEXED), true)) {
                    continue;
                }

                try {
                    addToIndex(writer, pages[i]);
                } catch (IOException ex) {
                    if (log_.isWarnEnabled()) {
                        log_.warn("Can't add to index: " + pages[i], ex);
                    }
                }
            }
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    if (log_.isWarnEnabled()) {
                        log_.warn("Can't close index writer", ex);
                    }
                }
            }
        }
    }


    public synchronized void removeFromIndex(Page[] pages)
    {
        IndexReader reader = null;
        try {
            reader = IndexReader.open(indexDirPath_);
            for (int i = 0; i < pages.length; i++) {
                try {
                    removeFromIndex(reader, pages[i].getId());
                } catch (IOException ex) {
                    if (log_.isWarnEnabled()) {
                        log_.warn("Can't remove from index: " + pages[i], ex);
                    }
                }
            }
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    if (log_.isWarnEnabled()) {
                        log_.warn("Can't close index reader", ex);
                    }
                }
            }
        }
    }


    public synchronized void removeFromIndex(int[] pageIds)
    {
        IndexReader reader = null;
        try {
            reader = IndexReader.open(indexDirPath_);
            for (int i = 0; i < pageIds.length; i++) {
                try {
                    removeFromIndex(reader, pageIds[i]);
                } catch (IOException ex) {
                    if (log_.isWarnEnabled()) {
                        log_.warn("Can't remove from index: " + pageIds[i], ex);
                    }
                }
            }
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    if (log_.isWarnEnabled()) {
                        log_.warn("Can't close index reader", ex);
                    }
                }
            }
        }
    }


    public synchronized void clearIndex()
    {
        if (log_.isDebugEnabled()) {
            log_.debug("clear index");
        }

        File indexDir = new File(indexDirPath_);
        if (!indexDir.exists()) {
            indexDir.mkdirs();
            if (log_.isInfoEnabled()) {
                log_.info("CREATE INDEX DIR: " + indexDirPath_);
            }
        }

        IndexWriter writer = null;
        try {
            writer = newIndexWriter(true);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    if (log_.isWarnEnabled()) {
                        log_.warn("Can't close index writer", ex);
                    }
                }
            }
        }
    }


    /*
     * private scope methods
     */

    private IndexWriter newIndexWriter(boolean create)
        throws IOException
    {
        return new IndexWriter(indexDirPath_, newAnalyzer(), create);
    }


    private void addToIndex(IndexWriter writer, Page page)
        throws IOException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("add to index: " + page);
        }

        if (page.getHeimId() == PathId.HEIM_ALFHEIM) {
            log_.debug("Ignore: " + page);
            return;
        }

        Document[] docs = documentCreator_.newDocuments(page);
        for (int i = 0; i < docs.length; i++) {
            writer.addDocument(docs[i]);
        }
    }


    private void removeFromIndex(IndexReader reader, int pageId)
        throws IOException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("remove from index: id=" + pageId);
        }

        reader.delete(new Term("id", String.valueOf(pageId)));
    }


    /*
     * for framework
     */

    public void setDocumentCreator(DocumentCreator documentCreator)
    {
        documentCreator_ = documentCreator;
    }


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }
}
