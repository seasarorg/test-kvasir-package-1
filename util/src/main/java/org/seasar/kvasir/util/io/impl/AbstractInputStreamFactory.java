package org.seasar.kvasir.util.io.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.InputStreamFactory;
import org.seasar.kvasir.util.io.IOUtils;


/**
 * InputStreamFactoryを実装するためのベースとして利用可能な抽象クラスです。
 * <p>このクラスの{@link #equals(Object)}メソッドは
 * {@link org.seasar.kvasir.util.io.InputStreamFactory}
 * の要求する条件を満たしています。
 * </p>
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
abstract public class AbstractInputStreamFactory
    implements InputStreamFactory
{
    private static final int        BUF_LENGTH = 4096;


    public boolean equals(Object o)
    {
        if (!(o instanceof InputStreamFactory)) {
            return false;
        }

        InputStreamFactory isf = (InputStreamFactory)o;

        InputStream in1 = getInputStream();
        InputStream in2 = isf.getInputStream();

        if (in1 == null) {
            if (in2 == null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (in2 == null) {
                return false;
            }
        }

        BufferedInputStream bis1;
        if (in1 instanceof BufferedInputStream) {
            bis1 = (BufferedInputStream)in1;
        } else {
            bis1 = new BufferedInputStream(in1);
        }
        BufferedInputStream bis2;
        if (in2 instanceof BufferedInputStream) {
            bis2 = (BufferedInputStream)in2;
        } else {
            bis2 = new BufferedInputStream(in2);
        }
        byte[] buf1 = new byte[BUF_LENGTH];
        byte[] buf2 = new byte[BUF_LENGTH];
        int len1;
        int len2;
        try {
            while ((len1 = bis1.read(buf1)) >= 0) {
                int offset = 0;
                int len = len1;
                while ((len2 = bis2.read(buf2, offset, len)) >= 0) {
                    if (offset + len2 < len1) {
                        offset += len2;
                        len = len1 - offset;
                    }
                }
                if (len2 < 0) {
                   return false;
                }
                for (int i = 0; i < len1; i++) {
                    if (buf1[i] != buf2[i]) {
                        return false;
                    }
                }
            }
            if (bis2.read() >= 0) {
                return false;
            }

            return true;
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(in1);
            IOUtils.closeQuietly(in2);
        }
    }
}
