package org.nds.dbdroid.remoting;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.remoting.client.ClientManager;
import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.dbdroid.remoting.front.ServerFactoryBean;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

public class DBDroidRemotingTest {

    private static final Logger log = LoggerFactory.getLogger(DBDroidRemotingTest.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLocalServer() throws DBDroidException {
        ServerFactoryBean sfb = new ServerFactoryBean();
        sfb.setUrlPattern("/dbdroid-remoting/*");
        sfb.registerService(null/*service*/);
        sfb.create();
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