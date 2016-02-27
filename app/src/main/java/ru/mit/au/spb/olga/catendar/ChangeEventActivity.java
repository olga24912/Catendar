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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ChangeEventActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener {
    private SQLiteDatabase mSQLiteDatabase;

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

    private int eventId;
    private int parentId;

    private ArrayList<EditText> taskText = new ArrayList<>();
    private ArrayList<Integer> taskId = new ArrayList<>();

    public final static String EVENT_NAME = "ru.mit.au.spb.olga.catendar.eventName";

    public ChangeEventActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_event);

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this, "mydatabase14.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        eventId = getIntent().getIntExtra("id", 0);

        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_EVENT, new String[]{DatabaseHelper._ID,
                        DatabaseHelper.EVENT_START_DATE, DatabaseHelper.EVENT_END_DATE,
                DatabaseHelper.EVENT_NAME, DatabaseHelper.EVENT_PARENT_TEMPLATE},
                null, null,
                null, null, null);

        eventText = (EditText)findViewById(R.id.changeEventText);

        tvInfo = (TextView)findViewById(R.id.changeStartDate);
        tvInfoStartTime = (TextView)findViewById(R.id.changeStartTime);
        lenOfEvent = (TextView)findViewById(R.id.durationValChange);
        lenOfEvent.setText("1");

        final SeekBar seekbar = (SeekBar)findViewById(R.id.changesSeekBarOfEventLen);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setMax(24);
        seekbar.setProgress(1);

        duration = 1;

        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_MONTH);

        while (cursor.moveToNext())  {
            int currentId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            if (currentId == eventId) {
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_NAME));
                int startTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.EVENT_START_DATE));
                int endTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.EVENT_END_DATE));
                parentId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.EVENT_PARENT_TEMPLATE));
                seekbar.setProgress((endTime - startTime)/(60*60));
                duration = (endTime - startTime)/(60*60);

                lenOfEvent.setText(Integer.toString((endTime - startTime) / (60 * 60)));

                GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis((long)startTime*1000);
                year = gc.get(Calendar.YEAR);
                month = gc.get(Calendar.MONTH);
                day = gc.get(Calendar.DAY_OF_MONTH);

                hour = gc.get(Calendar.HOUR_OF_DAY);
                minute = gc.get(Calendar.MINUTE);
                eventText.setText(name);
            }
        }

        cursor.close();

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

    public void onDeleteClick(View view) {
        Intent answerIntent = new Intent();

        mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_EVENT, "_id " + "=" + eventId, null);

        for (int tId : taskId) {
            mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_TASK, "_id " + "=" + tId, null);
        }
        setResult(RESULT_OK, answerIntent);
        finish();
    }

    public void onOkClick(View view) {
        Intent answerIntent = new Intent();

        Event createEvent = new Event();
        createEvent.setText(String.valueOf(eventText.getText()));

        ContentValues newValues = new ContentValues();

        GregorianCalendar startCal = new GregorianCalendar(year, month, day, hour, minute);
        GregorianCalendar endCal = new GregorianCalendar(year, month, day, hour, minute);

        newValues.put(DatabaseHelper.EVENT_NAME, String.valueOf(createEvent.getText()));
        newValues.put(DatabaseHelper.EVENT_PARENT_TEMPLATE, 0);
        newValues.put(DatabaseHelper.EVENT_START_DATE, startCal.getTimeInMillis() / 1000);
        endCal.add(Calendar.HOUR_OF_DAY, duration);
        newValues.put(DatabaseHelper.EVENT_END_DATE, endCal.getTimeInMillis() / 1000);
        newValues.put(DatabaseHelper.EVENT_PARENT_TEMPLATE, parentId);

        mSQLiteDatabase.update(DatabaseHelper.DATABASE_TABLE_EVENT, newValues, "_id " + "=" + eventId, null);

        for (int i = 0; i < taskText.size(); i++) {
            EditText currentTask = taskText.get(i);
            newValues = new ContentValues();

            newValues.put(DatabaseHelper.TASK_NAME_COLUMN, String.valueOf(currentTask.getText()));
            newValues.put(DatabaseHelper.TASK_IS_DONE, 0);

            if (i >= taskId.size()) {
                mSQLiteDatabase.insert("tasks", null, newValues);
            } else {
                if (String.valueOf(currentTask.getText()).equals("")) {
                    mSQLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE_TASK, "_id " + "=" + taskId.get(i), null);
                } else {
                    mSQLiteDatabase.update(DatabaseHelper.DATABASE_TABLE_TASK, newValues, "_id " + "=" + taskId.get(i), null);
                }
            }
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
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayoutInChangeEvent);

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
