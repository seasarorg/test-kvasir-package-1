package org.seasar.kvasir.cms.pop.pop;

import static org.seasar.kvasir.cms.pop.ValidationResult.ERROR_ASIS;
import static org.seasar.kvasir.page.Page.VARIANT_DEFAULT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopContextWrapper;
import org.seasar.kvasir.cms.pop.PopPropertyEntry;
import org.seasar.kvasir.cms.pop.PopPropertyEntryBag;
import org.seasar.kvasir.cms.pop.PopPropertyMetaData;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.ValidationResult;
import org.seasar.kvasir.cms.pop.extension.PopElement;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.LocaleUtils;
import org.seasar.kvasir.util.StringUtils;
import org.seasar.kvasir.util.collection.I18NProperties;
import org.seasar.kvasir.util.io.I18NResource;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * 基本的なPopクラスです。
 * <p>このまま使用することもできますし、
 * 独自のPopを作成する場合のベースとすることもできます。</p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 * @see Pop
 */
public class GenericPop
    implements Pop
{
    public static final String PROP_TITLE = PopElement.PROP_TITLE;

    public static final String PROP_BODY = PopElement.PROP_BODY;

    public static final String PROP_BODY_TYPE = PopElement.PROP_BODY_TYPE;

    private PageAlfr pageAlfr_;

    private PopElement element_;

    private int heimId_;

    private int instanceId_ = INSTANCEID_DEFAULT;


    final public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    final public void setElement(PopElement element)
    {
        element_ = element;
    }


    final public String getId()
    {
        return getPopId() + INSTANCE_DELIMITER + instanceId_;
    }


    final public String getPopId()
    {
        return element_.getFullId();
    }


    final public int getInstanceId()
    {
        return instanceId_;
    }


    final public void setInstanceId(int instanceId)
    {
        instanceId_ = instanceId;
    }


    /**
     * POPをレンダリングします。
     * <p>サブクラスでレンダリング処理を変更する場合は、
     * <p>プレビューモードでも適切に動作するよう、通常はこのメソッドの代わりに
     * {@link #render(PopContext, String[], Map)}
     * をオーバライドするようにして下さい。
     * </p>
     *
     * @param context コンテキスト。
     * @param args 引数。
     * @return レンダリングした結果。
     */
    public RenderedPop render(PopContext context, String[] args)
    {
        Pop old = context.setPop(this);
        try {
            return render(context, args, populatePropertiesTo(
                new HashMap<String, Object>(), context));
        } finally {
            context.setPop(old);
        }
    }


    /**
     * POPをレンダリングします。
     * <p>サブクラスでレンダリング処理を変更する場合は、
     * <p>プレビューモードでも適切に動作するよう、通常は
     * {@link #render(PopContext, String[])}
     * の代わりにこのメソッドをオーバライドするようにして下さい。
     * また、{@link PopContext#isInPreviewMode()}がtrueの場合は、
     * システムの状態を変更しないように注意して下さい。
     * </p>
     *
     * @param context コンテキスト。
     * @param args 引数。
     * @param popScope POPの現在のロケールにおけるプロパティ値（プレビューモードの時は、
     * プレビュー対象のプロパティ値）が全て入ったMapオブジェクト。
     * プロパティ値を参照する場合は{@link #getProperty(Map, String)}
     * を使うなどしてこのMapから値を取り出すようにして下さい。
     * また、popScopeにオブジェクトをputすることもできます。
     * putしたオブジェクトはボディテンプレートから参照することができます。
     * @return レンダリングした結果。
     */
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        context = new PopContextWrapper(context, popScope);
        return new RenderedPop(getId(), getPopId(), element_.renderTitle(
            getProperty(popScope, PROP_TITLE), context), element_
            .renderBodyWithoutException(getProperty(popScope, PROP_BODY),
                getProperty(popScope, PROP_BODY_TYPE), context));
    }


    protected final String getProperty(Map<String, Object> popScope, String id)
    {
        return toString(popScope.get(id));
    }


    protected final String toString(Object object)
    {
        if (object == null) {
            return null;
        } else {
            return object.toString();
        }
    }


    protected final void setProperty(Map<String, Object> popScope, String id,
        Object value)
    {
        popScope.put(id, value);
    }


    protected final void removeProperty(Map<String, Object> popScope, String id)
    {
        popScope.remove(id);
    }


    /**
     * 指定されたMapにこのPOPのプロパティを登録します。
     * <p>現在のコンテキストにおけるロケールに対応するプロパティ値を、
     * 指定されたMapに登録します。
     * </p>
     *
     * @param popScope POPのプロパティを登録するためのMap。
     * @param context コンテキスト。
     * @return 引数に渡されたMap。
     */
    protected Map<String, Object> populatePropertiesTo(
        Map<String, Object> popScope, PopContext context)
    {
        Locale locale = context.getLocale();
        if (popScope != null) {
            PopPropertyMetaData[] metaDatas = getPropertyMetaDatas();
            for (int i = 0; i < metaDatas.length; i++) {
                String id = metaDatas[i].getId();
                popScope.put(id, getProperty(context, id, locale));
            }
            // 以下のプロパティはPOPによって編集対象外にすることが可能で、その場合
            // getPropertyMetaDatas()の返り値に含まれないためここで明示的に
            // レンダリング用のデータを用意するようにしている。
            putPropertyUnlessContained(popScope, context, PROP_TITLE, locale);
            putPropertyUnlessContained(popScope, context, PROP_BODY, locale);
            putPropertyUnlessContained(popScope, context, PROP_BODY_TYPE,
                locale);
        }
        return popScope;
    }


    protected final void putPropertyUnlessContained(
        Map<String, Object> popScope, PopContext context, String id,
        Locale locale)
    {
        if (!popScope.containsKey(id)) {
            popScope.put(id, getProperty(context, id, locale));
        }
    }


    protected final String getProperty(PopPropertyEntryBag bag,
        PopContext context, String id, String variant)
    {
        String value = bag.getProperty(id);
        if (value == null) {
            value = getProperty(context, id, variant);
        }
        return value;
    }


    final protected PageAlfr getPageAlfr()
    {
        return pageAlfr_;
    }


    final public PopElement getElement()
    {
        return element_;
    }


    final protected Plugin<?> getPlugin()
    {
        return element_.getPlugin();
    }


    public PopPropertyMetaData[] getPropertyMetaDatas()
    {
        return element_.getPropertyMetaDatas();
    }


    /**
     * プロパティの値を取得します。
     *
     * @param context POPの評価コンテキスト。
     * @param id プロパティのID。
     * @param variant バリアント。
     * @return プロパティの値。
     */
    public String getProperty(PopContext context, String id, String variant)
    {
        String value = null;
        if (!isBodyAndShouldReferBodyFileDirectly(id)) {
            PropertyAbility prop = getRootPagePropertyAbility();
            value = prop.getProperty(getPropertyKey(id), variant);
            if (value == null) {
                value = prop.getProperty(getPopPropertyKey(id), variant);
            }
        }
        if (value == null) {
            value = element_.getProperty(id, variant);
        }
        return value;
    }


    public String getProperty(PopContext context,
        Map<String, PopPropertyEntry> map, String id, String variant)
    {
        return getProperty(context, id, variant);
    }


    protected PropertyAbility getRootPagePropertyAbility()
    {
        return pageAlfr_.getRootPage(heimId_).getAbility(PropertyAbility.class);
    }


    /**
     * プロパティの値を取得します。
     *
     * @param context POPの評価コンテキスト。
     * @param id プロパティのID。
     * @param locale ロケール。
     * @return プロパティの値。
     */
    public String getProperty(PopContext context, String id, Locale locale)
    {
        String value = null;
        if (!isBodyAndShouldReferBodyFileDirectly(id)) {
            PropertyAbility prop = getRootPagePropertyAbility();
            value = prop.getProperty(getPropertyKey(id), locale);
            if (value == null) {
                value = prop.getProperty(getPopPropertyKey(id), locale);
            }
        }
        if (value == null) {
            value = element_.getProperty(id, locale);
        }
        return value;
    }


    public void setProperty(PopContext context, String id, String variant,
        String value)
    {
        PopPropertyMetaData metaData = element_.getPropertyMetaData(id);
        if (metaData == null) {
            return;
        }

        if (!metaData.isHumanReadable()) {
            variant = VARIANT_DEFAULT;
        }

        if (!isModified(context, id, variant, value)) {
            // Pageのプロパティとして無駄な値を設定しないようにこうしている。
            return;
        }

        setProperty0(context, id, variant, value);
    }


    boolean isModified(PopContext context, String id, String variant,
        String value)
    {
        return !isSimilar(value, getProperty(context, id, LocaleUtils
            .getLocale(variant)));
    }


    /**
     * プロパティの値を設定します。
     * <p>プロパティの設定方法を変更したい場合は、
     * 通常は{@link #setProperty(PopContext, String, String, String)}
     * ではなくこのメソッドをオーバライドして下さい。
     * </p>
     *
     * @param context POPの評価コンテキスト。
     * @param id プロパティのID。
     * @param variant バリアント。
     * @pram value 設定する値。nullを指定してはいけません。
     */
    protected void setProperty0(PopContext context, String id, String variant,
        String value)
    {
        if (isBodyAndShouldReferBodyFileDirectly(id)) {
            I18NResource resource = element_.getBodyResource();
            resource.getParent().mkdirs();
            try {
                IOUtils.writeString(resource.getOutputStream(variant), value,
                    "UTF-8", true);
            } catch (ResourceNotFoundException ex) {
                throw new IORuntimeException("Can't write property to: "
                    + resource);
            }
        } else if (element_.getPlugin().isUnderDevelopment()) {
            // 開発モードでは、プロパティをroot pageにしまわずに
            // plugin.xpropertiesにしまうようにする。
            storeProperty(id, variant, value);
        } else {
            getRootPagePropertyAbility().setProperty(getPropertyKey(id),
                variant, value);
        }
    }


    void storeProperty(String id, String variant, String value)
    {
        I18NProperties prop = new I18NProperties(element_.getPlugin()
            .getHomeSourceDirectory(), PluginDescriptor.PROPERTIES_BASENAME,
            PluginDescriptor.PROPERTIES_SUFFIX);
        prop.setProperty(element_.getPropertyKey(id), variant, value);
        try {
            prop.store();
        } catch (IOException ex) {
            throw new IORuntimeException(
                "Can't store property in plugin.xproperties", ex);
        }
    }


    boolean isBodyAndShouldReferBodyFileDirectly(String id)
    {
        return (element_.getPlugin().isUnderDevelopment()
            && PROP_BODY.equals(id) && element_.getBodyResource() != null);
    }


    public void setProperties(PopContext context, String variant,
        PopPropertyEntry[] entries)
    {
        for (int i = 0; i < entries.length; i++) {
            setProperty(context, entries[i].getId(), variant, entries[i]
                .getValue());
        }
    }


    public ValidationResult validateProperties(PopContext context,
        String variant, PopPropertyEntry[] entries)
    {
        ValidationResult result = new ValidationResult();
        PopPropertyEntryBag bag = new PopPropertyEntryBag(entries);

        String body = bag.getProperty(PROP_BODY);
        if (body != null) {
            String bodyType = bag.getProperty(PROP_BODY_TYPE);
            if (bodyType == null) {
                bodyType = getProperty(context, PROP_BODY_TYPE, LocaleUtils
                    .getLocale(variant));
            }
            try {
                element_.renderBody(body, bodyType, context);
            } catch (Throwable t) {
                result.addEntry(new ValidationResult.Entry(PROP_BODY,
                    ERROR_ASIS, t.toString()));
            }
        }

        return result;
    }


    public RenderedPop preview(PopContext context, String[] args,
        String variant, PopPropertyEntry[] entries)
    {
        Pop old = context.setPop(this);
        boolean oldPreviewMode = context.setInPreviewMode(true);
        try {
            return render(context, args, newPopPropertyEntryBag(context,
                variant, entries).getPropertyMap());
        } finally {
            context.setInPreviewMode(oldPreviewMode);
            context.setPop(old);
        }
    }


    protected PopPropertyEntryBag newPopPropertyEntryBag(PopContext context,
        String variant, PopPropertyEntry[] entries)
    {
        return new PopPropertyEntryBag(entries, this, context, variant);
    }


    boolean isSimilar(String value, String oldValue)
    {
        if (oldValue == null) {
            return (value.length() == 0);
        } else {
            return StringUtils.normalizeLineSeparator(value).equals(
                StringUtils.normalizeLineSeparator(oldValue));
        }
    }


    protected final boolean isEmpty(String value)
    {
        return (value == null || value.length() == 0);
    }


    public void removeProperty(PopContext context, String id, String variant)
    {
        PopPropertyMetaData metaData = element_.getPropertyMetaData(id);
        if (metaData == null) {
            return;
        }

        if (!metaData.isHumanReadable()) {
            variant = VARIANT_DEFAULT;
        }

        removeProperty0(context, id, variant);
    }


    /**
     * プロパティの値を削除します。
     * <p>プロパティ自体はなくならず、単に値が削除されます。
     * その結果、プロパティの見かけの値は初期値に戻ります。
     * </p>
     * <p>プロパティの値の削除方法を変更したい場合は、
     * 通常は{@link #removeProperty(PopContext, String, String)}
     * ではなくこのメソッドをオーバライドして下さい。
     * </p>
     *
     * @param context POPの評価コンテキスト。
     * @param id プロパティのID。
     * @param variant バリアント。
     */
    protected void removeProperty0(PopContext context, String id, String variant)
    {
        getRootPagePropertyAbility()
            .removeProperty(getPropertyKey(id), variant);
    }


    /**
     * POPのインスタンスが削除される時に呼ばれます。
     */
    public void notifyRemoving()
    {
        PropertyAbility prop = getRootPagePropertyAbility();
        PopPropertyMetaData[] metaDatas = getPropertyMetaDatas();
        String[] variants = prop.getVariants();
        for (int i = 0; i < metaDatas.length; i++) {
            for (int j = 0; j < variants.length; j++) {
                removeProperty0(null, metaDatas[i].getId(), variants[j]);
            }
        }
    }


    protected String getPropertyKey(String metaDataId)
    {
        return "pop." + getId() + "." + metaDataId;
    }


    protected String getPopPropertyKey(String metaDataId)
    {
        return "pop." + getPopId() + "." + metaDataId;
    }


    public int getHeimId()
    {
        return heimId_;
    }


    public void setHeimId(int heimId)
    {
        heimId_ = heimId;
    }
}
