package org.seasar.kvasir.cms.extension;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.impl.FilteredPageFilter;
import org.seasar.kvasir.webapp.Dispatcher;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MUST, isa = PageFilter.class)
@Bean("page-filter")
public class PageFilterElement extends AbstractPageElement
{
    private static final Dispatcher[] DISPATCHERS_DEFAULTS = new Dispatcher[] { Dispatcher.REQUEST };

    private String dispatcher_;

    private Dispatcher[] dispatchers_ = DISPATCHERS_DEFAULTS;


    public Dispatcher[] getDispatchers()
    {
        return dispatchers_;
    }


    public String getDispatcher()
    {
        return dispatcher_;
    }


    @Attribute
    public void setDispatcher(String dispatcher)
    {
        dispatcher_ = dispatcher;
        String[] dispatchers = dispatcher.split(",");
        List<Dispatcher> dispatcherList = new ArrayList<Dispatcher>();
        for (int i = 0; i < dispatchers.length; i++) {
            dispatcherList.add(Dispatcher.valueOf(dispatchers[i]));
        }
        dispatchers_ = dispatcherList.toArray(new Dispatcher[0]);
    }


    public PageFilter getPageFilter()
    {
        return new FilteredPageFilter((PageFilter)getComponent(), getWhat(),
            getHow(), getExcept(), isNot(), isRegex(), getGardIdProvider(),
            dispatchers_);
    }
}
