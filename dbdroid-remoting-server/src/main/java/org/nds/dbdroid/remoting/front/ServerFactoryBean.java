package org.nds.dbdroid.remoting.front;

import java.util.List;

import org.nds.dbdroid.remoting.controller.IServiceController;
import org.nds.dbdroid.remoting.controller.ServiceController;
import org.nds.dbdroid.remoting.front.server.Server;
import org.nds.dbdroid.remoting.front.server.ServerImpl;
import org.nds.dbdroid.remoting.front.server.handler.ServiceDispatcherHandler;
import org.nds.dbdroid.remoting.service.IServiceManager;
import org.nds.dbdroid.remoting.service.ServiceManager;
import org.nds.dbdroid.service.IAndroidService;

public class ServerFactoryBean {

    private Server server;

    private String urlPattern;

    private final IServiceManager serviceManager = new ServiceManager();

    public ServerFactoryBean() {
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    /**
     * Create a server with a service controller
     */
    public void create() {
        IServiceController serviceController = new ServiceController();
        serviceController.setServiceManager(serviceManager);

        server = new ServerImpl();
        server.register(urlPattern, new ServiceDispatcherHandler(serviceController));
    }

    public void registerService(IAndroidService service) {
        serviceManager.registerService(service, service.getClass().getName());
    }

    public void setServices(List<IAndroidService> services) {
        for (IAndroidService service : services) {
            registerService(service);
        }
    }
}
