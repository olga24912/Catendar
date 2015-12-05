package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by olga on 18.10.15.
 */
public class CreateEventActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private EditText eventText;
    private EditText eventStartDate;
    private EditText eventEndDate;
    private EditText eventStartTime;
    private EditText eventEndTime;

    public final static String EVENT_NAME = "ru.mit.au.spb.olga.catendar.eventName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_event);

        eventText = (EditText)findViewById(R.id.editEventText);
        eventStartDate = (EditText)findViewById(R.id.editDateStartEvent);
        eventEndDate = (EditText)findViewById(R.id.editDateEndEvent);
        eventStartTime = (EditText)findViewById(R.id.editTimeStartEvent);
        eventEndTime = (EditText)findViewById(R.id.editTimeEndEvent);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase6.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }

    private int yearFromDate (String date) {
        String[] ddmmyyyy = date.split("\\.");
        return Integer.parseInt(ddmmyyyy[2]);
    }

    private int monthFromDate (String date) {
        String[] ddmmyyyy = date.split("\\.");
        return Integer.parseInt(ddmmyyyy[1]);
    }

    private int dayFromDate (String date) {
        String[] ddmmyyyy = date.split("\\.");
        return Integer.parseInt(ddmmyyyy[0]);
    }

    private int  hourFromTime (String time) {
        String[] hhmm = time.split(":");
        return Integer.parseInt(hhmm[0]);
    }

    private int minuteFromTime (String time) {
        String[] hhmm = time.split(":");
        return Integer.parseInt(hhmm[1]);
    }

    public void onOkClick(View view) {
        Intent answerIntent = new Intent();

        String dateStart = String.valueOf(eventStartDate.getText());
        String dateEnd = String.valueOf(eventEndDate.getText());
        String timeStart = String.valueOf(eventStartTime.getText());
        String timeEnd = String.valueOf(eventEndTime.getText());

        Event createEvent = new Event();
        createEvent.setText(String.valueOf(eventText.getText()));

        ContentValues newValues = new ContentValues();

        newValues.put(DatabaseHelper.EVENT_NAME, String.valueOf(createEvent.getText()));
        newValues.put(DatabaseHelper.EVENT_PARENT_CALENDAR, 0);
        newValues.put(DatabaseHelper.EVENT_YEAR_OF_START, Integer.valueOf(yearFromDate(dateStart)));
        newValues.put(DatabaseHelper.EVENT_MONTH_OF_START, monthFromDate(dateStart));
        newValues.put(DatabaseHelper.EVENT_DAY_OF_START, dayFromDate(dateStart));
        newValues.put(DatabaseHelper.EVENT_HOUR_OF_START, hourFromTime(timeStart));
        newValues.put(DatabaseHelper.EVENT_MINUTE_OF_START, minuteFromTime(timeStart));

        newValues.put(DatabaseHelper.EVENT_YEAR_OF_END, yearFromDate(dateEnd));
        newValues.put(DatabaseHelper.EVENT_MONTH_OF_END, monthFromDate(dateEnd));
        newValues.put(DatabaseHelper.EVENT_DAY_OF_END, dayFromDate(dateEnd));
        newValues.put(DatabaseHelper.EVENT_HOUR_OF_END, hourFromTime(timeEnd));
        newValues.put(DatabaseHelper.EVENT_MINUTE_OF_END, minuteFromTime(timeEnd));

        mSQLiteDatabase.insert("events", null, newValues);

        answerIntent.putExtra(EVENT_NAME, createEvent.getText());

        setResult(RESULT_OK, answerIntent);
        finish();
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
