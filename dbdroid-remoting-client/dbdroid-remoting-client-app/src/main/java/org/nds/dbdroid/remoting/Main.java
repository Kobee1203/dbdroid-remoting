package org.nds.dbdroid.remoting;

import java.io.InputStream;
import java.util.List;

import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.remoting.client.ClientManager;
import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String serverUrl = "http://192.168.0.12:50460/dbdroid-remoting";

    private IContactService contactService;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            InputStream in = getClass().getResourceAsStream("/dbdroid.xml");
            ClientManager clientManager = new ClientManager(in, serverUrl);
            clientManager.loadConfig();

            contactService = clientManager.getService(IContactService.class);
            logger.debug("contactService: %s", contactService);
        } catch (DBDroidException e) {
            Toast.makeText(this, "Cannot initialize the client manager for Web Services: " + e.getMessage(), Toast.LENGTH_LONG);
            e.printStackTrace();
        }

        List<Contact> contacts = contactService.listContact();
        logger.debug("contacts: %s", contacts);

        TableLayout tl = (TableLayout) findViewById(R.id.tableLayout1);
        logger.info("Retrieve TableLayout: " + tl.getId());

        Contact contact = new Contact("Nicolas", "Dos Santos", "nicolas.dossantos@gmail.com", "+33123465789");
        for (int i = 0; i < 15; i++) {
            logger.info("Create Row (" + i + ")...");
            TableRow tr = createRow(i % 2 == 0, contact);
            tl.addView(tr);
            logger.info("Row created");
        }
    }

    private TableRow createRow(boolean oddRow, Contact contact) {
        logger.info("Row is odd: " + oddRow);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow tr;
        if (oddRow) {
            tr = (TableRow) inflater.inflate(R.layout.odd_row, null, true);
        } else {
            tr = (TableRow) inflater.inflate(R.layout.even_row, null, true);
        }
        TextView firstname = (TextView) tr.getChildAt(0);
        firstname.setText(contact.getFirstname());
        TextView lastname = (TextView) tr.getChildAt(1);
        lastname.setText(contact.getLastname());

        return tr;
    }

}