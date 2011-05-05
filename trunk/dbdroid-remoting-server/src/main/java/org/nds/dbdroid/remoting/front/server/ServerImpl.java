package org.nds.dbdroid.remoting.front.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.nds.dbdroid.remoting.front.server.handler.EchoHandler;
import org.nds.dbdroid.remoting.front.server.handler.RandomHandler;

public class ServerImpl implements Server {

    public static final InetSocketAddress TEST_SERVER_ADDR = new InetSocketAddress("127.0.0.1", 0);
    private final HttpRequestHandlerRegistry handlerRegistry;
    private final ConnectionReuseStrategy reuseStrategy;
    private final BasicHttpProcessor httpProcessor;
    private final HttpParams serverParams;
    private final SSLContext sslcontext;
    protected volatile ServerSocket serverSocket;
    protected volatile Thread listenerThread;
    private final AtomicInteger acceptedConnections;

    public ServerImpl(BasicHttpProcessor proc, ConnectionReuseStrategy reuseStrat, HttpParams params, SSLContext sslcontext) {
        this.acceptedConnections = new AtomicInteger(0);

        this.handlerRegistry = new HttpRequestHandlerRegistry();
        this.reuseStrategy = ((reuseStrat != null) ? reuseStrat : newConnectionReuseStrategy());
        this.httpProcessor = ((proc != null) ? proc : newProcessor());
        this.serverParams = ((params != null) ? params : newDefaultParams());
        this.sslcontext = sslcontext;
    }

    public ServerImpl(BasicHttpProcessor proc, HttpParams params) {
        this(proc, null, params, null);
    }

    public ServerImpl() {
        this(null, null);
    }

    protected BasicHttpProcessor newProcessor() {
        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new ResponseDate());
        httpproc.addInterceptor(new ResponseServer());
        httpproc.addInterceptor(new ResponseContent());
        httpproc.addInterceptor(new ResponseConnControl());

        return httpproc;
    }

    protected HttpParams newDefaultParams() {
        HttpParams params = new BasicHttpParams();
        params.setIntParameter("http.socket.timeout", 60000).setIntParameter("http.socket.buffer-size", 8192).setBooleanParameter("http.connection.stalecheck", false).setBooleanParameter("http.tcp.nodelay", true).setParameter("http.origin-server", "ServerImpl/1.1");

        return params;
    }

    protected ConnectionReuseStrategy newConnectionReuseStrategy() {
        return new DefaultConnectionReuseStrategy();
    }

    public int getAcceptedConnectionCount() {
        return this.acceptedConnections.get();
    }

    public void registerDefaultHandlers() {
        this.handlerRegistry.register("/echo/*", new EchoHandler());
        this.handlerRegistry.register("/random/*", new RandomHandler());
    }

    public void register(String pattern, HttpRequestHandler handler) {
        this.handlerRegistry.register(pattern, handler);
    }

    public void unregister(String pattern) {
        this.handlerRegistry.unregister(pattern);
    }

    public void start() throws Exception {
        if (this.serverSocket != null) {
            throw new IllegalStateException(toString() + " already running");
        }
        ServerSocket ssock;
        if (this.sslcontext != null) {
            SSLServerSocketFactory sf = this.sslcontext.getServerSocketFactory();
            ssock = sf.createServerSocket();
        } else {
            ssock = new ServerSocket();
        }

        ssock.setReuseAddress(true);
        ssock.bind(TEST_SERVER_ADDR);
        this.serverSocket = ssock;

        this.listenerThread = new Thread(new RequestListener());
        this.listenerThread.setDaemon(false);
        this.listenerThread.start();
    }

    public void stop() throws Exception {
        if (this.serverSocket == null) {
            return;
        }
        try {
            this.serverSocket.close();
        } catch (IOException iox) {
            System.out.println("error stopping " + this);
            iox.printStackTrace(System.out);
        } finally {
            this.serverSocket = null;
        }

        if (this.listenerThread != null) {
            this.listenerThread.interrupt();
        }
    }

    public void awaitTermination(long timeMs) throws InterruptedException {
        if (this.listenerThread != null) {
            this.listenerThread.join(timeMs);
        }
    }

    @Override
    public String toString() {
        ServerSocket ssock = this.serverSocket;
        StringBuffer sb = new StringBuffer(80);
        sb.append("ServerImpl/");
        if (ssock == null) {
            sb.append("stopped");
        } else {
            sb.append(ssock.getLocalSocketAddress());
        }
        return sb.toString();
    }

    public int getServerPort() {
        ServerSocket ssock = this.serverSocket;
        if (ssock == null) {
            throw new IllegalStateException("not running");
        }
        return ssock.getLocalPort();
    }

    public String getServerHostName() {
        ServerSocket ssock = this.serverSocket;
        if (ssock == null) {
            throw new IllegalStateException("not running");
        }
        return ((InetSocketAddress) ssock.getLocalSocketAddress()).getHostName();
    }

    public SocketAddress getServerAddress() {
        ServerSocket ssock = this.serverSocket;
        if (ssock == null) {
            throw new IllegalStateException("not running");
        }
        return ssock.getLocalSocketAddress();
    }

    public class RequestListener implements Runnable {
        private final Set<Thread> workerThreads;

        public RequestListener() {
            this.workerThreads = Collections.synchronizedSet(new HashSet<Thread>());
        }

        public void run() {
            try {
                if ((ServerImpl.this.serverSocket == null) || (ServerImpl.this.listenerThread != Thread.currentThread()) || (Thread.interrupted())) {
                    ;
                }
            } finally {
                cleanup();
            }
        }

        protected void accept() throws IOException {
            Socket socket = ServerImpl.this.serverSocket.accept();
            ServerImpl.this.acceptedConnections.incrementAndGet();
            DefaultHttpServerConnection conn = new DefaultHttpServerConnection();

            conn.bind(socket, ServerImpl.this.serverParams);

            HttpService httpService = new HttpService(ServerImpl.this.httpProcessor, ServerImpl.this.reuseStrategy, new DefaultHttpResponseFactory());

            httpService.setParams(ServerImpl.this.serverParams);
            httpService.setHandlerResolver(ServerImpl.this.handlerRegistry);

            Thread t = new Thread(new Worker(httpService, conn));
            this.workerThreads.add(t);
            t.setDaemon(true);
            t.start();
        }

        protected void cleanup() {
            Thread[] threads = this.workerThreads.toArray(new Thread[0]);
            for (int i = 0; i < threads.length; ++i) {
                if (threads[i] != null) {
                    threads[i].interrupt();
                }
            }
        }

        public class Worker implements Runnable {
            private final HttpService httpservice;
            private final HttpServerConnection conn;

            public Worker(HttpService paramHttpService, HttpServerConnection conn) {
                this.httpservice = paramHttpService;
                this.conn = conn;
            }

            public void run() {
                HttpContext context = new BasicHttpContext(null);
                try {
                    while ((ServerImpl.this.serverSocket != null) && (this.conn.isOpen()) && (!(Thread.interrupted()))) {
                        this.httpservice.handleRequest(this.conn, context);
                    }
                } catch (IOException ignore) {
                } catch (HttpException ignore) {
                } finally {
                    ServerImpl.RequestListener.this.workerThreads.remove(Thread.currentThread());
                    try {
                        this.conn.shutdown();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }
}
