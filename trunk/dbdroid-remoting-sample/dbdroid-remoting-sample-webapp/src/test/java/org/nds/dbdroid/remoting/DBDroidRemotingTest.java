package org.nds.dbdroid.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.remoting.client.ClientManager;
import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.dbdroid.remoting.front.ServerFactoryBean;
import org.nds.dbdroid.remoting.front.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class DBDroidRemotingTest {

    private static final Logger log = Logger.getLogger(DBDroidRemotingTest.class);

    private Server server;

    @Autowired
    private IContactService contactService;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        ServerFactoryBean sfb = new ServerFactoryBean();
        sfb.setUrlPattern("/dbdroid-remoting/*");
        sfb.registerService(contactService);
        server = sfb.create();
        server.start();

        String serverUrl = "http://" + server.getServerHostName() + ":" + server.getServerPort();
        log.debug("Server available at " + serverUrl);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testLocalServer() throws DBDroidException {
        String serverUrl = "http://" + server.getServerHostName() + ":" + server.getServerPort() + "/dbdroid-remoting";
        runtTest(serverUrl);
    }

    @Ignore
    public void testRemoteServer() throws DBDroidException {
        String serverUrl = "http://localhost:8080/dbdroid-remoting-sample-webapp/dbdroid-remoting";
        runtTest(serverUrl);
    }

    private void runtTest(String serverUrl) throws DBDroidException {
        InputStream in = getClass().getResourceAsStream("/dbdroid.xml");
        ClientManager clientManager = new ClientManager(in, serverUrl);
        clientManager.loadConfig();

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(serverUrl + "/index");
            HttpResponse response = httpClient.execute(httpget);
            log.debug(HttpHelper.getStringResponse(response));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        IContactService contactService = clientManager.getService(IContactService.class);

        invokeService(contactService);
    }

    private void invokeService(IContactService contactService) {

        Contact contact = null;

        // Find All
        log.debug("** FIND ALL CONTACTS **");
        List<Contact> contacts = contactService.listContact();
        Assert.assertNotNull(contacts);
        Assert.assertEquals(2, contacts.size());
        Assert.assertEquals("[1] Nicolas Dos Santos (nicolas.dossantos@gmail.com, 06-XX-XX-XX-XX)", contacts.get(0).toString());
        Assert.assertEquals("[2] John Doe (john@doe.com, 01-XX-XX-XX-XX)", contacts.get(1).toString());
        /*for (Contact c : contacts) {
                log.debug(c.toString());
        }*/

        // Find By Id
        log.debug("** FIND CONTACT BY ID **");
        contact = contactService.findById(1);
        Assert.assertNotNull(contact);
        Assert.assertEquals("[1] Nicolas Dos Santos (nicolas.dossantos@gmail.com, 06-XX-XX-XX-XX)", contact.toString());
        // log.debug(contact.toString());

        // Save
        log.debug("** SAVE CONTACT **");
        contact = new Contact("firstname", "lastname", "email", "telephone");
        contact = contactService.save(contact);
        Assert.assertNotNull(contact);
        Assert.assertEquals("[3] firstname lastname (email, telephone)", contact.toString());
        // log.debug(contact.toString());

        // Update
        log.debug("** UPDATE CONTACT **");
        contact.setFirstname("new firstname");
        contact.setTelephone("new telephone");
        contact = contactService.update(contact);
        Assert.assertNotNull(contact);
        Assert.assertEquals("[3] new firstname lastname (email, new telephone)", contact.toString());
        // log.debug(contact.toString());

        // Delete by object
        log.debug("** DELETE CONTACT BY OBJECT **");
        contact = contactService.findById(contact.getId());
        Assert.assertNotNull(contact);
        Assert.assertEquals("[3] new firstname lastname (email, new telephone)", contact.toString());

        contactService.delete(contact);

        contact = contactService.findById(contact.getId());
        Assert.assertNull(contact);
        // log.debug("Contact with ID " + contact.getId() + " is deleted.");

        // Delete by id
        log.debug("** DELETE CONTACT BY ID **");
        contact = contactService.findById(1);
        Assert.assertNotNull(contact);
        Assert.assertEquals("[1] Nicolas Dos Santos (nicolas.dossantos@gmail.com, 06-XX-XX-XX-XX)", contact.toString());

        contactService.delete(1);
        // Find By Id
        contact = contactService.findById(1);
        Assert.assertNull(contact);
        // log.debug("Contact with ID " + 1 + " is deleted.");

        // Find All
        log.debug("** FIND ALL CONTACTS **");
        contacts = contactService.listContact();
        Assert.assertNotNull(contacts);
        Assert.assertEquals(1, contacts.size());
        Assert.assertEquals("[2] John Doe (john@doe.com, 01-XX-XX-XX-XX)", contacts.get(0).toString());
        /*for (Contact c : contacts) {
                log.debug(c.toString());
        }*/

        // Raw Query

        // Query
    }
}
