package org.nds.dbdroid.remoting.front.server.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.nds.dbdroid.remoting.XStreamHelper;
import org.nds.dbdroid.remoting.controller.IServiceController;
import org.nds.dbdroid.service.HttpMethod;

public class ServiceDispatcherHandler implements HttpRequestHandler {

    private final IServiceController serviceController;

    public ServiceDispatcherHandler(IServiceController serviceController) {
        this.serviceController = serviceController;
    }

    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        System.out.println("***** BEGIN SERVICE DISPATCHER HANDLER *****");

        String httpMethod = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        System.out.println("# HTTP METHOD: " + httpMethod);

        try {
            String service = null;
            String method = null;
            List<Object> arguments = new ArrayList<Object>();

            String pathInfo = request.getRequestLine().getUri();
            if (pathInfo.startsWith("/")) {
                pathInfo = pathInfo.substring(1);
            }
            String[] split = pathInfo.split("/");
            if (split != null && split.length >= 2) {
                service = split[0];
                method = split[1];
                if (split.length > 2) { // any arguments in the URI
                    for (int i = 2; i < split.length; i++) {
                        arguments.add(split[i]);
                    }
                }

                HttpEntity entity = null;
                if (request instanceof HttpEntityEnclosingRequest) {
                    entity = ((HttpEntityEnclosingRequest) request).getEntity();
                    if (entity != null) {
                        String data = EntityUtils.toString(entity);
                        Object stringArgs = XStreamHelper.fromXML(data, null);
                        Object[] args = stringArgs instanceof Object[] ? (Object[]) stringArgs : new Object[] { stringArgs };
                        for (Object arg : args) {
                            arguments.add(arg);
                        }
                    }
                }

                System.out.println("SERVICE NAME: " + service);
                System.out.println("METHOD: " + method);
                System.out.println("ARGUMENTS: " + Arrays.toString(arguments.toArray()));
            }
            // Dispatch the requests to the good Services
            Object o = serviceController.invoke(service, method, arguments.toArray(), HttpMethod.getHttpMethod(httpMethod));
            String xml = XStreamHelper.toXML(o, null);

            StringEntity entity = new StringEntity(xml);
            entity.setContentType("text/xml");

            response.setHeader("Content-Type", "text/xml");
            response.setStatusCode(200);
            response.setEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("***** END SERVICE DISPATCHER HANDLER *****");
    }

}
