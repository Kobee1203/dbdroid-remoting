package org.nds.dbdroid.remoting;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactActivity extends Activity {

    private static final Logger logger = LoggerFactory.getLogger(ContactActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
        
        Bundle bundle = getIntent().getExtras();
        //ContactParcelable contactParcelable = bundle.getParcelable("contact");
        // logger.debug("Edit contact : %s", contactParcelable.getContact());
        Contact contact = (Contact) bundle.getSerializable("contact");
        logger.debug("Edit contact : %s", contact);
        
        initForm(contact);
    }
    
    private void initForm(Contact contact) {
        EditText firstNameEditText = (EditText) findViewById(R.id.contact_firstname);
        firstNameEditText.setText(contact.getFirstname());
        EditText lastNameEditText = (EditText) findViewById(R.id.contact_lastname);
        lastNameEditText.setText(contact.getLastname());
        EditText phoneEditText = (EditText) findViewById(R.id.contact_phone);
        phoneEditText.setText(contact.getTelephone());
        EditText emailEditText = (EditText) findViewById(R.id.contact_email);
        emailEditText.setText(contact.getEmail());    	
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
