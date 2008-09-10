package org.seasar.kvasir.cms.zpt.impl;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.impl.VariableResolverImpl.EntryImpl;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class VariableResolverAdapter
    implements VariableResolver
{
    private org.seasar.kvasir.util.el.VariableResolver kvasirVariableResolver_;

    private VariableResolver parent_;


    public VariableResolverAdapter(
        org.seasar.kvasir.util.el.VariableResolver kvasirVariableResolver,
        VariableResolver parent)
    {
        kvasirVariableResolver_ = kvasirVariableResolver;
        parent_ = parent;
    }


    /*
     * VariableResolver
     */

    public Object getVariable(TemplateContext context, String name)
    {
        Object value = kvasirVariableResolver_.getValue(name);
        if (value != null) {
            return value;
        } else if (parent_ != null) {
            return parent_.getVariable(context, name);
        }
        return null;
    }


    public void setVariable(String name, Object value)
    {
        if (parent_ != null) {
            parent_.setVariable(name, value);
        }
    }


    public void removeVariable(String name)
    {
        if (parent_ != null) {
            parent_.removeVariable(name);
        }
    }


    public boolean containsVariable(String name)
    {
        if (kvasirVariableResolver_.getValue(name) != null) {
            return true;
        } else if (parent_ != null) {
            return parent_.containsVariable(name);
        }
        return false;
    }


    public String[] getVariableNames()
    {
        if (parent_ != null) {
            return parent_.getVariableNames();
        } else {
            return new String[0];
        }
    }


    public Entry getVariableEntry(TemplateContext context, String name)
    {
        Object value = kvasirVariableResolver_.getValue(name);
        if (value != null) {
            return new EntryImpl(name, value);
        } else if (parent_ != null) {
            return parent_.getVariableEntry(context, name);
        }
        return null;
    }
}
