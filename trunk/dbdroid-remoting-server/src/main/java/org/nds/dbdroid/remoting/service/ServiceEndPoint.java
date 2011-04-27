package org.nds.dbdroid.remoting.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.apache.log4j.Logger;
import org.nds.dbdroid.reflect.utils.AnnotationUtils;
import org.nds.dbdroid.reflect.utils.ReflectUtils;
import org.nds.dbdroid.service.EndPoint;
import org.nds.dbdroid.service.HttpMethod;

public class ServiceEndPoint {

    private static final Logger logger = Logger.getLogger(AndroidServiceManager.class);

    private final Object serviceBean;

    private String serviceName;

    private final Map<String, List<HttpMethod>> methodMap = new HashMap<String, List<HttpMethod>>();

    public ServiceEndPoint(Object serviceBean) {
        this.serviceBean = serviceBean;
        Class<?> cl = serviceBean.getClass().getInterfaces()[0];

        this.serviceName = cl.getName();
        EndPoint endPoint = AnnotationUtils.getAnnotation(cl, EndPoint.class);
        if (endPoint != null) {
            String endPointValue = endPoint.value();
            if (!StringUtils.isBlank(endPointValue)) {
                this.serviceName = endPointValue;
            }
        }

        HttpMethod[] servicerHttpMethod = endPoint.httpMethod();

        Method[] methods = ReflectUtils.getMethods(cl);
        for (Method method : methods) {
            String methodName = method.getName();
            List<HttpMethod> httpMethodList = new ArrayList<HttpMethod>();

            EndPoint ep = AnnotationUtils.getAnnotation(method, EndPoint.class);
            if (ep != null) {
                String epValue = ep.value();
                if (!StringUtils.isBlank(epValue)) {
                    methodName = epValue;
                }

                HttpMethod[] methodHttpMethod = ep.httpMethod();
                if (methodHttpMethod.length > 0) {
                    httpMethodList.addAll(Arrays.asList(methodHttpMethod));
                } else if (servicerHttpMethod.length > 0) {
                    httpMethodList.addAll(Arrays.asList(servicerHttpMethod));
                }
            }

            methodMap.put(methodName, httpMethodList);
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public Object getServiceBean() {
        return serviceBean;
    }

    public boolean acceptHttpMethod(String method, HttpMethod httpMethod) {
        if (!methodMap.containsKey(method)) {
            logger.error("Method '" + method + "' not found for the Service '" + serviceName + "' (" + serviceBean + ")");
            return false;
        }
        List<HttpMethod> httpMethodList = methodMap.get(method);
        return httpMethodList.isEmpty() || httpMethodList.contains(httpMethod);
    }

    public Object invokeMethod(String method, Object[] arguments) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return MethodUtils.invokeMethod(serviceBean, method, arguments);
    }
}
