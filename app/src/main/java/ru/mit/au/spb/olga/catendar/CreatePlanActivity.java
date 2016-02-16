package ru.mit.au.spb.olga.catendar;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by olga on 16.02.16.
 */
public class CreatePlanActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private int DIALOG_DATE_START = 1;
    private DatePicker startDate;
    private TextView startDateTextView;
    private int startYear, startMonth, startDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_plan);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase13.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        startDateTextView = (TextView)findViewById(R.id.createPlanTextViewDate);

        Calendar today = Calendar.getInstance();
        startYear = today.get(Calendar.YEAR);
        startMonth = today.get(Calendar.MONTH);
        startDay = today.get(Calendar.DAY_OF_MONTH);
    }

    public void onCreateTaskClick(View view) {
        Intent intent = new Intent(CreatePlanActivity.this, CreateTaskActivity.class);

        startActivityForResult(intent, 0);
    }

    public void onSetDate(View view) {
        showDialog(DIALOG_DATE_START);
    }

    protected Dialog onCreateDialog(int id) {
        DatePickerDialog tpd = new DatePickerDialog(this,
                (DatePickerDialog.OnDateSetListener) myCallBackStartDate, startYear, startMonth, startDay);
        return tpd;
    }

    DatePickerDialog.OnDateSetListener myCallBackStartDate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int _year, int monthOfYear,
                              int dayOfMonth) {
            startYear = _year;
            startMonth = monthOfYear;
            startDay = dayOfMonth;
            startDateTextView.setText("Plan on: " + startDay + "." + startMonth + "." + startYear);
        }
    };
}
