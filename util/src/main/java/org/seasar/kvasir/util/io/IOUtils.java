package org.seasar.kvasir.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.seasar.kvasir.util.StringUtils;


/**
 * @author YOKOTA Takehiko
 */
public class IOUtils
{
    private static final int BUF_SIZE = 32768;


    private IOUtils()
    {
    }


    public static void pipe(InputStream in, OutputStream out)
        throws IOException
    {
        pipe(in, out, true, true);
    }


    public static void pipe(InputStream in, OutputStream out,
        boolean closeInputStream, boolean closeOutputStream)
        throws IOException
    {
        BufferedInputStream bis;
        if (in instanceof BufferedInputStream) {
            bis = (BufferedInputStream)in;
        } else {
            bis = new BufferedInputStream(in);
        }
        BufferedOutputStream bos;
        if (out instanceof BufferedOutputStream) {
            bos = (BufferedOutputStream)out;
        } else {
            bos = new BufferedOutputStream(out);
        }
        try {
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        } finally {
            if (closeInputStream) {
                closeQuietly(bis);
            }
            if (closeOutputStream) {
                closeQuietly(bos);
            }
        }
    }


    public static String readString(InputStream in, String encoding,
        boolean normalizeLineSeparator)
    {
        if (in == null) {
            return null;
        }
        try {
            return readString(new InputStreamReader(in, encoding),
                normalizeLineSeparator);
        } catch (UnsupportedEncodingException ex) {
            throw new IORuntimeException(ex);
        }
    }


    public static String readString(Reader reader,
        boolean normalizeLineSeparator)
    {
        if (reader == null) {
            return null;
        }
        BufferedReader br;
        if (reader instanceof BufferedReader) {
            br = (BufferedReader)reader;
        } else {
            br = new BufferedReader(reader);
        }
        String str;
        try {
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            char[] buf = new char[BUF_SIZE];
            int len;
            while ((len = br.read(buf)) >= 0) {
                bw.write(buf, 0, len);
            }
            bw.close();
            str = sw.toString();
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            closeQuietly(br);
        }
        if (normalizeLineSeparator) {
            return StringUtils.normalizeLineSeparator(str);
        } else {
            return str;
        }
    }


    public static byte[] readBytes(InputStream in)
    {
        if (in == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            pipe(in, baos);
            baos.close();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
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


    public static void writeString(OutputStream os, String string,
        String encoding, boolean normalizeLineSeparator)
    {
        writeString(os, string, encoding, normalizeLineSeparator, true);
    }


    public static void writeString(OutputStream os, String string,
        String encoding, boolean normalizeLineSeparator, boolean close)
    {
        try {
            writeString(new OutputStreamWriter(os, encoding), string,
                normalizeLineSeparator, close);
        } catch (UnsupportedEncodingException ex) {
            throw new IORuntimeException(ex);
        }
    }


    public static void writeString(Writer writer, String string,
        boolean normalizeLineSeparator)
    {
        writeString(writer, string, normalizeLineSeparator, true);
    }


    public static void writeString(Writer writer, String string,
        boolean normalizeLineSeparator, boolean close)
    {
        try {
            BufferedWriter bw = null;
            if (writer instanceof BufferedWriter) {
                bw = (BufferedWriter)writer;
            } else {
                bw = new BufferedWriter(writer);
            }
            bw.write(normalizeLineSeparator ? StringUtils
                .normalizeLineSeparator(string) : string);
            bw.flush();
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            if (close) {
                closeQuietly(writer);
            }
        }
    }


    public static void writeBytes(OutputStream out, byte[] bytes)
    {
        writeBytes(out, bytes, true);
    }


    public static void writeBytes(OutputStream out, byte[] bytes, boolean close)
    {
        BufferedOutputStream bos;
        if (out instanceof BufferedOutputStream) {
            bos = (BufferedOutputStream)out;
        } else {
            bos = new BufferedOutputStream(out);
        }
        try {
            bos.write(bytes);
            bos.flush();
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            if (close) {
                closeQuietly(out);
            }
        }
    }
}
