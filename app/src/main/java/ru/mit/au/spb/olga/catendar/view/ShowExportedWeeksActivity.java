package ru.mit.au.spb.olga.catendar.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

import io.cloudboost.CloudException;
import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.utils.CalendarToICSWriter;

public class ShowExportedWeeksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_exported_weeks);

        EditText urlsList = (EditText) findViewById(R.id.urlList);

        ArrayList<String> urls = new ArrayList<>();
        try {
            urls = CalendarToICSWriter.getUrlsFromCloud();
        } catch (InterruptedException | CloudException e) {
            new RuntimeException(e.getMessage(), e);
        }
//        urlsList.setText(urls == null ? "null" : urls.toString());
        StringBuilder text = new StringBuilder("");
        for (int i = 0; i < urls.size(); i++) {
            text.append(urls.get(i));
            text.append("\n");
        }

        urlsList.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_exported_weeks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
