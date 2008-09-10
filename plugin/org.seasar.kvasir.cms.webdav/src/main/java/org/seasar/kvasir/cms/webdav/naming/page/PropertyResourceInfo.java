package org.seasar.kvasir.cms.webdav.naming.page;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.TreeMap;

import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.IORuntimeException;

public class PropertyResourceInfo implements ResourceInfo {
    private Page page_;

    private byte[] body_;

    public PropertyResourceInfo(final Page page, final String variant) {
        page_ = page;

        PropertyHandler handler = page
                .runWithLocking(new Processable<PropertyHandler>() {
                    public PropertyHandler process()
                            throws ProcessableRuntimeException {
                        PropertyHandler handler = new MapProperties(
                                new TreeMap<String, String>());
                        PropertyAbility ability = page
                                .getAbility(PropertyAbility.class);
                        for (Iterator<String> itr = ability
                                .attributeNames(variant); itr.hasNext();) {
                            String name = itr.next();
                            handler.setProperty(name, ability.getProperty(name,
                                    variant));
                        }
                        return handler;
                    }
                });

        StringWriter sw = new StringWriter();
        try {
            handler.store(sw);
        } catch (IOException ex) {
            throw new IORuntimeException("Can't happen!", ex);
        }
        try {
            body_ = sw.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IORuntimeException("Can't happen!", ex);
        }
    }

    public long getSize() {
        return body_.length;
    }

    public long getLastModifiedTime() {
        return page_.getModifyDate().getTime();
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(body_);
    }

    public long getCreationTime() {
        return page_.getCreateDate().getTime();
    }
}
