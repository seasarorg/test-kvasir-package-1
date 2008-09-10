package org.seasar.kvasir.cms.webdav.naming.page;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.cms.webdav.naming.impl.NullResourceInfo;
import org.seasar.kvasir.cms.webdav.naming.impl.ResourceInfoAdapter;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentAbilityAlfr;


public class ContentElementFactory extends AbstractPageElementFactory
{
    private static final String ATTRIBUTE = ContentAbilityAlfr.SHORTID;

    private static final String PATTERNSTRING = "^([^\\$]*?)\\$\\$([^\\.]*)((?:\\.[^\\.\\$]+)?)$";

    private static final Pattern PATTERN = Pattern.compile(PATTERNSTRING);


    @Override
    public ResourceEntry analyzeResourceName(String name)
    {
        if (name == null) {
            return null;
        }

        Matcher matcher = PATTERN.matcher(name);
        if (matcher.matches()) {
            String variant = matcher.group(2);
            return new ResourceEntry(this, matcher.group(1) + matcher.group(3),
                variant, "");
        }

        return null;
    }


    @Override
    protected boolean resourceExists(PageElement element)
    {
        return (element.getElement().getAbility(ContentAbility.class)
            .getLatestContent(element.getVariant()) != null);
    }


    @Override
    protected ResourceInfo getResourceInfo(PageElement element)
    {
        Page page = element.getElement();
        if (page == null) {
            return NullResourceInfo.INSTANCE;
        }
        Content latestContent = getLatestContent(page, element.getVariant());
        if (latestContent == null) {
            return NullResourceInfo.INSTANCE;
        }
        return new ResourceInfoAdapter(latestContent.getBodyResource());
    }


    @Override
    public String getAttribute()
    {
        return ATTRIBUTE;
    }


    protected void create(PageElement element, String encoding, InputStream in)
        throws NamingException
    {
        createPageContent(element, encoding, in);
    }


    protected void delete(PageElement element)
        throws NamingException
    {
        if (element.getElement() != null) {
            element.getElement().getAbility(ContentAbility.class)
                .clearContents(element.getVariant());
        }
    }


    public String[] getResourceNames(Page page)
    {
        List<String> list = new ArrayList<String>();

        String name = page.getName();
        int dot = name.lastIndexOf('.');
        String prefix;
        String suffix;
        if (dot >= 0) {
            prefix = name.substring(0, dot);
            suffix = name.substring(dot);
        } else {
            prefix = name;
            suffix = "";
        }

        ContentAbility ability = page.getAbility(ContentAbility.class);
        String[] variants = ability.getVariants();
        for (int i = 0; i < variants.length; i++) {
            if (variants[i].equals(Page.VARIANT_DEFAULT)) {
                if (!page.isNode()
                    || ability.getLatestContent(variants[i]) == null) {
                    continue;
                }
            }
            list.add(prefix + "$$" + variants[i] + suffix);
        }

        return list.toArray(new String[0]);
    }


    protected void move(PageElement element, PageElement destination)
        throws NamingException
    {
        Page page = element.getElement();
        ContentAbility ability = page.getAbility(ContentAbility.class);
        String variant = element.getVariant();
        Content content = ability.getLatestContent(variant);
        destination.create(content.getEncoding(), content.getBodyInputStream());
        ability.clearContents(variant);
        page.touch();
    }


    protected void setContent(PageElement element, String encoding,
        InputStream in)
        throws NamingException
    {
        setPageContent(element, encoding, in);
    }
}
