package org.seasar.kvasir.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Base64
{
    private static final byte[] TABLE
         = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
        .getBytes();

    private static int[]        reverse_ = new int[256];


    /*
     * static initializer
     */

    static
    {
        for (int i = 0; i < reverse_.length; i++) {
            reverse_[i] = -1;
        }
        for (int i = 0; i < TABLE.length; i++) {
            reverse_[(int)TABLE[i]] = i;
        }
        reverse_[64] = 0;
    }


    /*
     * static methods
     */
 
    public static byte[] encode(byte[] src)
    {
        int n = src.length / 3;
        int r = src.length - n * 3;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int s = 0;
            for (int i = 0; i < n; i++, s += 3) {
                out.write(TABLE[((src[s] & 0xfc) >> 2) & 0x3f]);
                out.write(TABLE[(((src[s] & 0x03) << 4) & 0x30)
                    | (((src[s + 1] & 0xf0) >> 4) & 0x0f)]);
                out.write(TABLE[(((src[s + 1] & 0x0f) << 2) & 0x3c)
                    | (((src[s + 2] & 0xc0) >> 6) & 0x03)]);
                out.write(TABLE[src[s + 2] & 0x3f]);
            }
            switch (r) {
            case 1:
                out.write(TABLE[((src[s] & 0xfc) >> 2) & 0x3f]);
                out.write(TABLE[((src[s] & 0x03) << 4) & 0x30]);
                out.write(TABLE[64]);
                out.write(TABLE[64]);
                break;
            case 2:
                out.write(TABLE[((src[s] & 0xfc) >> 2) & 0x3f]);
                out.write(TABLE[(((src[s] & 0x03) << 4) & 0x30)
                    |(((src[ s + 1] & 0xf0) >> 4) & 0x0f)]);
                out.write(TABLE[((src[s+1] & 0x0f) << 2) & 0x3c]);
                out.write(TABLE[64]);
                break;
            }
            out.flush();
        } catch (IOException ex) {
            ;
        }

        return out.toByteArray();
    }


    public static byte[] decode(byte[] src)
    {
        int n = src.length / 4 - 1;
        if (n < 0) {
            return new byte[0];
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int s = 0;
            int s0;
            int s1;
            int s2;
            int s3;
            for (int i = 0; i < n; i++) {
                s0 = reverse_[((int)src[s++] + 256) % 256];
                s1 = reverse_[((int)src[s++] + 256) % 256];
                s2 = reverse_[((int)src[s++] + 256) % 256];
                s3 = reverse_[((int)src[s++] + 256) % 256];
                out.write(((s0 << 2) & 0xfc) | ((s1 >> 4) & 0x03));
                out.write(((s1 << 4) & 0xf0) | ((s2 >> 2) & 0x0f));
                out.write(((s2 << 6) & 0xc0) | (s3 & 0x3f));
            }
            s0 = reverse_[((int)src[s++] + 256) % 256];
            s1 = reverse_[((int)src[s++] + 256) % 256];
            int ss0 = ((int)src[s++] + 256) % 256;
            s2 = reverse_[ss0];
            int ss1 = ((int)src[s++] + 256) % 256;
            s3 = reverse_[ss1];

            out.write(((s0 << 2) & 0xfc) | ((s1 >> 4) & 0x03));
            if (ss0 != TABLE[64]) {
                out.write(((s1 << 4) & 0xf0) | ((s2 >> 2) & 0x0f));
                if (ss1 != TABLE[64]) {
                    out.write(((s2 << 6) & 0xc0) | (s3 & 0x3f));
                }
            }
            out.flush();
        } catch (IOException ex) {
            ;
        }

        return out.toByteArray();
    }
}
