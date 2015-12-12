package ru.mit.au.spb.olga.catendar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase7.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void onCalendarButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    static final private int CREATE_EVENT = 0;
    public void onCreateEventClick(View view) {
        Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);

        startActivityForResult(intent, CREATE_EVENT);
    }

    private ArrayList<String> eventList = new ArrayList<>();

    public void onTaskListClick(View view) {
        Intent intent = new Intent(MainActivity.this, TaskListActivity.class);

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ListView listOfEvent = (ListView) findViewById(R.id.listView);

        if (requestCode == CREATE_EVENT) {
            if (resultCode == RESULT_OK) {
                eventList.add(data.getStringExtra(CreateEventActivity.EVENT_NAME));

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, eventList);

                listOfEvent.setAdapter(adapter);
            }
        }
    }
}
