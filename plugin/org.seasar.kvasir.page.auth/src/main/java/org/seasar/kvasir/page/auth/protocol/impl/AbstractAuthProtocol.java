package org.seasar.kvasir.page.auth.protocol.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.seasar.kvasir.page.auth.protocol.AuthProtocol;



abstract public class AbstractAuthProtocol
    implements AuthProtocol
{
    public static final String  VOID_PASSWORD = "";
    public static final String  ANY_PASSWORD = "*";

    protected static final String   MD5 = "MD5";


    abstract protected boolean doAuthenticate(
        String password, String challenge);

    abstract protected String doGetInnerExpression(String password);

    abstract protected String doGlimpseChallenge(String password);


    /**
     * 指定された文字列のダイジェストを返します。
     *
     * @param algorithm ダイジェスト生成アルゴリズム。
     * @param str ダイジェストを生成したい文字列。
     * @return ダイジェスト。
     */
    protected static String digest(String algorithm, String str)
    {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("NoSuchAlgorithmException: "
            + algorithm);
        }

        byte[] digest = md.digest(str.getBytes());
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            int v = digest[i] & 0xff;
            if (v < 16) {
                res.append("0");
            }
            res.append(Integer.toString(v, 16).toLowerCase());
        }

        return res.toString();
    }


    /*
     * public scope methods
     */

    public boolean authenticate(String password, String challenge)
    {
        if (password.equals(VOID_PASSWORD)) {
            return false;
        } else if (password.equals(ANY_PASSWORD)) {
            return true;
        }

        return doAuthenticate(password, challenge);
    }


    public String getInnerExpression(String password)
    {
        if (password.equals(VOID_PASSWORD) || password.equals(ANY_PASSWORD)) {
            return password;
        } else {
            return doGetInnerExpression(password);
        }
    }


    public String glimpseChallenge(String password)
    {
        if (password.equals(VOID_PASSWORD) || password.equals(ANY_PASSWORD)) {
            return "";
        } else {
            return doGlimpseChallenge(password);
        }
    }
}
