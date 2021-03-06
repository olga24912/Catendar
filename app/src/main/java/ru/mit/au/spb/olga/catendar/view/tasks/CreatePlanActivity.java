package ru.mit.au.spb.olga.catendar.view.tasks;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;
import ru.mit.au.spb.olga.catendar.model.Task;

public class CreatePlanActivity extends AppCompatActivity {
    private SQLiteDatabase mSQLiteDatabase;

    private EditText nameEditText;
    private int startYear, startMonth, startDay;

    ArrayList<Task> taskArrayList = new ArrayList<>();
    ArrayList<Long> taskId = new ArrayList<>();

    private long plan_id = -1;
    private int countTaskWas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        nameEditText = (EditText)findViewById(R.id.createPlanEditTextName);

        plan_id = getIntent().getLongExtra("id", -1);

        if (plan_id == -1) {
            Calendar today = Calendar.getInstance();
            startYear = today.get(Calendar.YEAR);
            startMonth = today.get(Calendar.MONTH);
            startDay = today.get(Calendar.DAY_OF_MONTH);
        } else {
            Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_HEAP,
                    new String[]{DatabaseHelper._ID,
                            DatabaseHelper.HEAP_DATE,
                            DatabaseHelper.HEAP_NAME},
                    DatabaseHelper._ID + "=" + plan_id, null,
                    null, null, null);

            cursor.moveToFirst();
            GregorianCalendar curDate  = new GregorianCalendar();
            curDate.setTimeInMillis(1000*cursor.getLong(cursor.getColumnIndex(DatabaseHelper.HEAP_DATE)));

            nameEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.HEAP_NAME)));

            startYear = curDate.get(Calendar.YEAR);
            startMonth = curDate.get(Calendar.MONTH);
            startDay = curDate.get(Calendar.DAY_OF_MONTH);

            getTaskFromDataBase();

            cursor.close();
        }

        synchronizedWithDataBase();
    }

    private void getTaskFromDataBase() {
        Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TASK_HEAP,
                new String[]{DatabaseHelper._ID,
                        DatabaseHelper.TASK_HEAP_TASK_ID,
                        DatabaseHelper.TASK_HEAP_HEAP_ID},
                DatabaseHelper.TASK_HEAP_HEAP_ID + "=" + plan_id, null,
                null, null, null);
        while (cursor.moveToNext()) {
            ++countTaskWas;
            taskId.add(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TASK_HEAP_TASK_ID)));
        }
        cursor.close();
    }

    static final int CREATE_TASK = 0;

    public void onCreateTaskClick(View view) {
        Intent intent = new Intent(CreatePlanActivity.this, CreateTaskActivity.class);

        startActivityForResult(intent, CREATE_TASK);
    }

    public void onSetDate(View view) {
        int DIALOG_DATE_START = 1;
        showDialog(DIALOG_DATE_START);
    }

    @NotNull
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, myCallBackStartDate, startYear, startMonth, startDay);
    }

    DatePickerDialog.OnDateSetListener myCallBackStartDate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int _year, int monthOfYear,
                              int dayOfMonth) {
            startYear = _year;
            startMonth = monthOfYear;
            startDay = dayOfMonth;
            nameEditText.setText("Plan on: " + startDay + "." + startMonth + "." + startYear);
        }
    };

    private void synchronizedWithDataBase() {
        taskArrayList.clear();
        for (int i = 0; i < taskId.size(); ++i) {
            Task current = new Task();
            Cursor cursor = mSQLiteDatabase.query(DatabaseHelper.DATABASE_TABLE_TASK, new String[]
                            {DatabaseHelper._ID,
                            DatabaseHelper.TASK_NAME_COLUMN,
                            DatabaseHelper.TASK_COMMENT,
                            DatabaseHelper.TASK_START_TIME,
                            DatabaseHelper.TASK_PRIORITY,
                            DatabaseHelper.TASK_IS_DONE,
                            DatabaseHelper.TASK_DURATION,
                            DatabaseHelper.TASK_DEADLINE,
                            },
                    DatabaseHelper._ID + "=" + taskId.get(i), null, null, null, null, null);

            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                continue;
            }
            current.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_NAME_COLUMN)));

            taskArrayList.add(current);
            cursor.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_TASK) {
            if (resultCode == RESULT_OK) {
                long[] args = data.getLongArrayExtra("id");
                if (args == null) {
                    return;
                }
                for (long i: args) {
                    taskId.add(i);
                }
                synchronizedWithDataBase();
                drawTaskList();
            }
        }
    }

    private void drawTaskList() {
        ListView listView = (ListView)findViewById(R.id.createPlanListView);

        final String[] tasksName = new String[taskArrayList.size()];
        for (int i = 0; i < taskArrayList.size(); i++) {
            tasksName[i] = taskArrayList.get(i).getText();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, tasksName);
        listView.setAdapter(adapter);
    }

    public void onOKClickInPlan(View view) {
        String heapName = nameEditText.getText().toString();

        GregorianCalendar currentDate = new GregorianCalendar(startYear, startMonth, startDay);
        Long time = currentDate.getTimeInMillis()/1000;

        ContentValues newValues = new ContentValues();

        newValues.put(DatabaseHelper.HEAP_DATE, time);
        newValues.put(DatabaseHelper.HEAP_NAME, heapName);


        long heapId = plan_id;
        if (heapId == -1) {
            heapId = mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_HEAP, null, newValues);
        } else {
            mSQLiteDatabase.update(DatabaseHelper.DATABASE_TABLE_HEAP, newValues, DatabaseHelper._ID
                    + "=" + heapId, null);
        }

        for (int i = countTaskWas; i < taskId.size(); ++i) {
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.TASK_HEAP_HEAP_ID, heapId);
            values.put(DatabaseHelper.TASK_HEAP_TASK_ID, taskId.get(i));

            mSQLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_TASK_HEAP, null, values);
        }

        finish();
    }

    public void onAddTaskInPlanClick(View view) {
        Intent intent = new Intent(this, AddTaskActivity.class);

        startActivityForResult(intent, 0);
    }
}
