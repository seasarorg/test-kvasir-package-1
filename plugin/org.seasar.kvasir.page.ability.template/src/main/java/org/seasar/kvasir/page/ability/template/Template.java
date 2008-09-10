package org.seasar.kvasir.page.ability.template;

import java.util.Date;

import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface Template
{
    String ENCODING = "UTF-8";


    String getBody();


    Date getModifyDate();


    long getSize();


    Resource getBodyResource();


    void setBodyResource(Resource bodyResource);
}
