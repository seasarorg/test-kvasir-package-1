package org.seasar.kvasir.cms.manage.manage.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.seasar.cms.ymir.FormFile;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.manage.tab.impl.PageTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentMold;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class EditContentPage extends MainPanePage
{
    /*
     * set by framework
     */

    private Boolean asFile_;

    private FormFile body_;

    private String bodyString_;

    private String command_;

    private String encoding_;

    private String mediaType_;

    private int revision_;

    private String variant_ = Page.VARIANT_DEFAULT;

    /*
     * for presentation tier
     */

    private String[] definedVariants_;

    private boolean exists_;

    private int earliestRevisionNumber_;

    private int latestRevisionNumber_;

    private boolean undefinedVariant_;

    private final KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    /*
     * public scope methods
     */

    public Object do_execute()
    {
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            try {
                updateContent();
                setNotes(new Notes().add(new Note(
                    "app.note.editContent.succeed")));
                Map<String, String[]> paramMap = new HashMap<String, String[]>();
                paramMap.put("variant", new String[] { variant_ });
                return getRedirection("/edit-content.do" + getPathname(),
                    paramMap);
            } catch (IOException ex) {
                log_.warn("Can't read body file: " + body_, ex);
                setNotes(new Notes().add(new Note("app.error.generic", ex
                    .toString())));
            }
        }

        return render();
    }


    /*
     * private scope methods
     */

    private void updateContent()
        throws IOException
    {
        Page page = getPage();
        ContentAbility content = page.getAbility(ContentAbility.class);

        if ("clear".equals(command_)) {
            content.clearContents(variant_);
        } else if ("clearAll".equals(command_)) {
            content.clearAllContents();
        } else {
            ContentMold mold = new ContentMold();
            boolean moldIsSet = false;

            String encoding = null;
            if (encoding_ != null && encoding_.length() > 0) {
                encoding = encoding_;
            } else if (body_ != null || bodyString_ != null) {
                encoding = "UTF-8";
            }
            if (encoding != null) {
                mold.setEncoding(encoding);
                moldIsSet = true;
            }

            String mediaType = null;
            if (mediaType_ != null && mediaType_.length() > 0) {
                mediaType = mediaType_;
            } else if (body_ != null) {
                mediaType = body_.getContentType();
                if (mediaType == null) {
                    mediaType = "application/octet-stream";
                }
            } else if (bodyString_ != null) {
                mediaType = "text/plain";
            }
            if (mediaType != null) {
                mold.setMediaType(mediaType);
                moldIsSet = true;
                if (!mediaType.startsWith("text/")) {
                    asFile_ = Boolean.TRUE;
                }
            }

            if (bodyString_ != null) {
                mold.setBodyString(bodyString_);
                moldIsSet = true;
            } else if (body_ != null && body_.getSize() > 0) {
                mold.setBodyInputStream(body_.getInputStream());
                moldIsSet = true;
            }

            if (moldIsSet) {
                if ("set".equals(command_)) {
                    content.setContent(variant_, mold);
                } else {
                    content.updateContent(variant_, mold);
                }
            }
        }
        page.touch();
    }


    private String render()
    {
        enableTab(PageTab.NAME_CONTENT);
        enableLocationBar(true);

        Page page = getPage();
        ContentAbility contentAbility = page.getAbility(ContentAbility.class);
        Content content = (revision_ > 0 ? contentAbility.getContent(variant_,
            revision_) : contentAbility.getLatestContent(variant_));

        definedVariants_ = contentAbility.getVariants();
        undefinedVariant_ = true;
        for (int i = 0; i < definedVariants_.length; i++) {
            if (variant_.equals(definedVariants_[i])) {
                undefinedVariant_ = false;
                break;
            }
        }

        exists_ = (content != null);
        earliestRevisionNumber_ = contentAbility
            .getEarliestRevisionNumber(variant_);
        latestRevisionNumber_ = contentAbility
            .getLatestRevisionNumber(variant_);

        if (asFile_ == null) {
            asFile_ = Boolean.valueOf(page.isAsFile());
        }
        if (mediaType_ == null) {
            mediaType_ = (exists_ ? content.getMediaType() : "text/plain");
        }
        if (encoding_ == null) {
            encoding_ = (exists_ ? content.getEncoding() : "");
        }
        if (bodyString_ == null) {
            if (mediaType_.startsWith("text/")) {
                bodyString_ = (exists_ ? content.getBodyString() : "");
            }
        }

        return "/edit-content.html";
    }


    /*
     * for framework / presentation tier
     */

    public Boolean getAsFile()
    {
        return asFile_;
    }


    public void setAsFile(Boolean asFile)
    {
        asFile_ = asFile;
    }


    public void setBody(FormFile body)
    {
        body_ = body;
    }


    public String getBodyString()
    {
        return bodyString_;
    }


    public void setBodyString(String bodyString)
    {
        bodyString_ = bodyString;
    }


    public String getCommand()
    {
        return command_;
    }


    public void setCommand(String command)
    {
        command_ = command;
    }


    public String getEncoding()
    {
        return encoding_;
    }


    public void setEncoding(String encoding)
    {
        encoding_ = encoding;
    }


    public String getMediaType()
    {
        return mediaType_;
    }


    public void setMediaType(String mediaType)
    {
        mediaType_ = mediaType.trim();
    }


    public int getRevision()
    {
        return revision_;
    }


    public void setRevision(int revision)
    {
        revision_ = revision;
    }


    public String getVariant()
    {
        return variant_;
    }


    public void setVariant(String variant)
    {
        variant_ = variant;
    }


    public void setVariants(String[] variants)
    {
        if (variants.length == 1 || !variants[0].equals(VARIANT_UNDEFINED)) {
            variant_ = variants[0];
        } else {
            variant_ = variants[1];
        }
    }


    /*
     * for presentation tier
     */

    public boolean isExists()
    {
        return exists_;
    }


    public String[] getDefinedVariants()
    {
        return definedVariants_;
    }


    public int getEarliestRevisionNumber()
    {
        return earliestRevisionNumber_;
    }


    public int getLatestRevisionNumber()
    {
        return latestRevisionNumber_;
    }


    public boolean isUndefinedVariant()
    {
        return undefinedVariant_;
    }
}
