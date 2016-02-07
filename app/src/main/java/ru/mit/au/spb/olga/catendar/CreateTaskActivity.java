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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by olga on 05.02.16.
 */
public class CreateTaskActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener {

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private EditText taskText;

    private int priority = 5;
    private TextView priorityView;

    private EditText commentText;

    private int DIALOG_DATE_START = 1;
    private DatePicker startDate;
    private TextView startDateTextView;
    private int startYear, startMonth, startDay;
    private int startFlag = 0;

    private int DIALOG_DATE_FINISH = 2;
    private DatePicker finishDate;
    private TextView finishDateTextView;
    private int finishYear, finishMonth, finishDay;
    private int finishFlag = 0;

    private int DIALOG_TIME_DURATION = 3;
    private TimePicker durationTime;
    private TextView durationTimeTextView;
    private int durationHours = 2, durationMinute = 0;

    private int DIALOG_TIME_START = 4;
    private TimePicker startTime;
    private TextView startTimeTextView;
    private int startHours = 0, startMinute = 0;

    private int DIALOG_TIME_FINISH = 5;
    private TimePicker finishTime;
    private TextView finishTimeTextView;
    private int finishHours = 23, finishMinute = 59;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_task);

        taskText = (EditText)findViewById(R.id.createTaskEditTextTaskText);

        commentText = (EditText)findViewById(R.id.createTaskEditTextComments);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase13.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        final SeekBar seekbar = (SeekBar)findViewById(R.id.createTaskSeekBar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setMax(10);
        seekbar.setProgress(5);
        priority = 5;
        priorityView = (TextView)findViewById(R.id.createTaskTextViewPriority);
        priorityView.setText(String.valueOf(priorityView.getText()) + " 5");

        startDateTextView = (TextView)findViewById(R.id.createTaskTextViewStartDate);
        finishDateTextView = (TextView)findViewById(R.id.createTaskTextViewFinishDate);
        durationTimeTextView = (TextView)findViewById(R.id.createTaskTextViewDuration);
        startTimeTextView = (TextView)findViewById(R.id.createTaskTextViewStartTime);
        finishTimeTextView = (TextView)findViewById(R.id.createTaskTextViewFinishTime);

        Calendar today = Calendar.getInstance();
        startYear = today.get(Calendar.YEAR);
        startMonth = today.get(Calendar.MONTH);
        startDay = today.get(Calendar.DAY_OF_MONTH);

        finishYear = today.get(Calendar.YEAR);
        finishMonth = today.get(Calendar.MONTH);
        finishDay = today.get(Calendar.DAY_OF_MONTH);
    }

    public void onSetStartDate(View view) {
        showDialog(DIALOG_DATE_START);
    }

    public void onSetFinishDate(View view) {
        showDialog(DIALOG_DATE_FINISH);
    }

    public void onSetStartTime(View view) {
        showDialog(DIALOG_TIME_START);
    }

    public void onSetFinishTime(View view) {
        showDialog(DIALOG_TIME_FINISH);
    }

    public void onSetDuration(View view) {
        showDialog(DIALOG_TIME_DURATION);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE_START) {
            DatePickerDialog tpd = new DatePickerDialog(this, (DatePickerDialog.OnDateSetListener) myCallBackStartDate, startYear, startMonth, startDay);
            return tpd;
        } else if (id == DIALOG_DATE_FINISH) {
            DatePickerDialog tpd = new DatePickerDialog(this, (DatePickerDialog.OnDateSetListener) myCallBackFinishDate, finishYear, finishMonth, finishDay);
            return tpd;
        } else if (id == DIALOG_TIME_START) {
            TimePickerDialog tpd = new TimePickerDialog(this, (TimePickerDialog.OnTimeSetListener) myCallBackStartTime, startHours, startMinute, true);
            return tpd;
        } else if (id == DIALOG_TIME_FINISH) {
            TimePickerDialog tpd = new TimePickerDialog(this, (TimePickerDialog.OnTimeSetListener) myCallBackFinishTime, finishHours, finishMinute, true);
            return tpd;
        } else if (id == DIALOG_TIME_DURATION) {
            TimePickerDialog tpd = new TimePickerDialog(this, (TimePickerDialog.OnTimeSetListener) myCallBackDurationTime, durationHours, durationMinute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }


    DatePickerDialog.OnDateSetListener myCallBackStartDate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int _year, int monthOfYear,
                              int dayOfMonth) {
            startFlag = 1;
            startYear = _year;
            startMonth = monthOfYear;
            startDay = dayOfMonth;
            startDateTextView.setText("Date when you can start: " + startDay + "." + startMonth + "." + startYear);
        }
    };

    TimePickerDialog.OnTimeSetListener myCallBackDurationTime = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int _minute) {
            durationHours = hourOfDay;
            durationMinute = _minute;
            durationTimeTextView.setText("Time for this task: " + durationHours + ":" + durationMinute);
        }
    };


    DatePickerDialog.OnDateSetListener myCallBackFinishDate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int _year, int monthOfYear,
                              int dayOfMonth) {
            finishFlag = 1;
            finishYear = _year;
            finishMonth = monthOfYear;
            finishDay = dayOfMonth;
            finishDateTextView.setText("Deadline date: " + finishDay + "." + finishMonth + "." + finishYear);
        }
    };

    TimePickerDialog.OnTimeSetListener myCallBackStartTime = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int _minute) {
            startHours = hourOfDay;
            startMinute = _minute;
            startTimeTextView.setText("Time when you can start: " + startHours + ":" + startMinute);
        }
    };


    TimePickerDialog.OnTimeSetListener myCallBackFinishTime = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int _minute) {
            finishHours = hourOfDay;
            finishMinute = _minute;
            finishTimeTextView.setText("Deadline time: " + finishHours + ":" + finishMinute);
        }
    };

    public void onOkClick(View view) {
        Intent answerIntent = new Intent();

        String taskTextString = String.valueOf(taskText.getText());

        String commentString = String.valueOf(commentText.getText());

        ContentValues newValues = new ContentValues();

        GregorianCalendar startCal = new GregorianCalendar(startYear, startMonth, startDay, startHours, startMinute);

        if (finishFlag == 0) {
            finishYear += 179;
        }
        GregorianCalendar endCal = new GregorianCalendar(finishYear, finishMonth, finishDay, finishHours, finishMinute);

        GregorianCalendar durCal = new GregorianCalendar(0, 0, 0, durationHours, durationMinute);

        newValues.put(DatabaseHelper.TASK_NAME_COLUMN, taskTextString);
        newValues.put(DatabaseHelper.TASK_COMMENT, commentString);
        newValues.put(DatabaseHelper.TASK_START_TIME, startCal.getTimeInMillis() / 1000);
        newValues.put(DatabaseHelper.TASK_DEADLINE, endCal.getTimeInMillis() / 1000);
        newValues.put(DatabaseHelper.TASK_DURATION, durCal.getTimeInMillis() / 1000);
        newValues.put(DatabaseHelper.TASK_IS_DONE, 0);
        newValues.put(DatabaseHelper.TASK_PRIORITY, priority);

        mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_TASK, null, newValues);

        setResult(RESULT_OK, answerIntent);
        finish();
    }


    public CreateTaskActivity() {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        priority = seekBar.getProgress();
        priorityView.setText("Priority: " + priority);
    }
}
