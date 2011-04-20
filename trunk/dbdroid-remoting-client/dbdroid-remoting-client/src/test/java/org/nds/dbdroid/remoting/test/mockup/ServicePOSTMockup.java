package org.nds.dbdroid.remoting.test.mockup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.nds.dbdroid.remoting.XStreamHelper;

public class ServicePOSTMockup implements HttpRequestHandler {

    public void handle(HttpRequest paramHttpRequest, HttpResponse paramHttpResponse, HttpContext paramHttpContext) throws HttpException, IOException {
        String method = paramHttpRequest.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if ((!("POST".equals(method)) && !("PUT".equals(method)))) {
            throw new MethodNotSupportedException(method + " not supported by " + super.getClass().getName());
        }

        HttpEntity entity = null;
        if (paramHttpRequest instanceof HttpEntityEnclosingRequest) {
            entity = ((HttpEntityEnclosingRequest) paramHttpRequest).getEntity();
        }

        String data = "NO DATA from " + paramHttpRequest.getRequestLine().getUri();
        if (entity != null) {
            data = EntityUtils.toString(entity);
            Object o = XStreamHelper.fromXML(data, null);
            if (!(o instanceof List<?>)) {
                List<Object> list = new ArrayList<Object>();
                list.add(o);
                data = XStreamHelper.toXML(list, null);
            }
        }

        StringEntity e = new StringEntity(data);
        if (entity != null) {
            e.setContentType(entity.getContentType());
        }
        entity = e;

        paramHttpResponse.setStatusCode(200);
        paramHttpResponse.setEntity(entity);
    }

}
