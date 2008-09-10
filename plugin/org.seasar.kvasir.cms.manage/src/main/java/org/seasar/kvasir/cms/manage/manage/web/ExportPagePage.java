package org.seasar.kvasir.cms.manage.manage.web;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.util.ThrowableUtils;
import org.seasar.kvasir.util.io.impl.ZipWriterResource;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class ExportPagePage extends MainPanePage
{
    /*
     * set by framework
     */

    private PagePlugin pagePlugin_;

    private HttpServletResponse response_;


    /*
     * for presentation tier
     */

    /*
     * public scope methods
     */

    public Object do_execute()
    {
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (exportPage()) {
                return null;
            }
        }

        return getRedirection("/edit-page.do" + getPathname());
    }


    /*
     * package scope methods
     */

    boolean exportPage()
    {
        Page page = getPage();
        if (page == null) {
            return false;
        }

        response_.setContentType("application/zip");
        response_.setHeader("Content-Disposition", "attachment; filename="
            + page.getName() + ".zip");

        OutputStream os = null;
        ZipOutputStream zos = null;
        try {
            os = new BufferedOutputStream(response_.getOutputStream());
            zos = new ZipOutputStream(os);
            pagePlugin_.exports(new ZipWriterResource(zos, false), page);
            zos.close();
            zos = null;
            os = null;
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            setNotes(new Notes().add(new Note("app.error.exportPage.failure",
                ThrowableUtils.getStackTraceString(ex))));
            return false;
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                    os = null;
                } catch (IOException ex) {
                    ;
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    ;
                }
            }
        }
    }


    /*
     * for framework / presentation tier
     */

    /*
     * for framework
     */

    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    public void setResponse(HttpServletResponse response)
    {
        response_ = response;
    }
}
