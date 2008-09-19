package org.seasar.kvasir.page.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.base.cache.CachePlugin;
import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.IndexedCache;
import org.seasar.kvasir.base.cache.ManagedCache;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.dao.PageDto;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageCache
    implements ManagedCache
{
    public static final String ID = PagePlugin.ID + ".page";

    private static final int LOADED_PROVIDER = 1;

    private static final int LOADED_PATHPAIRPROVIDER = 2;

    private static final int LOADED_CACHEPLUGIN = 4;

    private static final int LOADED_ALL = LOADED_PROVIDER
        | LOADED_PATHPAIRPROVIDER | LOADED_CACHEPLUGIN;

    private int loaded_;

    private Cache<PageKey, PageDto> cache_;

    private IndexedCache<Integer, PathPairKey, String> pathPairCache_;

    private PageProvider provider_;

    private PathPairProvider pathPairProvider_;

    private CachePlugin cachePlugin_;


    public void setProvider(PageProvider provider)
    {
        provider_ = provider;

        if ((loaded_ |= LOADED_PROVIDER) == LOADED_ALL) {
            initialize();
        }
    }


    public void setPathPairProvider(PathPairProvider pathPairProvider)
    {
        pathPairProvider_ = pathPairProvider;

        if ((loaded_ |= LOADED_PATHPAIRPROVIDER) == LOADED_ALL) {
            initialize();
        }
    }


    public void setCachePlugin(CachePlugin cachePlugin)
    {
        cachePlugin_ = cachePlugin;

        if ((loaded_ |= LOADED_CACHEPLUGIN) == LOADED_ALL) {
            initialize();
        }
    }


    void initialize()
    {
        cache_ = cachePlugin_.newCache(ID, PageKey.class, PageDto.class, false);
        cache_.setObjectProvider(provider_);
        pathPairCache_ = cachePlugin_.newIndexedCache(ID, Integer.class,
            PathPairKey.class, String.class, false);
        pathPairCache_.setObjectProvider(pathPairProvider_);
        cachePlugin_.register(ID, this);
    }


    public synchronized PageDto createChildPageDto(PageDto parent, PageMold mold)
        throws DuplicatePageException
    {
        PageDto dto = provider_.createChildPageDto(parent, mold);

        cache_.register(new PageKey(dto.getId()), dto);
        cache_.register(new PageKey(dto.getHeimId(), dto.getPathname()), dto);
        pathPairCache_.clear(dto.getHeimId());

        return dto;
    }


    public synchronized PageDto createRootPageDto(int heimId)
        throws DuplicatePageException
    {
        PageDto dto = provider_.createRootPageDto(heimId);

        cache_.register(new PageKey(dto.getId()), dto);
        cache_.register(new PageKey(dto.getHeimId(), dto.getPathname()), dto);

        return dto;
    }


    public synchronized String findPathname(int heimId, String basePathname,
        String subPathname)
    {
        return pathPairCache_.get(new PathPairKey(heimId, basePathname,
            subPathname));
    }


    public synchronized PageDto[] getChildPageDtos(PageDto dto,
        PageCondition cond)
    {
        return getPageDtos0(provider_.getChildPageIds(dto.getHeimId(), dto
            .getPathname(), cond));
    }


    public int[] getChildPageIds(PageDto dto, PageCondition cond)
    {
        // XXX 今のところキャッシュしていない。
        return toIntArray(provider_.getChildPageIds(dto.getHeimId(), dto
            .getPathname(), cond));
    }


    int[] toIntArray(Number[] numbers)
    {
        int[] ints = new int[numbers.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = numbers[i].intValue();
        }
        return ints;
    }


    public synchronized PageDto getPageDto(int id)
    {
        return cache_.get(new PageKey(id));
    }


    public synchronized PageDto getPageDto(int heimId, String pathname)
    {
        return cache_.get(new PageKey(heimId, pathname));
    }


    public synchronized PageDto[] getPageDtos(int[] ids)
    {
        Number[] objs = new Number[ids.length];
        for (int i = 0; i < ids.length; i++) {
            objs[i] = new Integer(ids[i]);
        }
        return getPageDtos0(objs);
    }


    public synchronized PageDto[] getPageDtos(Number[] ids)
    {
        return getPageDtos0(ids);
    }


    /*
     * synchronizedして呼び出すこと！
     */
    PageDto[] getPageDtos0(Number[] ids)
    {
        PageDto[] dtos = new PageDto[ids.length];
        List<Number> idList = new ArrayList<Number>(ids.length);
        // まずキャッシュから取り出す。
        for (int i = 0; i < ids.length; i++) {
            CachedEntry<PageKey, PageDto> entry = cache_.getEntry(new PageKey(
                ids[i]), false);
            if (entry != null && entry.getCached() != null) {
                dtos[i] = entry.getCached();
            } else {
                idList.add(ids[i]);
            }
        }
        int size = idList.size();
        if (size > 0) {
            // キャッシュにないページオブジェクトを一括読み込みする。
            PageDto[] ds = provider_.getPageDtos(idList.toArray(new Number[0]));
            for (int i = 0; i < ds.length; i++) {
                PageDto dto = ds[i];
                cache_.register(new PageKey(dto.getId()), dto);
                cache_.register(
                    new PageKey(dto.getHeimId(), dto.getPathname()), dto);
            }
            // IDが指定されている順と同じ順に並べる作業をしている。
            // またIDに対応するページがなくなってしまった場合の処理もしている。
            boolean restructure = false;
            for (int i = 0; i < ids.length; i++) {
                if (dtos[i] == null) {
                    dtos[i] = cache_.get(new PageKey(ids[i]));
                    if (dtos[i] == null) {
                        // そのIDのPageは処理中に削除された。
                        restructure = true;
                    }
                }
            }
            if (restructure) {
                List<PageDto> dtoList = new ArrayList<PageDto>(ids.length);
                for (int i = 0; i < ids.length; i++) {
                    if (dtos[i] != null) {
                        dtoList.add(dtos[i]);
                    }
                }
                dtos = dtoList.toArray(new PageDto[0]);
            }
        }
        return dtos;
    }


    public synchronized PageDto[] getPageDtos(PageCondition cond)
    {
        return getPageDtos0(provider_.getPageIds(cond));
    }


    public void deletePageDto(PageDto dto)
    {
        provider_.deleteById(dto.getId());

        cache_.remove(new PageKey(dto.getId()));
        cache_.remove(new PageKey(dto.getHeimId(), dto.getPathname()));
        pathPairCache_.clear(dto.getHeimId());
    }


    public String[] getChildPageNames(PageDto dto, PageCondition cond)
    {
        // XXX 今のところキャッシュしていない。
        return provider_.getNameListByParentPathname(dto.getHeimId(),
            dto.getPathname(), cond).toArray(new String[0]);
    }


    public int getChildPageCount(PageDto dto, PageCondition cond)
    {
        // XXX 今のところキャッシュしていない。
        return provider_.getCountByParentPathname(dto.getHeimId(),
            dto.getPathname(), cond).intValue();
    }


    public int[] getPageIds(PageCondition cond)
    {
        // XXX 今のところキャッシュしていない。
        return toIntArray(provider_.getPageIds(cond));
    }


    public void changeLordId(PageDto dto, int oldLordId, int newLordId)
    {
        provider_.changeLordId(dto.getHeimId(), dto.getPathname(), oldLordId,
            newLordId);

        cache_.clear();
        pathPairCache_.clear(dto.getHeimId());
    }


    public void moveTo(int heimId, PageDto fromDto, PageDto toParentDto,
        String toName)
    {
        provider_.moveTo(heimId, fromDto.getId(), fromDto.getParentPathname(),
            fromDto.getName(), toParentDto.getId(), toParentDto.getPathname(),
            toName);

        cache_.clear();
        pathPairCache_.clear(heimId);
    }


    public int updatePageDto(PageDto dto, PageDto changeSet)
    {
        int result = provider_.updateById(dto.getId(), changeSet);

        cache_.remove(new PageKey(dto.getId()));
        cache_.remove(new PageKey(dto.getHeimId(), dto.getPathname()));
        if (changeSet.getRevealDate() != null
            || changeSet.getConcealDate() != null) {
            pathPairCache_.clear(dto.getHeimId());
        }

        return result;
    }


    public PageDto refresh(PageDto dto)
    {
        cache_.remove(new PageKey(dto.getId()));
        cache_.remove(new PageKey(dto.getHeimId(), dto.getPathname()));

        return cache_.get(new PageKey(dto.getId()));
    }


    public void releasePages(PageDto dto)
    {
        provider_.releasePages(dto.getId());

        cache_.clear();
    }


    public <R> R runWithLocking(Integer[] ids, Processable<R> processable)
        throws ProcessableRuntimeException
    {
        return provider_.runWithLocking(ids, processable);
    }


    public synchronized long getTotalSize()
    {
        long totalSize = 0;
        long total = cache_.getTotalSize();
        if (total == CacheStorage.TOTALSIZE_UNLIMITED) {
            return CacheStorage.TOTALSIZE_UNLIMITED;
        }
        totalSize += total;
        total = pathPairCache_.getTotalSize();
        if (total == CacheStorage.TOTALSIZE_UNLIMITED) {
            return CacheStorage.TOTALSIZE_UNLIMITED;
        }
        totalSize += total;
        return totalSize;
    }


    public synchronized long getUsedSize()
    {
        return cache_.getUsedSize() + cache_.getUsedSize();
    }


    public synchronized void ping()
    {
        cache_.ping();
        pathPairCache_.ping();
    }


    public synchronized void refresh()
    {
        cache_.refresh();
        pathPairCache_.refresh();
    }
}
