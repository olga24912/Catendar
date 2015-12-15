package ru.mit.au.spb.olga.catendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by olga on 15.12.15.
 */
public class CreateWeekActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private EditText weekDate;
    private ArrayList<CheckBox> existsTemplate = new ArrayList<>();
    private ArrayList<Integer> templateId = new ArrayList<>();

    private int selectedRadioButton = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_week);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase8.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        weekDate = (EditText)findViewById(R.id.write_date);

        createCheckBox();

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.current_week:
                        selectedRadioButton = 0;
                        break;
                    case R.id.next_week:
                        selectedRadioButton = 1;
                        break;
                    case R.id.other_week:
                        selectedRadioButton = 2;
                        break;

                    default:
                        break;
                }
            }
        });
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
        String date = String.valueOf(weekDate.getText());

        GregorianCalendar gc = new GregorianCalendar(yearFromDate(date), monthFromDate(date),
                dayFromDate(date));

        Week nw = new Week(gc);
        long msTime = nw.getTimeInMS();

        ContentValues newValues = new ContentValues();

        newValues.put(DatabaseHelper.WEEK_START_DATE, msTime);

        mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_WEEK, null, newValues);

        Integer id = findIdWithThisTime(msTime);

        for (int i = 0; i < existsTemplate.size(); i++) {
            if (existsTemplate.get(i).isChecked()) {
                ContentValues twValues = new ContentValues();

                newValues.put(DatabaseHelper.TEMPLATES_IN_WEEKS_TEMPLATE_ID, templateId.get(i));
                newValues.put(DatabaseHelper.TEMPLATES_IN_WEEKS_WEEK_ID, id);

                mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_TEMPLATES_IN_WEEKS, null, newValues);
            }
        }

        setResult(RESULT_OK);
        finish();
    }

    public void onCancelWeekClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
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

}
