package org.nds.dbdroid.remoting.service;

import org.nds.dbdroid.service.HttpMethod;

public interface IServiceManager {

    /**
     * Call a Service.
     * @param <T>
     * @param serviceClass
     * @return
     */
    <T> T getService(Class<T> serviceClass);

    /**
     * Register a Service 
     * @param serviceBean
     * @param beanName
     */
    void registerService(Object serviceBean, String beanName);

    /**
     * Invoke request, and call the asked Service
     * @param request current HTTP request
     * @param response current HTTP response
     * @return 
     * @throws Exception in case of errors
     */
    Object invoke(String serviceClass, String method, Object[] arguments, HttpMethod httpMethod) throws Exception;
}
