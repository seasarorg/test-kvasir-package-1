package org.seasar.kvasir.webapp.servlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


public class HttpServletResponseGrabber extends HttpServletResponseWrapper
{
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String PREFIX_CHARSET = "charset=";

    private ServletOutputStreamImpl outputStream_;

    private StringWriter stringWriter_;

    private PrintWriter writer_;

    private String charset_ = DEFAULT_CHARSET;


    public HttpServletResponseGrabber(HttpServletResponse response)
    {
        super(response);
    }


    @Override
    public ServletOutputStream getOutputStream()
        throws IOException
    {
        if (writer_ != null) {
            throw new IllegalStateException();
        }
        if (outputStream_ == null) {
            outputStream_ = new ServletOutputStreamImpl();
        }
        return outputStream_;
    }


    @Override
    public PrintWriter getWriter()
        throws IOException
    {
        if (outputStream_ != null) {
            throw new IllegalStateException();
        }
        if (writer_ == null) {
            stringWriter_ = new StringWriter();
            writer_ = new PrintWriter(stringWriter_);
        }
        return writer_;
    }


    @Override
    public void setCharacterEncoding(String charset)
    {
        super.setCharacterEncoding(charset);
        charset_ = charset;
    }


    @Override
    public void setContentType(String type)
    {
        super.setContentType(type);
        int semi = type.indexOf(';');
        if (semi >= 0) {
            String param = type.substring(semi + 1).trim();
            if (param.startsWith(PREFIX_CHARSET)) {
                charset_ = param.substring(PREFIX_CHARSET.length()).trim();
            }
        }
    }


    public String getContentString()
    {
        if (outputStream_ != null) {
            byte[] bytes = outputStream_.getBytes();
            try {
                return new String(bytes, charset_);
            } catch (UnsupportedEncodingException ex) {
                return new String(bytes);
            }
        } else if (writer_ != null) {
            writer_.flush();
            return stringWriter_.toString();
        } else {
            return "";
        }
    }


    public static class ServletOutputStreamImpl extends ServletOutputStream
    {
        private BufferedOutputStream bos_;

        private ByteArrayOutputStream baos_;


        public ServletOutputStreamImpl()
        {
            baos_ = new ByteArrayOutputStream();
            bos_ = new BufferedOutputStream(baos_);
        }


        @Override
        public void write(int b)
            throws IOException
        {
            bos_.write(b);
        }


        @Override
        public void flush()
            throws IOException
        {
            bos_.flush();
        }


        public byte[] getBytes()
        {
            try {
                bos_.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return baos_.toByteArray();
        }
    }
}
