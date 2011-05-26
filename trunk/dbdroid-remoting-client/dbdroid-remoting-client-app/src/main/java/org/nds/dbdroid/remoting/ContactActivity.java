package org.nds.dbdroid.remoting;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;

public class ContactActivity extends Activity {

    private static final Logger logger = LoggerFactory.getLogger(ContactActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        Bundle bundle = getIntent().getExtras();
        //ContactParcelable contactParcelable = bundle.getParcelable("contact");
        // logger.debug("Edit contact : %s", contactParcelable.getContact());
        Contact contact = (Contact) bundle.getSerializable("contact");
        logger.debug("Edit contact : %s", contact);
    }
}
