package org.seasar.kvasir.cms.manage.manage.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipFile;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;

import org.seasar.cms.ymir.FormFile;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.ThrowableUtils;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.impl.ZipReaderResource;


public class ImportPagePage extends MainPanePage
{
    /*
     * set by framework
     */

    private PagePlugin pagePlugin_;

    private String name_;

    private FormFile archive_;

    private boolean overwrite_;

    private boolean replace_;


    /*
     * for presentation tier
     */

    /*
     * public scope methods
     */

    public Object do_execute()
    {
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (importPage()) {
                setNotes(new Notes()
                    .add(new Note("app.note.importPage.succeed")));
                updateMenu();
                return getRedirection("/list-page.do" + getPathname());
            }
        }

        return "/import-page.html";
    }


    /*
     * package scope methods
     */

    boolean importPage()
    {
        if ((name_ == null || name_.trim().length() == 0) && !overwrite_) {
            setNotes(new Notes().add(new Note(
                "app.error.importPage.nameIsEmpty")));
            return false;
        }
        if (archive_ == null || archive_.getSize() == 0) {
            setNotes(new Notes().add(new Note(
                "app.error.importPage.archiveIsEmpty")));
            return false;
        }

        File tempFile = null;
        ZipFile zipFile = null;
        try {
            tempFile = File.createTempFile("kvasir", null);
            IOUtils.pipe(archive_.getInputStream(), new FileOutputStream(
                tempFile));
            zipFile = new ZipFile(tempFile);
            if (overwrite_) {
                pagePlugin_.imports(getPage(), new ZipReaderResource(zipFile),
                    replace_);
            } else {
                pagePlugin_.imports(getPage(), name_.trim(),
                    new ZipReaderResource(zipFile));
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            setNotes(new Notes().add(new Note("app.error.importPage.failure",
                ThrowableUtils.getStackTraceString(ex))));
            return false;
        } catch (DuplicatePageException ex) {
            setNotes(new Notes().add(new Note(
                "app.error.importPage.pageAlreadyExists")));
            return false;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException ex2) {
                    ;
                }
            }
            if (tempFile != null) {
                if (!tempFile.delete()) {
                    tempFile.deleteOnExit();
                }
            }
        }
    }


    /*
     * for framework / presentation tier
     */

    public String getName()
    {
        return name_;
    }


    public void setName(String name)
    {
        name_ = name;
    }


    public void setArchive(FormFile archive)
    {
        archive_ = archive;
    }


    public void setOverwrite(String overwrite)
    {
        overwrite_ = PropertyUtils.valueOf(overwrite, false);
    }


    public void setReplace(String replace)
    {
        replace_ = PropertyUtils.valueOf(replace, false);
    }


    /*
     * for framework
     */

    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }
}
