package org.nds.dbdroid.remoting.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.localserver.LocalTestServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nds.dbdroid.dao.Dao1;
import org.nds.dbdroid.dao.Dao2;
import org.nds.dbdroid.dao.IDao3;
import org.nds.dbdroid.entity.Entity1;
import org.nds.dbdroid.entity.Entity2;
import org.nds.dbdroid.entity.Entity3;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.remoting.HttpHelper;
import org.nds.dbdroid.remoting.client.ClientManager;
import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.dbdroid.remoting.test.mockup.ServiceGETMockup;
import org.nds.dbdroid.remoting.test.mockup.ServicePOSTMockup;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

public class ClientTest {

    private static final Logger log = LoggerFactory.getLogger(ClientTest.class);

    private LocalTestServer server = null;

    @Before
    public void setUp() {
        try {
            server = new LocalTestServer(null, null);
            server.register("/dbdroid/*", new ServiceGETMockup());
            server.register("/dbdroid-remoting/*", new ServicePOSTMockup());
            server.start();

            // report how to access the server
            String serverUrl = "http://" + server.getServiceHostName() + ":" + server.getServicePort();
            System.out.println("LocalTestServer available at " + serverUrl);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testLocalServer() throws DBDroidException {
        InputStream in = getClass().getResourceAsStream("/dbdroid.xml");
        String serverUrl = "http://" + server.getServiceHostName() + ":" + server.getServicePort() + "/dbdroid-remoting";
        ClientManager clientManager = new ClientManager(in, serverUrl);
        clientManager.loadConfig();

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet("http://" + server.getServiceHostName() + ":" + server.getServicePort() + "/dbdroid/index");
            HttpResponse response = httpClient.execute(httpget);
            System.out.println(HttpHelper.getStringResponse(response));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        Dao1 dao1 = clientManager.getDAO(Dao1.class);
        List<Entity1> list = dao1.findAll();
        dao1.save(new Entity1("The name of the Entity1 object"));

        Dao2 dao2 = clientManager.getDAO(Dao2.class);
        List<Entity2> list2 = dao2.findAll();
        Entity2 e2 = new Entity2(System.currentTimeMillis());
        Entity3 e3 = new Entity3("name of Entity3", "<document><tag1 attr1=\"attr1 value\">text into tag1</tag1><document>".getBytes());
        e2.setEntity3(e3);
        dao2.saveOrUpdate(e2);

        IDao3 idao3 = clientManager.getDAO(IDao3.class);
        Entity3 entity = idao3.getByName("The Name");
        idao3.delete(entity);
    }

    @Test
    public void testRemoteServer() throws DBDroidException {
        InputStream in = getClass().getResourceAsStream("/dbdroid.xml");
        String serverUrl = "http://localhost:8080/dbdroid-remoting-sample-webapp/dbdroid-remoting";
        ClientManager clientManager = new ClientManager(in, serverUrl);
        clientManager.loadConfig();

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(serverUrl + "/index");
            HttpResponse response = httpClient.execute(httpget);
            System.out.println(HttpHelper.getStringResponse(response));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        IContactService contactService = clientManager.getService(IContactService.class);

        Contact contact = null;

        // Find All
        /*log.debug("** FIND ALL CONTACTS **");
        List<Contact> contacts = contactDAO.findAll();
        if (contacts != null) {
            for (Contact c : contacts) {
                log.debug(c.toString());
            }
        }

        // Find By Id
        log.debug("** FIND CONTACT BY ID **");
        contact = contactDAO.findById("1");
        if (contact != null) {
            log.debug(contact.toString());
        }*/

        // Save
        /*log.debug("** SAVE CONTACT **");
        Contact c = new Contact("firstname", "lastname", "email", "telephone");
        contact = contactService.save(c);
        if (contact != null) {
            log.debug(contact.toString());
        }*/

        // Update
        log.debug("** UPDATE CONTACT **");
        contact = new Contact("firstname", "lastname", "email", "telephone");
        contact.setId(2);
        contact.setFirstname("new firstname");
        contact.setTelephone("new telephone");
        contact = contactService.update(contact);
        if (contact != null) {
            log.debug(contact.toString());
        }

        // Delete
        log.debug("** DELETE CONTACT **");
        contactService.delete(contact);
        // Find By Id
        Contact contact2 = contactService.findById(contact.getId());
        if (contact2 == null) {
            log.debug("Contact with ID " + contact.getId() + " is deleted.");
        }

        // Raw Query

        // Query

    }
}
