package org.nds.dbdroid.remoting.controller;

import org.nds.dbdroid.remoting.service.IServiceManager;
import org.nds.dbdroid.service.EndPoint;
import org.nds.dbdroid.service.HttpMethod;

public interface IServiceController {

    /**
     * Invoke Service according to the arguments
     * @param request current HTTP request
     * @param response current HTTP response
     * @throws Exception in case of errors

     * @param service : Service name (can be class name, or a name specified by {@link EndPoint} annotation
     * @param method       : method invoked in the Service
     * @param arguments    : optional arguments for the Service method
     * @param httpMethod   : HTTP Method
     * @return 
     * @throws Exception
     */
    Object invoke(String service, String method, Object[] arguments, HttpMethod httpMethod) throws Exception;

    /**
     * Sets the ServiceManager
     * @param serviceManager
     */
    void setServiceManager(IServiceManager serviceManager);
}
