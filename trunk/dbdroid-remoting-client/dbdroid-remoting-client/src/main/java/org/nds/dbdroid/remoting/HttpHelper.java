package org.nds.dbdroid.remoting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public class HttpHelper {

    public static InputStream getInpusStreamResponse(HttpResponse response) throws IOException {
        InputStream in = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                in = entity.getContent();
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            // In case of an IOException the connection will be released
            // back to the connection manager automatically
            throw ex;
        }

        return in;
    }

    public static Reader getReaderResponse(HttpResponse response) throws IOException {
        Reader reader = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream in = entity.getContent();
                reader = new BufferedReader(new InputStreamReader(in));
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            // In case of an IOException the connection will be released
            // back to the connection manager automatically
            throw ex;
        }

        return reader;
    }

    public static String getStringResponse(HttpResponse response) throws IOException {
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream in = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                in.close();
                result = sb.toString();
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            // In case of an IOException the connection will be released
            // back to the connection manager automatically
            throw ex;
        }

        return result;
    }
}
