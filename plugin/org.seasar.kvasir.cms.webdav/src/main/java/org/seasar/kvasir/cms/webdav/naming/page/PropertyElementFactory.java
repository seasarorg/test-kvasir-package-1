package org.seasar.kvasir.cms.webdav.naming.page;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.cms.webdav.naming.impl.NullResourceInfo;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.PropertyAbilityAlfr;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.IORuntimeException;

public class PropertyElementFactory extends AbstractPageElementFactory {
    private static final String ATTRIBUTE = PropertyAbilityAlfr.SHORTID;

    private static final String PATTERNSTRING = "^([^\\$]*?)\\$" + ATTRIBUTE
            + "((?:\\$.*?)?)\\.xproperties$";

    private static final Pattern PATTERN = Pattern.compile(PATTERNSTRING);

    @Override
    public ResourceEntry analyzeResourceName(String name) {
        if (name == null) {
            return null;
        }

        Matcher matcher = PATTERN.matcher(name);
        if (matcher.matches()) {
            String variant = matcher.group(2);
            if (variant.length() > 0) {
                variant = variant.substring(1/*="$".length()*/);
            }
            return new ResourceEntry(this, matcher.group(1), variant, "");
        }

        return null;
    }

    @Override
    protected boolean resourceExists(PageElement element) {
        String[] variants = element.getElement().getAbility(
                PropertyAbility.class).getVariants();
        for (int i = 0; i < variants.length; i++) {
            if (variants[i].equals(element.getVariant())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ResourceInfo getResourceInfo(PageElement element) {
        Page page = element.getElement();
        if (page == null) {
            return NullResourceInfo.INSTANCE;
        }
        return new PropertyResourceInfo(page, element.getVariant());
    }

    @Override
    public String getAttribute() {
        return ATTRIBUTE;
    }

    protected void create(PageElement element, String encoding, InputStream in)
            throws NamingException {
        try {
            setProperties(getPage(element, true, false), element.getVariant(),
                    encoding, in);
        } catch (IOException ex) {
            throw new IORuntimeException("Can't create property: page="
                    + element.getPath(), ex);
        }
    }

    void setProperties(Page page, String variant, String encoding,
            InputStream in) throws IOException {
        if (page != null) {
            PropertyAbility ability = page.getAbility(PropertyAbility.class);
            PropertyHandler prop = new MapProperties(
                    new TreeMap<String, String>());
            prop.load(new InputStreamReader(in, encoding));
            ability.setProperties(variant, prop);
        }
    }

    protected void delete(PageElement element) throws NamingException {
        if (element.getElement() != null) {
            element.getElement().getAbility(PropertyAbility.class)
                    .clearProperties(element.getVariant());
        }
    }

    public String[] getResourceNames(Page page) {
        List<String> list = new ArrayList<String>();

        String name = page.getName();
        PropertyAbility ability = page.getAbility(PropertyAbility.class);
        String[] variants = ability.getVariants();
        for (int i = 0; i < variants.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append("$").append(ATTRIBUTE);
            if (!variants[i].equals(Page.VARIANT_DEFAULT)) {
                sb.append("$").append(variants[i]);
            }
            sb.append(".xproperties");
            list.add(sb.toString());
        }

        return list.toArray(new String[0]);
    }

    protected void move(PageElement element, PageElement destination)
            throws NamingException {
        Page page = element.getElement();
        String variant = element.getVariant();
        PropertyHandler handler = getProperties(page, variant);
        PropertyAbility ability = page.getAbility(PropertyAbility.class);
        ability.clearProperties(variant);
        ability.setProperties(destination.getVariant(), handler);
    }

    PropertyHandler getProperties(final Page page, final String variant) {
        return page.runWithLocking(new Processable<PropertyHandler>() {
            public PropertyHandler process() throws ProcessableRuntimeException {
                PropertyHandler handler = new MapProperties(
                        new TreeMap<String, String>());
                PropertyAbility ability = page
                        .getAbility(PropertyAbility.class);
                for (Iterator<String> itr = ability.attributeNames(variant); itr
                        .hasNext();) {
                    String name = itr.next();
                    handler.setProperty(name, ability
                            .getProperty(name, variant));
                }
                return handler;
            }
        });
    }

    protected void setContent(PageElement element, String encoding,
            InputStream in) throws NamingException {
        try {
            setProperties(element.getElement(), element.getVariant(), encoding,
                    in);
        } catch (IOException ex) {
            throw new IORuntimeException("Can't set property: page="
                    + element.getPath(), ex);
        }
    }
}
