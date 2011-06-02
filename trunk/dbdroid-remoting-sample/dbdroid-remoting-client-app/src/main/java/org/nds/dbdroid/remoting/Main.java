package org.nds.dbdroid.remoting;

import java.util.List;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setUpTableLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpTableLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_list, menu);
        logger.debug("Menu created.");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean success = false;
        switch (item.getItemId()) {
            case R.id.itemNewContact:
                Toast.makeText(Main.this, "New Contact", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Main.this, ContactActivity.class);
                Main.this.startActivity(intent);
                success = true;
                break;
            case R.id.itemExit:
                Toast.makeText(Main.this, "Bye bye...", Toast.LENGTH_SHORT).show();
                this.finish();
                success = true;
                break;
        }
        return success;
    }

    @Override
    public Context getApplicationContext() {
        return getApplicationState().getApplicationContext();
    }

    private ApplicationState getApplicationState() {
        return (ApplicationState) getApplication();
    }

    private void setUpTableLayout() {
        IContactService contactService = getApplicationState().getService(IContactService.class);
        List<Contact> contacts = contactService.listContact();
        logger.debug("contacts: %s", contacts);
        if (contacts == null) {
            Toast t = Toast.makeText(this, "Cannot connect to remote server -> cannot retrieve contacts", Toast.LENGTH_LONG);
            t.show();
            return;
        }
        TableLayout tl = (TableLayout) findViewById(R.id.tableLayout1);
        logger.info("Retrieve TableLayout: %s", tl.getId());
        tl.removeAllViews();
        for (int i = 0; i < contacts.size(); i++) {
            logger.info("Create Row (%d)...", i);
            TableRow tr = createRow(i % 2 == 0, contacts.get(i));
            tl.addView(tr);
            logger.info("Row created");
        }
    }

    private TableRow createRow(boolean oddRow, Contact contact) {
        logger.info("Row is odd: %b", oddRow);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow tr;
        if (oddRow) {
            tr = (TableRow) inflater.inflate(R.layout.odd_row, null, true);
        } else {
            tr = (TableRow) inflater.inflate(R.layout.even_row, null, true);
        }
        TextView firstname = (TextView) tr.findViewById(R.id.firstname);
        firstname.setText(contact.getFirstname());
        TextView lastname = (TextView) tr.findViewById(R.id.lastname);
        lastname.setText(contact.getLastname());
        Button editButton = (Button) tr.findViewById(R.id.edit);
        editButton.setOnClickListener(new EditClickListener(contact));
        Button deleteButton = (Button) tr.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new DeleteClickListener(contact));

        return tr;
    }

    private class EditClickListener implements View.OnClickListener {

        private final Contact contact;

        public EditClickListener(Contact contact) {
            this.contact = contact;
        }

        public void onClick(View view) {
            Toast.makeText(Main.this, "Edit " + contact, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Main.this, ContactActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("contact", contact);
            intent.putExtras(bundle);
            Main.this.startActivity(intent);
        }
    }

    private class DeleteClickListener implements View.OnClickListener {

        private final Contact contact;

        public DeleteClickListener(Contact contact) {
            this.contact = contact;
        }

        public void onClick(View view) {
            IContactService contactService = getApplicationState().getService(IContactService.class);
            Toast.makeText(Main.this, "Delete " + contact, Toast.LENGTH_LONG).show();
            contactService.delete(contact.getId());
            setUpTableLayout();
        }
    }

}