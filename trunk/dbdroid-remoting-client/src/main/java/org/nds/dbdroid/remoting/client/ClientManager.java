package org.nds.dbdroid.remoting.client;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.nds.dbdroid.dao.IAndroidDAO;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.remoting.proxy.IProxyFactory;
import org.nds.dbdroid.remoting.proxy.JavaProxyFactory;
import org.nds.dbdroid.service.IAndroidService;

public class ClientManager {

    private final ClientDataBaseManager dbManager;

    public ClientManager(InputStream config, String serverUrl) throws DBDroidException {
        this.dbManager = new ClientDataBaseManager(config, serverUrl);
    }

    public void loadConfig() throws DBDroidException {
        this.dbManager.open();
    }
    
	public void setClassLoader(ClassLoader classLoader) {
		this.dbManager.setClassLoader(classLoader);
	}

    public <T extends IAndroidService> T getService(Class<T> serviceClass) {
        if (serviceClass.isInterface()) {
            return createAndroidService(serviceClass);
        } else {
            return this.dbManager.getService(serviceClass);
        }
    }

    public final <T extends IAndroidDAO<?, ?>> T getDAO(Class<T> daoClass) {
        if (daoClass.isInterface()) {
            return createAndroidDAO(daoClass);
        } else {
            return this.dbManager.getDAO(daoClass);
        }
    }
    
    

    private <T extends IAndroidService> T createAndroidService(Class<T> service) {
        try {
            IProxyFactory<T> proxyFactory = createProxyFactory();
            return proxyFactory.createProxy(service, new ClientInvocationHandler(service, this));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return null;
    }

    private <T extends IAndroidDAO<?, ?>> T createAndroidDAO(Class<T> dao) {
        try {
            IProxyFactory<T> proxyFactory = createProxyFactory();
            return proxyFactory.createProxy(dao, new ClientInvocationHandler(dao, this));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected Object invoke(Object proxy, Method method, Object... args) {
        return this.dbManager.execute(proxy, method, args);
    }

    private <T> IProxyFactory<T> createProxyFactory() {
        return new JavaProxyFactory<T>();
    }

}
