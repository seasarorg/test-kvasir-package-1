package org.seasar.kvasir.base.descriptor;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.descriptor.Extension;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.util.ClassUtils;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Default;


/**
 * <p><b>注意：</b>
 * getterメソッドが返すオブジェクトは変更しないで下さい。</p>
 * <p><b>同期化：</b>
 * このクラスが持つsetterメソッドは、
 * フレームワークによって非同期に呼び出さなれないことが保証されています。
 * getterメソッドは非同期に呼び出されることがあります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class AbstractElement
    implements ExtensionElement
{
    private Extension parent_;

    private String id_;

    private String fullId_;

    private String action_;

    private ActionType actionType_;

    private String before_;

    private String after_;


    /*
     * Object
     */

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer().append(getShortClassName())
            .append("{ ");
        if (getPlugin() != null) {
            sb.append("parent's plugin-id=(").append(getPlugin().getId())
                .append(")");
        }
        appendAttribute(
            appendAttribute(appendAttribute(appendAttribute(appendAttribute(sb,
                "id", id_), "full-id", fullId_), "action", action_), "before",
                before_), "after", after_).append(" }");
        return sb.toString();
    }


    String getShortClassName()
    {
        String name = getClass().getName();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            name = name.substring(dot + 1);
        }
        return name;
    }


    StringBuffer appendAttribute(StringBuffer sb, String name, Object value)
    {
        if (value != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(name).append("=\"").append(value).append("\"");
        }
        return sb;
    }


    /*
     * ExtensionElement
     */

    public Plugin<?> getPlugin()
    {
        if (parent_ == null) {
            return null;
        }
        PluginDescriptor descriptor = parent_.getParent();
        if (descriptor == null) {
            return null;
        }
        return descriptor.getPlugin();
    }


    public Extension getParent()
    {
        return parent_;
    }


    public String getId()
    {
        return id_;
    }


    public String getFullId()
    {
        if (fullId_ != null) {
            return fullId_;
        }

        if (id_ != null) {
            Plugin<?> plugin = getPlugin();
            if (plugin == null) {
                return null;
            }
            return plugin.getId() + "." + id_;
        }

        return null;
    }


    public String getAction()
    {
        return action_;
    }


    public ActionType getActionType()
    {
        return actionType_;
    }


    public String getBefore()
    {
        return (before_ == null ? ID_LAST : before_);
    }


    public String getAfter()
    {
        return (after_ == null ? ID_FIRST : after_);
    }


    public void setParent(Extension parent)
    {
        parent_ = parent;
    }


    @Attribute
    public void setId(String id)
    {
        id_ = id;
    }


    @Attribute
    public void setFullId(String fullId)
    {
        fullId_ = fullId;
    }


    @Attribute
    public void setAction(String action)
    {
        action_ = action;
        actionType_ = ActionType.getActionType(action);
    }


    @Attribute
    @Default(ID_LAST)
    public void setBefore(String before)
    {
        // beforeにあるIDがafterにもある場合はafterから除外しておく。
        // （でないとループしてしまう。）
        Set<String> beforeSet = constructSet(before);
        Set<String> afterSet = constructSet(getAfter());
        for (Iterator<String> itr = beforeSet.iterator(); itr.hasNext();) {
            afterSet.remove(itr.next());
        }

        before_ = before;
        after_ = constructString(afterSet);
    }


    @Attribute
    @Default(ID_FIRST)
    public void setAfter(String after)
    {
        // afterにあるIDがbeforeにもある場合はbeforeから除外しておく。
        // （でないとループしてしまう。）
        Set<String> afterSet = constructSet(after);
        Set<String> beforeSet = constructSet(getBefore());
        for (Iterator<String> itr = afterSet.iterator(); itr.hasNext();) {
            beforeSet.remove(itr.next());
        }

        after_ = after;
        before_ = constructString(beforeSet);
    }


    Set<String> constructSet(String str)
    {
        Set<String> set = new LinkedHashSet<String>();
        StringTokenizer st = new StringTokenizer(str, ",");
        while (st.hasMoreTokens()) {
            set.add(st.nextToken().trim());
        }
        return set;
    }


    String constructString(Set<String> set)
    {
        StringBuffer sb = new StringBuffer();
        for (Iterator<String> itr = set.iterator(); itr.hasNext();) {
            String id = itr.next();
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(id);
        }
        return sb.toString();
    }


    public Object getComponent()
    {
        ComponentContainer container = getPlugin().getComponentContainer();
        if (id_ == null || !container.hasComponent(id_)) {
            return null;
        }
        Object component = container.getComponent(id_);
        if (component != null) {
            ClassUtils.setProperty(component, this);
        }
        return component;
    }
}
