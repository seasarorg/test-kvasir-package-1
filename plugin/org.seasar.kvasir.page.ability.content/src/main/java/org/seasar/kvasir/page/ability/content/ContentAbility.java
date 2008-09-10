package org.seasar.kvasir.page.ability.content;

import java.util.Date;
import java.util.Locale;

import org.seasar.kvasir.page.ability.PageAbility;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface ContentAbility
    extends PageAbility
{
    int REVISIONNUMBER_FIRST = 1;


    Date getModifyDate();


    int getEarliestRevisionNumber(String variant);


    int getLatestRevisionNumber(String variant);


    Content getLatestContent(String variant);


    Content getLatestContent(Locale locale);


    Content getContent(Locale locale, Date date);


    Content getContent(String variant, int revisionNumber);


    void setContent(String variant, ContentMold mold);


    void updateContent(String variant, ContentMold mold);


    void removeContentsBefore(String variant, int revisionNumber);


    void clearContents(String variant);


    void clearAllContents();
}
