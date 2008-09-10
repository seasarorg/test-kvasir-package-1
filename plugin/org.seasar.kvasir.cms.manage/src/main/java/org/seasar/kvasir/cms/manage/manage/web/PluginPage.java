package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;
import net.skirnir.freyja.render.html.OptionTag;
import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.MalformedValueException;
import net.skirnir.xom.PropertyDescriptor;
import net.skirnir.xom.TargetNotFoundException;

import org.seasar.cms.ymir.Request;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.util.XOMUtils;
import org.seasar.kvasir.cms.manage.manage.dto.ElementDto;
import org.seasar.kvasir.page.SecurityRuntimeException;


public class PluginPage extends MainPanePage
{
    public static final String PARAM_PREFIX_NAME = "name";

    public static final String PARAM_PREFIX_NAME_DELIM = PARAM_PREFIX_NAME
        + ElementDto.PARAM_DELIM;

    private PluginAlfr pluginAlfr_;

    private String pluginId_;

    private ElementDto settings_;

    private boolean reset_;


    public void setPluginAlfr(PluginAlfr pluginAlfr)
    {
        pluginAlfr_ = pluginAlfr;
    }


    public OptionTag[] getOptions()
    {
        Plugin<?>[] plugins = pluginAlfr_.getPlugins();
        List<OptionTag> optionList = new ArrayList<OptionTag>();
        for (int i = 0; i < plugins.length; i++) {
            optionList.add(new OptionTag(plugins[i].getId(), plugins[i].getId()
                + " ("
                + plugins[i].resolveString(
                    plugins[i].getDescriptor().getName(), getLocale()) + ")"));
        }

        OptionTag[] options = optionList.toArray(new OptionTag[0]);
        Arrays.sort(options, new Comparator<OptionTag>() {
            public int compare(OptionTag o1, OptionTag o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return options;
    }


    public Object do_execute()
    {
        checkPermission();

        return "/plugin.html";
    }


    protected void checkPermission()
    {
        if (!isInMidgard()) {
            throw new SecurityRuntimeException();
        }
    }


    public void setPluginId(String pluginId)
    {
        pluginId_ = pluginId;
    }


    public void setReset(boolean reset)
    {
        reset_ = reset;
    }


    public Object do_configurationForm()
    {
        checkPermission();

        if (pluginId_ == null) {
            return "content:";
        }

        Plugin<?> plugin = pluginAlfr_.getPlugin(pluginId_);
        if (plugin == null) {
            return "content:";
        }

        if (Request.METHOD_POST.equals(getYmirRequest().getMethod()) && reset_) {
            plugin.resetSettings();
            setTransferredNotes(new Notes().add(new Note(
                "app.note.plugin.reset.succeed")));
        }

        Object settings = plugin.getSettings();
        if (settings == null) {
            return "content:"
                + getPlugin().resolveString(
                    "%app.note.plugin.configurationNotFound", getLocale());
        }

        settings_ = new ElementDto(settings, XOMUtils.newMapper()
            .getBeanAccessor(settings), getLocale(), "", "", PARAM_PREFIX_NAME,
            null, null);

        return "/plugin.configurationForm.html";
    }


    public String getPluginId()
    {
        return pluginId_;
    }


    public ElementDto getSettings()
    {
        return settings_;
    }


    @SuppressWarnings("unchecked")
    public String do_updateConfiguration()
    {
        checkPermission();

        if (pluginId_ == null) {
            return "content:";
        }

        do {
            Plugin<?> plugin = pluginAlfr_.getPlugin(pluginId_);
            if (plugin == null) {
                setTransferredNotes(new Notes().add(new Note(
                    "app.note.plugin.unknownPluginId", pluginId_)));
                break;
            }

            Object settings = plugin.newSettings();
            if (settings == null) {
                return "content:";
            }

            try {
                buildSettings(settings, XOMUtils.newMapper().getBeanAccessor(
                    plugin.getSettingsClass()), PARAM_PREFIX_NAME);
            } catch (Throwable t) {
                if (log_.isDebugEnabled()) {
                    log_.debug("Can't build settings object: pluginId="
                        + pluginId_, t);
                }
                setTransferredNotes(new Notes().add(new Note(
                    "app.note.plugin.failure")));
                break;
            }

            try {
                // FIXME キャストしないとコンパイルエラーになる。何で？
                ((Plugin<Object>)plugin).storeSettings(settings);
            } catch (Throwable t) {
                if (log_.isDebugEnabled()) {
                    log_.debug("Can't store settings object: pluginId="
                        + pluginId_, t);
                }
                setTransferredNotes(new Notes().add(new Note(
                    "app.note.plugin.failure")));
                break;
            }

            setTransferredNotes(new Notes().add(new Note(
                "app.note.plugin.updateConfiguration.succeed")));
        } while (false);

        return "/plugin.notes.html";
    }


    void buildSettings(Object element, BeanAccessor accessor, String name)
        throws TargetNotFoundException, MalformedValueException
    {
        String[] names = accessor.getAttributeNames();
        for (int i = 0; i < names.length; i++) {
            String paramName = name + ElementDto.PARAM_DELIM
                + ElementDto.PARAM_TYPE_ATTRIBUTE + names[i];
            String value = getYmirRequest().getParameter(paramName);
            if (shouldSetValue(accessor.getAttributeDescriptor(names[i]), value)) {
                accessor.setAttribute(element, names[i], value);
            }
        }

        names = accessor.getChildNames();
        for (int i = 0; i < names.length; i++) {
            PropertyDescriptor descriptor = accessor
                .getChildDescriptor(names[i]);
            String paramName = name + ElementDto.PARAM_DELIM
                + ElementDto.PARAM_TYPE_CHILD + names[i];
            if (descriptor.isMultiple()) {
                String[] values = getYmirRequest()
                    .getParameterValues(paramName);
                if (values != null) {
                    Integer[] indices = toSortedIntArray(values);
                    for (int j = 0; j < indices.length; j++) {
                        Object child;
                        if (descriptor.isScalar()) {
                            child = getYmirRequest().getParameter(
                                paramName + "[" + indices[j] + "]");
                        } else {
                            BeanAccessor childAccessor = descriptor
                                .getTypeAccessor();
                            child = childAccessor.newInstance();
                            buildSettings(child, childAccessor, paramName + "["
                                + indices[j] + "]");
                        }
                        if (shouldSetValue(descriptor, child)) {
                            accessor.setChild(element, names[i], child);
                        }
                    }
                }
            } else {
                Object child;
                if (descriptor.isScalar()) {
                    child = getYmirRequest().getParameter(paramName);
                } else {
                    BeanAccessor childAccessor = descriptor.getTypeAccessor();
                    child = childAccessor.newInstance();
                    buildSettings(child, childAccessor, paramName);
                }
                if (shouldSetValue(descriptor, child)) {
                    accessor.setChild(element, names[i], child);
                }
            }
        }
    }


    boolean shouldSetValue(PropertyDescriptor descriptor, Object value)
    {
        if (value == null) {
            return false;
        }
        String defaultValue = descriptor.getDefault();
        if (defaultValue == null) {
            if ("".equals(value)) {
                return false;
            }
        } else if (defaultValue.equals(String.valueOf(value))) {
            return false;
        }
        return true;
    }


    Integer[] toSortedIntArray(String[] values)
    {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < values.length; i++) {
            try {
                list.add(Integer.valueOf(values[i]));
            } catch (NumberFormatException ignore) {
            }
        }
        Integer[] integers = list.toArray(new Integer[0]);
        Arrays.sort(integers);
        if (integers.length > 0) {
            // 最後の一個はプロトタイプなので除去しておく。
            Integer[] newIntegers = new Integer[integers.length - 1];
            System.arraycopy(integers, 0, newIntegers, 0, newIntegers.length);
            integers = newIntegers;
        }
        return integers;
    }
}
