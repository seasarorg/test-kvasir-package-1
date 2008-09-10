package org.seasar.kvasir.page.auth.protocol.impl;




public class PasswordAuthProtocol extends AbstractAuthProtocol
{
    /*
     * protected scope methods
     */

    protected boolean doAuthenticate(String password, String challenge)
    {
        return digest(MD5, challenge).equals(password);
    }


    protected String doGetInnerExpression(String password)
    {
        return digest(MD5, password);
    }


    protected String doGlimpseChallenge(String password)
    {
        throw new UnsupportedOperationException();
    }
}
