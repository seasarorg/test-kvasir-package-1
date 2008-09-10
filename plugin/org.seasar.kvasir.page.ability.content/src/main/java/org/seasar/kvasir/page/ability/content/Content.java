package org.seasar.kvasir.page.ability.content;

import java.io.InputStream;
import java.util.Date;

import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface Content
{
    String VARNAME_THIS = "this";


    int getId();


    int getRevisionNumber();


    String getVariant();


    Date getCreateDate();


    Date getModifyDate();


    String getMediaType();


    String getEncoding();


    InputStream getBodyInputStream();


    byte[] getBodyBytes();


    String getBodyString();


    Resource getBodyResource();


    String getBodyHTMLString(VariableResolver resolver);
}
