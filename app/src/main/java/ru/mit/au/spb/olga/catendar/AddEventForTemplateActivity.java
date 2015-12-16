package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.GregorianCalendar;

/**
 * Created by olga on 16.12.15.
 */
public class AddEventForTemplateActivity  extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private EditText eventText;
    private EditText eventStartTime;
    private EditText eventEndTime;

    private int selectedRadioButton = 0;

    private final static int year = 1970;
    private final static int month = 0;
    private final static int day[] = {4, 5, 6, 7, 8, 9, 10};

    private int idTemplate;

    public final static String EVENT_NAME = "ru.mit.au.spb.olga.catendar.eventName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_event_in_temlate);

        idTemplate = getIntent().getIntExtra("id", 0);

        eventText = (EditText)findViewById(R.id.editEventTextForTemplate);
        eventStartTime = (EditText)findViewById(R.id.editStartTime);
        eventEndTime = (EditText)findViewById(R.id.editFinishTime);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase10.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup2);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.radioButtonSunday:
                        selectedRadioButton = 0;
                        break;
                    case R.id.radioButtonMonday:
                        selectedRadioButton = 1;
                        break;
                    case R.id.radioButtonTuesday:
                        selectedRadioButton = 2;
                        break;
                    case R.id.radioButtonWednesday:
                        selectedRadioButton = 3;
                        break;
                    case R.id.radioButtonThursday:
                        selectedRadioButton = 4;
                        break;
                    case R.id.radioButtonFriday:
                        selectedRadioButton = 5;
                        break;
                    case R.id.radioButtonSaturday:
                        selectedRadioButton = 6;
                        break;
                    default:
                        break;
                }
            }
        });
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

        String timeStart = String.valueOf(eventStartTime.getText());
        String timeEnd = String.valueOf(eventEndTime.getText());

        Event createEvent = new Event();
        createEvent.setText(String.valueOf(eventText.getText()));

        ContentValues newValues = new ContentValues();

        GregorianCalendar startCal = new GregorianCalendar(year, month, day[selectedRadioButton],
                hourFromTime(timeStart),
                minuteFromTime(timeStart));

        GregorianCalendar endCal = new GregorianCalendar(year, month, day[selectedRadioButton],
                hourFromTime(timeEnd),
                minuteFromTime(timeEnd));

        newValues.put(DatabaseHelper.EVENT_NAME, String.valueOf(createEvent.getText()));
        newValues.put(DatabaseHelper.EVENT_PARENT_TEMPLATE, idTemplate);
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

