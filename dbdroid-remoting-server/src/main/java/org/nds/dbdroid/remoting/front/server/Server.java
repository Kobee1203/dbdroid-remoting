package org.nds.dbdroid.remoting.front.server;

import java.net.SocketAddress;

import org.apache.http.protocol.HttpRequestHandler;

public interface Server {

    String DEFAULT_HOSTNAME = "127.0.0.1";
    int DEFAULT_PORT = 0;
	
    void start() throws Exception;

    void stop() throws Exception;

    void register(String pattern, HttpRequestHandler handler);

    void unregister(String pattern);

    int getServerPort();

    String getServerHostName();

    SocketAddress getServerAddress();

	String getServerURL();
}
