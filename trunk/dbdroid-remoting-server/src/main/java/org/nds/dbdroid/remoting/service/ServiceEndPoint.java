package org.nds.dbdroid.remoting.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nds.dbdroid.reflect.utils.AnnotationUtils;
import org.nds.dbdroid.reflect.utils.ReflectUtils;
import org.nds.dbdroid.service.EndPoint;
import org.nds.dbdroid.service.HttpMethod;

public class ServiceEndPoint {

    private static final Logger logger = Logger.getLogger(AndroidServiceManager.class);

    private final Object serviceBean;

    private String serviceName;

    private final Map<String, List<HttpMethod>> httpMethodsByEndPoint = new HashMap<String, List<HttpMethod>>();

    private final Map<String, Method> methodByEndPoint = new HashMap<String, Method>();

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
            String endPointValue = method.getName();
            List<HttpMethod> httpMethodList = new ArrayList<HttpMethod>();

            EndPoint ep = AnnotationUtils.getAnnotation(method, EndPoint.class);
            if (ep != null) {
                String epValue = ep.value();
                if (!StringUtils.isBlank(epValue)) {
                    endPointValue = epValue;
                }

                HttpMethod[] methodHttpMethod = ep.httpMethod();
                if (methodHttpMethod.length > 0) {
                    httpMethodList.addAll(Arrays.asList(methodHttpMethod));
                } else if (servicerHttpMethod.length > 0) {
                    httpMethodList.addAll(Arrays.asList(servicerHttpMethod));
                }
            }

            httpMethodsByEndPoint.put(endPointValue, httpMethodList);
            methodByEndPoint.put(endPointValue, method);
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public Object getServiceBean() {
        return serviceBean;
    }

    public boolean acceptHttpMethod(String endPoint, HttpMethod httpMethod) {
        if (endPoint == null || !httpMethodsByEndPoint.containsKey(endPoint)) {
            logger.error("EndPoint '" + endPoint + "' not found for the Service '" + serviceName + "' (" + serviceBean + ")");
            return false;
        }
        List<HttpMethod> httpMethodList = httpMethodsByEndPoint.get(endPoint);
        return httpMethodList.isEmpty() || httpMethodList.contains(httpMethod);
    }

    private Method getRealMethod(String method) {
        return methodByEndPoint.get(method);
    }

    public Object invokeMethod(String method, Object[] arguments) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method m = getRealMethod(method);
        if (m == null) {
            throw new NoSuchMethodException("Cannot found method: " + method + "() on object: " + serviceBean.getClass().getName());
        }
        List<Object> args = new ArrayList<Object>();
        for (Object arg : arguments) {
            convert to the good primitive
            args.add(arg);
        }
        return m.invoke(serviceBean, args.toArray());
        // return MethodUtils.invokeMethod(serviceBean, m.getName(), arguments);
    }
}
