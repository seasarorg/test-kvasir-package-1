package org.seasar.kvasir.base.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.Test;

import org.seasar.cms.beantable.Beantable;
import org.seasar.cms.beantable.impl.BeantableImpl;
import org.seasar.cms.database.identity.impl.H2Identity;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class DaoPluginIT extends KvasirPluginTestCase<DaoPlugin>
{
    private DaoPlugin target_;


    @Override
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.base.dao";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(DaoPluginIT.class);
    }


    @Override
    protected void setUp()
        throws Exception
    {
        target_ = (DaoPlugin)getPlugin();
    }


    public void testGetDataSource()
        throws Exception
    {
        DataSource ds = getComponent(DataSource.class);
        assertNotNull(ds);
        Connection con = null;
        try {
            con = ds.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("Can't get connection");
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }


    public void testGetIdentity()
        throws Exception
    {
        assertTrue(target_.getIdentity() instanceof H2Identity);
    }


    public void test_BeantableImplに正しくアスペクトがかかっていること()
        throws Exception
    {
        assertFalse(target_.getComponentContainer().getComponent(
            Beantable.class).getClass().getName().equals(
            BeantableImpl.class.getName()));
    }
}
