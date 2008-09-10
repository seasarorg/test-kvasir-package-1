package org.seasar.kvasir.cms.manage.manage.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.cms.ymir.Request;
import org.seasar.kvasir.cms.manage.manage.dto.PropertyRow;
import org.seasar.kvasir.cms.manage.tab.impl.PageTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageNotFoundRuntimeException;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class PropertyPage extends MainPanePage
{
    /*
     * set by framework
     */

    private String body_;

    private String name_ = "";

    private String[] names_;

    private boolean replace_;

    private String value_ = "";

    private String variant_ = Page.VARIANT_DEFAULT;

    /*
     * for presentation tier
     */

    private String[] definedVariants_;

    private PropertyRow[] properties_;

    private boolean undefinedVariant_;

    private String action_;


    /*
     * inner fields
     */

    /*
     * public scope methods
     */

    public Object do_rename()
    {
        Page page = getPage();
        if (page == null) {
            throw new PageNotFoundRuntimeException().setPathname(getPathname());
        }

        if (action_ != null) {
            return processRename(page);
        }

        if (names_ == null || names_.length == 0) {
            setTransferredNotes(new Notes().add(new Note(
                "app.note.property.namesIsEmpty")));
        }

        enableTab(PageTab.NAME_PROPERTY);
        enableLocationBar(true);
        return "/property.rename.html";
    }


    Object processRename(final Page page)
    {
        page.runWithLocking(new Processable<Object>() {
            @SuppressWarnings("unchecked")
            public Object process()
                throws ProcessableRuntimeException
            {
                PropertyAbility prop = page.getAbility(PropertyAbility.class);
                Request request = getYmirRequest();
                for (Iterator<String> itr = request.getParameterNames(); itr
                    .hasNext();) {
                    String name = itr.next();
                    if (!name.startsWith(":")) {
                        continue;
                    }
                    String oldName = name.substring(":".length());
                    String newName = request.getParameter(name);
                    if (oldName.equals(newName)) {
                        continue;
                    }

                    String[] variants = prop.getVariants();
                    for (int i = 0; i < variants.length; i++) {
                        String value = prop.getProperty(oldName, variants[i]);
                        if (value != null) {
                            prop.setProperty(newName, value);
                            prop.removeProperty(oldName);
                        }
                    }
                }
                return null;
            }
        });

        return getRedirection("/property.list.do" + getPathname());
    }


    public String do_list()
    {
        enableTab(PageTab.NAME_PROPERTY);
        enableLocationBar(true);
        prepareDefinedVariants();
        if (undefinedVariant_) {
            variant_ = Page.VARIANT_DEFAULT;
        }

        PropertyAbility prop = (PropertyAbility)getPage().getAbility(
            PropertyAbility.class);
        List<String> nameList = new ArrayList<String>();
        for (Enumeration<String> enm = prop.propertyNames(variant_); enm
            .hasMoreElements();) {
            nameList.add(enm.nextElement());
        }
        preparePropertyRows((String[])nameList.toArray(new String[0]));

        return "/property.list.html";
    }


    public Object do_new()
    {
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (createProperty()) {
                updateMenu();
                Map<String, String[]> paramMap = new HashMap<String, String[]>();
                paramMap.put("names", new String[] { name_ });
                return getRedirection("/property.edit.do" + getPathname(),
                    paramMap);
            }
        }

        enableTab(PageTab.NAME_PROPERTY);
        enableLocationBar(true);
        prepareDefinedVariants();

        return "/property.new.html";
    }


    public Object do_edit()
    {
        if (names_ == null || names_.length == 0) {
            setTransferredNotes(new Notes().add(new Note(
                "app.note.property.namesIsEmpty")));
        }

        enableTab(PageTab.NAME_PROPERTY);
        enableLocationBar(true);
        prepareDefinedVariants();

        preparePropertyRows(names_ != null ? names_ : new String[0]);
        return "/property.edit.html";
    }


    public Object do_update()
    {
        if (!"POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            return null;
        }
        if (names_ == null || names_.length == 0) {
            return null;
        }
        updateProperties();

        updateMenu();
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.put("variant", new String[] { variant_ });
        paramMap.put("names", names_);
        return getRedirection("/property.edit.do" + getPathname(), paramMap);
    }


    public Object do_edit2()
    {
        enableTab(PageTab.NAME_PROPERTY);
        enableLocationBar(true);
        prepareDefinedVariants();

        prepareBody(names_ != null ? names_ : new String[0]);
        return "/property.edit2.html";
    }


    public Object do_update2()
    {
        if (!"POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            return null;
        }

        update2Properties();

        updateMenu();
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.put("variant", new String[] { variant_ });
        return getRedirection("/property.list.do" + getPathname(), paramMap);
    }


    public Object do_delete()
    {
        if (names_ == null || names_.length == 0) {
            setNotes(new Notes()
                .add(new Note("app.note.property.namesIsEmpty")));
        } else {
            PropertyAbility prop = (PropertyAbility)getPage().getAbility(
                PropertyAbility.class);
            for (int i = 0; i < names_.length; i++) {
                prop.removeProperty(names_[i], variant_);
            }
            setNotes(new Notes().add(new Note(
                "app.note.property.delete.succeed")));
            updateMenu();
        }

        return do_list();
    }


    public Object do_deleteVariant()
    {
        if (!variant_.equals(Page.VARIANT_DEFAULT)) {
            deleteVariant(variant_);
            setNotes(new Notes().add(new Note(
                "app.note.property.deleteVariant.succeed", variant_)));
        }

        updateMenu();
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.put("variant", new String[] { Page.VARIANT_DEFAULT });
        return getRedirection("/property.list.do" + getPathname(), paramMap);
    }


    private void deleteVariant(String variant)
    {
        PropertyAbility prop = (PropertyAbility)getPage().getAbility(
            PropertyAbility.class);
        prop.clearProperties(variant);
    }


    /*
     * private scpoe methods
     */

    private void prepareDefinedVariants()
    {
        PropertyAbility prop = getPage().getAbility(PropertyAbility.class);
        definedVariants_ = prop.getVariants();
        if (definedVariants_.length == 0) {
            definedVariants_ = new String[] { Page.VARIANT_DEFAULT };
        }

        undefinedVariant_ = true;
        for (int i = 0; i < definedVariants_.length; i++) {
            if (variant_.equals(definedVariants_[i])) {
                undefinedVariant_ = false;
                break;
            }
        }
    }


    private void preparePropertyRows(String[] names)
    {
        PropertyAbility prop = getPage().getAbility(PropertyAbility.class);
        List<PropertyRow> rowList = new ArrayList<PropertyRow>();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String value = prop.getProperty(name, variant_);

            rowList.add(new PropertyRow(name, value, (i % 2 == 0)));
        }
        properties_ = rowList.toArray(new PropertyRow[0]);
    }


    private void prepareBody(String[] names)
    {
        PropertyAbility prop = (PropertyAbility)getPage().getAbility(
            PropertyAbility.class);

        PropertyHandler ph = new MapProperties(
            new LinkedHashMap<String, String>());
        for (int i = 0; i < names.length; i++) {
            String value = prop.getProperty(names[i], variant_);
            if (value != null) {
                ph.setProperty(names[i], value);
            }
        }
        StringWriter sw = new StringWriter();
        try {
            ph.store(sw);
        } catch (IOException ex) {
            throw new RuntimeException("Can't happen!");
        }
        body_ = sw.toString();
    }


    private boolean createProperty()
    {
        PropertyAbility prop = (PropertyAbility)getPage().getAbility(
            PropertyAbility.class);

        if (name_.length() == 0) {
            setNotes(new Notes().add(new Note(
                "app.note.property.new.nameIsEmpty")));
            return false;
        }

        if (prop.containsPropertyName(name_)) {
            setNotes(new Notes().add(new Note(
                "app.note.property.new.alreadyExists", name_)));
            return false;
        }

        prop.setProperty(name_, value_);
        getPage().touch();
        setNotes(new Notes().add(new Note("app.note.property.new.succeed")));

        return true;
    }


    @SuppressWarnings("unchecked")
    private void updateProperties()
    {
        PropertyAbility prop = getPage().getAbility(PropertyAbility.class);
        Map<String, String[]> paramMap = getYmirRequest().getParameterMap();
        for (int i = 0; i < names_.length; i++) {
            String name = names_[i];
            String value = PropertyUtils.valueOf(paramMap.get("value:" + name),
                (String)null);
            if (value != null) {
                prop.setProperty(name, variant_, value);
            }
        }
        getPage().touch();

        setNotes(new Notes().add(new Note("app.note.property.update.succeed")));
    }


    @SuppressWarnings("unchecked")
    private void update2Properties()
    {
        if (body_ != null) {
            PropertyAbility prop = getPage().getAbility(PropertyAbility.class);
            PropertyHandler ph = new MapProperties(
                new HashMap<String, String>());
            try {
                ph.load(new StringReader(body_));
            } catch (IOException ex) {
                throw new RuntimeException("Can't happen!", ex);
            }
            if (replace_) {
                prop.setProperties(variant_, ph);
            } else {
                for (Enumeration<String> enm = ph.propertyNames(); enm
                    .hasMoreElements();) {
                    String name = (String)enm.nextElement();
                    prop.setProperty(name, variant_, ph.getProperty(name));
                }
            }

            getPage().touch();
            setNotes(new Notes().add(new Note(
                "app.note.property.update2.succeed")));
        }
    }


    /*
     * for framework / presentation tier
     */

    public String getBody()
    {
        return body_;
    }


    public void setBody(String body)
    {
        body_ = body;
    }


    public String getName()
    {
        return name_;
    }


    public void setName(String name)
    {
        name_ = name;
    }


    public String[] getNames()
    {
        return names_;
    }


    public void setNames(String[] names)
    {
        names_ = names;
    }


    public void setReplace(boolean replace)
    {
        replace_ = replace;
    }


    public String getValue()
    {
        return value_;
    }


    public void setValue(String value)
    {
        value_ = value;
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

    public String[] getDefinedVariants()
    {
        return definedVariants_;
    }


    public PropertyRow[] getProperties()
    {
        return properties_;
    }


    public boolean isUndefinedVariant()
    {
        return undefinedVariant_;
    }


    public void setAction(String action)
    {
        action_ = action;
    }
}
