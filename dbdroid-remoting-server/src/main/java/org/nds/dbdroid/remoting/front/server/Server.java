package org.nds.dbdroid.remoting.front.server;

import org.apache.http.protocol.HttpRequestHandler;
import org.nds.dbdroid.service.IAndroidService;

public interface Server {

    void start() throws Exception;

    void stop() throws Exception;

    void register(String pattern, HttpRequestHandler handler);

    void unregister(String pattern);

    void registerService(IAndroidService service);

    void unregisterService(IAndroidService service);
}
