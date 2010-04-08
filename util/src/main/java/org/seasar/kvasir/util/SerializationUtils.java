package org.seasar.kvasir.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;


/**
 * シリアライズ処理に関するユーティリティメソッドを提供するクラスです。
 * 
 * @author yokota
 */
public class SerializationUtils
{
    /**
     * 指定されたオブジェクトを文字列にシリアライズします。
     * 
     * @param obj オブジェクト。nullを指定した場合はnullが返されます。
     * @return シリアライズ結果。
     */
    public static String serialize(Serializable obj)
    {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(oos);
        }
        return StringUtils.toHexString(baos.toByteArray());
    }


    /**
     * 指定された文字列にデシリアライズします。
     * <p>コンテキストクラスローダが設定されている場合、
     * デシリアライズ処理の際のクラス解決にはコンテキストクラスローダが使用されます。
     * </p>
     * 
     * @param serialized 文字列。nullを指定した場合はnullが返されます。
     * @return デシリアライズ結果。
     */
    public static Object deserialize(String serialized)
    {
        if (serialized == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(StringUtils
            .toBytes(serialized));
        ObjectInputStream ois = null;
        try {
            ois = new CustomObjectInputStream(bais, Thread.currentThread()
                .getContextClassLoader());
            return ois.readObject();
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new IORuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(ois);
        }
    }


    static class CustomObjectInputStream extends ObjectInputStream
    {
        private ClassLoader cl_;


        public CustomObjectInputStream(InputStream in, ClassLoader cl)
            throws IOException
        {
            super(in);
            cl_ = cl;
        }


        protected Class resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException
        {
            if (cl_ != null) {
                String name = desc.getName();
                try {
                    return cl_.loadClass(name);
                } catch (ClassNotFoundException ignore) {
                }
            }
            return super.resolveClass(desc);
        }
    }
}
