package org.nds.dbdroid.remoting;

import java.io.InputStream;

import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.remoting.client.ClientManager;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.dbdroid.service.IAndroidService;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

import android.app.Application;
import android.widget.Toast;

public class ApplicationState extends Application {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationState.class);
	
	private static final String serverUrl = "http://10.0.2.2:12345/dbdroid-remoting";
	
	private ClientManager clientManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
        try {
            InputStream in = getClass().getResourceAsStream("/dbdroid.xml");
            clientManager = new ClientManager(in, serverUrl);
            clientManager.loadConfig();
            logger.debug("Client Manager initialized");
        } catch (DBDroidException e) {
            Toast t = Toast.makeText(this, "Cannot initialize the client manager for Web Services: " + e.getMessage(), Toast.LENGTH_LONG);
            t.show();
            e.printStackTrace();
        }
	}
	
	public <T extends IAndroidService> T getService(Class<T> serviceClass) {
		T service = clientManager.getService(serviceClass);
		logger.debug("Service: %s", service);
		return service;
	}
}
