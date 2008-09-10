package org.seasar.kvasir.util.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;


/**
 * @author YOKOTA Takehiko
 */
public class FileUtils
{
    private FileUtils()
    {
    }


    public static String toNativePath(String path)
    {
        if (path == null) {
            return null;
        }
        return path.replace('/', File.separatorChar);
    }


    /**
     * 指定された抽象パスをネイティブパスに変換します。
     * 
     * @param baseDirectory 相対パス指定を解釈するためのベースとするディレクトリのパス。
     * nullを指定することもできます。
     * このパスはネイティブパスである必要があります。
     * @param path 抽象パス。
     * nullや相対パスを指定することもできます。
     * nullを指定した場合はnullを返します。
     * @return 変換されたネイティブパス。
     */
    public static String toNativePath(String baseDirectory, String path)
    {
        if (path == null) {
            return null;
        }

        path = path.replace('/', File.separatorChar);

        if (path.length() >= 2 && path.charAt(1) == ':'
        || path.startsWith("\\")) {
            // Windows環境のため。
            return path;
        } else if (path.startsWith(File.separator)) {
            // 共通。
            return path;
        }

        if (baseDirectory == null) {
            // baseDirectoryが指定されていない場合は相対パスから絶対パスを
            // 構築できない。
            return null;
        } else if (baseDirectory.endsWith(File.separator)) {
            return baseDirectory + path;
        } else {
            return baseDirectory + File.separator + path;
        }
    }


    public static String toAbstractPath(String path)
    {
        if (path == null || File.separatorChar == '/') {
            return path;
        }
        return path.replace(File.separatorChar, '/');
    }


    public static String encodeAsFileName(String str)
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
            if (ch >= 0x30 && ch <= 0x39
            || ch >= 0x41 && ch <= 0x5a || ch >= 0x61 && ch <= 0x7a
            || ch == 0x2d) {
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


    public static String decodeFromFileName(String encoded)
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
}
