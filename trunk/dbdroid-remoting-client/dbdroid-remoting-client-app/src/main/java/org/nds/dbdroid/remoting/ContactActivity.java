package org.nds.dbdroid.remoting;

import android.app.Activity;
import android.os.Bundle;

public class ContactActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact);
		
		Bundle bundle = getIntent().getExtras();
		ContactParcelable contactParcelable = bundle.getParcelable("contact");
	}
}
