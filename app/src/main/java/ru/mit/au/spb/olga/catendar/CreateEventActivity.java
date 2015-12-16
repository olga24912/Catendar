package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.GregorianCalendar;

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

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase9.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }


    Boolean notCorrectMonth(String month) {
        try {
            int vl = Integer.parseInt(month);
            if (vl < 1 || vl > 12) {
                return true;
            }
        } catch (Exception ignored) {
            return true;
        }
        return false;
    }

    Boolean notCorrectYear(String year) {
        try {
            int vl = Integer.parseInt(year);
            if (vl < 0 || vl > 9999) {
                return true;
            }
        } catch (Exception ignored) {
            return true;
        }
        return false;
    }

    Boolean notCorrectDay(String day) {
        try {
            int vl = Integer.parseInt(day);
            if (vl < 1 || vl > 31) {
                return true;
            }
        } catch (Exception ignored) {
            return true;
        }
        return false;
    }

    Boolean notCorrectHours(String hours) {
        try {
            int vl = Integer.parseInt(hours);
            if (vl < 0 || vl > 23) {
                return true;
            }
        } catch (Exception ignored) {
            return true;
        }
        return false;
    }

    Boolean notCorrectMinute(String minute) {
        try {
            int vl = Integer.parseInt(minute);
            if (vl < 0 || vl > 59) {
                return true;
            }
        } catch (Exception ignored) {
            return true;
        }
        return false;
    }


    private int yearFromDate (String date) {
        String[] ddmmyyyy = date.split("\\.");
        if (ddmmyyyy.length < 3 || notCorrectYear(ddmmyyyy[2])) {
            return 9999;
        }
        return Integer.parseInt(ddmmyyyy[2]);
    }

    private int monthFromDate (String date) {
        String[] ddmmyyyy = date.split("\\.");
        if (ddmmyyyy.length < 2 || notCorrectMonth(ddmmyyyy[1])){
            return 01;
        }
        return Integer.parseInt(ddmmyyyy[1]);
    }

    private int dayFromDate (String date) {
        String[] ddmmyyyy = date.split("\\.");
        if (ddmmyyyy.length < 1 || notCorrectDay(ddmmyyyy[0])) {
            return 01;
        }
        return Integer.parseInt(ddmmyyyy[0]);
    }

    private int  hourFromTime (String time) {
        String[] hhmm = time.split(":");
        if (hhmm.length < 1 || notCorrectHours(hhmm[0])) {
            return 0;
        }
        return Integer.parseInt(hhmm[0]);
    }

    private int minuteFromTime (String time) {
        String[] hhmm = time.split(":");
        if (hhmm.length < 2 || notCorrectMinute(hhmm[1])) {
            return 0;
        }
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

        GregorianCalendar startCal = new GregorianCalendar(yearFromDate(dateStart),
                monthFromDate(dateStart),
                dayFromDate(dateStart),
                hourFromTime(timeStart),
                minuteFromTime(timeStart));

        GregorianCalendar endCal = new GregorianCalendar(yearFromDate(dateEnd),
                monthFromDate(dateEnd),
                dayFromDate(dateEnd),
                hourFromTime(timeEnd),
                minuteFromTime(timeEnd));

        newValues.put(DatabaseHelper.EVENT_NAME, String.valueOf(createEvent.getText()));
        newValues.put(DatabaseHelper.EVENT_PARENT_TEMPLATE, 0);
        newValues.put(DatabaseHelper.EVENT_START_DATE, startCal.getTimeInMillis()/1000);
        newValues.put(DatabaseHelper.EVENT_END_DATE, endCal.getTimeInMillis()/1000);

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
