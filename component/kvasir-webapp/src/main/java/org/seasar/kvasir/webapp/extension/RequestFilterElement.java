package org.seasar.kvasir.webapp.extension;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.webapp.Dispatcher;
import org.seasar.kvasir.webapp.filter.RequestFilter;
import org.seasar.kvasir.webapp.filter.impl.FilteredRequestFilter;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Bean("request-filter")
@Component(bindingType = BindingType.MUST, isa = RequestFilter.class)
public class RequestFilterElement extends AbstractPhasedElement
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


    public RequestFilter getRequestFilter()
    {
        return new FilteredRequestFilter((RequestFilter)getComponent(),
            dispatchers_);
    }
}
