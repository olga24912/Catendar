package ru.mit.au.spb.olga.catendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by olga on 18.10.15.
 */
public class CreateEventActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private EditText eventText;

    private int DIALOG_DATE = 1;
    private DatePicker pickerDate;

    private int DIALOG_TIME = 2;
    private int hour = 14;
    private int minute = 00;

    private TextView tvInfo;
    private TextView tvInfoStartTime;

    private TextView lenOfEvent;

    private int year;
    private int month;
    private int day;

    private int duration;

    private ArrayList<EditText> taskText = new ArrayList<>();

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

        final SeekBar seekbar = (SeekBar)findViewById(R.id.seekBarOfEventLen);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setMax(24);
        seekbar.setProgress(1);

        duration = 1;

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase10.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_MONTH);
        tvInfoStartTime.setText("Start time is " + hour + " hours " + minute + " minutes");
        tvInfo.setText("Event day is " + day + "/" + (month + 1) + "/" + year);
    }

    public void onSetDateClick(View view) {
        showDialog(DIALOG_DATE);
    }

    public void onSetTimeClick(View view) {
        showDialog(DIALOG_TIME);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, (DatePickerDialog.OnDateSetListener) myCallBackDate, year, month, day);
            return tpd;
        } else if (id == DIALOG_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, (TimePickerDialog.OnTimeSetListener) myCallBackTime, hour, minute, true);
            return tpd;
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

        GregorianCalendar endCal = startCal;

        newValues.put(DatabaseHelper.EVENT_NAME, String.valueOf(createEvent.getText()));
        newValues.put(DatabaseHelper.EVENT_PARENT_TEMPLATE, 0);
        newValues.put(DatabaseHelper.EVENT_START_DATE, startCal.getTimeInMillis()/1000);
        endCal.add(Calendar.HOUR_OF_DAY, duration);
        newValues.put(DatabaseHelper.EVENT_END_DATE, endCal.getTimeInMillis() / 1000);

        int parentID = (int)mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_EVENT, null, newValues);

        for (EditText currentTask : taskText) {
            newValues = new ContentValues();

            newValues.put(DatabaseHelper.TASK_NAME_COLUMN, String.valueOf(currentTask.getText()));
            newValues.put(DatabaseHelper.TASK_PARENT_EVENT_ID, parentID);
            newValues.put(DatabaseHelper.TASK_IS_DONE, 0);

            mSQLiteDatabase.insert("tasks", null, newValues);
        }


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

    public void onAddTaskClick(View view) {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayoutInCreateTask);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.gravity = Gravity.LEFT;
        layoutParams.setMargins(0, 10, 10, 10);
        EditText textView = new EditText(this);
        textView.setLayoutParams(layoutParams);
        textView.setHint("Task text");
        taskText.add(textView);
        linearLayout.addView(textView);
    }
}
