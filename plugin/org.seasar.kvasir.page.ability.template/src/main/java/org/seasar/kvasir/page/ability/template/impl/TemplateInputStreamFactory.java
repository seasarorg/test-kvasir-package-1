package org.seasar.kvasir.page.ability.template.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.InputStreamFactory;
import org.seasar.kvasir.util.io.impl.AbstractInputStreamFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 *
 * @author YOKOTA Takehiko
 */
public class TemplateInputStreamFactory extends AbstractInputStreamFactory
    implements InputStreamFactory
{
    private Template template_;


    public TemplateInputStreamFactory(Template template)
    {
        template_ = template;
    }


    public InputStream getInputStream()
    {
        String body = template_.getBody();
        if (body == null) {
            return null;
        } else {
            try {
                return new ByteArrayInputStream(body
                    .getBytes(Template.ENCODING));
            } catch (UnsupportedEncodingException ex) {
                throw new IORuntimeException(ex);
            }
        }
    }
}
