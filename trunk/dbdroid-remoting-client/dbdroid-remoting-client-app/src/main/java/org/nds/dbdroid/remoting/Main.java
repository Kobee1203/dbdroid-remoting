package org.nds.dbdroid.remoting;

import org.nds.dbdroid.remoting.commons.entity.Contact;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /*TableLayout tl = (TableLayout) findViewById(R.id.tableLayout1);
        
        Contact contact = new Contact("Nicolas", "Dos Santos", "nicolas.dossantos@gmail.com", "+33123465789");
        for(int i = 0; i< 15; i++) {
        	TableRow tr = createRow(i%1==1, contact);
        	tl.addView(tr);
        }*/
    }

	private TableRow createRow(boolean oddRow, Contact contact) {
        TableRow tr = new TableRow(this);
        tr.setBackgroundColor(oddRow ? R.color.odd_row : R.color.even_row);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        
        View cell = createCell(oddRow, contact.getFirstname());
        tr.addView(cell);
        cell = createCell(oddRow, contact.getLastname());
        tr.addView(cell);
        cell = createCellButton(oddRow, R.drawable.edit);
        tr.addView(cell);
        
        return tr;
	}

	private View createCell(boolean oddRow, String text) {
        TextView cell = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(1, 1, 1, 1); // left,top,right, bottom
        cell.setLayoutParams(params);
        cell.setBackgroundColor(oddRow ? R.color.odd_row_cell : R.color.even_row_cell);
        cell.setText(text);
        return cell;
	}
	
	private View createCellButton(boolean oddRow, int edit) {
		LinearLayout cell = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(1, 1, 1, 1); // left,top,right, bottom
        cell.setLayoutParams(params);
        cell.setBackgroundColor(oddRow ? R.color.odd_row_cell : R.color.even_row_cell);
        Button button = new Button(this);
        button.setBackgroundResource(edit);
        button.setGravity(Gravity.RIGHT);
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cell.addView(button);
		return cell;
	}
}