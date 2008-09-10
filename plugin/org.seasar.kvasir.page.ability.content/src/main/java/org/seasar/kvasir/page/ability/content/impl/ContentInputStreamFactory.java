package org.seasar.kvasir.page.ability.content.impl;

import java.io.InputStream;

import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.util.io.InputStreamFactory;
import org.seasar.kvasir.util.io.impl.AbstractInputStreamFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 *
 * @author YOKOTA Takehiko
 */
public class ContentInputStreamFactory extends AbstractInputStreamFactory
    implements InputStreamFactory
{
    private Content content_;


    public ContentInputStreamFactory(Content content)
    {
        content_ = content;
    }


    public InputStream getInputStream()
    {
        return content_.getBodyInputStream();
    }
}
