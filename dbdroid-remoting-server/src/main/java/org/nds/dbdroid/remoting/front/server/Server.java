package org.nds.dbdroid.remoting.front.server;

import java.net.SocketAddress;

import org.apache.http.protocol.HttpRequestHandler;

public interface Server {

    void start() throws Exception;

    void stop() throws Exception;

    void register(String pattern, HttpRequestHandler handler);

    void unregister(String pattern);

    int getServerPort();

    String getServerHostName();

    SocketAddress getServerAddress();
}
