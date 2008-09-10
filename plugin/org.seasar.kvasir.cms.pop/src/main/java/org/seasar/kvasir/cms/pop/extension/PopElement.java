package org.seasar.kvasir.cms.pop.extension;

import static org.seasar.kvasir.cms.pop.PopPlugin.COMPONENTNAME_GENERICPOP;
import static org.seasar.kvasir.util.collection.I18NPropertyHandler.VARIANT_DEFAULT;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.base.KvasirUtils;
import org.seasar.kvasir.base.annotation.ForPreparingMode;
import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.util.ArrayUtils;
import org.seasar.kvasir.cms.pop.Kind;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopContextWrapper;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.PopPropertyEntry;
import org.seasar.kvasir.cms.pop.PopPropertyMetaData;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.ValidationResult;
import org.seasar.kvasir.cms.pop.pop.GenericPop;
import org.seasar.kvasir.cms.pop.util.PresentationUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.I18NPropertyHandler;
import org.seasar.kvasir.util.collection.I18NPropertyReader;
import org.seasar.kvasir.util.io.I18NResource;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;
import org.seasar.kvasir.util.io.impl.I18NResourceImpl;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Default;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MAY, isa = Pop.class)
@Bean("pop")
public class PopElement extends AbstractElement
    implements I18NPropertyReader
{
    private static final String VARNAME_PLUGIN = "plugin";

    public static final String PROP_TITLE = "title";

    public static final String PROP_BODY = "body";

    public static final String PROP_BODY_TYPE = "body-type";

    private static final String PROP_NAME = "name";

    private static final String PROP_DESCRIPTION = "description";

    private String bodyResourcePath_;

    private String bodyType_;

    private String title_;

    private String name_;

    private String description_;

    private String imageResourcePath_;

    private ContentAbilityPlugin contentPlugin_;

    private PopPlugin popPlugin_;

    private I18NResource body_;

    private FormUnitElement[] formUnits_ = new FormUnitElement[0];

    private PopPropertyMetaData[] propertyMedaDatas_ = new PopPropertyMetaData[0];

    private Map<String, PopPropertyMetaData> propertyMetaDataMap_ = new HashMap<String, PopPropertyMetaData>();

    private String gardId_;

    private boolean embedded_;

    private KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public String getBodyResourcePath()
    {
        return bodyResourcePath_;
    }


    String findBodyResourcePath()
    {
        String bodyResourcePath = getBodyResourcePath();
        if (bodyResourcePath != null) {
            return bodyResourcePath;
        } else {
            return "pops/" + getId() + "/body.html";
        }
    }


    public I18NResource getBodyResource()
    {
        return body_;
    }


    @Attribute
    public void setBodyResourcePath(String bodyResourcePath)
    {
        bodyResourcePath_ = bodyResourcePath;
    }


    public String getBodyType()
    {
        return bodyType_;
    }


    public String findBodyType()
    {
        String bodyType = getBodyType();
        if (bodyType == null) {
            bodyType = PropertyUtils.valueOf(getProperty0(PROP_BODY_TYPE,
                I18NPropertyHandler.VARIANT_DEFAULT), "text/x-zpt");
        }
        return bodyType;
    }


    @Attribute
    public void setBodyType(String bodyType)
    {
        bodyType_ = bodyType;
    }


    public String getTitle()
    {
        return title_;
    }


    public String findTitle()
    {
        String title = getTitle();
        if (title == null) {
            title = "%pop." + getId() + "." + PROP_TITLE;
        }
        return title;
    }


    public String findTitle(String variant)
    {
        if (VARIANT_DEFAULT.equals(variant)) {
            if (title_ != null) {
                return title_;
            }
        }
        return PropertyUtils.valueOf(getProperty0(PROP_TITLE, variant), "");
    }


    public String findTitle(Locale locale)
    {
        return PropertyUtils.valueOf(getPlugin().resolveString(findTitle(),
            locale), "");
    }


    public String renderTitle(String title, PopContext context)
    {
        return PresentationUtils.filter(title, context);
    }


    @Attribute
    public void setTitle(String title)
    {
        title_ = title;
    }


    public String getName()
    {
        return name_;
    }


    public String findName()
    {
        String name = getName();
        if (name == null) {
            name = "%pop." + getId() + "." + PROP_NAME;
        }
        return name;
    }


    public String findName(Locale locale)
    {
        return getPlugin().resolveString(findName(), locale);
    }


    @Attribute
    public void setName(String name)
    {
        name_ = name;
    }


    public String getDescription()
    {
        return description_;
    }


    public String findDescription()
    {
        String description = getDescription();
        if (description == null) {
            description = "%pop." + getId() + "." + PROP_DESCRIPTION;
        }
        return description;
    }


    public String findDescription(Locale locale)
    {
        return getPlugin().resolveString(findDescription(), locale);
    }


    @Attribute
    public void setDescription(String description)
    {
        description_ = description;
    }


    public String getImageResourcePath()
    {
        return imageResourcePath_;
    }


    String findImageResourcePath()
    {
        String imageResourcePath = getImageResourcePath();
        if (imageResourcePath != null) {
            return imageResourcePath;
        } else {
            return "pops/" + getId() + "/image.jpg";
        }
    }


    public Resource getImageResource()
    {
        String imageResourcePath = findImageResourcePath();
        Resource imageResource = getHomeDirectory().getChildResource(
            imageResourcePath);
        if (imageResource.exists()) {
            return imageResource;
        } else {
            return popPlugin_.getDefaultPopImageResource();
        }
    }


    @Attribute
    public void setImageResourcePath(String imageResourcePath)
    {
        imageResourcePath_ = imageResourcePath;
    }


    public void init(ContentAbilityPlugin contentPlugin, PopPlugin popPlugin)
    {
        contentPlugin_ = contentPlugin;
        popPlugin_ = popPlugin;

        body_ = getI18NResource(findBodyResourcePath());
    }


    I18NResource getI18NResource(String resourcePath)
    {
        if (resourcePath == null) {
            return null;
        }
        return new I18NResourceImpl(getHomeDirectory().getChildResource(
            resourcePath));
    }


    Resource getHomeDirectory()
    {
        Resource homeDirectory = null;
        Plugin<?> plugin = getPlugin();
        if (plugin.isUnderDevelopment()) {
            homeDirectory = plugin.getHomeSourceDirectory();
        }
        if (homeDirectory == null) {
            homeDirectory = plugin.getHomeDirectory();
        }
        return homeDirectory;
    }


    public String findBody(String variant)
    {
        if (body_ != null && body_.exists(variant)) {
            try {
                return PropertyUtils.valueOf(IOUtils.readString(body_
                    .getInputStream(variant), "UTF-8", false), "");
            } catch (ResourceNotFoundException ignore) {
            }
        }
        return PropertyUtils.valueOf(getProperty0(PROP_BODY, variant), "");
    }


    public String findBody(Locale locale)
    {
        String body = getString(body_, locale);
        if (body == null) {
            body = PropertyUtils.valueOf(getProperty0(PROP_BODY, locale), "");
        }
        return body;
    }


    public String getBodyFromResourcePath(String bodyResourcePath, Locale locale)
    {
        return getString(getI18NResource(bodyResourcePath), locale);
    }


    String getString(I18NResource resource, Locale locale)
    {
        if (resource != null && resource.exists(locale)) {
            try {
                return PropertyUtils.valueOf(IOUtils.readString(resource
                    .getInputStream(locale), "UTF-8", false), "");
            } catch (ResourceNotFoundException ignore) {
                return null;
            }
        } else {
            return null;
        }
    }


    public String renderBodyWithoutException(String body, String bodyType,
        PopContext context)
    {
        if (body != null) {
            try {
                return renderBody(new ByteArrayInputStream(body
                    .getBytes("UTF-8")), "UTF-8", bodyType, context);
            } catch (Throwable t) {
                getPlugin().getLog().error("Can't generate body", t);
                return "";
            }
        } else {
            return "";
        }
    }


    public String renderBody(String body, String bodyType, PopContext context)
    {
        if (body != null) {
            try {
                return renderBody(new ByteArrayInputStream(body
                    .getBytes("UTF-8")), "UTF-8", bodyType, context);
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException("Can't happen!");
            }
        } else {
            return "";
        }
    }


    String renderBody(InputStream in, String encoding, String type,
        PopContext context)
    {
        ContentHandler handler = contentPlugin_.getContentHandler(type);
        Map<String, Object> popScope = new HashMap<String, Object>();
        popScope.put(VARNAME_PLUGIN, getPlugin());
        return PresentationUtils.filter(handler.toHTML(in, encoding, type,
            new PopContextWrapper(context, popScope)), context);
    }


    public Pop newPop(int heimId, int instanceId)
    {
        if (getPlugin().isUnderDevelopment()) {
            // 開発中のプラグインの場合はアクセスの度にPopクラスをリロードするような仕組みにしておく。
            return new HotdeployPopWrapper(heimId, instanceId);
        } else {
            return newPop0(heimId, instanceId);
        }
    }


    boolean isPopComponentRegistered()
    {
        return (getId() != null && getPlugin().getComponentContainer()
            .hasComponent(getId()));
    }


    Pop newPop0(int heimId, int instanceId)
    {
        Pop pop = (Pop)getComponent();
        if (pop == null) {
            log_.warn("Can't find POP component. Use generic POP: heimId="
                + heimId + ", instanceId=" + instanceId + ", popId="
                + getFullId());
            GenericPop genericPop = (GenericPop)popPlugin_
                .getComponentContainer().getComponent(COMPONENTNAME_GENERICPOP);
            genericPop.setElement(this);
            pop = genericPop;
        }
        pop.setHeimId(heimId);
        pop.setInstanceId(instanceId);
        KvasirUtils.start(pop);
        return pop;
    }


    public String getProperty(String id)
    {
        return getProperty(id, Page.VARIANT_DEFAULT);
    }


    public String getProperty(String id, String variant)
    {
        if (PROP_TITLE.equals(id)) {
            return findTitle(variant);
        } else if (PROP_BODY.equals(id)) {
            return findBody(variant);
        } else if (PROP_BODY_TYPE.equals(id)) {
            return findBodyType();
        } else {
            return getProperty0(id, variant);
        }
    }


    String getProperty0(String id, String variant)
    {
        String value = getPlugin().getProperty(getPropertyKey(id), variant);
        if (value == null) {
            PopPropertyMetaData metaData = getPropertyMetaData(id);
            if (metaData != null) {
                value = metaData.getDefault();
            }
        }
        return value;
    }


    public PopPropertyMetaData getPropertyMetaData(String id)
    {
        return propertyMetaDataMap_.get(id);
    }


    public String getProperty(String id, Locale locale)
    {
        if (PROP_TITLE.equals(id)) {
            return findTitle(locale);
        } else if (PROP_BODY.equals(id)) {
            return findBody(locale);
        } else if (PROP_BODY_TYPE.equals(id)) {
            return findBodyType();
        } else {
            return getProperty0(id, locale);
        }
    }


    String getProperty0(String id, Locale locale)
    {
        String value = getPlugin().getProperty(getPropertyKey(id), locale);
        if (value == null) {
            PopPropertyMetaData metaData = getPropertyMetaData(id);
            if (metaData != null) {
                value = metaData.getDefault();
            }
        }
        return value;
    }


    public String getPropertyKey(String id)
    {
        return "pop." + getId() + "." + id;
    }


    public FormUnitElement[] getFormUnits()
    {
        return formUnits_;
    }


    @Child
    @ForPreparingMode
    public void addFormUnit(FormUnitElement formUnit)
    {
        formUnits_ = ArrayUtils.add(formUnits_, formUnit);
        if (formUnit.getKind() == Kind.PROPERTY) {
            propertyMedaDatas_ = ArrayUtils.add(propertyMedaDatas_, formUnit);
            propertyMetaDataMap_.put(formUnit.getId(), formUnit);
        }
    }


    public void setFormUnits(FormUnitElement[] formUnits)
    {
        propertyMedaDatas_ = new PopPropertyMetaData[0];
        propertyMetaDataMap_.clear();

        formUnits_ = formUnits;
        for (int i = 0; i < formUnits.length; i++) {
            if (formUnits[i].getKind() == Kind.PROPERTY) {
                propertyMedaDatas_ = ArrayUtils.add(propertyMedaDatas_,
                    formUnits[i]);
                propertyMetaDataMap_.put(formUnits[i].getId(), formUnits[i]);
            }
        }
    }


    public PopPropertyMetaData[] getPropertyMetaDatas()
    {
        return propertyMedaDatas_;
    }


    public String getGardId()
    {
        return gardId_;
    }


    @Attribute
    public void setGardId(String gardId)
    {
        gardId_ = gardId;
    }


    /**
     * POPが埋め込みPOPであるかどうかを返します。
     * <p>埋め込みPOPとは、POPが部品としてWebサイト上に配置されるためのものではなく、
     * 主にコンテンツの本文などに埋め込まれて使われるものであることを表します。
     * 例えばWiki形式で用いる「改行タグを生成するPOP」などは埋め込みPOPです。
     * なお埋め込みPOPは通常、インスタンス毎のプロパティによってではなく評価時の引数によって
     * 見た目や挙動が変わるように作成されます。
     * </p>
     * 
     * @return 埋め込みPOPかどうか。
     */
    public boolean isEmbedded()
    {
        return embedded_;
    }


    @Attribute
    @Default("false")
    public void setEmbedded(boolean embedded)
    {
        embedded_ = embedded;
    }


    /*
     * inner classes
     */

    class HotdeployPopWrapper
        implements Pop
    {
        private int heimId_;

        private int instanceId_;


        public HotdeployPopWrapper(int heimId, int instanceId)
        {
            heimId_ = heimId;
            instanceId_ = instanceId;
        }


        public RenderedPop render(PopContext context, String[] args)
        {
            return newInstance().render(context, args);
        }


        Pop newInstance()
        {
            return newPop0(heimId_, instanceId_);
        }


        public PopElement getElement()
        {
            return PopElement.this;
        }


        public String getId()
        {
            return newInstance().getId();
        }


        public int getInstanceId()
        {
            return instanceId_;
        }


        public int getHeimId()
        {
            return heimId_;
        }


        public String getPopId()
        {
            return newInstance().getPopId();
        }


        public String getProperty(PopContext context, String id, String variant)
        {
            return newInstance().getProperty(context, id, variant);
        }


        public String getProperty(PopContext context,
            Map<String, PopPropertyEntry> map, String id, String variant)
        {
            return newInstance().getProperty(context, map, id, variant);
        }


        public String getProperty(PopContext context, String id, Locale locale)
        {
            return newInstance().getProperty(context, id, locale);
        }


        public PopPropertyMetaData[] getPropertyMetaDatas()
        {
            return newInstance().getPropertyMetaDatas();
        }


        public void setElement(PopElement element)
        {
            throw new UnsupportedOperationException("Must not called!");
        }


        public void setInstanceId(int instanceId)
        {
            throw new UnsupportedOperationException("Must not called!");
        }


        public void setHeimId(int heimId)
        {
            throw new UnsupportedOperationException("Must not called!");
        }


        public void setProperty(PopContext context, String id, String variant,
            String value)
        {
            newInstance().setProperty(context, id, variant, value);
        }


        public void removeProperty(PopContext context, String id, String variant)
        {
            newInstance().removeProperty(context, id, variant);
        }


        public void notifyRemoving()
        {
            newInstance().notifyRemoving();
        }


        public void setProperties(PopContext context, String variant,
            PopPropertyEntry[] entries)
        {
            newInstance().setProperties(context, variant, entries);
        }


        public ValidationResult validateProperties(PopContext context,
            String variant, PopPropertyEntry[] entries)
        {
            return newInstance().validateProperties(context, variant, entries);
        }


        public RenderedPop preview(PopContext context, String[] args,
            String variant, PopPropertyEntry[] entries)
        {
            return newInstance().preview(context, args, variant, entries);
        }
    }
}
