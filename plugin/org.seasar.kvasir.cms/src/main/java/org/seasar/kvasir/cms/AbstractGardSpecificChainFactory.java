package org.seasar.kvasir.cms;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.framework.util.ArrayUtil;
import org.seasar.kvasir.base.chain.Chain;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.util.collection.LRUMap;
import org.seasar.kvasir.cms.extension.AbstractPageElement;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.webapp.impl.PhasedElementComparator;


abstract public class AbstractGardSpecificChainFactory<C, E extends AbstractPageElement, P, H extends Chain<P>>
{
    private static final String GARDID_GLOBAL = null;

    private String[] phases_;

    private String defaultPhase_;

    private Class<P> processorClass_;

    private P[] processors_;

    private KvasirLog log_ = KvasirLogFactory.getLog(getClass());

    private Map<Key, P[]> processorMap_ = new HashMap<Key, P[]>();

    private Map<CacheKey, P[]> processorsCache_ = Collections
        .synchronizedMap(new LRUMap<CacheKey, P[]>(64));


    @SuppressWarnings("unchecked")
    public AbstractGardSpecificChainFactory(E[] elements,
        Class<P> processorClass, C config, String[] phases, String defaultPhase)
    {
        phases_ = phases;
        defaultPhase_ = defaultPhase;
        processorClass_ = processorClass;

        Arrays
            .sort(elements, new PhasedElementComparator(phases, defaultPhase));

        processors_ = (P[])Array.newInstance(processorClass, elements.length);
        for (int i = 0; i < elements.length; i++) {
            processors_[i] = getProcessor(elements[i]);
            init(processors_[i], config, elements[i]);
            registerProcessor(processors_[i], elements[i]);
        }
    }


    void registerProcessor(P processor, E element)
    {
        String phase = element.getPhase(defaultPhase_);
        String gardId = element.getGardId();
        if (gardId != null) {
            registerProcessor(processor, phase, gardId);
        } else {
            GardIdProvider provider = element.getGardIdProvider();
            if (provider != null) {
                String[] gardIds = provider.getGardIds();
                for (int i = 0; i < gardIds.length; i++) {
                    registerProcessor(processor, phase, gardIds[i]);
                }
            } else {
                registerProcessor(processor, phase, GARDID_GLOBAL);
            }
        }
    }


    @SuppressWarnings("unchecked")
    void registerProcessor(P processor, String phase, String gardId)
    {
        Key key = new Key(phase, gardId);
        P[] processors = processorMap_.get(key);
        if (processors == null) {
            processors = (P[])Array.newInstance(processorClass_, 1);
            processors[0] = processor;
        } else {
            processors = (P[])ArrayUtil.add(processors, processor);
        }
        processorMap_.put(key, processors);
    }


    private static class Key
    {
        private String phase_;

        private String gardId_;


        public Key(String phase, String gardId)
        {
            phase_ = phase;
            gardId_ = gardId;
        }


        @Override
        public String toString()
        {
            return "phase=" + phase_ + ", gardId=" + gardId_;
        }


        @Override
        public int hashCode()
        {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result
                + ((gardId_ == null) ? 0 : gardId_.hashCode());
            result = PRIME * result
                + ((phase_ == null) ? 0 : phase_.hashCode());
            return result;
        }


        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Key other = (Key)obj;
            if (gardId_ == null) {
                if (other.gardId_ != null)
                    return false;
            } else if (!gardId_.equals(other.gardId_))
                return false;
            if (phase_ == null) {
                if (other.phase_ != null)
                    return false;
            } else if (!phase_.equals(other.phase_))
                return false;
            return true;
        }
    }


    abstract protected P getProcessor(E element);


    abstract protected void init(P processor, C config, E element);


    public H newChain(PageDispatch pageDispatch)
    {
        H chain = newChain();
        chain.addProcessors(getProcessors(pageDispatch));
        return chain;
    }


    abstract protected H newChain();


    @SuppressWarnings("unchecked")
    P[] getProcessors(PageDispatch pageDispatch)
    {
        Page[] gardRootPages = pageDispatch.getGardRootPages();
        CacheKey cacheKey = new CacheKey(gardRootPages);
        P[] processors = processorsCache_.get(cacheKey);
        if (processors != null) {
            return processors;
        }

        List<P> list = new ArrayList<P>();
        for (int i = 0; i < phases_.length; i++) {
            for (int j = 1; j < gardRootPages.length; j++) {
                processors = processorMap_.get(new Key(phases_[i],
                    gardRootPages[j].getGardId()));
                if (processors != null) {
                    list.add(newGardRootChangeProcessor(gardRootPages[j]));
                    for (int k = 0; k < processors.length; k++) {
                        list.add(processors[k]);
                    }
                }
            }
            processors = processorMap_.get(new Key(phases_[i], GARDID_GLOBAL));
            if (processors != null) {
                list.add(newGardRootChangeProcessor(gardRootPages[0]));
                for (int j = 0; j < processors.length; j++) {
                    list.add(processors[j]);
                }
            }
        }
        processors = list.toArray((P[])Array.newInstance(processorClass_, list
            .size()));
        processorsCache_.put(cacheKey, processors);
        return processors;
    }


    abstract protected P newGardRootChangeProcessor(Page gardRootPage);


    static class CacheKey
    {
        private int[] gardRootIds_;

        private String[] gardIds_;


        public CacheKey(Page[] gardRootPages)
        {
            gardRootIds_ = new int[gardRootPages.length - 1];
            gardIds_ = new String[gardRootPages.length - 1];
            for (int i = 1, idx = 0; i < gardRootPages.length; i++, idx++) {
                gardRootIds_[idx] = gardRootPages[i].getId();
                gardIds_[idx] = gardRootPages[i].getGardId();
            }
        }


        @Override
        public int hashCode()
        {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + Arrays.hashCode(gardIds_);
            result = PRIME * result + Arrays.hashCode(gardRootIds_);
            return result;
        }


        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final CacheKey other = (CacheKey)obj;
            if (!Arrays.equals(gardIds_, other.gardIds_))
                return false;
            if (!Arrays.equals(gardRootIds_, other.gardRootIds_))
                return false;
            return true;
        }
    }


    public void destroy()
    {
        for (int i = 0; i < processors_.length; i++) {
            try {
                destroy(processors_[i]);
            } catch (Throwable t) {
                log_.warn("Can't destroy "
                    + getShortName(processorClass_.getName()) + ": "
                    + processors_[i], t);
            }
        }
    }


    abstract protected void destroy(P processor);


    String getShortName(String className)
    {
        int dot = className.lastIndexOf('.');
        if (dot >= 0) {
            return className.substring(dot + 1);
        } else {
            return className;
        }
    }
}
