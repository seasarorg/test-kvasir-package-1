package org.seasar.kvasir.base.cache.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.kvasir.base.cache.CachedEntry;


/**
 * オブジェクトをシリアライズしてファイルシステムに保存するような
 * CachingStrategyの実装クラスです。
 * <p>このCachingStrategyを使用する場合、
 * 保存するオブジェクトはシリアライズ可能である必要があります。
 * シリアライズ可能でないオブジェクトをキャッシュに登録した場合、
 * <code>java.io.NotSerializableException</code>がスローされます。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class FileCacheStorage<K, T extends Serializable> extends
    AbstractCacheStorage<K, T>
{
    private File baseDir_;

    private long usedSize_;


    /**
     * このクラスのオブジェクトを生成します。
     *
     * @param baseDir キャッシュのエントリを保存するディレクトリ。
     * ディレクトリが存在しない場合は自動的に生成されます。
     */
    public FileCacheStorage(File baseDir)
    {
        baseDir_ = baseDir;
        baseDir_.mkdirs();
        clear();
    }


    public void clear()
    {
        File[] files = baseDir_.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteFile(files[i]);
        }
    }


    public CachedEntry<K, T> get(K key)
    {
        return getFrom(getFile(key));
    }


    @SuppressWarnings("unchecked")
    CachedEntry<K, T> getFrom(File file)
    {
        CachedEntry<K, T> entry;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(is);
            entry = (CachedEntry<K, T>)ois.readObject();
        } catch (ClassNotFoundException ex) {
            // XXX ログに吐こう。
            return null;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            // XXX ログに吐こう。
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable ignore) {
                }
            }
        }

        return entry;
    }


    public Iterator<CachedEntry<K, T>> getEntryIterator()
    {
        File[] files = baseDir_.listFiles();
        List<CachedEntry<K, T>> entryList = new ArrayList<CachedEntry<K, T>>();
        for (int i = 0; i < files.length; i++) {
            CachedEntry<K, T> entry = getFrom(files[i]);
            if (entry != null) {
                entryList.add(entry);
            }
        }
        return entryList.iterator();
    }


    public void register(CachedEntry<K, T> entry)
    {
        File file = getFile(entry.getKey());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        OutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            os = new FileOutputStream(file);
            oos = new ObjectOutputStream(os);
            os = null;
            oos.writeObject(entry);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (Throwable ignore) {
                }
            } else if (os != null) {
                try {
                    os.close();
                } catch (Throwable ignore) {
                }
            }
        }
        usedSize_++;

        notifyRegistered(entry);
    }


    public void remove(K key)
    {
        deleteFile(getFile(key));
        usedSize_--;
    }


    protected String getPath(K key)
    {
        String keyString = key.toString();
        StringBuffer sb = new StringBuffer();
        int len = keyString.length();
        for (int i = 0; i < len; i++) {
            int ch = keyString.charAt(i);
            if (ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z' || ch >= 'A'
                && ch <= 'Z' || ch == '_' || ch == '.' || ch == '-') {
                sb.append((char)ch);
            } else {
                sb.append('%');
                String str = Integer.toHexString(ch);
                if (str.length() == 1) {
                    sb.append('0');
                }
                sb.append(str);
            }
        }
        return sb.toString();
    }


    protected File getFile(K key)
    {
        return new File(baseDir_, getPath(key));
    }


    boolean deleteFile(File file)
    {
        if (!file.exists()) {
            return true;
        }
        boolean retval = true;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (!deleteFile(files[i])) {
                    retval = false;
                }
            }
        } else {
            retval = file.delete();
        }
        return retval;
    }


    public long getTotalSize()
    {
        return TOTALSIZE_UNLIMITED;
    }


    public long getUsedSize()
    {
        return usedSize_;
    }
}
