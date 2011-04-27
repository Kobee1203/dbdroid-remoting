package org.nds.dbdroid.remoting.controller;

import org.apache.log4j.Logger;
import org.nds.dbdroid.remoting.service.AndroidServiceManager;
import org.nds.dbdroid.remoting.service.IServiceManager;
import org.nds.dbdroid.service.HttpMethod;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringServiceController implements IServiceController, InitializingBean {

    private static final Logger logger = Logger.getLogger(DBDroidRemotingController.class);

    @Autowired
    private IServiceManager serviceManager;

    public void afterPropertiesSet() throws Exception {
        if (serviceManager == null) {
            logger.warn("AndroidServiceManager must be initialized in the Spring configuration if the Android Services must be registered automatically -> Android Service Manager initialized with just the NetBeans Lookup.");
            serviceManager = new AndroidServiceManager();
        }
    }

    public Object invoke(String service, String method, Object[] arguments, HttpMethod httpMethod) throws Exception {
        return serviceManager.invoke(service, method, arguments, httpMethod);
    }

    public void setServiceManager(IServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
}
