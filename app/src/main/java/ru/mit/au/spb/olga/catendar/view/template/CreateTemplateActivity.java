package ru.mit.au.spb.olga.catendar.view.template;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;
import ru.mit.au.spb.olga.catendar.model.Event;
import ru.mit.au.spb.olga.catendar.view.events.AddEventForTemplateActivity;

public class CreateTemplateActivity extends AppCompatActivity {
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Event> eventList = new ArrayList<>();

    private ListView listOfEvent;
    private EditText templateName;

    private long templateId = -1;
    private Boolean deleteOnCancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_template);

        templateId = getIntent().getLongExtra("id", -1);
        listOfEvent = (ListView) findViewById(R.id.listViewInCreateTemplate);

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        templateName = (EditText) findViewById(R.id.editTemplate);

        String newTemplate = "unknownTemplate179";
        if (templateId == -1) {
            deleteOnCancel = true;
            ContentValues newValues = new ContentValues();
            newValues.put(DatabaseHelper.TEMPLATE_NAME, newTemplate);

            templateId = mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_TEMPLATE, null, newValues);
        } else {
            Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{
                            DatabaseHelper._ID, DatabaseHelper.TEMPLATE_NAME,
                            DatabaseHelper.TEMPLATE_FOR_WEEK, DatabaseHelper.TEMPLATE_WEEK_ID,
                            DatabaseHelper.TEMPLATE_ORIGIN_ID
                    },
                    null, null,
                    null, null, null) ;


            while (cursor.moveToNext()) {
                int idTmp = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEMPLATE_NAME));

                if (idTmp == templateId) {
                    templateName.setText(name);
                }
            }
            cursor.close();
        }
        synchronizedWithDataBase();
        drawEventList();
    }

    @NotNull
    public String getDayOfWeekAndTime(Event event) {
        String[] days = new DateFormatSymbols().getShortWeekdays();

        return "(" + days[event.getStartDate().get(Calendar.DAY_OF_WEEK)] + " "
                + event.getStartDate().get(Calendar.HOUR_OF_DAY) + ":"
                + event.getStartDate().get(Calendar.MINUTE) + " - "
                + event.getEndDate().get(Calendar.HOUR_OF_DAY) + ":"
                + event.getEndDate().get(Calendar.MINUTE) + ")";
    }

    private void drawEventList() {
        String[] myEventInString = new String[eventList.size()];

        for (int i = 0; i < eventList.size(); i++) {
            Event currentEvent = eventList.get(i);
            myEventInString[i] = currentEvent.getText() + " " + getDayOfWeekAndTime(currentEvent);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, myEventInString);

        listOfEvent.setAdapter(adapter);

        listOfEvent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long delId = eventList.get(position).getId();
                mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_EVENT, DatabaseHelper._ID + "=" + delId, null);

                synchronizedWithDataBase();
                drawEventList();
                return true;
            }
        });
    }


    private void synchronizedWithDataBase() {
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

            currentEvent.setStartDateInSeconds(cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper.EVENT_START_DATE)));

            currentEvent.setDuration(cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper.EVENT_END_DATE)) -
                    cursor.getInt(cursor
                            .getColumnIndex(DatabaseHelper.EVENT_START_DATE)));

            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            currentEvent.setId((long)id);
            eventList.add(currentEvent);
        }

        cursor.close();
    }

    public void onCancelClick(View view) {
        if (deleteOnCancel) {
            mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_TEMPLATE, DatabaseHelper._ID + "=" + templateId, null);
        }
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
                synchronizedWithDataBase();
                drawEventList();
            }
        }
    }
}