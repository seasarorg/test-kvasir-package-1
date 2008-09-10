package org.seasar.kvasir.page.type.mock;

import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.type.Directory;


public class MockDirectory extends MockPage
    implements Directory
{

    public MockDirectory(int id, String pathname)
    {
        this(id, PathId.HEIM_MIDGARD, pathname);
    }


    public MockDirectory(int id, int heimId, String pathname)
    {
        super(id, heimId, pathname);
        setNode(true);
    }
}
