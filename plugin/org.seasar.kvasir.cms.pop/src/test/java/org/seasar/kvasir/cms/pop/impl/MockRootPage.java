package org.seasar.kvasir.cms.pop.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.mock.MockPage;


/**
 * @author YOKOTA Takehiko
 */
public class MockRootPage extends MockPage
{
    public MockRootPage()
    {
        super(Page.ID_ROOT, PathId.HEIM_MIDGARD, "");
    }


    @SuppressWarnings("unchecked")
    public <P extends PageAbility> P getAbility(Class<P> key)
    {
        if (key == PropertyAbility.class) {
            return (P)new MockPropertyAbility();
        } else {
            return null;
        }
    }
}
