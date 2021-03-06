package ru.mit.au.spb.olga.catendar.view.template;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;
import ru.mit.au.spb.olga.catendar.model.Week;
import ru.mit.au.spb.olga.catendar.utils.DataBaseUtils;

public class CreateWeekActivity extends AppCompatActivity {
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<CheckBox> existsTemplate = new ArrayList<>();
    private ArrayList<Integer> templateId = new ArrayList<>();
    private ArrayList<String> templateText = new ArrayList<>();

    private int DIALOG_DATE = 1;

    private int year;
    private int month;
    private int day;

    private TextView tvInfo;

    private Week nw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_week);

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
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
                        tvInfo.setText("");
                        break;
                    case R.id.other_week:
                        showDialog(DIALOG_DATE);
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
                        DatabaseHelper.TEMPLATE_NAME,
                        DatabaseHelper.TEMPLATE_FOR_WEEK,
                        DatabaseHelper.TEMPLATE_WEEK_ID,
                        DatabaseHelper.TEMPLATE_ORIGIN_ID
                },
                null, null,
                null, null, null) ;

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
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayoutInCreateWeek);

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
                templateText.add(name);
            }
        }
        cursor.close();
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            return new DatePickerDialog(this, myCallBackDate, year, month, day);
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

    public void onOkWeekClick(View view) {
        GregorianCalendar gc = new GregorianCalendar(year, month, day);

        nw = new Week(gc);
        long sTime = nw.getStartDateInSeconds();

        if (DataBaseUtils.findIdBySpecifiedTime(sTime, mSQLiteDatabase) == null) {
            DataBaseUtils.createWeek(sTime, mSQLiteDatabase);
        }

        Integer id = DataBaseUtils.findIdBySpecifiedTime(sTime, mSQLiteDatabase);

        for (int i = 0; i < existsTemplate.size(); i++) {
            if (existsTemplate.get(i).isChecked()) {
                int tId = DataBaseUtils.findTIdWithThisOIdWId(id, templateId.get(i), mSQLiteDatabase);
                copyEvent(templateId.get(i), tId);
            }
        }

        setResult(RESULT_OK);
        finish();
    }

    private void copyEvent(int oldTp, int nwId) {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_EVENT, new String[]{
                        DatabaseHelper._ID, DatabaseHelper.EVENT_NAME,
                        DatabaseHelper.EVENT_START_DATE, DatabaseHelper.EVENT_END_DATE,
                        DatabaseHelper.EVENT_PARENT_TEMPLATE
                },
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            int curId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.EVENT_PARENT_TEMPLATE));
            if (curId == oldTp) {
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_NAME));
                long startTime = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.EVENT_START_DATE));
                long endTime = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.EVENT_END_DATE));

                GregorianCalendar startOfLife = new GregorianCalendar(1970, 0, 4, 0, 0);
                GregorianCalendar startGC = new GregorianCalendar();
                startGC.setTimeInMillis(startTime*1000);
                GregorianCalendar endGC = new GregorianCalendar();
                endGC.setTimeInMillis(endTime * 1000);

                startTime = nw.getStartDateInSeconds() + startTime - startOfLife.getTimeInMillis()/1000;
                endTime = nw.getStartDateInSeconds() + endTime - startOfLife.getTimeInMillis()/1000;

                ContentValues tValues = new ContentValues();

                tValues.put(DatabaseHelper.EVENT_NAME, name);
                tValues.put(DatabaseHelper.EVENT_START_DATE, startTime);
                tValues.put(DatabaseHelper.EVENT_END_DATE, endTime);
                tValues.put(DatabaseHelper.EVENT_PARENT_TEMPLATE, nwId);

                mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_EVENT, null, tValues);
            }
        }
        cursor.close();
    }

    public void onCancelWeekClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
