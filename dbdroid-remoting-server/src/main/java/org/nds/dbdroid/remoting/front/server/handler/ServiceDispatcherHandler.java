package org.nds.dbdroid.remoting.front.server.handler;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.nds.dbdroid.remoting.controller.IServiceController;

public class ServiceDispatcherHandler implements HttpRequestHandler {

    private final IServiceController serviceController;

    public ServiceDispatcherHandler(IServiceController serviceController) {
        this.serviceController = serviceController;
    }

    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        // Dispatch the requests to the good Services
        //serviceController.invoke(service, method, arguments, HttpMethod.getHttpMethod(method));
    }

}
