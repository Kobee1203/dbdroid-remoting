package org.nds.dbdroid.remoting.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nds.dbdroid.dao.IAndroidDAO;
import org.nds.dbdroid.service.HttpMethod;
import org.nds.dbdroid.service.IAndroidService;
import org.openide.util.Lookup;

public class ServiceManager implements IServiceManager {

    private static final Logger logger = Logger.getLogger(AndroidServiceManager.class);

    private final Map<String, ServiceEndPoint> serviceMap = new HashMap<String, ServiceEndPoint>();

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        T service = (T) serviceMap.get(serviceClass);
        if (service == null) {
            // NetBeans Lookup to find Service/DAO implementation
            Lookup lookup = Lookup.getDefault();
            Lookup.Template<T> template = new Lookup.Template<T>(serviceClass);
            Lookup.Item<T> item = lookup.lookupItem(template);
            service = item.getInstance();
            if (service != null) {
                registerService(service, service.getClass().getName());
            }
        }

        return service;
    }

    public void registerService(Object serviceBean, String beanName) {
        if (serviceBean instanceof IAndroidService || serviceBean instanceof IAndroidDAO) {
            ServiceEndPoint serviceEndPoint = new ServiceEndPoint(serviceBean);
            serviceMap.put(serviceEndPoint.getServiceName(), serviceEndPoint);
            logger.info("Load Android Service " + beanName + " (" + serviceEndPoint.getServiceBean().getClass() + ")");
        }
    }

    public Object invoke(String service, String method, Object[] arguments, HttpMethod httpMethod) throws Exception {
        Object o = null;

        ServiceEndPoint serviceEndPoint = serviceMap.get(service);
        if (serviceEndPoint != null && serviceEndPoint.acceptHttpMethod(method, httpMethod)) {
            o = serviceEndPoint.invokeMethod(method, arguments);
        } else {
            logger.error("Service '" + service + "' not found.");
        }

        return o;
    }
}
