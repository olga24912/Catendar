package ru.mit.au.spb.olga.catendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by olga on 22.02.16.
 */
public class AddTaskActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private ExpandableListView listOfTask;

    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<Long> chooseTaskId = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_tasks);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase14.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        listOfTask = (ExpandableListView)findViewById(R.id.addTaskExpandableListViw);

        listOfTask.setOnChildClickListener(myOnChildClickListener);

        synchronizedWithDataBase();
        drawTaskList();
    }

    ExpandableListView.OnChildClickListener myOnChildClickListener = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {
            chooseTaskId.add(taskList.get(groupPosition).getId());
            return true;
        }
    };

    private void synchronizedWithDataBase() {
        taskList.clear();

        Cursor cursor = mSQLiteDatabase.query("tasks", new String[]{DatabaseHelper._ID,
                        DatabaseHelper.TASK_NAME_COLUMN,
                        DatabaseHelper.TASK_PRIORITY,
                        DatabaseHelper.TASK_COMMENT,
                        DatabaseHelper.TASK_DURATION,
                        DatabaseHelper.TASK_START_TIME,
                        DatabaseHelper.TASK_DEADLINE,
                        DatabaseHelper.TASK_IS_DONE},
                null, null,
                null, null, null);

        while (cursor.moveToNext()) {
            Task currentTask = new Task();

            currentTask.changeText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_NAME_COLUMN)));
            currentTask.setComment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_COMMENT)));
            currentTask.changeIsDone(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_IS_DONE)) == 1);
            currentTask.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper._ID)));

            Long idInHeap = currentTask.getId();

            GregorianCalendar curDate = new GregorianCalendar();
            curDate.setTimeInMillis(1000 * cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_DEADLINE)));

            currentTask.setDeadlineTime(curDate);

            curDate.setTimeInMillis(1000 * cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_DURATION)));
            currentTask.setDuration(curDate);

            currentTask.setPriority(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_PRIORITY)));

            curDate.setTimeInMillis(1000 * cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_START_TIME)));
            currentTask.setStartTime(curDate);

            taskList.add(currentTask);
        }
        cursor.close();
    }

    private void drawTaskList() {
        ExpListAdapterAddTask adapter;
        adapter = new ExpListAdapterAddTask(getApplicationContext(), taskList, mSQLiteDatabase);
        listOfTask.setAdapter(adapter);
    }


}
