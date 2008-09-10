package org.seasar.kvasir.page.auth.protocol.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;


public class TimeSynchronousAuthProtocol extends AbstractAuthProtocol
{
    public static final int             ERROR_RANGE = 4;


    private final KvasirLog     log_ = KvasirLogFactory.getLog(getClass());


    /*
     * static methods
     */

    protected static String[] getPassCodes(int errorRange)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        if ((errorRange % 2) == 1) {
            errorRange++;
        }

        String[] passCodes = new String[errorRange + 1];

        cal.add(Calendar.MINUTE, (errorRange / 2));
        for (int i = 0; i <= errorRange; i++) {
            passCodes[i] = sdf.format(cal.getTime());
            cal.add(Calendar.MINUTE, -1);
        }

        return passCodes;
    }


    /*
     * protected scope methods
     */

    protected boolean doAuthenticate(String password, String challenge)
    {
        String[] passCodes = getPassCodes(ERROR_RANGE);
        if (log_.isDebugEnabled()) {
            log_.debug("CHALLENGE = " + challenge);
        }
        for (int i = 0; i < passCodes.length; i++) {
            if (log_.isDebugEnabled()) {
                log_.debug("PASSCODE = " + passCodes[i]);
                log_.debug("(CORRECT ANSWER = "
                    + password + passCodes[i] + ")");
            }
            if (digest(MD5, password + passCodes[i]).equals(challenge)) {
                return true;
            }
        }
        return false;
    }


    protected String doGetInnerExpression(String password)
    {
        return password;
    }


    protected String doGlimpseChallenge(String password)
    {
        String[] passCodes = getPassCodes(0);
        return digest(MD5, password + passCodes[0]);
    }
}
