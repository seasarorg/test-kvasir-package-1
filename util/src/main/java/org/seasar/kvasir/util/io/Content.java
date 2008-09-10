package org.seasar.kvasir.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.seasar.kvasir.util.MimeUtils;


/**
 * 種別、文字セット、本文からなるコンテントを表すクラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class Content
{
    private static final int BUF_SIZE = 4096;

    private static final String MEDIATYPE_XML = "text/xml";

    private static final String DEFAULT_CHARSET = "ISO-8859-1";

    private static final String MEDIATYPE_HTML = "text/html";

    private String mediaType_;

    private String charset_ = null;

    private InputStream in_;

    private String str_;


    public String getContentType()
    {
        if (mediaType_ == null) {
            return null;
        } else if (!isText()) {
            return mediaType_;
        } else {
            return mediaType_ + ";charset=" + getCharset();
        }
    }


    public void setContentType(String contentType)
    {
        if (contentType == null) {
            mediaType_ = null;
            charset_ = null;
            return;
        }

        contentType = contentType.trim();
        mediaType_ = contentType;
        charset_ = null;

        int semi = contentType.indexOf(";");
        if (semi >= 0) {
            mediaType_ = contentType.substring(0, semi).trim();
            String subpart = contentType.substring(semi + 1).trim();
            int equal = subpart.indexOf("=");
            if (equal >= 0
                && subpart.substring(0, equal).trim().equalsIgnoreCase(
                    "charset")) {
                charset_ = subpart.substring(equal + 1).trim();
            }
        }
    }


    public String getMediaType()
    {
        return mediaType_;
    }


    public void setMediaType(String mediaType)
    {
        if (mediaType == null) {
            mediaType_ = null;
            return;
        }

        mediaType_ = mediaType.trim();
    }


    public String getCharset()
    {
        if (charset_ == null) {
            if (in_ != null) {
                BufferedInputStream bis;
                if (in_ instanceof BufferedInputStream) {
                    bis = (BufferedInputStream)in_;
                } else {
                    bis = new BufferedInputStream(in_);
                    in_ = bis;
                }
                try {
                    if (MimeUtils.isXML(mediaType_)) {
                        charset_ = EncodingUtils.getXMLEncoding(bis);
                    }
                    if (charset_ == null) {
                        if (MimeUtils.isHTML(mediaType_)) {
                            charset_ = EncodingUtils.getHTMLEncoding(bis);
                        }
                    }
                } catch (IOException ex) {
                    throw new IORuntimeException(ex);
                }
            }
        }

        if (charset_ != null) {
            return charset_;
        } else {
            return DEFAULT_CHARSET;
        }
    }


    public void setCharset(String charset)
    {
        if (charset == null) {
            charset_ = charset;
            return;
        }

        charset_ = charset.trim();
    }


    public boolean isText()
    {
        return MimeUtils.isText(mediaType_);
    }


    public InputStream getInputStream()
    {
        InputStream in;
        if (in_ == null) {
            if (str_ == null) {
                return new ByteArrayInputStream(new byte[0]);
            }
            try {
                in = new ByteArrayInputStream(str_.getBytes(getCharset()));
            } catch (UnsupportedEncodingException ex) {
                throw new IORuntimeException(ex);
            }
        } else {
            in = in_;
            in_ = null;
        }

        return in;
    }


    public void setInputStream(InputStream in)
    {
        if (in_ != null) {
            try {
                in_.close();
            } catch (IOException ignore) {
            }
        }

        in_ = in;
        str_ = null;
    }


    public Reader getReader()
    {
        if (in_ == null) {
            if (str_ == null) {
                return new StringReader("");
            }
            return new StringReader(str_);
        } else {
            String charset = getCharset();
            Reader reader;
            try {
                reader = new InputStreamReader(in_, charset);
            } catch (UnsupportedEncodingException ex) {
                throw new IORuntimeException(ex);
            }
            in_ = null;
            return reader;
        }
    }


    public void setReader(Reader reader)
    {
        if (in_ != null) {
            try {
                in_.close();
            } catch (IOException ignore) {
            }
            in_ = null;
        }
        str_ = readerToString(reader);
    }


    public byte[] getBytes()
    {
        if (str_ != null) {
            try {
                return str_.getBytes(getCharset());
            } catch (UnsupportedEncodingException ex) {
                throw new IORuntimeException(ex);
            }
        } else if (in_ != null) {
            byte[] bytes = inputStreamToBytes(in_);
            in_ = null;
            return bytes;
        } else {
            return new byte[0];
        }
    }


    public void setBytes(byte[] bytes)
    {
        if (in_ != null) {
            try {
                in_.close();
            } catch (IOException ignore) {
            }
            in_ = null;
        }
        in_ = new ByteArrayInputStream(bytes);
        str_ = null;
    }


    public String getString()
    {
        if (str_ != null) {
            return str_;
        }

        Reader reader = getReader();
        if (reader == null) {
            return "";
        }

        return readerToString(reader);
    }


    public void setString(String str)
    {
        if (in_ != null) {
            try {
                in_.close();
            } catch (IOException ignore) {
            }
        }

        in_ = null;
        str_ = str;
    }


    public void close()
    {
        if (in_ != null) {
            try {
                in_.close();
            } catch (IOException ignore) {
            }
        }

        in_ = null;
        str_ = null;
    }


    private byte[] inputStreamToBytes(InputStream in)
    {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        try {
            try {
                byte[] buf = new byte[BUF_SIZE];
                int len;
                while ((len = bis.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                bos.flush();
                return baos.toByteArray();
            } finally {
                try {
                    bis.close();
                } finally {
                    bos.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    private String readerToString(Reader reader)
    {
        BufferedReader in = new BufferedReader(reader);
        StringWriter sw = new StringWriter();
        BufferedWriter out = new BufferedWriter(sw);
        try {
            try {
                char[] buf = new char[BUF_SIZE];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                out.flush();
                return sw.toString();
            } finally {
                try {
                    in.close();
                } finally {
                    out.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
