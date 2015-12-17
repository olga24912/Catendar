package ru.mit.au.spb.olga.catendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by olga on 16.12.15.
 */
public class AddEventForTemplateActivity  extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private EditText eventText;

    private int selectedRadioButton = 0;

    private final static int year = 1970;
    private final static int month = 0;
    private final static int day[] = {4, 5, 6, 7, 8, 9, 10};

    private int idTemplate;

    private int DIALOG_TIME = 2;
    private int hour = 14;
    private int minute = 00;
    private TextView tvInfoStartTime;

    private TextView lenOfEvent;

    private int duration;

    public final static String EVENT_NAME = "ru.mit.au.spb.olga.catendar.eventName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_event_in_temlate);

        idTemplate = getIntent().getIntExtra("id", 0);

        eventText = (EditText)findViewById(R.id.editEventTextForTemplate);
        tvInfoStartTime = (TextView)findViewById(R.id.setTimeTextViewInTemplate);
        lenOfEvent = (TextView)findViewById(R.id.durationValForTemplate);
        lenOfEvent.setText("1");

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase10.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        final SeekBar seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setMax(24);
        seekbar.setProgress(1);

        duration = 1;

        tvInfoStartTime.setText("Start time is \n" + hour + " hours " + minute + " minutes");

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

    public void onSetTimeClick(View view) {
        showDialog(DIALOG_TIME);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, (TimePickerDialog.OnTimeSetListener) myCallBackTime, hour, minute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myCallBackTime = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int _minute) {
            hour = hourOfDay;
            minute = _minute;
            tvInfoStartTime.setText("Start time is \n" + hour + " hours " + minute + " minutes");
        }
    };

    public void onOkClick(View view) {
        Intent answerIntent = new Intent();

        Event createEvent = new Event();
        createEvent.setText(String.valueOf(eventText.getText()));

        ContentValues newValues = new ContentValues();

        GregorianCalendar startCal = new GregorianCalendar(year, month, day[selectedRadioButton],
                hour, minute);

        GregorianCalendar endCal = startCal;

        newValues.put(DatabaseHelper.EVENT_NAME, String.valueOf(createEvent.getText()));
        newValues.put(DatabaseHelper.EVENT_PARENT_TEMPLATE, idTemplate);
        newValues.put(DatabaseHelper.EVENT_START_DATE, startCal.getTimeInMillis() / 1000);
        endCal.add(Calendar.HOUR_OF_DAY, duration);
        newValues.put(DatabaseHelper.EVENT_END_DATE, endCal.getTimeInMillis() / 1000);

        mSQLiteDatabase.insert("events", null, newValues);

        answerIntent.putExtra(EVENT_NAME, createEvent.getText());

        setResult(RESULT_OK, answerIntent);
        finish();
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        lenOfEvent.setText(String.valueOf(seekBar.getProgress()));
        duration = seekBar.getProgress();
    }
}

