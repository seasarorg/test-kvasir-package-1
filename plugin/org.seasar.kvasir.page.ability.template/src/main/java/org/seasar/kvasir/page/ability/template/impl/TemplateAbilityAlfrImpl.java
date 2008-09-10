package org.seasar.kvasir.page.ability.template.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.AbstractCachedPageAbilityAlfr;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.AttributeFilter;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.TemplateAbility;
import org.seasar.kvasir.page.ability.template.TemplateAbilityAlfr;
import org.seasar.kvasir.page.ability.template.TemplateAbilityPlugin;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.InputStreamFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class TemplateAbilityAlfrImpl extends AbstractCachedPageAbilityAlfr
    implements TemplateAbilityAlfr
{
    private TemplateAbilityPlugin plugin_;

    private TemplateCache cache_;


    public void setPlugin(TemplateAbilityPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setCache(TemplateCache cache)
    {
        cache_ = cache;
    }


    @Override
    protected Cache<?, ?> getCache()
    {
        return cache_;
    }


    @Override
    protected String getCacheId()
    {
        return TemplateAbilityPlugin.ID + ".templateAbilityAlfr";
    }


    /*
     * AbstractPageAbilityAlfr
     */

    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
        cache_ = null;
        plugin_ = null;
    }


    /*
     * TemplateAbilityAlfr
     */

    public Template getTemplate(Page page)
    {
        return getTemplate(page, Page.VARIANT_DEFAULT);
    }


    public synchronized Template getTemplate(Page page, String variant)
    {
        return cache_.get(newKey(page, variant));
    }


    TemplateKey newKey(Page page, String variant)
    {
        return new TemplateKey(page.getDto().getId(), variant);
    }


    public void setTemplate(Page page, String template)
    {
        setTemplate(page, Page.VARIANT_DEFAULT, template);
    }


    public void setTemplate(final Page page, final String variant,
        final String template)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                synchronized (TemplateAbilityAlfrImpl.this) {
                    cache_.setTempate(newKey(page, variant), template);
                }
                return null;
            }
        });
    }


    public void removeTemplate(Page page)
    {
        removeTemplate(page, Page.VARIANT_DEFAULT);
    }


    public void removeTemplate(final Page page, final String variant)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                synchronized (TemplateAbilityAlfrImpl.this) {
                    cache_.removeTemplate(newKey(page, variant));
                }
                return null;
            }
        });
    }


    public void clearAllTemplates(final Page page)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                synchronized (TemplateAbilityAlfrImpl.this) {
                    cache_.clearAllTemplates(page.getDto().getId());
                }
                return null;
            }
        });
    }


    public synchronized String getType(Page page)
    {
        return cache_.getType(page.getDto().getId());
    }


    public synchronized void setType(Page page, String type)
    {
        cache_.setType(page.getDto().getId(), type);
    }


    public synchronized String getResponseContentType(Page page)
    {
        return cache_.getResponseContentType(page.getDto().getId());
    }


    public synchronized void setResponseContentType(Page page, String type)
    {
        cache_.setResponseContentType(page.getDto().getId(), type);
    }


    /*
     * PageAbilityAlfr
     */

    public PageAbility getAbility(Page page)
    {
        return new TemplateAbilityImpl(this, page);
    }


    public Class<? extends PageAbility> getAbilityInterfaceClass()
    {
        return TemplateAbility.class;
    }


    public String getShortId()
    {
        return SHORTID;
    }


    public void create(Page page)
    {
        // 特に何もしない。
    }


    public void delete(Page page)
    {
        cache_.clearAllTemplates(page.getDto().getId());
    }


    public String[] getVariants(Page page)
    {
        return cache_.getVariants(page.getDto().getId());
    }


    public Attribute getAttribute(Page page, String name, String variant)
    {
        Attribute attr = null;
        if (ATTR_TYPE.equals(name)) {
            if (Page.VARIANT_DEFAULT.equals(variant)) {
                attr = new Attribute();
                attr.setString(SUBNAME_DEFAULT, getType(page));
            }
        } else if (ATTR_RESPONSECONTENTTYPE.equals(name)) {
            if (Page.VARIANT_DEFAULT.equals(variant)) {
                attr = new Attribute();
                attr.setString(SUBNAME_DEFAULT, getResponseContentType(page));
            }
        } else {
            Template template = getTemplate(page, variant);
            if (template == null) {
                return null;
            }

            if (ATTR_MODIFYDATE.equals(name)) {
                attr = new Attribute();
                attr.setString(SUBNAME_DEFAULT, String.valueOf(template
                    .getModifyDate().getTime()));
            } else if (ATTR_BODY.equals(name)) {
                attr = new Attribute();
                attr.setStream(SUBNAME_DEFAULT, new TemplateInputStreamFactory(
                    template));
            }
        }
        return attr;
    }


    public void setAttribute(Page page, String name, String variant,
        Attribute attr)
    {
        if (ATTR_TYPE.equals(name)) {
            if (Page.VARIANT_DEFAULT.equals(variant)) {
                String type = attr.getString(SUBNAME_DEFAULT);
                if (type != null) {
                    setType(page, type);
                }
            }
        } else if (ATTR_RESPONSECONTENTTYPE.equals(name)) {
            if (Page.VARIANT_DEFAULT.equals(variant)) {
                String responseContentType = attr.getString(SUBNAME_DEFAULT);
                if (responseContentType != null) {
                    setResponseContentType(page, responseContentType);
                }
            }
        } else if (ATTR_MODIFYDATE.equals(name)) {
            // modifyDateは今のところ復元できない。
        } else if (ATTR_BODY.equals(name)) {
            InputStreamFactory isf = attr.getStream(SUBNAME_DEFAULT);
            if (isf != null) {
                setTemplate(page, variant, IOUtils.readString(isf
                    .getInputStream(), Template.ENCODING, false));
            }
        }
    }


    public void removeAttribute(final Page page, final String name,
        final String variant)
    {
        if (ATTR_BODY.equals(name)) {
            removeTemplate(page, variant);
        }
    }


    public void clearAttributes(final Page page)
    {
        clearAllTemplates(page);
    }


    public boolean containsAttribute(Page page, String name, String variant)
    {
        if (ATTR_TYPE.equals(name) || ATTR_RESPONSECONTENTTYPE.equals(name)) {
            if (Page.VARIANT_DEFAULT.equals(variant)) {
                return true;
            }
        } else if (ATTR_MODIFYDATE.equals(name) || ATTR_BODY.equals(name)) {
            return (getTemplate(page, variant) != null);
        }
        return false;
    }


    public Iterator<String> attributeNames(Page page, String variant,
        AttributeFilter filter)
    {
        List<String> list = new ArrayList<String>();
        if (hasAnyTemplates(page)) {
            if (Page.VARIANT_DEFAULT.equals(variant)) {
                list.add(ATTR_TYPE);
                list.add(ATTR_RESPONSECONTENTTYPE);
            }
            if (getTemplate(page, variant) != null) {
                list.add(ATTR_MODIFYDATE);
                list.add(ATTR_BODY);
            }
        }
        return list.iterator();
    }


    synchronized boolean hasAnyTemplates(Page page)
    {
        return cache_.hasAnyTemplates(page.getDto().getId());
    }
}
