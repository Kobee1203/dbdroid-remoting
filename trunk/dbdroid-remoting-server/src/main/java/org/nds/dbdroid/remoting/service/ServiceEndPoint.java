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

    private final Map<String, List<HttpMethod>> httpMethodsByMethod = new HashMap<String, List<HttpMethod>>();

    private final Map<String, String> methodByEndPoint = new HashMap<String, String>();

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

            httpMethodsByMethod.put(methodName, httpMethodList);
            methodByEndPoint.put(methodName, method.getName());
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public Object getServiceBean() {
        return serviceBean;
    }

    public boolean acceptHttpMethod(String endPoint, HttpMethod httpMethod) {
        if (endPoint == null || !httpMethodsByMethod.containsKey(endPoint)) {
            logger.error("EndPoint '" + endPoint + "' not found for the Service '" + serviceName + "' (" + serviceBean + ")");
            return false;
        }
        List<HttpMethod> httpMethodList = httpMethodsByMethod.get(endPoint);
        return httpMethodList.isEmpty() || httpMethodList.contains(httpMethod);
    }

    private String getRealMethod(String method) {
        return methodByEndPoint.get(method);
    }

    public Object invokeMethod(String method, Object[] arguments) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String m = getRealMethod(method);
        if (m == null) {
            throw new NoSuchMethodException("Cannot found method: " + method + "() on object: " + serviceBean.getClass().getName());
        }
        return MethodUtils.invokeMethod(serviceBean, m, arguments);
    }
}
