package org.seasar.kvasir.base.dao.impl;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.cms.beantable.Beantable;
import org.seasar.cms.database.identity.Identity;
import org.seasar.cms.database.identity.IdentitySelector;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.dao.DaoPlugin;
import org.seasar.kvasir.base.dao.extension.DatabaseSystemElement;
import org.seasar.kvasir.base.dao.extension.PersistentBeanElement;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.plugin.PluginUtils;


/**
 * <p><b>同期化：</b>
 * このクラスは<code>start()</code>
 * が呼び出された後はスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class DaoPluginImpl extends AbstractPlugin<EmptySettings>
    implements DaoPlugin
{
    private Map<String, DatabaseSystemElement> elementByProductIdMap_;

    private Identity identity_;

    private Beantable<?>[] beantables_;

    private Map<String, Beantable<?>> beantableMap_;


    /*
     * DaoPlugin
     */

    public Identity getIdentity()
    {
        return identity_;
    }


    public String getDatabaseProductId()
    {
        return identity_.getDatabaseProductId();
    }


    public Beantable<?> getBeantable(String tableName)
    {
        return beantableMap_.get(tableName.toUpperCase());
    }


    /*
     * AbstractPlugin
     */

    @Override
    protected boolean doStart()
    {
        elementByProductIdMap_ = new HashMap<String, DatabaseSystemElement>();
        DatabaseSystemElement[] databaseSystems = getExtensionElements(DatabaseSystemElement.class);
        for (int i = 0; i < databaseSystems.length; i++) {
            DatabaseSystemElement element = databaseSystems[i];
            String id = element.getId();
            if (elementByProductIdMap_.containsKey(id)) {
                log_.warn("Product-id duplication: " + id);
            }
            elementByProductIdMap_.put(id, element);
        }

        for (Iterator<DatabaseSystemElement> itr = elementByProductIdMap_
            .values().iterator(); itr.hasNext();) {
            DatabaseSystemElement element = itr.next();
            String driverClass = element.getDriverClass();
            if (driverClass != null) {
                ClassLoader cl = element.getPlugin().getInnerClassLoader();
                try {
                    Class.forName(driverClass, true, cl);
                } catch (ClassNotFoundException ex) {
                    log_.error("Can't resolve jdbc driver class: "
                        + driverClass, ex);
                    return false;
                }
            }
        }

        setUpIdentity();

        List<Beantable<?>> beantableList = new ArrayList<Beantable<?>>();
        beantableMap_ = new HashMap<String, Beantable<?>>();
        PersistentBeanElement[] persistentBeans = getExtensionElements(PersistentBeanElement.class);
        for (int i = 0; i < persistentBeans.length; i++) {
            PersistentBeanElement element = persistentBeans[i];
            Class<?> beanClass;
            try {
                beanClass = PluginUtils.getClass(element.getParent()
                    .getParent().getPlugin(), element.getClassName());
            } catch (ClassNotFoundException ex) {
                log_.error("Class not found: className="
                    + element.getClassName(), ex);
                return false;
            }
            if (beanClass == null) {
                log_.error("Persistent bean class name"
                    + " is not specified with 'class-name' attribute");
                return false;
            }
            Beantable<?> beantable = newBeantable(beanClass);
            beantableList.add(beantable);
        }
        beantables_ = beantableList.toArray(new Beantable[0]);

        for (int i = 0; i < beantables_.length; i++) {
            String beanName = beantables_[i].getBeanClass().getName();
            if (log_.isInfoEnabled()) {
                log_.info("Activating beantable for bean: " + beanName);
            }
            try {
                beantables_[i].activate();
                if (log_.isInfoEnabled()) {
                    log_.info("Activated.");
                }
            } catch (SQLException ex) {
                log_.error("Can't activate" + " beantable for bean: "
                    + beanName, ex);
                return false;
            }
            try {
                beantables_[i].update(false);
            } catch (SQLException ex) {
                log_.error("Can't update table: " + beanName, ex);
            }
            beantableMap_.put(beantables_[i].getTableMetaData().getName()
                .toUpperCase(), beantables_[i]);
        }

        return true;
    }


    @SuppressWarnings("unchecked")
    public <T> Beantable<T> newBeantable(Class<T> beanClass)
    {
        Beantable<T> beantable = getComponentContainer().getComponent(
            Beantable.class);
        beantable.setBeanClass(beanClass);
        return beantable;
    }


    void setUpIdentity()
    {
        identity_ = getComponentContainer()
            .getComponent(IdentitySelector.class).getIdentity();
    }


    @Override
    protected void doStop()
    {
        identity_ = null;

        if (elementByProductIdMap_ != null) {
            Set<String> driverClassSet = new HashSet<String>();
            for (Iterator<DatabaseSystemElement> itr = elementByProductIdMap_
                .values().iterator(); itr.hasNext();) {
                DatabaseSystemElement metaData = itr.next();
                String driverClass = metaData.getDriverClass();
                driverClassSet.add(driverClass);
            }
            for (Enumeration<Driver> enm = DriverManager.getDrivers(); enm
                .hasMoreElements();) {
                Driver driver = enm.nextElement();
                if (driverClassSet.contains(driver.getClass().getName())) {
                    try {
                        DriverManager.deregisterDriver(driver);
                    } catch (SQLException ex) {
                        log_.warn("Can't deregister JDBC driver: "
                            + driver.getClass(), ex);
                    }
                }
            }
        }

        beantables_ = null;
        beantableMap_ = null;
        elementByProductIdMap_ = null;
    }

    /*
     * for framework
     */
}
