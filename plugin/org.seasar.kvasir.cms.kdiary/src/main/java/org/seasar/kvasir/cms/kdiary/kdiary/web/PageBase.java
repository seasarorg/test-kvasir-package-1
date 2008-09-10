package org.seasar.kvasir.cms.kdiary.kdiary.web;

import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.DIR_ARTICLE;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.DIR_THEME;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.LIMIT_INFINITY;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_AUTHORMAILADDRESS;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_AUTHORNAME;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_COMMENTANCHOR;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_CSS;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_FOOTER;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_HEADER;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_JAVADATEFORMAT;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_PARAGRAPHANCHOR;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_SHOWCOMMENT;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.PROP_TITLE;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cms.ymir.Request;
import org.seasar.cms.ymir.extension.annotation.In;
import org.seasar.cms.ymir.extension.annotation.Out;
import org.seasar.cms.ymir.scope.impl.SessionScope;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.kdiary.kdiary.Globals;
import org.seasar.kvasir.cms.kdiary.kdiary.KdiaryDate;
import org.seasar.kvasir.cms.kdiary.kdiary.dao.CommentDao;
import org.seasar.kvasir.cms.kdiary.kdiary.dto.CommentDto;
import org.seasar.kvasir.cms.kdiary.kdiary.dto.CommonDto;
import org.seasar.kvasir.cms.kdiary.kdiary.dto.EntryDto;
import org.seasar.kvasir.cms.kdiary.kdiary.dxo.CommentDxo;
import org.seasar.kvasir.cms.pop.util.PopUtils;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.Directory;
import org.seasar.kvasir.page.type.DirectoryMold;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.el.VariableResolver;

import net.skirnir.freyja.render.Notes;


abstract public class PageBase
{
    protected static final String PASSTHROUGH = "passthrough:";

    protected CommentDao commentDao_;

    protected CommentDxo commentDxo_;

    private AuthPlugin authPlugin_;

    private PageRequest pageRequest_;

    private Request ymirRequest_;

    private PageAlfr pageAlfr_;

    private Plugin<?> plugin_;

    private CommonDto common_;

    private HttpServletRequest request_;

    private HttpServletRequest httpRequest_;

    private HttpServletResponse httpResponse_;

    private Notes notes_;


    public void setCommentDao(CommentDao commentDao)
    {
        commentDao_ = commentDao;
    }


    public void setCommentDxo(CommentDxo commentDxo)
    {
        commentDxo_ = commentDxo;
    }


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    public String _default()
    {
        // 処理対象外のHTTPメソッドが指定された場合は何もしない。
        return null;
    }


    public User getCurrentActor()
    {
        return authPlugin_.getCurrentActor();
    }


    public PageRequest getPageRequest()
    {
        return pageRequest_;
    }


    public void setPageRequest(PageRequest pageRequest)
    {
        pageRequest_ = pageRequest;
    }


    public Request getYmirRequest()
    {
        return ymirRequest_;
    }


    public void setYmirRequest(Request ymirRequest)
    {
        ymirRequest_ = ymirRequest;
    }


    public PageAlfr getPageAlfr()
    {
        return pageAlfr_;
    }


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    @Out(SessionScope.class)
    public Notes getNotes()
    {
        return notes_;
    }


    @In(SessionScope.class)
    public void setNotes(Notes notes)
    {
        notes_ = notes;
    }


    public String toURL(String path)
    {
        if (path == null) {
            return null;
        } else if (path.length() == 0 || path.startsWith("/")) {
            return pageRequest_.getContextPath() + path;
        } else if (path.startsWith("@") || path.startsWith("%")) {
            return pageRequest_.getContextPath()
                + pageRequest_.getMy().getNearestPage().getLordPathname()
                + path.substring(1/*= "@".length */);
        } else {
            return path;
        }
    }


    public Page getGardRootPage()
    {
        return pageRequest_.getMy().getGardRootPage();
    }


