package org.seasar.kvasir.page.ability.content;

import java.util.Date;
import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface ContentAbilityAlfr
    extends PageAbilityAlfr
{
    String SHORTID = "content";

    String ATTR_MODIFYDATE = "modifyDate";

    String ATTR_EARLIESTREVISIONNUMBER = "earliestRevisionNumber";

    String ATTR_LATESTREVISIONNUMBER = "latestRevisionNumber";

    String SUBNAME_CREATEDATE = "createDate";

    String SUBNAME_MODIFYDATE = "modifyDate";

    String SUBNAME_MEDIATYPE = "mediaType";

    String SUBNAME_ENCODING = "encoding";


    Date getModifyDate(Page page);


    int getEarliestRevisionNumber(Page page, String variant);


    int getLatestRevisionNumber(Page page, String variant);


    int getRevisionNumber(Page page, String variant, Date date);


    Content getLatestContent(Page page, String variant);


    Content getLatestContent(Page page, Locale locale);


    Content getContent(Page page, Locale locale, Date date);


    Content getContent(Page page, String variant, int revisionNumber);


    void setContent(Page page, String variant, ContentMold mold);


    void updateContent(Page page, String variant, ContentMold mold);


    void removeContentsBefore(Page page_, String variant, int revisionNumber);


    void clearContents(Page page, String variant);


    void clearAllContents(Page page);
}
