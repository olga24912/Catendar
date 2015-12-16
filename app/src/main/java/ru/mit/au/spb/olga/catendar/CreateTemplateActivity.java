package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by olga on 16.12.15.
 */
public class CreateTemplateActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Event> eventList = new ArrayList<>();
    private ListView listOfEvent;

    private EditText templateName;

    private int templateId;

    private String newTemplate = "unknownTemplate179";

    private void drawEventList() {
        String[] myEventInString = new String[eventList.size()];

        for (int i = 0; i < eventList.size(); i++) {
            Event currentEvent = eventList.get(i);
            myEventInString[i] = currentEvent.getText() + " " + currentEvent.getDayOfWeekAndTime();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, myEventInString);

        listOfEvent.setAdapter(adapter);
    }


    private void synchronizedWithDateBase() {
        Cursor cursor = mSQLiteDatabase.query("events", new String[]{DatabaseHelper._ID, DatabaseHelper.EVENT_NAME,
                        DatabaseHelper.EVENT_PARENT_TEMPLATE, DatabaseHelper.EVENT_START_DATE,
                        DatabaseHelper.EVENT_END_DATE
                },
                null, null,
                null, null, null) ;

        eventList.clear();

        while (cursor.moveToNext()) {
            Event currentEvent = new Event();
            int tmp_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.EVENT_PARENT_TEMPLATE));
            if (tmp_id != templateId) {
                continue;
            }
            currentEvent.setText(cursor.getString(cursor
                    .getColumnIndex(DatabaseHelper.EVENT_NAME)));

            currentEvent.setStartDate(cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper.EVENT_START_DATE)));

            currentEvent.setEndDate(cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper.EVENT_END_DATE)));
            eventList.add(currentEvent);
        }

        cursor.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_template);

        listOfEvent = (ListView) findViewById(R.id.listViewInCreateTemplate);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase9.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        templateName = (EditText) findViewById(R.id.editTemplate);

        ContentValues newValues = new ContentValues();

        newValues.put(DatabaseHelper.TEMPLATE_NAME, newTemplate);

        mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_TEMPLATE, null, newValues);

        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.TEMPLATE_NAME,
                      },
                null, null,
                null, null, null) ;


        while (cursor.moveToNext()) {
            int idTmp = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));;
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEMPLATE_NAME));

            if (name == newTemplate) {
                templateId = idTmp;
            }
        }
        synchronizedWithDateBase();
        drawEventList();
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onOkClick(View view) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.TEMPLATE_NAME, String.valueOf(templateName.getText()));

        mSQLiteDatabase.update(DatabaseHelper.DATABASE_TABLE_TEMPLATE, cv, "_id " + "=" + templateId, null);

        setResult(RESULT_OK);
        finish();
    }

    static final private int CREATE_EVENT = 0;
    public void onCreateEventClick(View view) {
        Intent intent = new Intent(CreateTemplateActivity.this, AddEventForTemplateActivity.class);
        intent.putExtra("id", templateId);
        startActivityForResult(intent, CREATE_EVENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT) {
            if (resultCode == RESULT_OK) {
                synchronizedWithDateBase();
                drawEventList();
            }
        }
    }
}