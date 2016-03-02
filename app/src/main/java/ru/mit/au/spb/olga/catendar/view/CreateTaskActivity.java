package ru.mit.au.spb.olga.catendar.view;

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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;

public class CreateTaskActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener {

    private SQLiteDatabase mSQLiteDatabase;

    private EditText taskText;

    private int priority = 5;
    private TextView priorityView;

    private EditText commentText;

    private int DIALOG_DATE_START = 1;
    private TextView startDateTextView;
    private int startYear, startMonth, startDay;

    private int DIALOG_DATE_FINISH = 2;
    private TextView finishDateTextView;
    private int finishYear, finishMonth, finishDay;
    private int finishFlag = 0;

    private int DIALOG_TIME_DURATION = 3;
    private TextView durationTimeTextView;
    private int durationHours = 2, durationMinute = 0;

    private int DIALOG_TIME_START = 4;
    private TextView startTimeTextView;
    private int startHours = 0, startMinute = 0;

    private int DIALOG_TIME_FINISH = 5;
    private TextView finishTimeTextView;
    private int finishHours = 23, finishMinute = 59;

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getLongExtra("id", -1);

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        setContentView(R.layout.activity_create_task);

        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TASK,
                new String[]{DatabaseHelper._ID,
                        DatabaseHelper.TASK_DEADLINE,
                        DatabaseHelper.TASK_NAME_COLUMN,
                        DatabaseHelper.TASK_IS_DONE,
                        DatabaseHelper.TASK_COMMENT,
                        DatabaseHelper.TASK_DURATION,
                        DatabaseHelper.TASK_START_TIME,
                        DatabaseHelper.TASK_PRIORITY},
                DatabaseHelper._ID + "=" + id, null,
                null, null, null);

        cursor.moveToFirst();

        taskText = (EditText)findViewById(R.id.createTaskEditTextTaskText);

        if (id != -1) {
            taskText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_NAME_COLUMN)));
        }

        commentText = (EditText)findViewById(R.id.createTaskEditTextComments);

        if (id != -1) {
            commentText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_COMMENT)));
        }

        final SeekBar seekbar = (SeekBar)findViewById(R.id.createTaskSeekBar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setMax(10);
        seekbar.setProgress(5);
        priority = 5;

        if (id != -1) {
            priority = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_PRIORITY));
            seekbar.setProgress(priority);
        }

        priorityView = (TextView)findViewById(R.id.createTaskTextViewPriority);
        priorityView.setText(String.valueOf(priorityView.getText()) + " " +  priority);

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


        if (id != -1) {
            Calendar current = new GregorianCalendar();
            current.setTimeInMillis((long)cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_START_TIME))*1000);

            startYear = current.get(Calendar.YEAR);
            startMonth = current.get(Calendar.MONTH);
            startDay = current.get(Calendar.DAY_OF_MONTH);

            startHours = current.get(Calendar.HOUR_OF_DAY);
            startMinute = current.get(Calendar.MINUTE);


            current.setTimeInMillis((long)cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_DEADLINE))*1000);


            finishYear = current.get(Calendar.YEAR);
            finishMonth = current.get(Calendar.MONTH);
            finishDay = current.get(Calendar.DAY_OF_MONTH);

            finishHours = current.get(Calendar.HOUR_OF_DAY);
            finishMinute = current.get(Calendar.MINUTE);

            current.setTimeInMillis((long)cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_DURATION))*1000);

            durationHours = current.get(Calendar.HOUR_OF_DAY);
            durationMinute = current.get(Calendar.MINUTE);
        }
        cursor.close();
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
            return new DatePickerDialog(this, myCallBackStartDate, startYear, startMonth, startDay);
        } else if (id == DIALOG_DATE_FINISH) {
            return new DatePickerDialog(this, myCallBackFinishDate, finishYear, finishMonth, finishDay);
        } else if (id == DIALOG_TIME_START) {
            return new TimePickerDialog(this, myCallBackStartTime, startHours, startMinute, true);
        } else if (id == DIALOG_TIME_FINISH) {
            return new TimePickerDialog(this, myCallBackFinishTime, finishHours, finishMinute, true);
        } else if (id == DIALOG_TIME_DURATION) {
            return new TimePickerDialog(this, myCallBackDurationTime, durationHours, durationMinute, true);
        }
        return super.onCreateDialog(id);
    }


    DatePickerDialog.OnDateSetListener myCallBackStartDate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int _year, int monthOfYear,
                              int dayOfMonth) {
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

        GregorianCalendar durCal = new GregorianCalendar(1969, 0, 0, durationHours, durationMinute);

        newValues.put(DatabaseHelper.TASK_NAME_COLUMN, taskTextString);
        newValues.put(DatabaseHelper.TASK_COMMENT, commentString);
        newValues.put(DatabaseHelper.TASK_START_TIME, startCal.getTimeInMillis() / 1000);
        newValues.put(DatabaseHelper.TASK_DEADLINE, endCal.getTimeInMillis() / 1000);
        newValues.put(DatabaseHelper.TASK_DURATION, durCal.getTimeInMillis() / 1000);
        newValues.put(DatabaseHelper.TASK_IS_DONE, 0);
        newValues.put(DatabaseHelper.TASK_PRIORITY, priority);

        long idVal = id;
        if (idVal == -1) {
            idVal =  mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_TASK, null, newValues);
        } else {
            mSQLiteDatabase.update(DatabaseHelper.DATABASE_TABLE_TASK, newValues, "_id = " + id, null);
        }
        setResult(RESULT_OK, answerIntent);
        answerIntent.putExtra("id", idVal);
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