    public String getGardRootPathname()
    {
        return getGardRootPage().getPathname();
    }


    public PropertyAbility getPropertyAbility()
    {
        return getGardRootPage().getAbility(PropertyAbility.class);
    }


    public String getProperty(String name)
    {
        return getPropertyAbility().getProperty(name);
    }


    public int getProperty(String name, int defaultValue)
    {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignore) {
            }
        }
        return defaultValue;
    }


    protected String getProperty(String name, String defaultValue)
    {
        String value = getProperty(name);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }


    public Directory getArticleDirectory()
    {
        return (Directory)getGardRootPage().getChild(DIR_ARTICLE);
    }


    public Directory getThemeDirectory()
    {
        return (Directory)getGardRootPage().getChild(DIR_THEME);
    }


    public Directory getMonthDirectory(KdiaryDate date, boolean create)
    {
        Directory articleDirectory = getArticleDirectory();
        String name = date.getMonthDirectoryName();
        Directory dir = (Directory)articleDirectory.getChild(name);
        if (dir == null && create) {
            try {
                dir = (Directory)articleDirectory
                    .createChild(new DirectoryMold().setName(name));
                dir.touch();
            } catch (DuplicatePageException ex) {
                dir = (Directory)articleDirectory.getChild(name);
            }
        }
        return dir;
    }


    public Page getArticle(KdiaryDate date)
    {
        if (date == null) {
            return null;
        }
        Directory monthDir = getArticleDirectory().getChild(Directory.class,
            date.getMonthDirectoryName());
        if (monthDir == null) {
            return null;
        }
        return monthDir.getChild(date.getArticleName());
    }


    public Page getPage()
    {
        return pageRequest_.getMy().getPage();
    }


    public String getPathname()
    {
        return pageRequest_.getMy().getPathname();
    }


    public Locale getLocale()
    {
        return pageRequest_.getLocale();
    }


    public Plugin<?> getPlugin()
    {
        return plugin_;
    }


    public void setPlugin(Plugin<?> plugin)
    {
        plugin_ = plugin;
    }


    public String getLocalizedMessage(String key)
    {
        String message = plugin_.getProperty(key, getLocale());
        if (message == null) {
            message = "!" + key + "!";
        }
        return message;
    }


    public String getLocalizedMessage(String key, Object arg)
    {
        return getLocalizedMessage(key, new Object[] { arg });
    }


    public String getLocalizedMessage(String key, Object[] args)
    {
        return MessageFormat.format(getLocalizedMessage(key), args);
    }


    EntryDto toEntry(Page article)
    {
        return toEntry(article, LIMIT_INFINITY, LIMIT_INFINITY);
    }


    EntryDto toEntry(Page article, int commentLimit, int commentBodyLength)
    {
        if (article == null) {
            return null;
        }
        KdiaryDate date = new KdiaryDate(article);
        CommentDto[] rawComments = commentDxo_.convert(commentDao_
            .selectAllByPageId(article.getId()));
        CommentDto[] comments = trimArray(rawComments, commentLimit);

        for (int i = 0; i < comments.length; i++) {
            int num = i + 1;
            comments[i].setAnchor(num < 10 ? "c0" + num : "c" + num);
            comments[i].setDateFormat(new SimpleDateFormat(
                Globals.COMMENT_DATEFORMAT));
            comments[i].setBodyLength(commentBodyLength);
        }

        return new EntryDto(toURL(article.getPathname()), article.getName(),
            getLocalizedBodyHTMLString(article), rawComments.length, comments,
            date.format(getJavaDateFormat()), getLocalizedTitle(article));
    }


    @SuppressWarnings("unchecked")
    <T> T[] trimArray(T[] array, int limit)
    {
        if (limit == LIMIT_INFINITY || limit > array.length) {
            return array;
        } else {
            List<T> list = new ArrayList<T>();
            for (int i = array.length - limit; i < array.length; i++) {
                list.add(array[i]);
            }
            return list.toArray((T[])Array.newInstance(array.getClass()
                .getComponentType(), limit));
        }
    }


    String getLocalizedBodyHTMLString(Page article)
    {
        if (article == null) {
            return "";
        }

        Content content = article.getAbility(ContentAbility.class)
            .getLatestContent(getLocale());
        String body = (content != null ? content
            .getBodyHTMLString(VariableResolver.EMPTY) : "");

        String url = toURL(article.getPathname());
        String anchor = getProperty(PROP_PARAGRAPHANCHOR);
        StringBuilder sb = new StringBuilder();
        int pre = 0;
        int idx;
        int sectionCount = 1;
        while ((idx = body.indexOf("<h3>", pre)) >= 0) {
            sb.append(body.substring(pre, idx));
            sb.append("<h3><a");
            String name = (sectionCount < 10) ? "p0" + sectionCount : "p"
                + sectionCount;
            sectionCount++;
            if (this instanceof DayPage) {
                sb.append(" name=\"").append(name).append("\"");
            }
            sb.append(" href=\"").append(url).append("#").append(name).append(
                "\">").append(anchor).append("</a> ");
            pre = idx + "<h3>".length();
        }
        sb.append(body.substring(pre));

        return sb.toString();
    }


    String getLocalizedTitle(Page article)
    {
        if (article == null) {
            return "";
        }
        return article.getAbility(PropertyAbility.class).getProperty(
            PROP_TITLE, getLocale());
    }


    String getBodyString(Page article)
    {
        if (article == null) {
            return "";
        }
        return article.getAbility(ContentAbility.class).getLatestContent(
            Page.VARIANT_DEFAULT).getBodyString();
    }


    String getTitle(Page article)
    {
        if (article == null) {
            return "";
        }
        return article.getAbility(PropertyAbility.class)
            .getProperty(PROP_TITLE);
    }


    String getJavaDateFormat()
    {
        return getGardRootPage().getAbility(PropertyAbility.class).getProperty(
            PROP_JAVADATEFORMAT);
    }


    public CommonDto getCommon()
    {
        return common_;
    }


    protected CommonDto createCommon()
    {
        Page gard = getGardRootPage();
        Locale locale = getLocale();
        PropertyAbility property = gard.getAbility(PropertyAbility.class);
        String title = property.getProperty(PROP_TITLE, locale);
        if (title == null) {
            title = gard.getName();
        }
        String header = property.getProperty(PROP_HEADER);
        if (header == null) {
            header = "";
        }
        header = PopUtils.evaluateText(gard, httpRequest_, httpResponse_,
            "<%=", "%>", header, "!!ERROR!!", true);
        String footer = property.getProperty(PROP_FOOTER);
        if (footer == null) {
            footer = "";
        }
        footer = PopUtils.evaluateText(gard, httpRequest_, httpResponse_,
            pageRequest_, "<%=", "%>", footer, "!!ERROR!!", true);

        return new CommonDto(property.getProperty(PROP_AUTHORNAME, locale),
            property.getProperty(PROP_COMMENTANCHOR), toURL(property
                .getProperty(PROP_CSS)), footer, header, property
                .getProperty(PROP_AUTHORMAILADDRESS), PropertyUtils.valueOf(
                property.getProperty(PROP_SHOWCOMMENT), false), title);
    }


    public void _render()
    {
        common_ = createCommon();
    }


    public void setHttpRequest(HttpServletRequest httpRequest)
    {
        httpRequest_ = httpRequest;
    }


    public void setHttpResponse(HttpServletResponse httpResponse)
    {
        httpResponse_ = httpResponse;
    }


    public KdiaryDate getToday()
    {
        return new KdiaryDate(new Date());
    }


    protected boolean isEmpty(Object obj)
    {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return (((String)obj).trim().length() == 0);
        } else {
            return false;
        }
    }
}
