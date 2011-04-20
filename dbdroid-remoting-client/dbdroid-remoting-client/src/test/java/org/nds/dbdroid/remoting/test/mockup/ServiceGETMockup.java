package org.nds.dbdroid.remoting.test.mockup;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class ServiceGETMockup implements HttpRequestHandler {

    public void handle(HttpRequest paramHttpRequest, HttpResponse paramHttpResponse, HttpContext paramHttpContext) throws HttpException, IOException {
        String method = paramHttpRequest.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if ((!("GET".equals(method)))) {
            throw new MethodNotSupportedException(method + " not supported by " + super.getClass().getName());
        }

        if ("/dbdroid/index".equals(paramHttpRequest.getRequestLine().getUri())) {
            HttpEntity entity = new StringEntity("request successfully on " + paramHttpRequest.getRequestLine().getUri(), "UTF-8");
            paramHttpResponse.setEntity(entity);
        }
        paramHttpResponse.setStatusCode(200);
    }

}
