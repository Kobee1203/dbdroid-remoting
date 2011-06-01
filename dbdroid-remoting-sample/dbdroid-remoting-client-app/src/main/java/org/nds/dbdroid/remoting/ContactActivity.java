package org.nds.dbdroid.remoting;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactActivity extends Activity {

    private static final Logger logger = LoggerFactory.getLogger(ContactActivity.class);

    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        Bundle bundle = getIntent().getExtras();
        contact = (Contact) bundle.getSerializable("contact");
        initForm(contact);

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save();
                finish();
            }
        });
    }

    private ApplicationState getApplicationState() {
        return (ApplicationState) getApplication();
    }

    private void save() {
        boolean newContact = false;
        if (contact == null) {
            contact = new Contact();
            newContact = true;
        }

        EditText firstNameEditText = (EditText) findViewById(R.id.contact_firstname);
        contact.setFirstname(firstNameEditText.getText().toString());
        EditText lastNameEditText = (EditText) findViewById(R.id.contact_lastname);
        contact.setLastname(lastNameEditText.getText().toString());
        EditText phoneEditText = (EditText) findViewById(R.id.contact_phone);
        contact.setTelephone(phoneEditText.getText().toString());
        EditText emailEditText = (EditText) findViewById(R.id.contact_email);
        contact.setEmail(emailEditText.getText().toString());

        IContactService contactService = getApplicationState().getService(IContactService.class);
        contact = newContact ? contactService.save(contact) : contactService.update(contact);
        logger.debug("Contact %s: %s", (newContact ? "saved" : "updated"), contact.toString());
    }

    private void initForm(Contact contact) {
        logger.debug("init Form with Contact : %s", contact);
        EditText firstNameEditText = (EditText) findViewById(R.id.contact_firstname);
        firstNameEditText.setText(contact.getFirstname());
        EditText lastNameEditText = (EditText) findViewById(R.id.contact_lastname);
        lastNameEditText.setText(contact.getLastname());
        EditText phoneEditText = (EditText) findViewById(R.id.contact_phone);
        phoneEditText.setText(contact.getTelephone());
        EditText emailEditText = (EditText) findViewById(R.id.contact_email);
        emailEditText.setText(contact.getEmail());
    }
}
