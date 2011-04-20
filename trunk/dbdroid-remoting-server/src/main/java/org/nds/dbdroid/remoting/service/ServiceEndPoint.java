package org.nds.dbdroid.remoting.service;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.nds.dbdroid.reflect.utils.AnnotationUtils;
import org.nds.dbdroid.reflect.utils.ReflectUtils;
import org.nds.dbdroid.service.EndPoint;
import org.nds.dbdroid.service.HttpMethod;

public class ServiceEndPoint {

    private final Object serviceBean;

    private String serviceName;

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
            EndPoint ep = AnnotationUtils.getAnnotation(method, EndPoint.class);
            if (ep != null) {
                String epValue = ep.value();
                if (!StringUtils.isBlank(epValue)) {
                    methodName = epValue;
                }
            }

            HttpMethod[] methodHttpMethod = ep.httpMethod();
            if (methodHttpMethod.length > 0) {

            } else if (servicerHttpMethod.length > 0) {

            } else {

            }
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public Object getServiceBean() {
        return serviceBean;
    }
}
