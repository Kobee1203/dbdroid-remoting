package org.nds.dbdroid.remoting;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TableLayout tl = (TableLayout) findViewById(R.id.tableLayout1);
        TableRow tr = new TableRow(this);
        tr.setBackgroundColor(R.color.odd_row);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView cell = new TextView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cell.setLayoutParams(params);
        cell.setBackgroundColor(R.color.odd_row_cell);
        cell.setText("Nicolas");

        tr.addView(cell);
        tl.addView(tr);
    }
}