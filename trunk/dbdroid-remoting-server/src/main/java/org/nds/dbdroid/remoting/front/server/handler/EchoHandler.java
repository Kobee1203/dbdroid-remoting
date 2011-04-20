package org.nds.dbdroid.remoting.front.server.handler;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

public class EchoHandler implements HttpRequestHandler {

    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if ((!("GET".equals(method))) && (!("POST".equals(method))) && (!("PUT".equals(method)))) {
            throw new MethodNotSupportedException(method + " not supported by " + super.getClass().getName());
        }

        HttpEntity entity = null;
        if (request instanceof HttpEntityEnclosingRequest) {
            entity = ((HttpEntityEnclosingRequest) request).getEntity();
        }
        byte[] data;
        if (entity == null) {
            data = new byte[0];
        } else {
            data = EntityUtils.toByteArray(entity);
        }

        ByteArrayEntity bae = new ByteArrayEntity(data);
        if (entity != null) {
            bae.setContentType(entity.getContentType());
        }
        entity = bae;

        response.setStatusCode(200);
        response.setEntity(entity);
    }
}
