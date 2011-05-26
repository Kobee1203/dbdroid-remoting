package org.nds.dbdroid.remoting.front;

import java.net.URL;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;
import org.nds.dbdroid.remoting.controller.IServiceController;
import org.nds.dbdroid.remoting.controller.ServiceController;
import org.nds.dbdroid.remoting.front.server.Server;
import org.nds.dbdroid.remoting.front.server.ServerImpl;
import org.nds.dbdroid.remoting.front.server.handler.ServiceDispatcherHandler;
import org.nds.dbdroid.remoting.service.IServiceManager;
import org.nds.dbdroid.remoting.service.ServiceManager;
import org.nds.dbdroid.service.IAndroidService;

public class ServerFactoryBean {
	
	private static final Logger log = Logger.getLogger(ServerFactoryBean.class);

	private Server server;

	private String urlPattern;

	private String hostName = Server.DEFAULT_HOSTNAME;

	private int port = Server.DEFAULT_PORT;

	private final IServiceManager serviceManager = new ServiceManager();

	private URL ksURL;

	private String ksPass;

	private String ctPass;

	public ServerFactoryBean() {
	}

	/**
	 * Create a server with a service controller
	 */
	public Server create() {
		IServiceController serviceController = new ServiceController();
		serviceController.setServiceManager(serviceManager);

		server = new ServerImpl(null, null, null, getSSLContext(), hostName, port);

		server.register(urlPattern, new ServiceDispatcherHandler(serviceController, urlPattern));

		return server;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public void setServerPort(int port) {
		this.port = port;
	}

	public void setServerHostName(String hostName) {
		this.hostName = hostName;
	}

	public void registerService(IAndroidService service) {
		serviceManager.registerService(service, service.getClass().getName());
	}

	public void setServices(List<IAndroidService> services) {
		for (IAndroidService service : services) {
			registerService(service);
		}
	}
	
	public void setKeyStore(URL ksURL, String ksPass, String ctPass) {
		this.ksURL = ksURL;
		this.ksPass = ksPass;
		this.ctPass = ctPass;
	}
	
	private SSLContext getSSLContext() {
		if(this.ksURL==null || this.ksPass==null || this.ctPass==null) {
			return null;
		}
		SSLContext sc = null;
		try {
			// SSLContext protocols: TLS, SSL, SSLv3
			sc = SSLContext.getInstance("SSLv3");
			log.debug("\nSSLContext class: " + sc.getClass());
			log.debug("   Protocol: " + sc.getProtocol());
			log.debug("   Provider: " + sc.getProvider());
			
			// SSLContext algorithms: SunX509
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			log.debug("\nKeyManagerFactory class: " + kmf.getClass());
			log.debug("   Algorithm: " + kmf.getAlgorithm());
			log.debug("   Provider: " + kmf.getProvider());
			
			// KeyStore types: JKS
			URL ksURL = this.ksURL;//"herong.jks";
			char ksPass[] = this.ksPass.toCharArray(); //"HerongJKS".toCharArray();
			char ctPass[] = this.ctPass.toCharArray(); //"My1stKey".toCharArray();
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(ksURL.openStream(), ksPass);
			log.debug("\nKeyStore class: " + ks.getClass());
			log.debug("   Type: " + ks.getType());
			log.debug("   Provider: " + ks.getProvider());
			log.debug("   Size: " + ks.size());
			
			// Generating KeyManager list
			kmf.init(ks, ctPass);
			KeyManager[] kmList = kmf.getKeyManagers();
			log.debug("\nKeyManager class: " + kmList[0].getClass());
			log.debug("   # of key manager: " + kmList.length);
			
			sc.init(kmList, null, null);	
		} catch (Exception e) {
			sc = null;
		}
		
		return sc;
	}
}
