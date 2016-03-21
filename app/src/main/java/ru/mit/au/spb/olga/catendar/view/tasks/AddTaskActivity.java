package ru.mit.au.spb.olga.catendar.view.tasks;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import ru.mit.au.spb.olga.catendar.R;
import ru.mit.au.spb.olga.catendar.model.DatabaseHelper;
import ru.mit.au.spb.olga.catendar.model.Task;

public class AddTaskActivity extends AppCompatActivity {
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<Long> chooseTaskId = new ArrayList<>();
    private ArrayList<String> taskListName = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_tasks);

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        synchronizedWithDataBase();

        ListView listOfTask = (ListView) findViewById(R.id.addTaskListViw);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, taskListName);

        listOfTask.setAdapter(adapter);

        listOfTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                chooseTaskId.add(taskList.get(position).getId());
                view.setBackgroundColor(0xffc5e384);
                return true;
            }
        });
    }

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

            currentTask.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_NAME_COLUMN)));
            currentTask.setComment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TASK_COMMENT)));
            currentTask.setIsDone(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_IS_DONE)) == 1);
            currentTask.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper._ID)));

            GregorianCalendar curDate = new GregorianCalendar();
            curDate.setTimeInMillis(1000 * cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_DEADLINE)));

            currentTask.setDeadlineTime(curDate);

            curDate.setTimeInMillis(1000 * cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_DURATION)));
            currentTask.setDuration(curDate);

            currentTask.setPriority(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_PRIORITY)));

            curDate.setTimeInMillis(1000 * cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TASK_START_TIME)));
            currentTask.setStartDate(curDate);

            taskList.add(currentTask);
            taskListName.add(currentTask.getText());
        }
        cursor.close();
    }

    private static final String ADD_TASK = "id";

    public void onOkClickInAddTask(View view) {
        Intent answerIntent = new Intent();
        long[] retVal = new long[chooseTaskId.size()];
        for (int i = 0; i < chooseTaskId.size(); ++i) {
            retVal[i] = chooseTaskId.get(i);
        }
        answerIntent.putExtra(ADD_TASK, retVal);
        setResult(RESULT_OK, answerIntent);
        finish();
    }
}