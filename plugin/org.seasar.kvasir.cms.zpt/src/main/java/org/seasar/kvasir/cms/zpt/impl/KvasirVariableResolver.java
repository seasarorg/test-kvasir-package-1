package org.seasar.kvasir.cms.zpt.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.impl.PageDispatchImpl;
import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.impl.VariableResolverImpl;
import net.skirnir.freyja.webapp.ServletVariableResolver;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirVariableResolver extends VariableResolverImpl
{
    public static final String VARNAME_PAGEREQUEST = "pageRequest";

    public static final String VARNAME_THAT = "that";

    public static final String VARNAME_MY = "my";

    public static final String VARNAME_THATLORD = "@";

    public static final String VARNAME_MYLORD = "%";

    public static final String VARNAME_ROOT = "root";

    public static final String VARNAME_KVASIR = "kvasir";

    private Kvasir kvasir_;

    private PagePlugin pagePlugin_;

    private PageAlfr pageAlfr_;

    private VariableResolver parent_;

    private HttpServletRequest request_;

    private PageRequest pageRequest_;

    private Page rootPage_;

    private boolean myGenerated_;

    private PageDispatch my_;


    public KvasirVariableResolver(Kvasir kvasir, PagePlugin pagePlugin,
        PageAlfr pageAlfr, VariableResolver parent)
    {
        kvasir_ = kvasir;
        pagePlugin_ = pagePlugin;
        pageAlfr_ = pageAlfr;
        parent_ = parent;
    }


    /*
     * VariableResolver
     */

    public Object getVariable(TemplateContext context, String name)
    {
        if (super.containsVariable(name)) {
            return super.getVariable(context, name);
        } else if (VARNAME_KVASIR.equals(name)) {
            return kvasir_;
        } else if (VARNAME_ROOT.equals(name)) {
            return getRootPage();
        } else if (VARNAME_THATLORD.equals(name)) {
            return getLord();
        } else if (VARNAME_MYLORD.equals(name)) {
            return getMyLord(context);
        } else if (VARNAME_PAGEREQUEST.equals(name)) {
            return getPageRequest();
        } else if (VARNAME_MY.equals(name)) {
            return getMy(context);
        } else if (VARNAME_THAT.equals(name)) {
            return getThat();
        } else if (parent_ != null) {
            return parent_.getVariable(context, name);
        }
        return null;
    }


    @Override
    public boolean containsVariable(String name)
    {
        if (super.containsVariable(name)) {
            return true;
        } else if (VARNAME_KVASIR.equals(name)) {
            return true;
        } else if (VARNAME_ROOT.equals(name)) {
            return true;
        } else if (VARNAME_THATLORD.equals(name)) {
            return true;
        } else if (VARNAME_MYLORD.equals(name)) {
            return true;
        } else if (VARNAME_PAGEREQUEST.equals(name)) {
            return true;
        } else if (VARNAME_MY.equals(name)) {
            return true;
        } else if (VARNAME_THAT.equals(name)) {
            return true;
        } else if (parent_ != null) {
            if (parent_.containsVariable(name)) {
                return true;
            }
        }
        return false;
    }


    public String[] getVariableNames()
    {
        Set<String> nameSet = new HashSet<String>();
        nameSet.addAll(Arrays.asList(super.getVariableNames()));
        nameSet.add(VARNAME_KVASIR);
        nameSet.add(VARNAME_ROOT);
        nameSet.add(VARNAME_THATLORD);
        nameSet.add(VARNAME_MYLORD);
        nameSet.add(VARNAME_PAGEREQUEST);
        nameSet.add(VARNAME_MY);
        nameSet.add(VARNAME_THAT);
        if (parent_ != null) {
            nameSet.addAll(Arrays.asList(parent_.getVariableNames()));
        }
        return (String[])nameSet.toArray(new String[0]);
    }


    public Entry getVariableEntry(TemplateContext context, String name)
    {
        Object value = null;
        Class<?> type = null;
        Entry entry = super.getVariableEntry(context, name);
        if (entry != null) {
            return entry;
        } else if (VARNAME_KVASIR.equals(name)) {
            value = kvasir_;
            type = Kvasir.class;
        } else if (VARNAME_ROOT.equals(name)) {
            value = getRootPage();
            type = Page.class;
        } else if (VARNAME_THATLORD.equals(name)) {
            value = getLord();
            type = Page.class;
        } else if (VARNAME_MYLORD.equals(name)) {
            value = getMyLord(context);
            type = Page.class;
        } else if (VARNAME_PAGEREQUEST.equals(name)) {
            value = getPageRequest();
            type = PageRequest.class;
        } else if (VARNAME_MY.equals(name)) {
            value = getMy(context);
            type = PageDispatch.class;
        } else if (VARNAME_THAT.equals(name)) {
            value = getThat();
            type = PageDispatch.class;
        } else if (parent_ != null) {
            entry = parent_.getVariableEntry(context, name);
            if (entry != null) {
                return entry;
            }
        }
        if (type != null) {
            return new EntryImpl(name, type, value);
        }
        return null;
    }


    HttpServletRequest getRequest()
    {
        if (request_ == null) {
            if (parent_ == null) {
                return null;
            }
            Object request = parent_.getVariable(null,
                ServletVariableResolver.VAR_REQUEST);
            if (request != null && request instanceof HttpServletRequest) {
                request_ = (HttpServletRequest)request;
            }
        }
        return request_;
    }


    PageRequest getPageRequest()
    {
        if (pageRequest_ == null) {
            if (parent_ == null) {
                return null;
            }
            // PageRequestは外から変えたい時もあるので先に親が直接持っているかを見る。
            Object pageRequest = parent_.getVariable(null, VARNAME_PAGEREQUEST);
            if (pageRequest != null && pageRequest instanceof PageRequest) {
                pageRequest_ = (PageRequest)pageRequest;
            } else {
                pageRequest_ = CmsUtils.getPageRequest(getRequest());
            }
        }
        return pageRequest_;
    }


    Page getLord()
    {
        PageDispatch that = getThat();
        if (that == null) {
            return null;
        } else {
            return that.getNearestPage().getLord();
        }
    }


    Page getMyLord(TemplateContext context)
    {
        PageDispatch my = getMy(context);
        if (my == null) {
            return null;
        } else {
            return my.getNearestPage().getLord();
        }
    }


    Page getRootPage()
    {
        if (rootPage_ == null) {
            PageRequest pageRequest = getPageRequest();
            if (pageRequest != null) {
                rootPage_ = pageRequest.getRootPage();
            } else {
                rootPage_ = pageAlfr_.getRootPage(PathId.HEIM_MIDGARD);
            }
        }
        return rootPage_;
    }


    PageDispatch getMy(TemplateContext context)
    {
        if (!myGenerated_) {
            // マクロを処理中の場合でもマクロのパスではなくマクロ利用側のパスを使う。
            // その方が便利そうなため。
            String path = context.getTemplateName();
            if (path != null) {
                PageRequest pageRequest = getPageRequest();
                PageDispatch my = null;
                if (pageRequest != null) {
                    my = pageRequest.getMy();
                    if (!path.equals(my.getPathname())) {
                        my = null;
                    }
                }
                if (my == null) {
                    my = new PageDispatchImpl(pageAlfr_, pagePlugin_, CmsUtils
                        .getHeimId(), path);
                    my
                        .setGardRootPage(my.getNearestPage()
                            .getNearestGardRoot());
                }
                my_ = my;
            } else {
                // 無名テンプレート。
                my_ = null;
            }
            myGenerated_ = true;
        }
        return my_;
    }


    PageDispatch getThat()
    {
        PageRequest pageRequest = getPageRequest();
        if (pageRequest != null) {
            return pageRequest.getThat();
        } else {
            return null;
        }
    }
}
