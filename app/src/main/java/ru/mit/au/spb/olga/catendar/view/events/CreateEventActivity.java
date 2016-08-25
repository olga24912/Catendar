package ru.mit.au.spb.olga.catendar.view.events;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;
import ru.mit.au.spb.olga.catendar.model.Event;

public class CreateEventActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener {

    SQLiteDatabase mSQLiteDatabase;

    private EditText eventText;

    private int DIALOG_DATE = 1;

    private int DIALOG_TIME = 2;
    private int hour = 14;
    private int minute = 0;

    private TextView tvInfo;
    private TextView tvInfoStartTime;

    private TextView lenOfEvent;

    private int year;
    private int month;
    private int day;

    private int duration;

    public final static String EVENT_NAME = "ru.mit.au.spb.olga.catendar.eventName";


    public CreateEventActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_event);

        eventText = (EditText)findViewById(R.id.editEventText);

        tvInfo = (TextView)findViewById(R.id.StartDate);
        tvInfoStartTime = (TextView)findViewById(R.id.StartTime);
        lenOfEvent = (TextView)findViewById(R.id.durationVal);
        lenOfEvent.setText("1");

        long startTime = getIntent().getLongExtra("startTime", (new GregorianCalendar().getTimeInMillis()/1000));
        GregorianCalendar currentTime = new GregorianCalendar();
        currentTime.setTimeInMillis(startTime*1000);

        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = 0;

        year = currentTime.get(Calendar.YEAR);
        month = currentTime.get(Calendar.MONTH);
        day = currentTime.get(Calendar.DAY_OF_MONTH);

        final SeekBar seekbar = (SeekBar)findViewById(R.id.seekBarOfEventLen);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setMax(24);
        seekbar.setProgress(1);

        duration = 1;

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        /*Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_MONTH);*/
        tvInfoStartTime.setText("Start time is " + hour + " hours " + minute + " minutes");
        tvInfo.setText("Event day is " + day + "/" + (month + 1) + "/" + year);

        createRadioButton();
    }

    private void createRadioButton() {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{DatabaseHelper._ID,
                        DatabaseHelper.TEMPLATE_NAME,
                        DatabaseHelper.TEMPLATE_FOR_WEEK,
                        DatabaseHelper.TEMPLATE_WEEK_ID,
                        DatabaseHelper.TEMPLATE_ORIGIN_ID},
                null, null,
                null, null, null) ;



        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.VERTICAL);

        RadioButton rbNon = new RadioButton(this);
        rg.addView(rbNon);
        rbNon.setText("");

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor
                    .getColumnIndex(DatabaseHelper.TEMPLATE_NAME));

            int id = cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper._ID));

            int forWeek = cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper.TEMPLATE_FOR_WEEK));

            if (forWeek == 1) {
                continue;
            }
            if (name.equals("unknownTemplate179")) {
                mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_TEMPLATE, DatabaseHelper._ID + "=" + id, null);
            } else {
                RadioButton rb = new RadioButton(this);
                rg.addView(rb);
                rb.setText(name);
            }
        }
        cursor.close();

        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollViewChoose);

        scrollView.addView(rg);
    }

    public void onSetDateClick(View view) {
        showDialog(DIALOG_DATE);
    }

    public void onSetTimeClick(View view) {
        showDialog(DIALOG_TIME);
    }

    @NotNull
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            return new DatePickerDialog(this, myCallBackDate, year, month, day);
        } else if (id == DIALOG_TIME) {
            return new TimePickerDialog(this, myCallBackTime, hour, minute, true);
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBackDate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int _year, int monthOfYear,
                              int dayOfMonth) {
            year = _year;
            month = monthOfYear;
            day = dayOfMonth;
            tvInfo.setText("Event day is " + day + "/" + (month + 1) + "/" + year);
        }
    };

    TimePickerDialog.OnTimeSetListener myCallBackTime = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int _minute) {
            hour = hourOfDay;
            minute = _minute;
            tvInfoStartTime.setText("Start time is " + hour + " hours " + minute + " minutes");
        }
    };

    public void onOkClick(View view) {
        Intent answerIntent = new Intent();

        Event createEvent = new Event();
        createEvent.setText(String.valueOf(eventText.getText()));

        ContentValues newValues = new ContentValues();

        GregorianCalendar startCal = new GregorianCalendar(year, month, day, hour, minute);
        GregorianCalendar endCal = new GregorianCalendar(year, month, day, hour, minute);

        newValues.put(DatabaseHelper.EVENT_NAME, String.valueOf(createEvent.getText()));
        newValues.put(DatabaseHelper.EVENT_PARENT_TEMPLATE, 0);
        newValues.put(DatabaseHelper.EVENT_START_DATE, startCal.getTimeInMillis()/1000);
        endCal.add(Calendar.HOUR_OF_DAY, duration);
        newValues.put(DatabaseHelper.EVENT_END_DATE, endCal.getTimeInMillis() / 1000);

        mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_EVENT, null, newValues);

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
