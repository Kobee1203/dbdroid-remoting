package org.nds.dbdroid.remoting.controller;

import org.apache.log4j.Logger;
import org.nds.dbdroid.remoting.service.IServiceManager;
import org.nds.dbdroid.remoting.service.ServiceManager;
import org.nds.dbdroid.service.HttpMethod;

public class ServiceController implements IServiceController {

    private static final Logger logger = Logger.getLogger(DBDroidRemotingController.class);

    private IServiceManager serviceManager = new ServiceManager();

    public Object invoke(String service, String method, Object[] arguments, HttpMethod httpMethod) throws Exception {
        return serviceManager.invoke(service, method, arguments, httpMethod);
    }

    public void setServiceManager(IServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
}
