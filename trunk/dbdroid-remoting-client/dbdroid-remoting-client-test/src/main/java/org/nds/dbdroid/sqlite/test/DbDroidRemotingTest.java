package org.nds.dbdroid.sqlite.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.query.Query;
import org.nds.dbdroid.remoting.client.ClientManager;
import org.nds.dbdroid.remoting.dao.Object1Dao;
import org.nds.dbdroid.remoting.entity.Object1;
import org.nds.dbdroid.remoting.front.ServerFactoryBean;
import org.nds.dbdroid.remoting.front.server.Server;
import org.nds.dbdroid.remoting.webapp.service.ContactServiceImpl;
import org.nds.dbdroid.sqlite.test.dao.TestDao;
import org.nds.dbdroid.sqlite.test.entity.Test;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Contacts;
import android.test.AndroidTestCase;

public class DbDroidRemotingTest extends AndroidTestCase {

    private static final Logger log = LoggerFactory.getLogger(DbDroidRemotingTest.class);

    private Server server;

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
        sfb.registerService(new ContactServiceImpl());
        server = sfb.create();
        server.start();

        String serverUrl = "http://" + server.getServerHostName() + ":" + server.getServerPort();
        log.debug("Server available at " + serverUrl);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    public void testPersistence() throws IOException, DBDroidException, NameNotFoundException {
        // ask for the code of the foreign context to be included and to ignore any security given by the cross-process(owner) execution
        // in working-environment to error checking ...
        Context ctx = getContext().createPackageContext("org.nds.dbdroid.sqlite", Context.CONTEXT_INCLUDE_CODE + Context.CONTEXT_IGNORE_SECURITY);

        InputStream config = getContext().getAssets().open("dbdroid/dbdroid.xml");

        String serverUrl = "http://" + server.getServerHostName() + ":" + server.getServerPort() + "/dbdroid-remoting";
        
        ClientManager clientManager = new ClientManager(config, serverUrl);
        clientManager.setClassLoader(ctx.getClassLoader());
        clientManager.loadConfig();

        
    }
}
