package org.seasar.kvasir.cms.webdav.naming.page;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.cms.webdav.naming.impl.NullResourceInfo;
import org.seasar.kvasir.cms.webdav.naming.impl.ResourceInfoAdapter;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.TemplateAbility;
import org.seasar.kvasir.page.ability.template.TemplateAbilityAlfr;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;


public class TemplateElementFactory extends AbstractPageElementFactory
{
    private static final String ATTRIBUTE = TemplateAbilityAlfr.SHORTID;

    private static final String PATTERNSTRING = "^([^\\$]*?)\\$" + ATTRIBUTE
        + "\\.([^\\.]+)((?:\\.[^\\.]*)?)$";

    private static final Pattern PATTERN = Pattern.compile(PATTERNSTRING);


    @Override
    public ResourceEntry analyzeResourceName(String name)
    {
        if (name == null) {
            return null;
        }

        Matcher matcher = PATTERN.matcher(name);
        if (!matcher.matches()) {
            return null;
        }

        String variant = matcher.group(3);
        if (variant.startsWith(".")) {
            variant = variant.substring(1/*=".".length()*/);
        }
        return new ResourceEntry(this, matcher.group(1), variant, matcher
            .group(2));
    }


    @Override
    protected boolean resourceExists(PageElement element)
    {
        return (element.getElement().getAbility(TemplateAbility.class)
            .getTemplate(element.getVariant()) != null);
    }


    @Override
    public ResourceInfo getResourceInfo(PageElement element)
    {
        Page page = element.getElement();
        if (page == null) {
            return NullResourceInfo.INSTANCE;
        }
        Template template = page.getAbility(TemplateAbility.class).getTemplate(
            element.getVariant());
        if (template == null) {
            return NullResourceInfo.INSTANCE;
        }
        return new ResourceInfoAdapter(template.getBodyResource());
    }


    @Override
    public String getAttribute()
    {
        return ATTRIBUTE;
    }


    protected void create(PageElement element, String encoding, InputStream in)
        throws NamingException
    {
        setContent(element, encoding, in);
    }


    protected void delete(PageElement element)
        throws NamingException
    {
        if (element.getElement() != null) {
            element.getElement().getAbility(TemplateAbility.class)
                .removeTemplate(element.getVariant());
        }
    }


    public String[] getResourceNames(Page page)
    {
        List<String> list = new ArrayList<String>();
        TemplateAbility ability = page.getAbility(TemplateAbility.class);
        String name = page.getName();
        String type = ability.getType();
        String[] variants = ability.getVariants();
        for (int i = 0; i < variants.length; i++) {
            if (variants[i].equals(Page.VARIANT_DEFAULT)
                && ability.getTemplate(variants[i]) == null) {
                // デフォルトバリアントだけは、テンプレートがなくてもgetVariants()で
                // 返されるため、テンプレートの存在チェックをして存在しなければスキップ
                // するようにしている。
                continue;
            }
            String variant;
            if (!variants[i].equals(Page.VARIANT_DEFAULT)) {
                variant = "." + variants[i];
            } else {
                variant = "";
            }
            list.add(name + "$template" + "." + type + variant);
        }

        return list.toArray(new String[0]);
    }


    protected void move(PageElement element, PageElement destination)
        throws NamingException
    {
        Page page = element.getElement();
        TemplateAbility ability = page.getAbility(TemplateAbility.class);
        String variant = element.getVariant();
        Template template = ability.getTemplate(variant);
        try {
            destination.create("UTF-8", new ByteArrayInputStream(template
                .getBody().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new IORuntimeException("Can't happen!", ex);
        }
        ability.removeTemplate(variant);
        page.touch();
    }


    protected void setContent(PageElement element, String encoding,
        InputStream in)
        throws NamingException
    {
        TemplateAbility template = getPage(element, true, false).getAbility(
            TemplateAbility.class);
        if (template.getType().length() == 0) {
            // テンプレートを持たない場合だけtypeを設定する。
            template.setType(element.getType());
        }
        template.setTemplate(element.getVariant(), IOUtils.readString(in,
            encoding, false));
    }
}
