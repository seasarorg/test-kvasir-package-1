package org.seasar.kvasir.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * @author YOKOTA Takehiko
 */
public class ResourceUtils
{
    private static final int BUF_SIZE = 32768;

    private static final Set EXCLUDES = new HashSet(Arrays.asList(new String[] {
        ".svn", "_svn", "CVS" }));

    private static final int RETRY_COUNT = 5;

    private static final long RETRY_WAITTIME = 1000;


    private ResourceUtils()
    {
    }


    /**
     * 指定されたリソースfromをリソースtoとしてコピーします。
     * <p>fromがディレクトリである場合は再帰的にコピーします。</p>
     *
     * @param from コピー元のリソース。
     * @param to コピー先のリソース。
     * @exception IORuntimeException I/O例外が発生した時。
     */
    public static void copy(Resource from, Resource to)
    {
        copy(from, to, EXCLUDES);
    }


    public static void copy(Resource from, Resource to, String[] excludes)
    {
        Set excludeSet = null;
        if (excludes != null) {
            excludeSet = new HashSet(Arrays.asList(excludes));
        }
        copy(from, to, excludeSet);
    }


    public static void copy(Resource from, Resource to, Set excludeSet)
    {
        if (!from.exists()) {
            return;
        } else if (from.isDirectory()) {
            if (to.exists()) {
                if (!to.isDirectory()) {
                    if (!to.delete() || !to.mkdir()) {
                        throw new IORuntimeException(
                            "Can't create destination directory: "
                                + to.getURL());
                    }
                }
            } else {
                to.mkdirs();
            }

            Resource[] resources = from.listResources();
            for (int i = 0; i < resources.length; i++) {
                Resource child = resources[i];
                if (excludeSet != null && excludeSet.contains(child.getName())) {
                    continue;
                }
                copy(child, to.getChildResource(child.getName()), excludeSet);
            }
        } else {
            if (excludeSet != null && excludeSet.contains(from.getName())) {
                return;
            }
            if (to.exists()) {
                if (to.isDirectory()) {
                    throw new IORuntimeException(
                        "Type mismatch: source resource (" + from.getURL()
                            + ")is not a directory, but destination resource ("
                            + to.getURL() + ") is a directory.");
                }
            } else {
                to.getParentResource().mkdirs();
            }

            BufferedInputStream in = null;
            BufferedOutputStream out = null;
            try {
                in = new BufferedInputStream(from.getInputStream());
                out = new BufferedOutputStream(to.getOutputStream());

                byte[] buf = new byte[BUF_SIZE];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (IOException ex) {
                throw new IORuntimeException(ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Throwable t) {
                        ;
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (Throwable t) {
                        ;
                    }
                }
            }
        }
    }


    public static boolean delete(Resource resource, boolean recursive)
    {
        boolean result = true;

        if (resource.exists()) {
            if (recursive && resource.isDirectory()) {
                Resource[] childResources = resource.listResources();
                boolean childrenDeleted = true;
                for (int i = 0; i < childResources.length; i++) {
                    if (!delete(childResources[i], true)) {
                        result = false;
                        childrenDeleted = false;
                    }
                }
                if (!childrenDeleted) {
                    // 子供が残っているので親は消せない。
                    return false;
                }
            }
            if (!deleteWithRetry(resource, RETRY_COUNT, RETRY_WAITTIME)) {
                result = false;
            }
        }

        return result;
    }


    public static boolean deleteWithRetry(Resource resource, int count,
        long waitTime)
    {
        for (int i = 0; i < count; i++) {
            if (resource.delete()) {
                return true;
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ex) {
                return false;
            }
        }

        return false;
    }


    public static boolean deleteChildren(Resource resource)
    {
        boolean result = true;

        if (resource.exists() && resource.isDirectory()) {
            Resource[] childResources = resource.listResources();
            for (int i = 0; i < childResources.length; i++) {
                if (!deleteChildren(childResources[i])) {
                    result = false;
                } else {
                    if (!deleteWithRetry(childResources[i], RETRY_COUNT,
                        RETRY_WAITTIME)) {
                        result = false;
                    }
                }
            }
        }

        return result;
    }


    public static String readString(Resource resource, String defaultString)
    {
        return readString(resource, "UTF-8", true, defaultString);
    }


    public static String readString(Resource resource, String encoding,
        boolean normalizeLineSeparator, String defaultString)
    {
        if (resource == null || !resource.exists() || resource.isDirectory()) {
            return defaultString;
        }
        try {
            return IOUtils.readString(resource.getInputStream(), encoding,
                normalizeLineSeparator);
        } catch (ResourceNotFoundException ex) {
            return defaultString;
        }
    }


    public static void writeString(Resource resource, String string)
    {
        writeString(resource, string, "UTF-8", true);
    }


    public static void writeString(Resource resource, String string,
        String encoding, boolean normalizeLineSeparator)
    {
        if (resource.isDirectory()) {
            throw new IllegalArgumentException("Resource type mismatch: "
                + resource.getURL());
        }

        if (string == null) {
            if (resource.exists()) {
                resource.delete();
            }
            return;
        }

        resource.getParentResource().mkdirs();

        try {
            IOUtils.writeString(resource.getOutputStream(), string, encoding,
                normalizeLineSeparator);
        } catch (ResourceNotFoundException ex) {
            throw new IORuntimeException(ex);
        }
    }


    public static String encodeAsResourceName(String str)
    {
        if (str == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        byte[] bytes;
        try {
            bytes = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        for (int i = 0; i < bytes.length; i++) {
            int ch;
            if (bytes[i] >= 0) {
                ch = bytes[i];
            } else {
                ch = bytes[i] + 256;
            }
            if (ch >= 0x30 && ch <= 0x39 || ch >= 0x41 && ch <= 0x5a
                || ch >= 0x61 && ch <= 0x7a || ch == 0x2d) {
                sb.append((char)ch);
            } else {
                sb.append('_');
                String hex = Integer.toHexString(ch);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
        }

        return sb.toString();
    }


    public static String decodeFromResourceName(String encoded)
    {
        if (encoded == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int n = encoded.length();
        for (int i = 0; i < n; i++) {
            char ch = encoded.charAt(i);
            if (ch == '_') {
                if (i < n - 2) {
                    char hi = encoded.charAt(++i);
                    char lo = encoded.charAt(++i);
                    int chr = Character.digit(hi, 16) * 16
                        + Character.digit(lo, 16);
                    out.write(chr);
                } else {
                    break;
                }
            } else {
                out.write(ch);
            }
        }

        try {
            return out.toString("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static void closeQuietly(InputStream in)
    {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ex) {
                ;
            }
        }
    }


    public static void closeQuietly(OutputStream out)
    {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
                ;
            }
        }
    }


    public static void closeQuietly(Reader r)
    {
        if (r != null) {
            try {
                r.close();
            } catch (IOException ex) {
                ;
            }
        }
    }


    public static void closeQuietly(Writer w)
    {
        if (w != null) {
            try {
                w.close();
            } catch (IOException ex) {
                ;
            }
        }
    }
}
