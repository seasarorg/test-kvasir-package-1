package org.seasar.kvasir.cms.webdav.setting;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.seasar.kvasir.base.util.ArrayUtils;

import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Default;


public class WebdavPluginSettings
{
    private boolean webdavEnabled_ = true;

    private String defaultCharset_;

    private String internalCharset_;

    private Set<String> embedContentExtensionSet_ = new HashSet<String>();

    private Map<String, ContentExtension> contentExtensionMap_ = new LinkedHashMap<String, ContentExtension>();

    private boolean keepConcealedWhenCreated_;

    private Match[] matches_ = new Match[0];


    public boolean isWebdavEnabled()
    {
        return webdavEnabled_;
    }


    @Child
    @Default("true")
    public void setWebdavEnabled(boolean webdavEnabled)
    {
        webdavEnabled_ = webdavEnabled;
    }


    public String getDefaultCharset()
    {
        return defaultCharset_;
    }


    @Child
    public void setDefaultCharset(String defaultCharset)
    {
        defaultCharset_ = defaultCharset;
    }


    public String getInternalCharset()
    {
        return internalCharset_;
    }


    public String findInternalCharset(String file)
    {
        String internalCharset = null;
        Match match = findMatch(file);
        if (match != null) {
            internalCharset = match.getInternalCharset();
        }
        if (internalCharset == null) {
            internalCharset = internalCharset_;
        }
        return internalCharset;
    }


    @Child
    public void setInternalCharset(String internalCharset)
    {
        internalCharset_ = internalCharset;
    }


    public ContentExtension[] getContentExtensions()
    {
        return contentExtensionMap_.values().toArray(new ContentExtension[0]);
    }


    @Child
    public void addContentExtension(ContentExtension contentExtension)
    {
        contentExtensionMap_.put(contentExtension.getName(), contentExtension);
        if (contentExtension.isEmbedded()) {
            embedContentExtensionSet_.add(contentExtension.getName());
        }
    }


    public void setContentExtensions(ContentExtension[] contentExtensions)
    {
        contentExtensionMap_.clear();
        for (int i = 0; i < contentExtensions.length; i++) {
            addContentExtension(contentExtensions[i]);
        }
    }


    public boolean isEmbeddedContent(String file)
    {
        Boolean embedded = null;
        Match match = findMatch(file);
        if (match != null) {
            embedded = match.getEmbedded();
        }
        if (embedded == null) {
            String extension = getExtension(file);
            if (extension == null) {
                embedded = Boolean.FALSE;
            } else {
                embedded = Boolean.valueOf(embedContentExtensionSet_
                    .contains(extension));
            }
        }
        return embedded;
    }


    String getExtension(String file)
    {
        int dot = file.lastIndexOf('.');
        if (dot < 0) {
            return null;
        } else {
            return file.substring(dot + 1);
        }
    }


    public String findCharset(String file)
    {
        String charset = null;
        Match match = findMatch(file);
        if (match != null) {
            charset = match.getCharset();
        }
        if (charset == null) {
            ContentExtension contentExtension = getContentExtension(file);
            if (contentExtension != null) {
                charset = contentExtension.getCharset();
            }
            if (charset == null) {
                charset = defaultCharset_;
            }
        }
        return charset;
    }


    Match findMatch(String file)
    {
        for (int i = 0; i < matches_.length; i++) {
            if (matches_[i].isMatched(file)) {
                return matches_[i];
            }
        }
        return null;
    }


    public String getMimeType(String file)
    {
        String mimeType = null;
        ContentExtension contentExtension = getContentExtension(file);
        if (contentExtension != null) {
            mimeType = contentExtension.getMimeType();
        }
        return mimeType;
    }


    ContentExtension getContentExtension(String file)
    {
        String extension = getExtension(file);
        if (extension == null) {
            return null;
        } else {
            return contentExtensionMap_.get(extension);
        }
    }


    public boolean isKeepConcealedWhenCreated()
    {
        return keepConcealedWhenCreated_;
    }


    public boolean shouldKeepConcealedWhenCreated(String file)
    {
        Boolean keepConcealedWhenCreated = null;
        Match match = findMatch(file);
        if (match != null) {
            keepConcealedWhenCreated = match.getKeepConcealedWhenCreated();
        }
        if (keepConcealedWhenCreated == null) {
            keepConcealedWhenCreated = Boolean
                .valueOf(keepConcealedWhenCreated_);
        }
        return keepConcealedWhenCreated.booleanValue();
    }


    @Child
    @Default("false")
    public void setKeepConcealedWhenCreated(boolean setConcealedWhenCreated)
    {
        keepConcealedWhenCreated_ = setConcealedWhenCreated;
    }


    public Match[] getMatches()
    {
        return matches_;
    }


    @Child
    public void addMatch(Match match)
    {
        matches_ = ArrayUtils.add(matches_, match);
    }


    public void setMatches(Match[] matches)
    {
        matches_ = matches;
    }
}
