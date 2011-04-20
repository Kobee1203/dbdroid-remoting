package org.nds.dbdroid.remoting.front.server.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class RandomHandler implements HttpRequestHandler {

    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if ((!("GET".equals(method))) && (!("HEAD".equals(method)))) {
            throw new MethodNotSupportedException(method + " not supported by " + super.getClass().getName());
        }

        String uri = request.getRequestLine().getUri();
        int slash = uri.lastIndexOf(47);
        int length = -1;
        if (slash < uri.length() - 1) {
            try {
                length = Integer.parseInt(uri.substring(slash + 1));

                if (length < 0) {
                    response.setStatusCode(400);
                    response.setReasonPhrase("LENGTH " + length);
                }
            } catch (NumberFormatException nfx) {
                response.setStatusCode(400);
                response.setReasonPhrase(nfx.toString());
            }
        } else {
            length = 1 + (int) (Math.random() * 79.0D);
        }

        if (length < 0) {
            return;
        }
        response.setStatusCode(200);

        if (!("HEAD".equals(method))) {
            RandomEntity entity = new RandomEntity(length);
            entity.setContentType("text/plain; charset=US-ASCII");
            response.setEntity(entity);
        } else {
            response.setHeader("Content-Type", "text/plain; charset=US-ASCII");

            response.setHeader("Content-Length", String.valueOf(length));
        }
    }

    public static class RandomEntity extends AbstractHttpEntity {
        private static final byte[] RANGE;
        protected final long length;

        public RandomEntity(long len) {
            if (len < 0L) {
                throw new IllegalArgumentException("Length must not be negative");
            }
            if (len > 2147483647L) {
                throw new IllegalArgumentException("Length must not exceed Integer.MAX_VALUE");
            }

            this.length = len;
        }

        public final boolean isStreaming() {
            return false;
        }

        public boolean isRepeatable() {
            return true;
        }

        public long getContentLength() {
            return this.length;
        }

        public InputStream getContent() {
            throw new UnsupportedOperationException();
        }

        public void writeTo(OutputStream out) throws IOException {
            int blocksize = 2048;
            int remaining = (int) this.length;
            byte[] data = new byte[Math.min(remaining, blocksize)];

            while (remaining > 0) {
                int end = Math.min(remaining, data.length);

                double value = 0.0D;
                for (int i = 0; i < end; ++i) {
                    if (i % 5 == 0) {
                        value = Math.random();
                    }
                    value *= RANGE.length;
                    int d = (int) value;
                    value -= d;
                    data[i] = RANGE[d];
                }
                out.write(data, 0, end);
                out.flush();

                remaining -= end;
            }
            out.close();
        }

        static {
            byte[] range = null;
            try {
                range = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".getBytes("US-ASCII");
            } catch (UnsupportedEncodingException uex) {
            }

            RANGE = range;
        }
    }
}
