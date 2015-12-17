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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by olga on 15.12.15.
 */
public class CreateWeekActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<CheckBox> existsTemplate = new ArrayList<>();
    private ArrayList<Integer> templateId = new ArrayList<>();

    private int selectedRadioButton = -1;

    private int DIALOG_DATE = 1;

    private int year;
    private int month;
    private int day;

    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_week);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase10.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        tvInfo = (TextView)findViewById(R.id.Selected_date);

        createCheckBox();

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Calendar today;
                Week week;
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.current_week:
                        today = Calendar.getInstance();
                        year = today.get(Calendar.YEAR);
                        month = today.get(Calendar.MONTH);
                        day = today.get(Calendar.DAY_OF_MONTH);
                        week = new Week(new GregorianCalendar(year, month, day));
                        year = week.getStartDate().get(Calendar.YEAR);
                        month = week.getStartDate().get(Calendar.MONTH);
                        day = week.getStartDate().get(Calendar.DAY_OF_MONTH);
                        selectedRadioButton = 0;
                        tvInfo.setText("");
                        break;
                    case R.id.next_week:
                        today = Calendar.getInstance();
                        year = today.get(Calendar.YEAR);
                        month = today.get(Calendar.MONTH);
                        day = today.get(Calendar.DAY_OF_MONTH);
                        week = new Week(new GregorianCalendar(year, month, day));
                        GregorianCalendar nextWeek = week.getStartDate();
                        nextWeek.add(Calendar.DAY_OF_MONTH, 7);
                        year = week.getStartDate().get(Calendar.YEAR);
                        month = week.getStartDate().get(Calendar.MONTH);
                        day = week.getStartDate().get(Calendar.DAY_OF_MONTH);
                        selectedRadioButton = 1;
                        tvInfo.setText("");
                        break;
                    case R.id.other_week:
                        showDialog(DIALOG_DATE);
                        selectedRadioButton = 2;
                        break;

                    default:
                        break;
                }
            }
        });
        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_MONTH);
    }

    private void createCheckBox() {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TEMPLATE, new String[]{DatabaseHelper._ID,
                        DatabaseHelper.TEMPLATE_NAME},
                null, null,
                null, null, null) ;

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor
                    .getColumnIndex(DatabaseHelper.TEMPLATE_NAME));

            int id = cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper._ID));

            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayoutInCreateWeek);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.gravity = Gravity.LEFT;
            layoutParams.setMargins(0, 10, 10, 10);

            CheckBox newCheckBox = new CheckBox(this);
            newCheckBox.setLayoutParams(layoutParams);
            newCheckBox.setText(name);
            linearLayout.addView(newCheckBox);

            existsTemplate.add(newCheckBox);
            templateId.add(id);
        }
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, (DatePickerDialog.OnDateSetListener) myCallBackDate, year, month, day);
            return tpd;
        }
        return super.onCreateDialog(id);
    }


    DatePickerDialog.OnDateSetListener myCallBackDate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int _year, int monthOfYear,
                              int dayOfMonth) {
            Week week = new Week(new GregorianCalendar(_year, monthOfYear, dayOfMonth));
            year = week.getStartDate().get(Calendar.YEAR);
            month = week.getStartDate().get(Calendar.MONTH);
            day = week.getStartDate().get(Calendar.DAY_OF_MONTH);
            tvInfo.setText("Event day is " + day + "/" + (month + 1) + "/" + year);
        }
    };

    private Integer findIdWithThisTime(long ms) {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_WEEK, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.WEEK_START_DATE
                },
                null, null, null,
                null, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            long time = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.WEEK_START_DATE));

            if (time == ms) {
                return id;
            }
        }

        return null;
    }

    public void onOkWeekClick(View view) {
        GregorianCalendar gc = new GregorianCalendar(year, month, day);

        Week nw = new Week(gc);
        long sTime = nw.getTimeInMS();

        if (findIdWithThisTime(sTime) == null) {
            ContentValues newValues = new ContentValues();

            newValues.put(DatabaseHelper.WEEK_START_DATE, sTime);

            mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_WEEK, null, newValues);
        }

        Integer id = findIdWithThisTime(sTime);

        for (int i = 0; i < existsTemplate.size(); i++) {
            if (existsTemplate.get(i).isChecked()) {
                ContentValues twValues = new ContentValues();

                twValues.put(DatabaseHelper.TEMPLATES_IN_WEEKS_TEMPLATE_ID, templateId.get(i));
                twValues.put(DatabaseHelper.TEMPLATES_IN_WEEKS_WEEK_ID, id);

                mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_TEMPLATES_IN_WEEKS, null, twValues);
            }
        }

        setResult(RESULT_OK);
        finish();
    }

    public void onCancelWeekClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
