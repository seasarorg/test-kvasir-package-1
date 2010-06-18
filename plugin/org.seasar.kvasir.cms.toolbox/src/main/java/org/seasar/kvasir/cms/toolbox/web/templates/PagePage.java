package org.seasar.kvasir.cms.toolbox.web.templates;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.java.Base;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.util.MimeUtils;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.webapp.util.LocaleUtils;
import org.seasar.kvasir.webapp.util.ServletUtils;


//rootPackageName=org.seasar.kvasir.cms.toolbox.web

public class PagePage extends Base
{
    public void execute()
        throws Exception
    {
        if (!that.getPage().isAsFile()) {
            ServletUtils.setNoCache(response);
            return;
        }

        Content content = ((ContentAbility)that.getPage().getAbility(
            ContentAbility.class)).getLatestContent(LocaleUtils
            .findLocale(request));

        if (content != null) {
            long modifiedTime = content.getModifyDate().getTime();
            long ims = request.getDateHeader("If-Modified-Since");
            if (ims != -1 && modifiedTime < ims) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            } else {
                response.setDateHeader("Last-Modified", modifiedTime);
                response.setContentType(MimeUtils.constructContentType(content
                    .getMediaType(), content.getEncoding()));
                OutputStream os = response.getOutputStream();
                // In case of exception response.getOutputStream() throws,
                // keeps OutputStream not inlined.
                IOUtils.pipe(content.getBodyInputStream(), os, true, false);
            }
        }
        finish();
    }
}
