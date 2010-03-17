package org.seasar.kvasir.page.impl;

import java.util.Date;
import java.util.List;

import org.seasar.framework.container.annotation.tiger.Aspect;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.ObjectProvider;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.dao.PageDao;
import org.seasar.kvasir.page.dao.PageDto;
import org.seasar.kvasir.page.type.Directory;


public class PageProvider
    implements ObjectProvider<PageKey, PageDto>
{
    private PageDao dao_;


    public void setDao(PageDao dao)
    {
        dao_ = dao;
    }


    public CachedEntry<PageKey, PageDto> get(PageKey key)
    {
        PageDto dto = getPageDto(key);
        long serialNumber;
        if (dto == null) {
            serialNumber = 0;
        } else {
            serialNumber = dto.getVersion();
        }
        return new CachedEntryImpl<PageKey, PageDto>(key, serialNumber, dto);
    }


    @Aspect("j2ee.requiredTx")
    PageDto getPageDto(PageKey key)
    {
        if (key.getId() != null) {
            return dao_.getObjectById(key.getId());
        } else if (key.getHeimId() != null && key.getPathname() != null) {
            return dao_.getObjectByPathname(key.getHeimId(), key.getPathname());
        } else {
            throw new IllegalArgumentException("Empty key");
        }
    }


    public boolean isModified(CachedEntry<PageKey, PageDto> entry)
    {
        PageDto dto = getPageDto(entry.getKey());
        PageDto cached = entry.getCached();
        if (dto == null) {
            if (cached != null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (cached != null) {
                return (!dto.getVersion().equals(cached.getVersion()));
            } else {
                return true;
            }
        }
    }


    public CachedEntry<PageKey, PageDto> refresh(
        CachedEntry<PageKey, PageDto> entry)
    {
        return get(entry.getKey());
    }


    public CachedEntry<PageKey, PageDto> newEntry(PageKey key, PageDto object)
    {
        return new CachedEntryImpl<PageKey, PageDto>(key, (object == null ? 0
            : object.getVersion()), object);
    }


    @Aspect("j2ee.requiredTx")
    public PageDto[] getPageDtos(Number[] ids)
    {
        return dao_.getObjectListByIds(ids).toArray(new PageDto[0]);
    }


    @Aspect("j2ee.requiredTx")
    public Number[] getPageIds(PageCondition cond)
    {
        return dao_.getIdList(cond).toArray(new Number[0]);
    }


    @Aspect("j2ee.requiredTx")
    public PageDto createChildPageDto(final PageDto parent, final PageMold mold)
        throws DuplicatePageException
    {
        if (mold.getName() == null) {
            throw new IllegalArgumentException("name must be specified");
        }

        try {
            return dao_.runWithLocking(new Number[] { parent.getId() },
                new Processable<PageDto>() {
                    public PageDto process()
                        throws ProcessableRuntimeException
                    {
                        String pathname = parent.getPathname();
                        if (dao_.childNameExists(parent.getHeimId(), pathname,
                            mold.getName())) {
                            throw new ProcessableRuntimeException(
                                new DuplicatePageException(
                                    "Duplicate entry: pathname=" + pathname
                                        + ", name=" + mold.getName()));
                        }
                        PageDto dto = newPageDto(parent, mold);
                        dao_.insert(dto);
                        return dao_.getObjectById(dto.getId());
                    }
                });
        } catch (ProcessableRuntimeException ex) {
            throw (DuplicatePageException)ex.getCause();
        }
    }


    PageDto newPageDto(PageDto parent, PageMold mold)
    {
        PageDto dto = new PageDto();
        Date now = new Date();
        String parentPathname = parent.getPathname();
        dto.setType((mold.getType() != null) ? mold.getType() : Page.TYPE);
        dto.setHeimId(parent.getHeimId());
        dto.setLordId(new Integer(parent.getLordId()));
        dto.setRawParentPathname(parentPathname);
        dto.setName(mold.getName());
        dto.setOrderNumber(mold.getOrderNumber());
        dto.setCreateDate((mold.getCreateDate() != null) ? mold.getCreateDate()
            : now);
        dto.setModifyDate((mold.getModifyDate() != null) ? mold.getModifyDate()
            : now);
        dto.setOwnerUserId((mold.getOwnerUser() != null) ? new Integer(mold
            .getOwnerUser().getId()) : new Integer(Page.ID_ADMINISTRATOR_USER));
        dto.setRevealDate((mold.getRevealDate() != null) ? mold.getRevealDate()
            : dto.getCreateDate());
        dto.setConcealDate((mold.getConcealDate() != null) ? mold
            .getConcealDate() : Page.DATE_RAGNAROK);
        dto.setNode((mold.getNode() != null) ? mold.getNode() : Boolean.FALSE);
        dto.setAsFile((mold.getAsFile() != null) ? mold.getAsFile()
            : Boolean.FALSE);
        dto.setListing((mold.getListing() != null) ? mold.getListing()
            : Boolean.TRUE);

        return dto;
    }


    PageDto newRootPageDto(Integer heimId)
    {
        PageDto dto = new PageDto();
        Date now = new Date();
        dto.setType(Directory.TYPE);
        dto.setHeimId(heimId);
        dto.setLordId(0);
        dto.setRawParentPathname("_");
        dto.setName("");
        dto.setOrderNumber(1);
        dto.setCreateDate(now);
        dto.setModifyDate(now);
        dto.setRevealDate(now);
        dto.setConcealDate(Page.DATE_RAGNAROK);
        dto.setOwnerUserId(Page.ID_ADMINISTRATOR_USER);
        dto.setNode(true);
        dto.setAsFile(false);
        dto.setListing(true);

        return dto;
    }


    @Aspect("j2ee.requiredTx")
    public Number[] getChildPageIds(Number heimId, String pathname,
        PageCondition cond)
    {
        return dao_.getIdListByParentPathname(heimId, pathname, cond).toArray(
            new Number[0]);
    }


    @Aspect("j2ee.requiredTx")
    public List<String> getNameListByParentPathname(Number heimId,
        String parentPathname, PageCondition cond)
    {
        return dao_.getNameListByParentPathname(heimId, parentPathname, cond);
    }


    @Aspect("j2ee.requiredTx")
    public Number getCountByParentPathname(Number heimId,
        String parentPathname, PageCondition cond)
    {
        return dao_.getCountByParentPathname(heimId, parentPathname, cond);
    }


    @Aspect("j2ee.requiredTx")
    public void deleteById(Number id)
    {
        dao_.deleteById(id);
    }


    @Aspect("j2ee.requiredTx")
    public void changeLordId(Number heimId, String pathname, Number oldLordId,
        Number newLordId)
    {
        dao_.changeLordId(heimId, pathname, oldLordId, newLordId);
    }


    @Aspect("j2ee.requiredTx")
    public void moveTo(Number heimId, Number fromId, String fromParentPathname,
        String fromName, Number toParentId, String toParentPathname,
        String toName)
    {
        dao_.moveTo(heimId, fromId, fromParentPathname, fromName, toParentId,
            toParentPathname, toName);
    }


    @Aspect("j2ee.requiredTx")
    public int updateById(Number id, PageDto dto)
    {
        return dao_.updateById(id, dto);
    }


    @Aspect("j2ee.requiredTx")
    public PageDto createRootPageDto(final Integer heimId)
        throws DuplicatePageException
    {
        try {
            return dao_.runWithLocking(new Number[] { Page.ID_ROOT },
                new Processable<PageDto>() {
                    public PageDto process()
                        throws ProcessableRuntimeException
                    {
                        if (dao_.getObjectByPathname(heimId, "") != null) {
                            throw new ProcessableRuntimeException(
                                new DuplicatePageException(
                                    "Duplicate root entry: heimId=" + heimId));
                        }
                        PageDto dto = newRootPageDto(heimId);
                        dao_.insert(dto);
                        PageDto dto2 = new PageDto();
                        dto2.setLordId(dto.getId());
                        dao_.updateById(dto.getId(), dto2);
                        return dao_.getObjectById(dto.getId());
                    }
                });
        } catch (ProcessableRuntimeException ex) {
            throw (DuplicatePageException)ex.getCause();
        }
    }


    @Aspect("j2ee.requiredTx")
    public void releasePages(Number ownerUserId)
    {
        dao_.releasePages(ownerUserId);
    }


    public <R> R runWithLocking(Number[] ids, Processable<R> processable)
        throws ProcessableRuntimeException
    {
        return dao_.runWithLocking(ids, processable);
    }
}
