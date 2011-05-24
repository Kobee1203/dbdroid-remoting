package org.nds.dbdroid.remoting;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Main extends Activity {
	
	Logger logger = LoggerFactory.getLogger(Main.class);
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TableLayout tl = (TableLayout) findViewById(R.id.tableLayout1);
        logger.info("Retrieve TableLayout: " + tl.getId());

        Contact contact = new Contact("Nicolas", "Dos Santos", "nicolas.dossantos@gmail.com", "+33123465789");
        for(int i = 0; i< 15; i++) {
        	logger.info("Create Row ("+i+")...");
        	TableRow tr = createRow(i%2==0, contact);
        	tl.addView(tr);
        	logger.info("Row created");
        }
    }

	private TableRow createRow(boolean oddRow, Contact contact) {
		logger.info("Row is odd: " + oddRow);
        
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow tr;
        if(oddRow) {
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