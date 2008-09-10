package org.seasar.kvasir.page.ability.content.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.seasar.framework.container.annotation.tiger.Aspect;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.impl.AbstractObjectProvider;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.page.CollisionDetectedRuntimeException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.page.ability.content.ContentMold;
import org.seasar.kvasir.page.ability.content.dao.ContentDao;
import org.seasar.kvasir.page.ability.content.dao.ContentsDao;
import org.seasar.kvasir.page.ability.content.dao.ContentsDto;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;


public class ContentProvider extends
    AbstractObjectProvider<ContentKey, Content>
{
    public static final String MEDIATYPE_DEFAULT = "application/octet-stream";

    public static final String ENCODING_DEFAULT = "UTF-8";

    private ContentDao contentDao_;

    private ContentsDao contentsDao_;

    private ContentAbilityPlugin plugin_;


    public void setContentDao(ContentDao contentDao)
    {
        contentDao_ = contentDao;
    }


    public void setContentsDao(ContentsDao contentsDao)
    {
        contentsDao_ = contentsDao;
    }


    public void setPlugin(ContentAbilityPlugin plugin)
    {
        plugin_ = plugin;
    }


    @Aspect("j2ee.requiredTx")
    public CachedEntry<ContentKey, Content> get(ContentKey key)
    {
        return newEntry(key, newContent(contentsDao_
            .getObjectByPageIdAndLocale(key.getPageId(), key.getLocale())));
    }


    public boolean isModified(CachedEntry<ContentKey, Content> entry)
    {
        return getModifyDate(entry.getKey().getPageId()).getTime() > entry
            .getSequenceNumber();
    }


    public CachedEntry<ContentKey, Content> newEntry(ContentKey key,
        Content object)
    {
        return new CachedEntryImpl<ContentKey, Content>(key,
            (object != null ? object.getModifyDate().getTime() : 0L), object);
    }


    @Aspect("j2ee.requiredTx")
    public String[] getVariants(Integer pageId)
    {
        Set<String> variantSet = new TreeSet<String>(Arrays.asList(contentsDao_
            .getVariantsByPageId(pageId)));
        variantSet.add(Page.VARIANT_DEFAULT);
        return variantSet.toArray(new String[0]);
    }


    @Aspect("j2ee.requiredTx")
    public void removeContentsBefore(Integer pageId, String variant,
        Integer revisionNumber)
    {
        Number earliestRevisionNumber = contentsDao_
            .getEarliestRevisionNumberByPageIdAndVariant(pageId, variant);
        if (earliestRevisionNumber == null) {
            // 指定されたバリアントに関するコンテンツは存在しないので何もしない。
            return;
        }

        for (int i = earliestRevisionNumber.intValue(); i < revisionNumber; i++) {
            ContentsDto dto = contentsDao_
                .getObjectByPageIdAndVariantAndRevisionNumber(pageId, variant,
                    i);
            if (dto != null) {
                plugin_.getContentResource(dto.getId()).delete();
                contentsDao_.deleteByPageIdAndVariantAndRevisionNumber(pageId,
                    variant, i);
            }
        }

        contentDao_.touchByPageId(pageId);
    }


    @Aspect("j2ee.requiredTx")
    public void clearContents(Integer pageId, String variant)
    {
        Number[] ids = contentsDao_.getIdsByPageIdAndVariant(pageId, variant);
        for (int i = 0; i < ids.length; i++) {
            plugin_.getContentResource(ids[i].intValue()).delete();
        }
        contentsDao_.deleteByPageIdAndVariant(pageId, variant);
        contentDao_.touchByPageId(pageId);
    }


    @Aspect("j2ee.requiredTx")
    public void clearAllContents(Integer pageId)
    {
        Number[] ids = contentsDao_.getIdsByPageId(pageId);
        for (int i = 0; i < ids.length; i++) {
            plugin_.getContentResource(ids[i].intValue()).delete();
        }
        contentsDao_.deleteByPageId(pageId);
        contentDao_.deleteByPageId(pageId);
    }


    @Aspect("j2ee.requiredTx")
    public Content getContent(Integer pageId, String variant,
        Integer revisionNumber)
    {
        return newContent(contentsDao_
            .getObjectByPageIdAndVariantAndRevisionNumber(pageId, variant,
                revisionNumber));
    }


    Content newContent(ContentsDto dto)
    {
        if (dto != null) {
            return new ContentImpl(dto, plugin_);
        } else {
            return null;
        }
    }


    @Aspect("j2ee.requiredTx")
    public int getEarliestRevisionNumber(Integer pageId, String variant)
    {
        Number number = contentsDao_
            .getEarliestRevisionNumberByPageIdAndVariant(pageId, variant);
        if (number == null) {
            return ContentAbility.REVISIONNUMBER_FIRST;
        } else {
            return number.intValue();
        }
    }


    @Aspect("j2ee.requiredTx")
    public int getLatestRevisionNumber(Integer pageId, String variant)
    {
        ContentsDto dto = contentsDao_.getLatestObjectByPageIdAndVariant(
            pageId, variant);
        if (dto == null) {
            return ContentAbility.REVISIONNUMBER_FIRST - 1;
        } else {
            return dto.getRevisionNumber();
        }
    }


    @Aspect("j2ee.requiredTx")
    public int getRevisionNumber(Integer pageId, String variant, Date date)
    {
        Number number = contentsDao_
            .getRevisionNumberByPageIdAndVariantAndDate(pageId, variant, date);
        if (number == null) {
            ContentsDto dto = contentsDao_.getLatestObjectByPageIdAndVariant(
                pageId, variant);
            if (dto != null && !dto.getCreateDate().after(date)) {
                number = dto.getRevisionNumber();
            } else {
                return ContentAbility.REVISIONNUMBER_FIRST - 1;
            }
        }
        return number.intValue();
    }


    @Aspect("j2ee.requiredTx")
    public Date getModifyDate(Integer pageId)
    {
        Date modifyDate = contentDao_.getModifyDateByPageId(pageId);
        if (modifyDate == null) {
            modifyDate = new Date(0L);
        }
        return modifyDate;
    }


    @Aspect("j2ee.requiredTx")
    public void updateContent(Integer pageId, String variant, ContentMold mold,
        boolean overwrite)
    {
        ContentsDto dto = contentsDao_.getLatestObjectByPageIdAndVariant(
            pageId, variant);
        if (mold.getVersion() != null) {
            // 楽観的排他制御を行なう。
            if (dto != null && !dto.getVersion().equals(mold.getVersion())) {
                throw new CollisionDetectedRuntimeException(pageId);
            }
        }

        if (dto != null && overwrite && !mold.bodyExists()) {
            // メタデータだけを更新する。
            ContentsDto changeSet = new ContentsDto();
            if (mold.getRevisionNumber() != null) {
                changeSet.setRevisionNumber(mold.getRevisionNumber());
            }
            if (mold.getCreateDate() != null) {
                changeSet.setCreateDate(mold.getCreateDate());
            }
            if (mold.getModifyDate() != null) {
                changeSet.setModifyDate(mold.getModifyDate());
            }
            if (mold.getMediaType() != null) {
                changeSet.setMediaType(mold.getMediaType());
            }
            if (mold.getEncoding() != null) {
                changeSet.setEncoding(mold.getEncoding());
            }
            contentsDao_.updateById(changeSet, dto.getId());
        } else {
            if (!mold.bodyExists()) {
                // bodyは必須。
                throw new IllegalArgumentException("ContentMold has no body.");
            }

            Date now = new Date();

            Integer revisionNumber = mold.getRevisionNumber();
            if (revisionNumber == null) {
                if (dto != null) {
                    if (overwrite) {
                        revisionNumber = 0;
                    } else {
                        revisionNumber = dto.getRevisionNumber() + 1;
                    }
                } else {
                    revisionNumber = ContentAbility.REVISIONNUMBER_FIRST;
                }
            }
            Date createDate = mold.getCreateDate();
            if (createDate == null) {
                createDate = now;
            }
            Date modifyDate = mold.getModifyDate();
            if (modifyDate == null) {
                modifyDate = now;
            }
            String mediaType = mold.getMediaType();
            if (mediaType == null) {
                if (dto != null) {
                    mediaType = dto.getMediaType();
                } else {
                    ContentsDto defaultDto = contentsDao_
                        .getLatestObjectByPageIdAndVariant(pageId,
                            Page.VARIANT_DEFAULT);
                    if (defaultDto != null) {
                        mediaType = defaultDto.getMediaType();
                    } else {
                        mediaType = MEDIATYPE_DEFAULT;
                    }
                }
            }
            String encoding = mold.getEncoding();
            if (encoding == null) {
                encoding = (dto != null ? dto.getEncoding() : ENCODING_DEFAULT);
                mold.setEncoding(encoding);
            }

            // メタデータをDBに格納する。
            ContentsDto inserted = new ContentsDto(pageId, variant,
                revisionNumber, createDate, modifyDate, false, mediaType,
                encoding);
            contentsDao_.insert(inserted);

            // 本文をファイルシステムに格納する。
            Resource resource = plugin_.getContentResource(inserted.getId());
            resource.getParentResource().mkdirs();
            InputStream is = null;
            OutputStream os = null;
            try {
                is = mold.getBodyInputStream();
                os = resource.getOutputStream();
                IOUtils.pipe(is, os);
            } catch (IOException ex) {
                throw new IORuntimeException(ex);
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(os);
            }

            // 上書きモードの時は古い本文をファイルシステムから削除する。
            if (overwrite && dto != null) {
                plugin_.getContentResource(dto.getId()).delete();
            }

            // 最新リビジョンフラグのつけかえ処理を行なう。
            ContentsDto changeSet = new ContentsDto();
            changeSet.setLatest(Boolean.TRUE);
            contentsDao_.updateById(changeSet, inserted.getId());
            if (dto != null) {
                if (overwrite) {
                    contentsDao_.deleteById(dto.getId());

                    if (mold.getRevisionNumber() == null) {
                        // リビジョン番号の重複を避けるために最初は0にしていたので、
                        // リビジョン番号を埋める。ちなみに上のlatestの設定をここで一緒にやらないのは、
                        // 一瞬でもlatestがついているレコードがなくなってしまうのは嫌だったから。
                        changeSet = new ContentsDto();
                        changeSet.setRevisionNumber(dto.getRevisionNumber());
                        contentsDao_.updateById(changeSet, inserted.getId());
                    }
                } else {
                    changeSet = new ContentsDto();

                    // 時刻指定検索が正しくできるように、旧リビジョンのmodifyDateを
                    // 新リビジョンのcreateDateと同じにしておく。
                    changeSet.setModifyDate(inserted.getCreateDate());
                    changeSet.setLatest(Boolean.FALSE);
                    contentsDao_.updateById(changeSet, dto.getId());
                }
            }
        }

        contentDao_.touchByPageId(pageId);
    }


    @Aspect("j2ee.requiredTx")
    public boolean hasAnyContents(Integer pageId)
    {
        return (contentDao_.getModifyDateByPageId(pageId) != null);
    }
}
