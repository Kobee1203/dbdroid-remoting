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
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
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

    /**
     * The local address to bind to.
     * The default host is an IP number rather than "localhost" to avoid surprises
     * on hosts that map "localhost" to an IPv6 address or something else.
     * The default port is 0 to let the system pick one.
     */
    public final InetSocketAddress TEST_SERVER_ADDR;

    /** The request handler registry. */
    private final HttpRequestHandlerRegistry handlerRegistry;

    /** The server-side connection re-use strategy. */
    private final ConnectionReuseStrategy reuseStrategy;

    /**
     * The HTTP processor.
     * If the interceptors are thread safe and the list is not
     * modified during operation, the processor is thread safe.
     */
    private final BasicHttpProcessor httpProcessor;

    /** The server parameters. */
    private final HttpParams serverParams;

    /** Optional SSL context */
    private final SSLContext sslcontext;

    /** The server socket, while being served. */
    protected volatile ServerSocket servicedSocket;

    /** The request listening thread, while listening. */
    protected volatile Thread listenerThread;

    /** The number of connections this accepted. */
    private final AtomicInteger acceptedConnections = new AtomicInteger(0);

    /**
     * Creates a new server.
     *
     * @param proc      the HTTP processors to be used by the server, or
     *                  <code>null</code> to use a
     *                  {@link #newProcessor default} processor
     * @param reuseStrat the connection reuse strategy to be used by the 
     *                  server, or <code>null</code> to use
     *                  {@link #newConnectionReuseStrategy() default}
     *                  strategy.                 
     * @param params    the parameters to be used by the server, or
     *                  <code>null</code> to use
     *                  {@link #newDefaultParams default} parameters
     * @param sslcontext optional SSL context if the server is to leverage
     *                   SSL/TLS transport security
     * @param hostname  the host name to be used by the server, or 
     *                  <code>null</code> to use {@link #DEFAULT_HOSTNAME}.
     * @param port      the port number. A valid port value is between 0 and 65535, or 
     *                  <code>null</code> to use {@link #DEFAULT_PORT}.
     *                  A port number of zero will let the system pick up an ephemeral port in a bind operation. 
     */
    public ServerImpl(BasicHttpProcessor proc, ConnectionReuseStrategy reuseStrat, HttpParams params, SSLContext sslcontext, String hostname, int port) {
        super();
        this.handlerRegistry = new HttpRequestHandlerRegistry();
        this.reuseStrategy = (reuseStrat != null) ? reuseStrat : newConnectionReuseStrategy();
        this.httpProcessor = (proc != null) ? proc : newProcessor();
        this.serverParams = (params != null) ? params : newDefaultParams();
        this.sslcontext = sslcontext;
        
        TEST_SERVER_ADDR = new InetSocketAddress(hostname, port);
    }

    /**
     * Creates a new server.
     * @param proc      the HTTP processors to be used by the server, or
     *                  <code>null</code> to use a
     *                  {@link #newProcessor default} processor
     * @param params    the parameters to be used by the server, or
     *                  <code>null</code> to use
     *                  {@link #newDefaultParams default} parameters
     * @param hostName  the host name to be used by the server, or 
     *                  <code>null</code> to use {@link #DEFAULT_HOSTNAME}.
     * @param port      the port number. A valid port value is between 0 and 65535, or 
     *                  <code>null</code> to use {@link #DEFAULT_PORT}.
     *                  A port number of zero will let the system pick up an ephemeral port in a bind operation. 
     */
    public ServerImpl(BasicHttpProcessor proc, HttpParams params, String hostName, int port) {
    	this(proc, null, params, null, hostName == null ? DEFAULT_HOSTNAME : hostName, port < 0 || port > 0xFFFF ? DEFAULT_PORT : port);
	}
    
    /**
     * <p>Creates a new server.</p>
     * <p>Host name = {@link #DEFAULT_HOSTNAME}, port number = {@link #DEFAULT_PORT}</p>
     *
     * @param proc      the HTTP processors to be used by the server, or
     *                  <code>null</code> to use a
     *                  {@link #newProcessor default} processor
     * @param params    the parameters to be used by the server, or
     *                  <code>null</code> to use
     *                  {@link #newDefaultParams default} parameters
     */
    public ServerImpl(BasicHttpProcessor proc, HttpParams params) {
        this(proc, params, DEFAULT_HOSTNAME, DEFAULT_PORT);
    }
    
    /**
     * <p>Creates a new server.</p>
     * <p>Host name = {@link #DEFAULT_HOSTNAME}, port number = {@link #DEFAULT_PORT}</p>
     * <ul>
     * <li>use {@link #newProcessor default} processor,</li> 
     * <li>use {@link #newConnectionReuseStrategy() default} strategy,</li> 
     * <li>use {@link #newDefaultParams default} parameters</li>
     * </ul>
     */
    public ServerImpl() {
        this(null, null, DEFAULT_HOSTNAME, DEFAULT_PORT);
    }

	/**
     * Obtains an HTTP protocol processor with default interceptors.
     *
     * @return  a protocol processor for server-side use
     */
    protected BasicHttpProcessor newProcessor() {
        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new ResponseDate());
        httpproc.addInterceptor(new ResponseServer());
        httpproc.addInterceptor(new ResponseContent());
        httpproc.addInterceptor(new ResponseConnControl());

        return httpproc;
    }

    /**
     * Obtains a set of reasonable default parameters for a server.
     *
     * @return  default parameters
     */
    protected HttpParams newDefaultParams() {
        HttpParams params = new BasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 60000).setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024).setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true).setParameter(CoreProtocolPNames.ORIGIN_SERVER, "LocalTestServer/1.1");
        return params;
    }

    protected ConnectionReuseStrategy newConnectionReuseStrategy() {
        return new DefaultConnectionReuseStrategy();
    }

    /**
     * Returns the number of connections this test server has accepted.
     */
    public int getAcceptedConnectionCount() {
        return acceptedConnections.get();
    }

    /**
     * {@link #register Registers} a set of default request handlers.
     * <pre>
     * URI pattern      Handler
     * -----------      -------
     * /echo/*          {@link EchoHandler EchoHandler}
     * /random/*        {@link RandomHandler RandomHandler}
     * </pre>
     */
    public void registerDefaultHandlers() {
        handlerRegistry.register("/echo/*", new EchoHandler());
        handlerRegistry.register("/random/*", new RandomHandler());
    }

    /**
     * Registers a handler with the local registry.
     *
     * @param pattern   the URL pattern to match
     * @param handler   the handler to apply
     */
    public void register(String pattern, HttpRequestHandler handler) {
        handlerRegistry.register(pattern, handler);
    }

    /**
     * Unregisters a handler from the local registry.
     *
     * @param pattern   the URL pattern
     */
    public void unregister(String pattern) {
        handlerRegistry.unregister(pattern);
    }

    /**
     * Starts this test server.
     * Use {@link #getServicePort getServicePort}
     * to obtain the port number afterwards.
     */
    public void start() throws Exception {
        if (servicedSocket != null) {
            throw new IllegalStateException(this.toString() + " already running");
        }

        ServerSocket ssock;
        if (sslcontext != null) {
            SSLServerSocketFactory sf = sslcontext.getServerSocketFactory();
            ssock = sf.createServerSocket();
        } else {
            ssock = new ServerSocket();
        }

        ssock.setReuseAddress(true); // probably pointless for port '0'
        ssock.bind(TEST_SERVER_ADDR);
        servicedSocket = ssock;

        listenerThread = new Thread(new RequestListener());
        listenerThread.setDaemon(false);
        listenerThread.start();
    }

    /**
     * Stops this test server.
     */
    public void stop() throws Exception {
        if (servicedSocket == null) {
            return; // not running
        }

        try {
            servicedSocket.close();
        } catch (IOException iox) {
            System.out.println("error stopping " + this);
            iox.printStackTrace(System.out);
        } finally {
            servicedSocket = null;
        }

        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }

    public void awaitTermination(long timeMs) throws InterruptedException {
        if (listenerThread != null) {
            listenerThread.join(timeMs);
        }
    }

    @Override
    public String toString() {
        ServerSocket ssock = servicedSocket; // avoid synchronization
        StringBuffer sb = new StringBuffer();
        sb.append("Server/");
        if (ssock == null) {
            sb.append("stopped");
        } else {
            sb.append(ssock.getLocalSocketAddress());
        }
        return sb.toString();
    }

    /**
     * Obtains the port this server is servicing.
     *
     * @return  the service port
     */
    public int getServerPort() {
        ServerSocket ssock = servicedSocket; // avoid synchronization
        if (ssock == null) {
            throw new IllegalStateException("not running");
        }

        return ssock.getLocalPort();
    }

    /**
     * Obtains the hostname of the server.
     *
     * @return  the hostname
     */
    public String getServerHostName() {
        ServerSocket ssock = servicedSocket; // avoid synchronization
        if (ssock == null) {
            throw new IllegalStateException("not running");
        }

        return ((InetSocketAddress) ssock.getLocalSocketAddress()).getHostName();
    }

    /**
     * Obtains the local address the server is listening on
     *  
     * @return the service address
     */
    public SocketAddress getServerAddress() {
        ServerSocket ssock = servicedSocket; // avoid synchronization
        if (ssock == null) {
            throw new IllegalStateException("not running");
        }

        return ssock.getLocalSocketAddress();
    }

    public String getServerURL() {
    	ServerSocket ssock = servicedSocket; // avoid synchronization
    	if (ssock == null) {
            throw new IllegalStateException("not running");
        }
    	StringBuilder sb = new StringBuilder();
    	sb.append("http");
    	if(ssock instanceof SSLServerSocket) {
    		sb.append("s");
    	}
    	sb.append("://");
    	sb.append(this.getServerHostName()).append(":").append(this.getServerPort());
    	return sb.toString();
    }
    
    /**
     * The request listener.
     * Accepts incoming connections and launches a service thread.
     */
    public class RequestListener implements Runnable {

        /** The workers launched from here. */
        private final Set<Thread> workerThreads = Collections.synchronizedSet(new HashSet<Thread>());

        public void run() {

            try {
                while ((servicedSocket != null) && (listenerThread == Thread.currentThread()) && !Thread.interrupted()) {
                    try {
                        accept();
                    } catch (Exception e) {
                        ServerSocket ssock = servicedSocket;
                        if ((ssock != null) && !ssock.isClosed()) {
                            System.out.println(ServerImpl.this.toString() + " could not accept");
                            e.printStackTrace(System.out);
                        }
                        // otherwise ignore the exception silently
                        break;
                    }
                }
            } finally {
                cleanup();
            }
        }

        protected void accept() throws IOException {
            // Set up HTTP connection
            Socket socket = servicedSocket.accept();
            acceptedConnections.incrementAndGet();
            DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
            conn.bind(socket, serverParams);

            // Set up the HTTP service
            HttpService httpService = new HttpService(httpProcessor, reuseStrategy, new DefaultHttpResponseFactory());
            httpService.setParams(serverParams);
            httpService.setHandlerResolver(handlerRegistry);

            // Start worker thread
            Thread t = new Thread(new Worker(httpService, conn));
            workerThreads.add(t);
            t.setDaemon(true);
            t.start();

        } // accept

        protected void cleanup() {
            Thread[] threads = workerThreads.toArray(new Thread[0]);
            for (int i = 0; i < threads.length; i++) {
                if (threads[i] != null) {
                    threads[i].interrupt();
                }
            }
        }

        /**
         * A worker for serving incoming requests.
         */
        public class Worker implements Runnable {

            private final HttpService httpservice;
            private final HttpServerConnection conn;

            public Worker(final HttpService httpservice, final HttpServerConnection conn) {

                this.httpservice = httpservice;
                this.conn = conn;
            }

            public void run() {
                HttpContext context = new BasicHttpContext(null);
                try {
                    while ((servicedSocket != null) && this.conn.isOpen() && !Thread.interrupted()) {
                        this.httpservice.handleRequest(this.conn, context);
                    }
                } catch (IOException ex) {
                    // ignore silently
                } catch (HttpException ex) {
                    // ignore silently
                } finally {
                    workerThreads.remove(Thread.currentThread());
                    try {
                        this.conn.shutdown();
                    } catch (IOException ignore) {
                    }
                }
            }

        } // class Worker

    } // class RequestListener

}
